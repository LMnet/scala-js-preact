package preact.dsl.tags

import java.net.URI
import java.util.UUID

import org.scalatest.FreeSpec
import preact.Preact

import scala.scalajs.js
import scala.scalajs.js.RegExp

class TagsDslTest extends FreeSpec {

  import Preact.raw._
  val lodash = js.Dynamic.global.`_`

  "tags dsl" - {
    "tags" - {
      "should render in the corresponding VNode" - {
        "when apply" - {
          "receives empty argument list" in {
            val node = div()
            val expected = h("div", null)
            assert(isEqual(node, expected))
          }

          "receives another symbol" in {
            val node = div(p())
            val expected = h("div", null, h("p", null))
            assert(isEqual(node, expected))
          }

          "receives multiple symbols" in {
            val node = div(p(), b())
            val expected = h("div", null, h("p", null), h("b", null))
            assert(isEqual(node, expected))
          }

          "receives multiple symbols as Iterable" in {
            val node = div(Seq(p(), b()))
            val expected = h("div", null, h("p", null), h("b", null))
            assert(isEqual(node, expected))
          }

          "receives vnode" in {
            val vnode = h("p", null, null)
            val node = div(vnode)
            val expected = h("div", null, vnode)
            assert(isEqual(node, expected))
          }

          "receives multiple vnodes as Iterable" in {
            val vnodes = Seq(h("p", null), h("b", null))
            val node = div(vnodes)
            val expected = h("div", null, vnodes(0), vnodes(1))
            assert(isEqual(node, expected))
          }

          "receives string" in {
            val node = div("test")
            val expected = h("div", null, "test")
            assert(isEqual(node, expected))
          }

          "receives EmptyChild" in {
            val node = div(Entry.EmptyChild)
            val expected = h("div", null, null)
            assert(isEqual(node, expected))
          }
        }
      }
    }
    "attributes" - {
      "should be constructed" -{
        "with string" in {
          val attr = id := "test"
          val node = div(attr)
          val expected = h("div", js.Dictionary[js.Any](
            "id" -> "test"
          ))
          assert(isEqual(node, expected))
        }

        "when attribute extends CustomAttributeValue" - {
          "with method call" in {
            val attr = target._blank
            val node = a(attr)
            val expected = h("a", js.Dictionary[js.Any](
              "target" -> "_blank"
            ))
            assert(isEqual(node, expected))
          }
        }

        "when attribute extends EmptyAttribute" - {
          "with just attribute name" in {
            val attr = disabled
            val node = a(attr)
            val expected = h("a", js.Dictionary[js.Any](
              "disabled" -> "true"
            ))
            assert(isEqual(node, expected))
          }
        }

        "when attribute extends ListOfKeyLabels" - {
          "with Char set" in {
            val attr = accesskey := Set('a', 'b')
            val node = div(attr)
            val expected = h("div", js.Dictionary[js.Any](
              "accesskey" -> "a,b"
            ))
            assert(isEqual(node, expected))
          }
        }

        "when attribute extends SpaceSeparatedSet" - {
          "with strings set" in {
            val attr = `class` := Set("foo", "bar")
            val node = div(attr)
            val expected = h("div", js.Dictionary[js.Any](
              "class" -> "foo bar"
            ))
            assert(isEqual(node, expected))
          }
        }

        "when attribute extends CommaSeparatedStringsSet" - {
          "with strings set" in {
            val attr = accept := Set("foo", "bar")
            val node = div(attr)
            val expected = h("div", js.Dictionary[js.Any](
              "accept" -> "foo,bar"
            ))
            assert(isEqual(node, expected))
          }
        }

        "when attribute extends BooleanValue" - {
          "with boolean value" in {
            val attr = contenteditable := true
            val node = div(attr)
            val expected = h("div", js.Dictionary[js.Any](
              "contenteditable" -> "true"
            ))
            assert(isEqual(node, expected))
          }
        }

        "when attribute extends HtmlIdReference" - {
          "with HtmlId value" in {
            val id = HtmlId("test")
            val attr = `for` := id
            val node = div(attr)
            val expected = h("div", js.Dictionary[js.Any](
              "for" -> "test"
            ))
            assert(isEqual(node, expected))
          }
        }

        "when attribute extends IntValue" - {
          "with Integer value" in {
            val attr = size := 5
            val node = div(attr)
            val expected = h("div", js.Dictionary[js.Any](
              "size" -> 5
            ))
            assert(isEqual(node, expected))
          }
        }

        "when attribute extends Url" - {
          "with URL value" in {
            val attr = href := new URI("http://github.com")
            val node = a(attr)
            val expected = h("a", js.Dictionary[js.Any](
              "href" -> "http://github.com"
            ))
            assert(isEqual(node, expected))
          }
        }

        "when attribute extends IntList" - {
          "with Integer list value" in {
            val attr = coords := (1, 2, 3, 4, 5)
            val node = div(attr)
            val expected = h("div", js.Dictionary[js.Any](
              "coords" -> "1,2,3,4,5"
            ))
            assert(isEqual(node, expected))
          }
        }

        "when attribute extends RegexpValue" - {
          "with js regexp as value" in {
            val regexp = RegExp(".*")
            val attr = pattern := regexp
            val node = div(attr)
            val expected = h("div", js.Dictionary[js.Any](
              "pattern" -> ".*"
            ))
            assert(isEqual(node, expected))
          }

          "with scala regexp as value" in {
            val regexp = """.*""".r
            val attr = pattern := regexp
            val node = div(attr)
            val expected = h("div", js.Dictionary[js.Any](
              "pattern" -> ".*"
            ))
            assert(isEqual(node, expected))
          }
        }

        "when builded with CallbackAttributeBuilder" - {
          "and receive function" - {
            // Because of implicit conversions between js functions and scala functions,
            // it is difficult to compare node and expected objects.
            // Let's assume, that other tests will check object equality,
            // and all we need in function tests - is comparing function results.
            "with 0 arguments" in {
              val id = UUID.randomUUID()
              val fun = () => id
              val node = div(onclick := fun)
              val expected = h("div", js.Dictionary[js.Any](
                "onclick" -> fun
              ))

              assert(
                node.attributes.get("onclick").asInstanceOf[js.Function0[UUID]]() ==
                  expected.attributes.get("onclick").asInstanceOf[js.Function0[UUID]]()
              )
            }

            "with 1 argument" in {
              val id = UUID.randomUUID()
              val fun = (one: String) => id
              val node = div(onclick := fun)
              val expected = h("div", js.Dictionary[js.Any](
                "onclick" -> fun
              ))

              assert(
                node.attributes.get("onclick").asInstanceOf[js.Function1[String, UUID]]("test") ==
                  expected.attributes.get("onclick").asInstanceOf[js.Function1[String, UUID]]("test")
              )
            }

            "with 2 arguments" in {
              val id = UUID.randomUUID()
              val fun = (one: String, two: String) => id
              val node = div(onclick := fun)
              val expected = h("div", js.Dictionary[js.Any](
                "onclick" -> fun
              ))

              assert(
                node.attributes.get("onclick").asInstanceOf[js.Function2[String, String, UUID]]("test", "test") ==
                  expected.attributes.get("onclick").asInstanceOf[js.Function2[String, String, UUID]]("test", "test")
              )
            }
          }
        }
      }

      "should be rendered inside tag" - {
        "when there is only one attribute in the tag" in {
          val node = div(`class` := "test")
          val expected = h("div", js.Dictionary[js.Any](
            "class" -> "test"
          ))
          assert(isEqual(node, expected))
        }

        "when there is mone than one attributes in the tag" in {
          val node = a(href := "/", target := "_blank")
          val expected = h("a", js.Dictionary[js.Any](
            "href" -> "/",
            "target" -> "_blank"
          ))
          assert(isEqual(node, expected))
        }

        "when there is attribute and string in the tag" in {
          val node = a(href := "/", "test")
          val expected = h("a", js.Dictionary[js.Any](
            "href" -> "/"
          ), "test")
          assert(isEqual(node, expected))
        }

        "when there is attributes, tags and string in the tag" in {
          val node = a(href := "/", target := "_blank", b("bold"), "test", i("italic"))
          val expected = h("a", js.Dictionary[js.Any](
            "href" -> "/",
            "target" -> "_blank"
          ), h("b", null, "bold"), "test", h("i", null, "italic"))
          assert(isEqual(node, expected))
        }
      }
    }
  }

  private def isEqual(lhs: js.Any, rhs: js.Any): Boolean = {
    lodash.isEqual(lhs, rhs).asInstanceOf[Boolean]
  }
}

