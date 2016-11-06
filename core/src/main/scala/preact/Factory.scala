package preact

import preact.Preact.VNode

import scala.scalajs.js

private[preact] object Factory {

  type WithProps = PropsComponentFactory
}

private[preact] trait ComponentFactory {

  type Props
  type State

  type Component <: Preact.Component[Props, State]
}

private[preact] trait Factory extends ComponentFactory {

  type Props = Unit

  def apply()(implicit ct: js.ConstructorTag[Component]): VNode = {
    Preact.raw.h(ct.constructor, null, null)
  }

  def apply(children: Preact.Child*)(implicit ct: js.ConstructorTag[Component]): VNode = {
    Preact.raw.h(ct.constructor, null, children: _*)
  }
}

private[preact] trait PropsComponentFactory extends ComponentFactory {

  def apply(props: Props)(implicit ct: js.ConstructorTag[Component]): VNode = {
    Preact.raw.h(ct.constructor, props.asInstanceOf[Preact.raw.Attributes], null)
  }

  def apply(props: Props, children: Preact.Child*)(implicit ct: js.ConstructorTag[Component]): VNode = {
    Preact.raw.h(ct.constructor, props.asInstanceOf[Preact.raw.Attributes], children: _*)
  }
}
