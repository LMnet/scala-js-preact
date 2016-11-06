package example

import org.scalajs.dom
import preact.Preact

import scala.scalajs.js.JSApp

object Main extends JSApp {

  import preact.dsl.symbol._

  def main(): Unit = {
    val root = dom.document.getElementById("scala")

    val props1 = FunctionComponents.Props(5)
    val props2 = FunctionComponents.Props(10)

    val appClass = StatefulComponent(
      'p("NANAN"),
      StatelessComponent(),
      FunctionComponents.withChildren(
        FunctionComponents.withPropsAndChildren(
          props2,
          FunctionComponents.simple(),
          FunctionComponents.withProps(props1)
        )
      )
    )

    Preact.render(appClass, root)
  }
}
