package preact.raw

import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSGlobal, JSName}
import scala.scalajs.js.|

@JSGlobal("preact")
@js.native
private[preact] object RawPreact extends js.Object {

  @js.native
  trait VNode extends js.Object {
    def nodeName: String | js.Dynamic = js.native
    def attributes: js.UndefOr[js.Dictionary[js.Any]] = js.native
    def children: js.Array[VNode] = js.native
    def key: js.UndefOr[String] = js.native
  }

  type Attributes = js.Dictionary[js.Any]

  type Child = VNode | String

  type FunctionComponent = js.Function0[VNode]
  type PropsFunctionComponent = js.Function1[js.Dynamic, VNode]

  def h(node: js.Dynamic,
        params: Attributes,
        children: Child*): VNode = js.native

  def h(node: FunctionComponent,
        params: Attributes,
        children: Child*): VNode = js.native

  def h(node: PropsFunctionComponent,
        params: js.Dictionary[js.Any],
        children: Child*): VNode = js.native

  def h(node: String,
        params: Attributes,
        children: Child*): VNode = js.native

  def render(node: VNode, parent: dom.Element): dom.Element = js.native
  def render(node: VNode, parent: dom.Element, mergeWith: dom.Element): dom.Element = js.native

  def rerender(): Unit = js.native

  @js.native
  abstract class Component extends js.Object {

    @JSName("props")
    @inline
    final def jsProps: js.Dynamic = js.native

    @JSName("state")
    @inline
    final var jsState: js.Dynamic = js.native

    @JSName("setState")
    final def jsSetState(newState: js.Dynamic): Unit = js.native

    @JSName("base")
    @inline
    final def jsBase: dom.Element = js.native

    def render(): VNode
  }
}
