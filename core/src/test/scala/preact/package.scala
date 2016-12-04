import scala.concurrent.duration.{FiniteDuration, _}
import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.util.Try

package object preact {

  def delay[T](timeout: FiniteDuration)(body: => T): Future[T] = {
    val promise = Promise[T]()
    js.timers.setTimeout(timeout) {
      promise.complete(Try(body))
    }
    promise.future
  }

  def nextTick[T](body: => T): Future[T] = {
    delay(0 millis)(body)
  }
}
