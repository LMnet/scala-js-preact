package preact.macros

import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.scalatest.{AsyncFreeSpec, BeforeAndAfterEach}
import preact.Preact
import preact.Preact.VNode

import scala.scalajs.js
import scala.scalajs.runtime.UndefinedBehaviorError

object PreactComponentTest {

  @PreactComponent[Unit, Unit]
  class Simple {
    def render(): VNode = {
      Preact.raw.h("p", null, "test")
    }
  }

  @PreactComponent[Unit, Unit](withChildren = true)
  class WithChildren {
    def render(): VNode = {
      Preact.raw.h("div", null, children: _*)
    }
  }

  object WithProps {
    case class Props(name: String)
  }
  @PreactComponent[WithProps.Props, Unit]
  class WithProps {
    def render(): VNode = {
      Preact.raw.h("p", null, props.name)
    }
  }

  object WithPropsAndChildren {
    case class Props(key: String, value: String)
  }
  @PreactComponent[WithPropsAndChildren.Props, Unit](withChildren = true)
  class WithPropsAndChildren {
    def render(): VNode = {
      Preact.raw.h("div", js.Dictionary[js.Any](props.key -> props.value), children: _*)
    }
  }

  object WithState {
    case class State(name: String)
  }
  @PreactComponent[Unit, WithState.State]
  class WithState {
    import WithState._

    initialState(State("test"))

    def onClick(): Unit = {
      setState(State("clicked"))
    }

    def render(): VNode = {
      Preact.raw.h("p", js.Dictionary[js.Any]("onclick" -> onClick _), state.name)
    }
  }

  object WithoutInitialState {
    case class State(name: String)
  }
  @PreactComponent[Unit, WithoutInitialState.State]
  class WithoutInitialState {
    def render(): VNode = {
      Preact.raw.h("p", null, state.name)
    }
  }

  object DoubleInitialState {
    case class State(name: String)
  }
  @PreactComponent[Unit, DoubleInitialState.State]
  class DoubleInitialState {
    import DoubleInitialState._

    initialState(State("test1"))
    initialState(State("test2"))

    def render(): VNode = {
      Preact.raw.h("p", null, state.name)
    }
  }
}

class PreactComponentTest extends AsyncFreeSpec with BeforeAndAfterEach {

  import PreactComponentTest._
  override implicit val executionContext = scala.concurrent.ExecutionContext.Implicits.global

  override def beforeEach(): Unit = {
    super.beforeEach()
    dom.document.body.innerHTML = ""
  }

  "PreactComponent" - {
    "apply()" - {
      "should construct component" - {
        "without children" in {
          Preact.render(Simple(), dom.document.body)
          assert(dom.document.body.innerHTML == """<p>test</p>""")
        }
        "with children" in {
          Preact.render(WithChildren(), dom.document.body)
          assert(dom.document.body.innerHTML == """<div></div>""")
        }
      }
    }
    "apply(children)" - {
      "should construct component" - {
        "with children" in {
          Preact.render(WithChildren(Preact.raw.h("b", null, "test")), dom.document.body)
          assert(dom.document.body.innerHTML == """<div><b>test</b></div>""")
        }
      }
    }
    "apply(props)" - {
      "should construct component" - {
        "without children" in {
          Preact.render(WithProps(WithProps.Props("test")), dom.document.body)
          assert(dom.document.body.innerHTML == """<p>test</p>""")
        }
        "with children" in {
          Preact.render(WithPropsAndChildren(WithPropsAndChildren.Props("testKey", "testValue")), dom.document.body)
          assert(dom.document.body.innerHTML == """<div testkey="testValue"></div>""")
        }
      }
    }
    "apply(props, children)" - {
      "should construct component" - {
        "with children" in {
          Preact.render(WithPropsAndChildren(
            WithPropsAndChildren.Props("testKey", "testValue"),
            Preact.raw.h("b", null, "test")
          ), dom.document.body)
          assert(dom.document.body.innerHTML == """<div testkey="testValue"><b>test</b></div>""")
        }
      }
    }
  }

  "Component" - {
    "state" - {
      "should return correct state value" in {
        Preact.render(WithState(), dom.document.body)
        assert(dom.document.body.innerHTML == """<p>test</p>""")
      }

      "should throw runtime UndefinedBehaviorError without initial value" in {
        assertThrows[UndefinedBehaviorError] {
          Preact.render(WithoutInitialState(), dom.document.body)
        }
        assert(dom.document.body.innerHTML == "")
      }
    }

    "initialState" - {
      "should throw runtime IllegalAccessException on multiple call" in {
        assertThrows[IllegalAccessException] {
          Preact.render(DoubleInitialState(), dom.document.body)
        }
        assert(dom.document.body.innerHTML == "")
      }
    }

    "setState" - {
      "should update component's state" in {
        Preact.render(WithState(), dom.document.body)
        assert(dom.document.body.innerHTML == """<p>test</p>""")

        dom.document.body.firstChild.asInstanceOf[HTMLElement].click()

        nextTick {
          assert(dom.document.body.innerHTML == """<p>clicked</p>""")
        }
      }
    }
  }

  //TODO: key???, lifecycle methods,
}
