package preact.dsl.symbol

import java.util.UUID

import org.scalatest.FreeSpec
import preact.Preact

import scala.scalajs.js

class SymbolDslTest extends FreeSpec {

  import Preact.raw._
  val lodash = js.Dynamic.global.`_`

  "symbol" - {
    "should render in the corresponding VNode" - {
      "when apply" - {
        "receives empty argument list" in {
          val node = 'div()
          val expected = h("div", null, null)
          assert(isEqual(node, expected))
        }

        "receives another symbol" in {
          val node = 'div('p())
          val expected = h("div", null, h("p", null, null))
          assert(isEqual(node, expected))
        }

        "receives multiple symbols" in {
          val node = 'div('p(), 'b())
          val expected = h("div", null, h("p", null, null), h("b", null, null))
          assert(isEqual(node, expected))
        }

        "receives multiple symbols as Iterable" in {
          val node = 'div(Seq('p(), 'b()))
          val expected = h("div", null, h("p", null, null), h("b", null, null))
          assert(isEqual(node, expected))
        }

        "receives vnode" in {
          val vnode = h("p", null, null)
          val node = 'div(vnode)
          val expected = h("div", null, vnode)
          assert(isEqual(node, expected))
        }

        "receives multiple vnodes as Iterable" in {
          val vnodes = Seq(h("p", null, null), h("b", null, null))
          val node = 'div(vnodes)
          val expected = h("div", null, vnodes(0), vnodes(1))
          assert(isEqual(node, expected))
        }

        "receives string" in {
          val node = 'div("test")
          val expected = h("div", null, "test")
          assert(isEqual(node, expected))
        }

        "receives tuple" - {
          "with js.Any value" in {
            val now = js.Date.now()
            val node = 'div("foo" -> now)
            val expected = h("div", js.Dictionary[js.Any](
              "foo" -> now
            ))
            assert(isEqual(node, expected))
          }

          "with string value" in {
            val node = 'div("foo" -> "bar")
            val expected = h("div", js.Dictionary[js.Any](
              "foo" -> "bar"
            ))
            assert(isEqual(node, expected))
          }

          "with conversable to js.Any value" in {
            case class A(value: String)
            implicit def aToJs(a: A): js.Any = {
              a.value
            }

            val a = A("test")
            val node = 'div("foo" -> a)
            val expected = h("div", js.Dictionary[js.Any](
              "foo" -> "test"
            ))
            assert(isEqual(node, expected))
          }
        }

        "receives function" - {
          // Because of implicit conversions between js functions and scala functions,
          // it is difficult to compare node and expected objects.
          // Let's assume, that other tests will check object equality,
          // and all we need in function tests - is comparing function results.
          "with 0 arguments" in {
            val id = UUID.randomUUID()
            val fun = () => id
            val node = 'div("foo" -> fun)
            val expected = h("div", js.Dictionary[js.Any](
              "foo" -> fun
            ), null)

            assert(
              node.attributes.get("foo").asInstanceOf[js.Function0[UUID]]() ==
                expected.attributes.get("foo").asInstanceOf[js.Function0[UUID]]()
            )
          }

          "with 1 argument" in {
            val id = UUID.randomUUID()
            val fun = (one: String) => id
            val node = 'div("foo" -> fun)
            val expected = h("div", js.Dictionary[js.Any](
              "foo" -> fun
            ), null)

            assert(
              node.attributes.get("foo").asInstanceOf[js.Function1[String, UUID]]("test") ==
                expected.attributes.get("foo").asInstanceOf[js.Function1[String, UUID]]("test")
            )
          }

          "with 2 arguments" in {
            val id = UUID.randomUUID()
            val fun = (one: String, two: String) => id
            val node = 'div("foo" -> fun)
            val expected = h("div", js.Dictionary[js.Any](
              "foo" -> fun
            ), null)

            assert(
              node.attributes.get("foo").asInstanceOf[js.Function2[String, String, UUID]]("test", "test") ==
                expected.attributes.get("foo").asInstanceOf[js.Function2[String, String, UUID]]("test", "test")
            )
          }
        }

      }
    }
  }

  private def isEqual(lhs: js.Any, rhs: js.Any): Boolean = {
    lodash.isEqual(lhs, rhs).asInstanceOf[Boolean]
  }
}
