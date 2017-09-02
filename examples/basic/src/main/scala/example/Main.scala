package example

import org.scalajs.dom
import preact.Preact

import scala.scalajs.js.annotation.JSExportTopLevel

object Main {

  import preact.dsl.symbol._

  @JSExportTopLevel("example.Main.main")
  def main(): Unit = {
    val root = dom.document.getElementById("scala")

    val appClass = StatefulComponent(
      'p("Test"),
      StatelessComponent(),
      FunctionComponents.withChildren(
        FunctionComponents.withPropsAndChildren(
          FunctionComponents.Props(10),
          FunctionComponents.simple(),
          FunctionComponents.withProps(FunctionComponents.Props(5))
        )
      )
    )

    Preact.render(appClass, root)
  }
}
