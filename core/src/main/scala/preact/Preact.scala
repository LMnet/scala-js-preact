package preact

import org.scalajs.dom

object Preact {

  val raw = preact.raw.RawPreact

  type VNode = raw.VNode

  type Child = raw.Child

  type Component[Props, State] = preact.Component[Props, State]

  type Factory = preact.Factory

  val Factory = preact.Factory

  val FunctionFactory = preact.FunctionFactory

  object FunctionComponent {

    trait WithProps[Props] {
      def apply(props: Props): VNode
    }

    trait WithChildren {
      def apply(children: Child*): VNode
    }

    trait WithPropsAndChildren[Props] {
      def apply(props: Props, children: Child*): VNode
    }
  }

  def render(node: VNode, parent: dom.Element): dom.Element = raw.render(node, parent)
  def render(node: VNode, parent: dom.Element, mergeWith: dom.Element): dom.Element =
    raw.render(node, parent, mergeWith)

  def rerender(): Unit = raw.rerender()
}
