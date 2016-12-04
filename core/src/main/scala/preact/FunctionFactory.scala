package preact

import preact.Preact.{Child, VNode}

import scala.scalajs.js

object FunctionFactory {

  import Preact.FunctionComponent._

  def withProps[Props <: Product](f: Props => VNode): WithProps[Props] = {
    new WithProps[Props] {
      override def apply(props: Props): VNode = {
        val jsWrapper = (jsProps: js.Dynamic) => {
          f(jsProps.asInstanceOf[Props])
        }
        Preact.raw.h(jsWrapper(_), props.asInstanceOf[Preact.raw.Attributes], null)
      }
    }
  }

  def withChildren(f: Seq[Child] => VNode): WithChildren = {
    new WithChildren {
      override def apply(children: Child*): VNode = {
        val jsWrapper = (jsProps: js.Dynamic) => {
          f(jsProps.children.asInstanceOf[js.Array[Child]])
        }
        Preact.raw.h(jsWrapper(_), null, children: _*)
      }
    }
  }

  def withPropsAndChildren[Props <: Product](f: (Props, Seq[Child]) => VNode): WithPropsAndChildren[Props] = {
    new WithPropsAndChildren[Props] {
      override def apply(props: Props, children: Child*): VNode = {
        val jsWrapper = (jsProps: js.Dynamic) => {
          f(jsProps.asInstanceOf[Props], jsProps.children.asInstanceOf[js.Array[Child]])
        }
        Preact.raw.h(jsWrapper(_), props.asInstanceOf[Preact.raw.Attributes], children: _*)
      }
    }
  }
}
