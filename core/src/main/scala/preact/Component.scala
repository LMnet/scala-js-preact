package preact

import preact.Preact.VNode

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSName, ScalaJSDefined}

@ScalaJSDefined
private[preact] abstract class Component[Props, State] extends Preact.raw.Component {

  @JSName("sProps")
  @inline
  protected final def props: Props = jsProps.asInstanceOf[Props]

  @JSName("sState")
  @inline
  protected final def state: State = jsState.asInstanceOf[State]

  @JSName("sSetState")
  @inline
  protected final def setState(state: State): Unit = {
    jsSetState(state.asInstanceOf[js.Dynamic])
  }

  protected final def initialState(state: State): Unit = {
    if (js.Object.keys(jsState.asInstanceOf[js.Object]).length == 0) {
      jsState = state.asInstanceOf[js.Dynamic]
    } else {
      throw new IllegalAccessException("You can't set initial state multiple times")
    }
  }

  @inline
  protected final def children: Seq[Preact.Child] = {
    jsProps.children.asInstanceOf[js.Array[Preact.Child]]
  }

  @inline
  protected final def key: Option[String] = {
    jsProps.key.asInstanceOf[js.UndefOr[String]].toOption
  }

  protected def componentWillMount(): Unit = {}

  protected def componentDidMount(): Unit = {}

  protected def componentWillUnmount(): Unit = {}

  protected def componentDidUnmount(): Unit = {}

  protected def componentWillReceiveProps(props: Props): Unit = {}

  protected def shouldComponentUpdate(props: Props): Boolean = true

  protected def componentWillUpdate(): Unit = {}

  protected def componentDidUpdate(): Unit = {}

  def render(): VNode
}
