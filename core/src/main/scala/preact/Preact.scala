package preact

import org.scalajs.dom

object Preact {

  val raw = preact.raw.RawPreact

  type VNode = raw.VNode

  type Child = raw.Child

  type Component[Props, State] = preact.Component[Props, State]

  def render(node: VNode, parent: dom.Element): dom.Element = raw.render(node, parent)
  def render(node: VNode, parent: dom.Element, mergeWith: dom.Element): dom.Element =
    raw.render(node, parent, mergeWith)

  def rerender(): Unit = raw.rerender()
}
