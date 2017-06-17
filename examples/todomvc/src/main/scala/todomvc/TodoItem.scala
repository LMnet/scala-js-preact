package todomvc

import org.scalajs.dom.raw.HTMLInputElement
import org.scalajs.dom.{Event, KeyboardEvent}
import preact.Preact.VNode
import preact.macros.PreactComponent
import todomvc.Model.ItemId

object TodoItem {

  case class Props(item: Model.Item,
                   onToggle: ItemId => Unit,
                   updateItem: (ItemId, String) => Unit,
                   onDestroy: ItemId => Unit)

  case class State(editing: Boolean)
}

import todomvc.TodoItem._

@PreactComponent[Props, State]
class TodoItem {

  import preact.dsl.symbol._

  initialState(State(false))

  def toggle(): Unit = {
    props.onToggle(props.item.id)
  }

  def editStart(): Unit = {
    setState(State(true))
  }

  def editingKeyDown(event: KeyboardEvent): Unit = {
    event.keyCode match {
      case Utils.KeyCodes.Enter =>
        submit(event)

      case Utils.KeyCodes.Escape =>
        event.preventDefault()
        setState(State(false))

      case _ =>
    }
  }

  def onBlur(event: Event): Unit = {
    submit(event)
  }

  def submit(event: Event): Unit = {
    val text = Utils.extractInputTarget(event).value.trim
    event.preventDefault()
    setState(State(false))

    if (text.nonEmpty) {
      updateItem(text)
    } else {
      destroy()
    }
  }

  def updateItem(newText: String): Unit = {
    props.updateItem(props.item.id, newText)
  }

  def destroy(): Unit = {
    props.onDestroy(props.item.id)
  }

  override def componentDidUpdate(): Unit = {
    base.get.querySelector(".edit") match {
      case x: HTMLInputElement => x.focus()
      case _ =>
    }
  }

  def render(): VNode = {
    val itemState = (props.item.checked, state.editing) match {
      case (_, true) => "editing"
      case (true, _) => "completed"
      case _ => ""
    }

    'li("class" -> itemState,
      'div("class" -> "view",
        'input("class" -> "toggle",
          "type" -> "checkbox",
          "checked" -> props.item.checked,
          "onclick" -> toggle _
        ),
        'label("ondblclick" -> editStart _, props.item.title),
        'button("class" -> "destroy",
          "onclick" -> destroy _
        )
      ),
      'input("class" -> "edit",
        "value" -> props.item.title,
        "onkeydown" -> editingKeyDown _,
        "onblur" -> onBlur _
      )
    )
  }
}
