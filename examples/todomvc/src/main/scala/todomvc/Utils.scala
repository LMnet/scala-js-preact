package todomvc

import org.scalajs.dom.Event
import org.scalajs.dom.raw.HTMLInputElement

object Utils {

  object KeyCodes {
    val Enter = 13
    val Escape = 27
  }

  def extractInputTarget(event: Event): HTMLInputElement = {
    event.currentTarget match {
      case x: HTMLInputElement => x
      case x => throw new IllegalArgumentException(s"Waiting for HTMLInputElement, but got $x")
    }
  }

  def pluralize(word: String, count: Int): String = {
    if (count == 1) word else word + "s"
  }
}
