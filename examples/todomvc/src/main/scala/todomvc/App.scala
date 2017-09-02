package todomvc

import org.scalajs.dom
import preact.Preact

import scala.scalajs.js.annotation.JSExportTopLevel

object App {

  @JSExportTopLevel("todomvc.App.main")
  def main(): Unit = {
    val appDiv = dom.document.getElementById("app")
    val initialTodos = Model.load()
    val app = TodoMvc(initialTodos, Model.persist)
    Preact.render(app, appDiv)
  }
}
