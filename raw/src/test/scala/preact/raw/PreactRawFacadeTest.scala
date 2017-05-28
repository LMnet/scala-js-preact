package preact.raw

import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.scalatest.{AsyncFreeSpec, BeforeAndAfterEach}
import preact.raw.RawPreact._

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined

object PreactRawFacadeTest {

  var dynVar: js.Dynamic = null

  @ScalaJSDefined
  class SimpleComponent extends Component {
    def render(): VNode = {
      h("div", null, "test")
    }
  }

  @ScalaJSDefined
  class PropsComponent extends Component {
    def render(): VNode = {
      dynVar = jsProps
      h("div", jsProps.asInstanceOf[Attributes], "test")
    }
  }

  @ScalaJSDefined
  class ChildrenComponent extends Component {
    def render(): VNode = {
      val children = jsProps.children.asInstanceOf[js.Array[Child]]
      h("div", null, children: _*)
    }
  }

  @ScalaJSDefined
  class PropsChildrenComponent extends Component {
    def render(): VNode = {
      val children = jsProps.children.asInstanceOf[js.Array[Child]]
      h("div", jsProps.asInstanceOf[Attributes], children: _*)
    }
  }

  @ScalaJSDefined
  class StateComponent extends Component {

    jsState = js.Dynamic.literal(foo = 1, bar = "lol")

    def render(): VNode = {
      dynVar = jsState
      h("div", jsState.asInstanceOf[Attributes], null)
    }
  }

  @ScalaJSDefined
  class SetStateComponent extends Component {

    jsState = js.Dynamic.literal(foo = 1)

    def render(): VNode = {
      dynVar = jsState
      val callback = () => {
        jsSetState(js.Dynamic.literal(foo = 2))
      }
      val attributes = js.Dictionary.empty[js.Any]
      attributes.update("foo", jsState.foo)
      attributes.update("onclick", callback)
      attributes.update("id", "set-state-component")
      h("div", attributes, null)
    }
  }

  @ScalaJSDefined
  class EventComponent extends Component {
    def render(): VNode = {
      dynVar = jsBase.asInstanceOf[js.Dynamic]
      val callback = () => {
        jsSetState(js.Dynamic.literal())
      }
      h("div", js.Dictionary[js.Any]("onclick" -> callback, "id" -> "event-component"), "test")
    }
  }
}

class PreactRawFacadeTest extends AsyncFreeSpec with BeforeAndAfterEach {

  import PreactRawFacadeTest._
  override implicit val executionContext = scala.concurrent.ExecutionContext.Implicits.global

  val lodash = js.Dynamic.global.`_`

  override def beforeEach(): Unit = {
    super.beforeEach()
    dynVar = null
    dom.document.body.innerHTML = ""
  }

  "render" - {
    "should render nodes in the body" in {
      render(h("div", null, "Test node"), dom.document.body)
      assert(dom.document.body.innerHTML == "<div>Test node</div>")
    }

    "should render nodes in the specific container" in {
      dom.document.body.innerHTML = """<div id="container"></div>"""
      render(h("div", null, "Test node"), dom.document.getElementById("container"))
      assert(dom.document.body.innerHTML == """<div id="container"><div>Test node</div></div>""")
    }

    "should merge nodes if mergeWith is set" in {
      dom.document.body.innerHTML = """<div foo="bar"></div>"""
      render(h("div", null, "Test node"), dom.document.body)
      assert(dom.document.body.innerHTML == """<div foo="bar"></div><div>Test node</div>""")
    }
  }

