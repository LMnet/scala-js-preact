package example

import org.scalajs.dom
import preact.Preact.VNode
import preact.dsl.symbol._
import preact.macros.PreactComponent

@PreactComponent[Unit]
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

@PreactComponent[StatefulComponent.State](withChildren = true)
class StatefulComponent {

  import StatefulComponent._

  initialState(State("Petya"))

  override protected def componentDidMount(): Unit = {
    setState(State("Grisha"))
  }

  def render(): VNode = {
    'div(PropsComponent(state.name), children)
  }
}

object PropsComponent {
  case class Props(name: String)
}

@PreactComponent[Unit]
class PropsComponent(props: PropsComponent.Props) {

  def render(): VNode = {
    'p(props.name)
  }
}
