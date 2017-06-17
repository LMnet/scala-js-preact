package todomvc

import preact.Preact.VNode
import preact.macros.PreactComponent
import todomvc.Model.Filter

object FilterButton {
  case class Props(filter: Filter, currentFilter: Filter)
}

import todomvc.FilterButton._

@PreactComponent[Props, Unit]
class FilterButton {

  import preact.dsl.symbol._

  def render(): VNode = {
    'li(
      'a("href" -> s"#/${props.filter.path}",
        if (props.currentFilter == props.filter) {
          "class" -> "selected"
        } else {
          Entry.EmptyAttribute
        },
        props.filter.label
      )
    )
  }
}