  "h" - {
    "should render single DOM node" - {
      "without attributes and children" in {
        render(h("p", null, null), dom.document.body)
        assert(dom.document.body.innerHTML == "<p></p>")
      }

      "with attributes" in {
        render(h("p", js.Dictionary[js.Any]("foo" -> "bar", "baz" -> "test"), null), dom.document.body)
        assert(dom.document.body.innerHTML == """<p foo="bar" baz="test"></p>""")
      }

      "with VNode child" in {
        render(h("div", null, h("p", null, null)), dom.document.body)
        assert(dom.document.body.innerHTML == """<div><p></p></div>""")
      }

      "with VNode children" in {
        render(h("div", null, h("p", null, null), h("b", null, null)), dom.document.body)
        assert(dom.document.body.innerHTML == """<div><p></p><b></b></div>""")
      }

      "with string child" in {
        render(h("div", null, "test"), dom.document.body)
        assert(dom.document.body.innerHTML == """<div>test</div>""")
      }

      "with string children" in {
        render(h("div", null, "test1", "test2"), dom.document.body)
        assert(dom.document.body.innerHTML == """<div>test1test2</div>""")
      }

      "with combined string and VNode children" in {
        render(h("div", null, h("p", null, null), "test1", h("b", null, null), "test2"), dom.document.body)
        assert(dom.document.body.innerHTML == """<div><p></p>test1<b></b>test2</div>""")
      }
    }

    "should render stateless function component" - {
      "from js.Function0 with explicit type declaration" in {
        val component: js.Function0[VNode] = () => {
          h("div", null, "test")
        }

        render(h(component, null, null), dom.document.body)
        assert(dom.document.body.innerHTML == """<div>test</div>""")
      }

      "from function without explicit type declaration" in {
        val component = () => {
          h("div", null, "test")
        }

        render(h(component, null, null), dom.document.body)
        assert(dom.document.body.innerHTML == """<div>test</div>""")
      }
    }

    "should render function component with props" - {
      "when only props is passed to component" in {
        val component = (props: js.Dynamic) => {
          h("div", js.Dictionary[js.Any]("foo" -> props.num), null)
        }

        render(
          h(component, js.Dictionary[js.Any]("num" -> 1), null),
          dom.document.body
        )
        assert(dom.document.body.innerHTML == """<div foo="1"></div>""")
      }

      "when only children is passed to component" in {
        val component = (props: js.Dynamic) => {
          val children = props.children.asInstanceOf[js.Array[Child]]
          h("div", null, children: _*)
        }

        render(
          h(component, null, h("p", null, "test")),
          dom.document.body
        )
        assert(dom.document.body.innerHTML == """<div><p>test</p></div>""")
      }

      "when props and children is passed to component" in {
        val component = (props: js.Dynamic) => {
          val children = props.children.asInstanceOf[js.Array[Child]]
          val attributes = js.Dictionary[js.Any]("foo" -> props.num)
          h("div", attributes, children: _*)
        }

        render(
          h(component, js.Dictionary[js.Any]("num" -> 1), h("p", null, "test")),
          dom.document.body
        )
        assert(dom.document.body.innerHTML == """<div foo="1"><p>test</p></div>""")
      }
    }

    "should render class component" - {
      "when component doesn't have props and children" in {
        render(
          h(js.constructorOf[SimpleComponent], null, null),
          dom.document.body
        )
        assert(dom.document.body.innerHTML == """<div>test</div>""")
      }

      "when component have props" in {
        render(
          h(js.constructorOf[PropsComponent], js.Dictionary[js.Any]("foo" -> "bar"), null),
          dom.document.body
        )
        assert(dom.document.body.innerHTML == """<div foo="bar">test</div>""")
      }

      "when component have children" in {
        render(
          h(js.constructorOf[ChildrenComponent], null, h("p", null, "test")),
          dom.document.body
        )
        assert(dom.document.body.innerHTML == """<div><p>test</p></div>""")
      }

      "when component have props and children" in {
        val props = js.Dictionary[js.Any]("foo" -> "bar")
        render(
          h(js.constructorOf[PropsChildrenComponent], props, h("p", null, "test")),
          dom.document.body
        )
        assert(dom.document.body.innerHTML == """<div foo="bar"><p>test</p></div>""")
      }
    }
  }

  "Component" - {
    "jsProps should give access to the props" in {
      val props = js.Dynamic.literal(foo = 3, bar = "lol")
      val attrs = props.asInstanceOf[Attributes]
      render(
        h(js.constructorOf[PropsComponent], attrs, null),
        dom.document.body
      )

      val objProps = props.asInstanceOf[js.Object]
      val objDynVar = dynVar.asInstanceOf[js.Object]
      assert(lodash.isEqual(objProps, objDynVar).asInstanceOf[Boolean])
    }

    "jsState should give access to the state" in {
      val expectedState = js.Dynamic.literal(foo = 1, bar = "lol")
      render(
        h(js.constructorOf[StateComponent], null, null),
        dom.document.body
      )

      assert(
        dom.document.body.innerHTML == """<div foo="1" bar="lol"></div>""" &&
        lodash.isEqual(expectedState, dynVar).asInstanceOf[Boolean]
      )
    }

    "jsSetState should be able to update state" in {
      val beforeUpdateExpected = js.Dynamic.literal(foo = 1)
      val afterUpdateExpected = js.Dynamic.literal(foo = 2)
      render(
        h(js.constructorOf[SetStateComponent], null, null),
        dom.document.body
      )

      assert(lodash.isEqual(beforeUpdateExpected, dynVar).asInstanceOf[Boolean])

      val componentElement = dom.document.getElementById("set-state-component").asInstanceOf[HTMLElement]
      componentElement.click()

      preact.nextTick {
        assert(lodash.isEqual(afterUpdateExpected, dynVar).asInstanceOf[Boolean])
      }
    }

    "base should returns attached node" in {
      dom.document.body.innerHTML = """<div id="container"></div>"""
      val container = dom.document.getElementById("container")
      render(
        h(js.constructorOf[EventComponent], null, null),
        container
      )

      assert(js.isUndefined(dynVar))

      val componentElement = dom.document.getElementById("event-component").asInstanceOf[HTMLElement]
      componentElement.click()

      preact.nextTick {
        assert(dynVar == componentElement)
      }
    }
  }

  // TODO: rerender
}
