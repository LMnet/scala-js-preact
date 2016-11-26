package todomvc

import preact.Preact
import preact.Preact.VNode
import todomvc.Model.Filter

import scala.scalajs.js.annotation.ScalaJSDefined

object FilterButton extends Preact.Factory.WithProps {

  case class Props(filter: Filter, currentFilter: Filter)

  @ScalaJSDefined
  class Component extends Preact.Component[Props, State] {

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

  def apply(filter: Filter, currentFilter: Filter): VNode = {
    apply(Props(filter, currentFilter))
  }

}
