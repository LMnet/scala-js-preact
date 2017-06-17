package todomvc

import org.scalajs.dom
import org.scalajs.dom.{Event, HashChangeEvent, KeyboardEvent}
import preact.Preact.VNode
import preact.macros.PreactComponent
import todomvc.Model.{Filter, Item, ItemId}

object TodoMvc {

  case class Props(initialTodos: Seq[Item], persist: Seq[Item] => Unit)

  case class State(todos: Seq[Item], filter: Filter, newTodo: String) {

    lazy val partitioned: (Seq[Item], Seq[Item]) = todos.partition(_.checked)

    lazy val completed: Seq[Item] = partitioned._1

    lazy val uncompleted: Seq[Item] = partitioned._2

    lazy val left: Int = uncompleted.size

    lazy val filteredTodos: Seq[Item] = {
      val predicate: Item => Boolean = filter match {
        case Filter.All =>
          _ => true

        case Filter.Active =>
          item => !item.checked

        case Filter.Completed =>
          item => item.checked
      }
      todos.filter(predicate)
    }

    def updateTodos(newTodos: Seq[Item])(implicit persist: Seq[Item] => Unit): State = {
      if (newTodos != todos) {
        persist(newTodos)
        copy(todos = newTodos)
      } else {
        this
      }
    }
  }
}

@PreactComponent[TodoMvc.Props, TodoMvc.State]
class TodoMvc(initialProps: TodoMvc.Props) {

  import TodoMvc._
  import preact.dsl.symbol._

  def todosFilter(): Filter = {
    dom.window.location.hash.split("/").toList match {
      case _ :: filter :: _ => filter match {
        case Filter.Active.path => Filter.Active
        case Filter.Completed.path => Filter.Completed
        case _ => Filter.All
      }
      case _ => Filter.All
    }
  }

  implicit private val persist = initialProps.persist

  initialState(State(
    todos = initialProps.initialTodos,
    newTodo = "",
    filter = todosFilter()
  ))

  dom.window.addEventListener("hashchange", { _: HashChangeEvent => handleRoute() })

  def handleRoute(): Unit = {
    setState(state.copy(filter = todosFilter()))
  }

  def onItemToggle(itemId: ItemId): Unit = {
    val newItems = state.todos.map { item =>
      if (item.id == itemId) {
        item.toggleChecked
      } else {
        item
      }
    }
    setState(state.updateTodos(newItems))
  }

  def updateItem(itemId: ItemId, newText: String): Unit = {
    val newItems = state.todos.map { item =>
      if (item.id == itemId) {
        item.copy(title = newText)
      } else {
        item
      }
    }
    setState(state.updateTodos(newItems))
  }

  def onItemDelete(itemId: ItemId): Unit = {
    val newItems = state.todos.filterNot(_.id == itemId)
    setState(state.updateTodos(newItems))
  }

  def onNewTodoInput(event: Event): Unit = {
    val target = Utils.extractInputTarget(event)
    event.preventDefault()
    setState(state.copy(newTodo = target.value))
  }

  def onNewTodoKeyDown(event: KeyboardEvent): Unit = {
    if (event.keyCode == Utils.KeyCodes.Enter) {
      val text = Utils.extractInputTarget(event).value.trim

      if (text.nonEmpty) {
        event.preventDefault()

        val newItem = Model.Item(title = text)
        val newState = state
          .updateTodos(state.todos :+ newItem)
          .copy(newTodo = "")
        setState(newState)
      }
    }
  }

  def clearCompleted(): Unit = {
    val newItems = state.todos.filterNot(_.checked)
    setState(state.updateTodos(newItems))
  }

  def toggleAll(event: Event): Unit = {
    val checked = Utils.extractInputTarget(event).checked
    val newItems = state.todos.map { item =>
      item.copy(checked = checked)
    }
    setState(state.updateTodos(newItems))
  }

  def render(): VNode = {
    'section("class" -> "todoapp",
      'header("class" -> "header",
        'h1("todos"),
        'input("class" -> "new-todo",
          "autofocus" -> "true",
          "placeholder" -> "What needs to be done",
          "value" -> state.newTodo,
          "oninput" -> onNewTodoInput _,
          "onkeydown" -> onNewTodoKeyDown _
        )
      ),
      if (state.todos.nonEmpty) {
        Entry.Children(Seq(
          'section("class" -> "main",
            'input("class" -> "toggle-all",
              "type" -> "checkbox",
              "onchange" -> toggleAll _,
              "checked" -> state.uncompleted.isEmpty
            ),
            'label("for" -> "toggle-all", "Mark all as complete"),
            'ul("class" -> "todo-list",
              state.filteredTodos.map { item =>
                TodoItem(TodoItem.Props(item, onItemToggle, updateItem, onItemDelete))
              }
            )
          ),
          'footer("class" -> "footer",
            'span("class" -> "todo-count",
              'strong(state.left.toString), s" ${Utils.pluralize("item", state.left)} left"
            ),
            'ul("class" -> "filters",
              FilterButton(Filter.All, state.filter),
              FilterButton(Filter.Active, state.filter),
              FilterButton(Filter.Completed, state.filter)
            ),
            if (state.completed.nonEmpty) {
              'button("class" -> "clear-completed",
                "onclick" -> clearCompleted _,
                "Clear completed"
              )
            } else {
              Entry.EmptyChild
            }
          )
        ))
      } else {
        Entry.EmptyChild
      }
    )
  }
}
