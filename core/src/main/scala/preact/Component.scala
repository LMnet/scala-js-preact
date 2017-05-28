package preact

import org.scalajs.dom
import preact.Preact.VNode
import com.github.ghik.silencer.silent
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSName, ScalaJSDefined}

@ScalaJSDefined
private[preact] abstract class Component[Props, State] extends Preact.raw.Component {

  @JSName("sProps")
  @inline
  protected final def props: Props = jsProps.asInstanceOf[Props]

  /**
    * Could be undefined at runtime!
    */
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

  @JSName("sBase")
  @inline
  protected final def base: Option[dom.Element] = {
    jsBase.asInstanceOf[js.UndefOr[dom.Element]].toOption
  }

  protected def componentWillMount(): Unit = js.undefined

  protected def componentDidMount(): Unit = js.undefined

  protected def componentWillUnmount(): Unit = js.undefined

  protected def componentDidUnmount(): Unit = js.undefined

  @silent
  protected def componentWillReceiveProps(props: Props): Unit = js.undefined

  @silent
  protected def shouldComponentUpdate(props: Props): Boolean = true

  protected def componentWillUpdate(): Unit = js.undefined

  protected def componentDidUpdate(): Unit = js.undefined

  def render(): VNode
}
