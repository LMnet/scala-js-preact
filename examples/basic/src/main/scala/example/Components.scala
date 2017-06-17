package example

import org.scalajs.dom
import preact.Preact.VNode
import preact.dsl.symbol._
import preact.macros.PreactComponent

@PreactComponent[Unit, Unit]
class StatelessComponent {

  def onClick = () => {
    dom.console.log("CLICK!")
  }

  def render(): VNode = {
    'b("onclick" -> onClick, "Some bold text")
  }
}


object StatefulComponent {
  case class State(name: String)
}

@PreactComponent[Unit, StatefulComponent.State](withChildren = true)
class StatefulComponent {

  import StatefulComponent._

  initialState(State("Foo"))

  override protected def componentDidMount(): Unit = {
    setState(State("Bar"))
  }

  def render(): VNode = {
    'div(PropsComponent(state.name), children)
  }
}

object PropsComponent {
  case class Props(name: String)
}

@PreactComponent[PropsComponent.Props, Unit]
class PropsComponent {

  def render(): VNode = {
    'p(props.name)
  }
}
