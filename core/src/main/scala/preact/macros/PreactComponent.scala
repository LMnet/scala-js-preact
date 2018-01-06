package preact.macros

import com.github.ghik.silencer.silent

import scala.annotation.compileTimeOnly
import scala.collection.immutable.Seq
import scala.meta._

private[preact] object PreactComponentImpl {

  case class Params(propsType: Type, stateType: Type, withChildrenValue: Boolean)

  private val ConstructorTag = t"_root_.scala.scalajs.js.ConstructorTag"
  private val VNode = t"_root_.preact.Preact.VNode"
  private val Child = t"_root_.preact.Preact.Child"
  private val Attributes = t"_root_.preact.Preact.raw.Attributes"
  private val h = q"_root_.preact.Preact.raw.h"

  private def isUnit(tpe: Type): Boolean = tpe.structure == t"Unit".structure

  def extractAnnotationParams(annotation: Stat): Params = {
    annotation match {
      case q"new $_[$propsTpe, $stateTpe](${Lit.Boolean(value)})" =>
        Params(propsTpe, stateTpe, value)
      case q"new $_[$propsTpe, $stateTpe](withChildren = ${Lit.Boolean(value)})" =>
        Params(propsTpe, stateTpe, value)
      case q"new $_[$propsTpe, $stateTpe]()" =>
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
        val newClass = addBaseClass(cls, params.propsType, params.stateType)
        Term.Block(Seq(newClass, newCompanion))

      case None =>
        checkCtorProps(cls.ctor.paramss, params.propsType)
        val applyMethods = createApplyMethods(cls.name, params.propsType, params.withChildrenValue, None)
        val companion = q"object ${Term.Name(cls.name.value)} { ..$applyMethods }"
        val newClass = addBaseClass(cls, params.propsType, params.stateType)
        Term.Block(Seq(newClass, companion))
    }
  }

  def checkCtorProps(paramss: Seq[Seq[Term.Param]], annotationPropsType: Type): Unit = {
    // TODO: validation monad?
    paramss match {
      case Seq(Seq(Term.Param(_, name, _, _))) =>
        if (name.value == "props") {
          abort("Component's constructor argument can't be named 'props', " +
            "because of naming conflict with Preact's 'props'. " +
            "If you want to access props in the class constructor you should rename constructor argument, " +
            "for example to 'initialProps'.")
        } else ()
      case Seq(Seq(Term.Param(_, _, Some(tpe), _)))
        if tpe.structure != annotationPropsType.structure =>
        abort("Props types in the annotation parameters and in the component's constructor didn't match.")
      case x if x.lengthCompare(1) > 0 =>
        abort("Component can have only one constructor argument - initial props.")
      case x if x.isEmpty =>
        ()
      case _ =>
        abort("Component's constructor is invalid")
    }
  }

  def createApplyMethods(
    name: Type.Name,
    propsType: Type,
    withChildren: Boolean,
    companionOpt: Option[Defn.Object]
  ): Seq[Defn] = {
    def commonApply(): Seq[Defn] = {
      if (isUnit(propsType)) {
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
          q"""def apply(props: $propsType)(implicit ct: $ConstructorTag[$name]): $VNode = {
            $h(ct.constructor, props.asInstanceOf[$Attributes], null)
          }"""
        ) ++ (if (withChildren) {
          Seq(
            q"""def apply(
              props: $propsType, children: $Child*)(implicit ct: $ConstructorTag[$name]
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
      val propsClassNameOpt: Option[Type.Name] = if (isUnit(propsType)) None else {
        propsType match {
          case Type.Select(_, x) => Some(x)
          case x: Type.Name => Some(x)
          case _ => None
        }
      }
      def ctorParamssToDefParamss(paramss: Seq[Seq[Term.Param]]): Seq[Seq[Term.Param]] = {
        paramss.map(_.map { param =>
          param.copy(mods = param.mods.filterNot { mod =>
            mod.is[Mod.ValParam] || mod.is[Mod.VarParam]
          })
        })
      }

      val result: Option[Option[Seq[Defn.Def]]] = for {
        propsClassName <- propsClassNameOpt
        companion <- companionOpt
        stats <- companion.templ.stats
      } yield {
        stats.collectFirst {
          // companion contains correct Props class of type `$tpe` in the body
          case q"case class $tpe(...$ctor)" if tpe.structure == propsClassName.structure =>
            val ctorCall = ctor.map(_.map { param =>
              Term.Name(param.name.value)
            })
            val isCtorHasImplicits = (for {
              last <- ctor.lastOption
              head <- last.headOption
            } yield head.mods.exists(_.is[Mod.Implicit])).contains(true)

            val ctorTagParam = param"implicit ct: $ConstructorTag[$name]"

            val propsClassNameTerm = Term.Name(tpe.value)

            val commonApplyParamss = ctorParamssToDefParamss(if (isCtorHasImplicits) {
              ctor.init ++ Seq(ctor.last :+ ctorTagParam)
            } else {
              ctor ++ Seq(Seq(ctorTagParam))
            })
            val commonApply = Seq(
              q"""def apply(...$commonApplyParamss): $VNode = {
                apply($propsClassNameTerm(...$ctorCall))
              }"""
            )

            val childrenApply = if (withChildren) {
              def emptyPropsConstructorError = abort("Props constructor should have at least one argument")
              val childrenParam = param"children: $Child*"

              val applyParamss: Seq[Seq[Term.Param]] = ctorParamssToDefParamss(ctor match {
                case Seq(params) =>
                  Seq(params :+ childrenParam, Seq(ctorTagParam))
                case Seq() =>
                  emptyPropsConstructorError
                case _ =>
                  if (isCtorHasImplicits) {
                    val ctorWithoutImplicits = ctor.init
                    val implicitParams = ctor.last
                    ctorWithoutImplicits match {
                      case Seq(params) =>
                        Seq(params :+ childrenParam) ++ Seq(implicitParams :+ ctorTagParam)
                      case Seq() =>
                        emptyPropsConstructorError
                      case _ =>
                        ctorWithoutImplicits ++ Seq(Seq(childrenParam)) ++ Seq(implicitParams :+ ctorTagParam)
                    }
                  } else {
                    ctor ++ Seq(Seq(childrenParam), Seq(ctorTagParam))
                  }
              })

              Seq(
                q"""def apply(...$applyParamss): $VNode = {
                  apply($propsClassNameTerm(...$ctorCall), children: _*)
                }"""
              )
            } else Nil

            commonApply ++ childrenApply
        }
      }

      result.flatten.getOrElse(Seq.empty)
    }

    commonApply() ++ propsApply()
  }

  def addBaseClass(cls: Defn.Class, propsType: Type, stateType: Type): Defn.Class = {
    val componentBaseClass = ctor"_root_.preact.Preact.Component[$propsType, $stateType]"
    val newParents = cls.templ.parents :+ componentBaseClass
    cls.copy(templ = cls.templ.copy(parents = newParents))
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
        abort("@PreactComponent must annotate a Preact component class.")
    }

    result
  }
}
