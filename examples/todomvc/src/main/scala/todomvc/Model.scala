package todomvc

import java.util.UUID

import org.scalajs.dom
import pushka.annotation.pushka

object Model {

  import pushka.json._

  object ItemId {
    def apply(): ItemId = ItemId(UUID.randomUUID())
  }

  @pushka
  case class ItemId(value: UUID) extends AnyVal

  @pushka
  case class Item(id: ItemId = ItemId(), title: String, checked: Boolean = false) {
    def toggleChecked: Item = copy(checked = !checked)
  }

  @pushka
  sealed trait Filter {
    def label: String
    def path: String
  }
  object Filter {
    case object All extends Filter {
      val label = "All"
      val path = ""
    }
    case object Active extends Filter {
      val label = "Active"
      val path = label.toLowerCase
    }
    case object Completed extends Filter {
      val label = "Completed"
      val path = label.toLowerCase
    }
  }

  val storageKey = "todos-scala-js-preact"

  def persist(todos: Seq[Item]): Unit = {
    val json = write(todos)
    dom.window.localStorage.setItem(storageKey, json.toString)
  }

  def load(): Seq[Item] = {
    Option(dom.window.localStorage.getItem(storageKey)).fold(Seq.empty[Item])(read[Seq[Item]])
  }

}
