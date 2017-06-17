package preact.macros

import com.github.ghik.silencer.silent

import scala.annotation.compileTimeOnly
import scala.collection.immutable.Seq
import scala.meta._

private[preact] object PreactComponentImpl {

  case class SimpleType(tpe: Either[Type.Name, Type.Select]) {

    val originalType: Type.Ref = tpe match {
      case Left(x) => x
      case Right(x) => x
    }

    val typeName: Type.Name = tpe match {
      case Left(name) => name
      case Right(Type.Select(_, name)) => name
    }

    def isUnit: Boolean = typeName.structure == t"Unit".structure
  }
  object SimpleType {

    def unapply(tpe: Type.Arg): Option[SimpleType] = {
      tpe match {
        case x: Type.Name => Some(SimpleType(Left(x)))
        case x: Type.Select => Some(SimpleType(Right(x)))
        case _ => None
      }
    }
  }

  case class Params(propsType: SimpleType, stateType: SimpleType, withChildrenValue: Boolean)

  private val ConstructorTag = t"_root_.scala.scalajs.js.ConstructorTag"
  private val VNode = t"_root_.preact.Preact.VNode"
  private val Child = t"_root_.preact.Preact.Child"
  private val Attributes = t"_root_.preact.Preact.raw.Attributes"
  private val h = q"_root_.preact.Preact.raw.h"
  private val ScalaJSDefined = mod"@_root_.scala.scalajs.js.annotation.ScalaJSDefined"

  type PropsTerm = Either[Unit, Term.Param]

  def extractAnnotationParams(annotation: Stat): Params = {
    annotation match {
      case q"new $_[${SimpleType(propsTpe)}, ${SimpleType(stateTpe)}](${Lit.Boolean(value)})" =>
        Params(propsTpe, stateTpe, value)
      case q"new $_[${SimpleType(propsTpe)}, ${SimpleType(stateTpe)}](withChildren = ${Lit.Boolean(value)})" =>
        Params(propsTpe, stateTpe, value)
      case q"new $_[${SimpleType(propsTpe)}, ${SimpleType(stateTpe)}]()" =>
        Params(propsTpe, stateTpe, withChildrenValue = false)
      case _  =>
        abort("PreactComponent annotation application is invalid")
    }
  }

  def expand(
    cls: Defn.Class,
    companionOpt: Option[Defn.Object],
    params: Params
  ): Term.Block = {
    companionOpt match {
      case Some(companion) =>
        checkCtorProps(cls.ctor.paramss, params.propsType)
        val applyMethods = createApplyMethods(cls.name, params.propsType, params.withChildrenValue, Some(companion))
        val templateStats: Seq[Stat] = companion.templ.stats.getOrElse(Nil) ++ applyMethods
        val newCompanion = companion.copy(templ = companion.templ.copy(stats = Some(templateStats)))
        val newClass = addScalaJsDefinedAnnotation(
          addBaseClass(cls, params.propsType, params.stateType)
        )
        Term.Block(Seq(newClass, newCompanion))

      case None =>
        checkCtorProps(cls.ctor.paramss, params.propsType)
        val applyMethods = createApplyMethods(cls.name, params.propsType, params.withChildrenValue, None)
        val companion = q"object ${Term.Name(cls.name.value)} { ..$applyMethods }"
        val newClass = addScalaJsDefinedAnnotation(
          addBaseClass(cls, params.propsType, params.stateType)
        )
        Term.Block(Seq(newClass, companion))
    }
  }

  def checkCtorProps(paramss: Seq[Seq[Term.Param]], annotationPropsType: SimpleType): Unit = {
    // TODO: validation monad?
    paramss match {
      case Seq(Seq(Term.Param(_, name, _, _))) =>
        if (name.value == "props") {
          abort("Component's constructor argument can't be named 'props', " +
            "because of naming conflict with Preact's 'props'. " +
            "If you want to access props in the class constructor you should rename constructor argument, " +
            "for example to 'initialProps'.")
        } else ()
      case Seq(Seq(Term.Param(_, _, Some(SimpleType(tpe)), _)))
        if tpe.originalType.structure != annotationPropsType.originalType.structure =>
        abort("Props types in the annotation parameters and in the component's constructor didn't match.")
      case x if x.length > 1 =>
        abort("Component can have only one constructor argument - initial props.")
      case x if x.isEmpty =>
        ()
      case _ =>
        abort("Component's constructor is invalid")
    }
  }

  def createApplyMethods(
    name: Type.Name,
    propsType: SimpleType,
    withChildren: Boolean,
    companionOpt: Option[Defn.Object]
  ): Seq[Defn] = {
    def commonApply(): Seq[Defn] = {
      if (propsType.isUnit) {
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
      } else {
        Seq(
          q"""def apply(props: ${propsType.typeName})(implicit ct: $ConstructorTag[$name]): $VNode = {
            $h(ct.constructor, props.asInstanceOf[$Attributes], null)
          }"""
        ) ++ (if (withChildren) {
          Seq(
            q"""def apply(
              props: ${propsType.typeName}, children: $Child*)(implicit ct: $ConstructorTag[$name]
            ): $VNode = {
              $h(ct.constructor, props.asInstanceOf[$Attributes], children: _*)
            }"""
          )
        } else Nil)
      }
    }

    /**
      * If companion contains `case class SomeProps($ctor)`,
      * where `SomeProps` is the Props type of this Component,
      * this method return convenient `apply($ctor)` in the companion.
      */
    def propsApply(): Seq[Defn] = {
      val propsClassNameOpt: Option[Type.Name] = if (propsType.isUnit) None else Some(propsType.typeName)

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

  def addBaseClass(cls: Defn.Class, propsType: SimpleType, stateType: SimpleType): Defn.Class = {
    val componentBaseClass = ctor"_root_.preact.Preact.Component[${propsType.originalType}, ${stateType.originalType}]"
    val newParents = cls.templ.parents :+ componentBaseClass
    cls.copy(templ = cls.templ.copy(parents = newParents))
  }

  def addScalaJsDefinedAnnotation(cls: Defn.Class): Defn.Class = {
    cls.copy(mods = cls.mods :+ ScalaJSDefined)
  }
}

@silent
@compileTimeOnly("PreactComponent annotation can be used only in compile time")
class PreactComponent[PropsType, StateType](withChildren: Boolean = false) extends scala.annotation.StaticAnnotation {

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
