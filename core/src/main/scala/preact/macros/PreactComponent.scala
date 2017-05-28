package preact.macros

import com.github.ghik.silencer.silent

import scala.annotation.compileTimeOnly
import scala.collection.immutable.Seq
import scala.meta._

private[preact] object PreactComponentImpl {

  case class Params(stateType: Type, withChildrenValue: Boolean)

  private val ConstructorTag = t"_root_.scala.scalajs.js.ConstructorTag"
  private val VNode = t"_root_.preact.Preact.VNode"
  private val Child = t"_root_.preact.Preact.Child"
  private val Attributes = t"_root_.preact.Preact.raw.Attributes"
  private val h = q"_root_.preact.Preact.raw.h"
  private val ScalaJSDefined = mod"@_root_.scala.scalajs.js.annotation.ScalaJSDefined"

  type PropsTerm = Either[Unit, Term.Param]

  def extractAnnotationParams(annotation: Stat): Params = {
    annotation match {
      case q"new $_[$tpe](${Lit.Boolean(value)})" =>
        Params(tpe, value)
      case q"new $_[$tpe](withChildren = ${Lit.Boolean(value)})" =>
        Params(tpe, value)
      case q"new $_[$tpe]()" =>
        Params(tpe, false)
      case _  =>
        abort("State type should be explicitly specified")
    }
  }

  def expand(
    cls: Defn.Class,
    companionOpt: Option[Defn.Object],
    params: Params
  ): Term.Block = {
    companionOpt match {
      case Some(companion) =>
        val propsTerm = getPropsTerm(cls.ctor.paramss)
        val applyMethods = createApplyMethods(cls.name, propsTerm, params.withChildrenValue, Some(companion))
        val templateStats: Seq[Stat] = companion.templ.stats.getOrElse(Nil) ++ applyMethods
        val newCompanion = companion.copy(templ = companion.templ.copy(stats = Some(templateStats)))
        val newClass = addScalaJsDefinedAnnotation(
          addBaseClass(cls, propsTerm, params.stateType)
        )
        Term.Block(Seq(newClass, newCompanion))

      case None =>
        val propsTerm = getPropsTerm(cls.ctor.paramss)
        val applyMethods = createApplyMethods(cls.name, propsTerm, params.withChildrenValue, None)
        val companion = q"object ${Term.Name(cls.name.value)} { ..$applyMethods }"
        val newClass = addScalaJsDefinedAnnotation(
          addBaseClass(cls, propsTerm, params.stateType)
        )
        Term.Block(Seq(newClass, companion))
    }
  }

  def getPropsTerm(paramss: Seq[Seq[Term.Param]]): PropsTerm = {
    paramss match {
      case Seq(Seq(props)) => Right(props)
      case x if x.isEmpty => Left(())
      case _ =>
        abort("Component can have only one constructor argument - props.")
    }
  }

  def createApplyMethods(
    name: Type.Name,
    propsTerm: PropsTerm,
    withChildren: Boolean,
    companionOpt: Option[Defn.Object]
  ): Seq[Defn] = {
    def commonApply(): Seq[Defn] = {
      propsTerm match {
        case Right(propsParam) =>
          val arg = Term.Name(propsParam.name.value)
          Seq(
            q"""def apply($propsParam)(implicit ct: $ConstructorTag[$name]): $VNode = {
              $h(ct.constructor, $arg.asInstanceOf[$Attributes], null)
            }"""
          ) ++ (if (withChildren) {
            Seq(
              q"""def apply($propsParam, children: $Child*)(implicit ct: $ConstructorTag[$name]): $VNode = {
                $h(ct.constructor, $arg.asInstanceOf[$Attributes], children: _*)
              }"""
            )
          } else Nil)

        case Left(_) =>
          Seq(
            q"""def apply()(implicit ct: $ConstructorTag[$name]): $VNode = {
              $h(ct.constructor, null, null)
            }"""
          ) ++ (if (withChildren) {
            Seq(
              q"""def apply(children: $Child*)(implicit ct: $ConstructorTag[$name]): $VNode = {
                $h(ct.constructor, null, children: _*)
              }"""
            )
          } else Nil)
      }
    }

    /**
      * If companion contains `case class SomeProps($ctor)`
      * this method return convenient `apply($ctor)` in the companion.
      */
    def propsApply(): Seq[Defn] = {
      val propsClassNameOpt: Option[Type.Name] = propsTerm match {
        case Right(Term.Param(_, _, Some(propsTpe), _)) =>
          propsTpe match {
            case Type.Select(_, x) => Some(x)
            case x @ Type.Name(_) => Some(x)
            case _ => None
          }
        case _ =>
          None
      }

      val result: Option[Option[Seq[Defn.Def]]] = for {
        propsClassName <- propsClassNameOpt
        companion <- companionOpt
        stats <- companion.templ.stats
      } yield {
        stats.collectFirst {
          // companion contains correct Props class of type `$tpe` in the body
          case q"case class $tpe(..$ctor)" if tpe.structure == propsClassName.structure =>
            val ctorCall = ctor.map { param =>
              Term.Name(param.name.value)
            }
            val propsClassNameTerm = Term.Name(tpe.value)
            Seq(
              q"""def apply(..$ctor)(implicit ct: $ConstructorTag[$name]): $VNode = {
                apply($propsClassNameTerm(..$ctorCall))
              }"""
            ) ++ (if (withChildren) {
              Seq(
                q"""def apply(..$ctor, children: $Child*)(implicit ct: $ConstructorTag[$name]): $VNode = {
                  apply($propsClassNameTerm(..$ctorCall), children: _*)
                }"""
              )
            } else Nil)
        }
      }

      result.flatten.getOrElse(Seq.empty)
    }

    commonApply() ++ propsApply()
  }

  def addBaseClass(cls: Defn.Class, propsTerm: PropsTerm, stateType: Type): Defn.Class = {
    val propsType: Type = propsTerm match {
      case Right(propsParam) =>
        propsParam.decltpe match {
          case Some(tpe: Type) =>
            tpe
          case Some(x) =>
            abort("Can't extract props type from component constructor: " +
              s"props type has invalid structure ${x.structure}")
          case None =>
            abort("Can't extract props type from component constructor")
        }
      case Left(_) =>
        t"_root_.scala.Unit"
    }
    val componentBaseClass = ctor"_root_.preact.Preact.Component[$propsType, $stateType]"
    val newParents = cls.templ.parents :+ componentBaseClass
    cls.copy(templ = cls.templ.copy(parents = newParents))
  }

  def addScalaJsDefinedAnnotation(cls: Defn.Class): Defn.Class = {
    cls.copy(mods = cls.mods :+ ScalaJSDefined)
  }
}

@silent
@compileTimeOnly("PreactComponent annotation can be used only in compile time")
class PreactComponent[StateType](withChildren: Boolean = false) extends scala.annotation.StaticAnnotation {

  inline def apply(defn: Any): Any = meta {
    val params = PreactComponentImpl.extractAnnotationParams(this)

    val result: Term.Block = defn match {
      case Term.Block(Seq(cls: Defn.Class, companion: Defn.Object)) =>
        PreactComponentImpl.expand(cls, Some(companion), params)

      case cls: Defn.Class =>
        PreactComponentImpl.expand(cls, None, params)

      case _ =>
        println(defn.structure)
        abort("@PreactComponent must annotate a Preact component class.")
    }

    result
  }
}
