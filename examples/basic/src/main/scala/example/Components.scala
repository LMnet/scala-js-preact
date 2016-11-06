package example

import org.scalajs.dom
import preact.Preact
import preact.Preact.VNode
import preact.dsl.symbol._

import scala.scalajs.js.annotation.ScalaJSDefined

object StatelessComponent extends Preact.Factory {

  type State = Unit

  @ScalaJSDefined
  class Component extends Preact.Component[Props, State] {

    def onClick = () => {
      dom.console.log("CLICK!")
    }

    def render(): VNode = {
      'b("onclick" -> onClick, "Some bold text")
    }
  }
}

object StatefulComponent extends Preact.Factory {

  case class State(name: String)

  @ScalaJSDefined
  class Component extends Preact.Component[Props, State] {

    initialState(State("Petya"))

    override protected def componentDidMount(): Unit = {
      setState(State("Grisha"))
    }

    def render(): VNode = {
      'div(PropsComponent(PropsComponent.Props(state.name)), children)
    }
  }
}

object PropsComponent extends Preact.Factory.WithProps {

  case class Props(name: String)
  type State = Unit

  @ScalaJSDefined
  class Component extends Preact.Component[Props, State] {

    def render(): VNode = {
      'p(props.name)
    }
  }
}
