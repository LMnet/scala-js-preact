package todomvc

import org.scalajs.dom
import preact.Preact

import scala.scalajs.js.JSApp

object App extends JSApp {

  def main(): Unit = {
    val appDiv = dom.document.getElementById("app")
    val initialTodos = Model.load()
    val app = TodoMvc(TodoMvc.Props(initialTodos, Model.persist))
    Preact.render(app, appDiv)
  }
}
