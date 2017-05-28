package preact.macros

import org.scalatest.FreeSpec

import scala.meta._

class PreactComponentMacroTest extends FreeSpec {

  "PreactComponent macro annotation should correctly expand" - {
    "when there is no companion object" - {
      "and component's constructor is empty" - {
        "and component has empty constructor" - {
          "and State type is Unit" - {
            "and component doesn't render children" in {
              val actual = PreactComponentImpl.expand(
                cls = q"""
                  class Test {
                    def render() = Preact.raw.h("div", null, null)
                  }
                """,
                companionOpt = None,
                PreactComponentImpl.Params(
                  stateType = t"Unit",
                  withChildrenValue = false
                )
              )

              val expected = q"""
                @_root_.scala.scalajs.js.annotation.ScalaJSDefined
                class Test extends _root_.preact.Preact.Component[_root_.scala.Unit, Unit] {
                  def render() = Preact.raw.h("div",null, null)
                }
                object Test {
                  def apply()(
                    implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                  ): _root_.preact.Preact.VNode = {
                    _root_.preact.Preact.raw.h(ct.constructor, null, null)
                  }
                }
              """
              assertStructurallyEqual(actual, expected)
            }
            "and component render children" in {
              val actual = PreactComponentImpl.expand(
                cls = q"""
                  class Test {
                    def render() = Preact.raw.h("div", null, children)
                  }
                """,
                companionOpt = None,
                PreactComponentImpl.Params(
                  stateType = t"Unit",
                  withChildrenValue = true
                )
              )

              val expected = q"""
                @_root_.scala.scalajs.js.annotation.ScalaJSDefined
                class Test extends _root_.preact.Preact.Component[_root_.scala.Unit, Unit] {
                  def render() = Preact.raw.h("div",null, children)
                }
                object Test {
                  def apply()(
                    implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                  ): _root_.preact.Preact.VNode = {
                    _root_.preact.Preact.raw.h(ct.constructor, null, null)
                  }
                  def apply(children: _root_.preact.Preact.Child*)(
                    implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                  ): _root_.preact.Preact.VNode = {
                    _root_.preact.Preact.raw.h(ct.constructor, null, children: _*)
                  }
                }
              """
              assertStructurallyEqual(actual, expected)
            }
          }
          "and State is non-Unit type" in {
            val actual = PreactComponentImpl.expand(
              cls = q"""
                class Test {
                  def render() = Preact.raw.h("div", null, null)
                }
              """,
              companionOpt = None,
              PreactComponentImpl.Params(
                stateType = t"State",
                withChildrenValue = false
              )
            )

            val expected = q"""
              @_root_.scala.scalajs.js.annotation.ScalaJSDefined
              class Test extends _root_.preact.Preact.Component[_root_.scala.Unit, State] {
                def render() = Preact.raw.h("div",null, null)
              }
              object Test {
                def apply()(
                  implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                ): _root_.preact.Preact.VNode = {
                  _root_.preact.Preact.raw.h(ct.constructor, null, null)
                }
              }
            """
            assertStructurallyEqual(actual, expected)
          }
        }
        "and component has Props in the constructor" - {
          "and State type is Unit" - {
            "and component doesn't render children" in {
              val actual = PreactComponentImpl.expand(
                cls = q"""
                  class Test(props: Props) {
                    def render() = Preact.raw.h("div", null, null)
                  }
                """,
                companionOpt = None,
                PreactComponentImpl.Params(
                  stateType = t"Unit",
                  withChildrenValue = false
                )
              )

              val expected = q"""
                @_root_.scala.scalajs.js.annotation.ScalaJSDefined
                class Test(props: Props) extends _root_.preact.Preact.Component[Props, Unit] {
                  def render() = Preact.raw.h("div",null, null)
                }
                object Test {
                  def apply(props: Props)(
                    implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                  ): _root_.preact.Preact.VNode = {
                    _root_.preact.Preact.raw.h(
                      ct.constructor, props.asInstanceOf[_root_.preact.Preact.raw.Attributes], null
                    )
                  }
                }
              """
              assertStructurallyEqual(actual, expected)
            }
            "and component render children" in {
              val actual = PreactComponentImpl.expand(
                cls = q"""
                  class Test(props: Props) {
                    def render() = Preact.raw.h("div", null, children)
                  }
                """,
                companionOpt = None,
                PreactComponentImpl.Params(
                  stateType = t"Unit",
                  withChildrenValue = true
                )
              )

              val expected = q"""
                @_root_.scala.scalajs.js.annotation.ScalaJSDefined
                class Test(props: Props) extends _root_.preact.Preact.Component[Props, Unit] {
                  def render() = Preact.raw.h("div",null, children)
                }
                object Test {
                  def apply(props: Props)(
                    implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                  ): _root_.preact.Preact.VNode = {
                    _root_.preact.Preact.raw.h(
                      ct.constructor, props.asInstanceOf[_root_.preact.Preact.raw.Attributes], null
                    )
                  }
                  def apply(props: Props, children: _root_.preact.Preact.Child*)(
                    implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                  ): _root_.preact.Preact.VNode = {
                    _root_.preact.Preact.raw.h(
                      ct.constructor, props.asInstanceOf[_root_.preact.Preact.raw.Attributes], children: _*
                    )
                  }
                }
              """
              assertStructurallyEqual(actual, expected)
            }
          }
          "and State is some non-Unit type" in {
            val actual = PreactComponentImpl.expand(
              cls = q"""
                class Test(props: Props) {
                  def render() = Preact.raw.h("div", null, null)
                }
              """,
              companionOpt = None,
              PreactComponentImpl.Params(
                stateType = t"State",
                withChildrenValue = false
              )
            )

            val expected = q"""
              @_root_.scala.scalajs.js.annotation.ScalaJSDefined
              class Test(props: Props) extends _root_.preact.Preact.Component[Props, State] {
                def render() = Preact.raw.h("div",null, null)
              }
              object Test {
                def apply(props: Props)(
                  implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                ): _root_.preact.Preact.VNode = {
                  _root_.preact.Preact.raw.h(
                    ct.constructor, props.asInstanceOf[_root_.preact.Preact.raw.Attributes], null
                  )
                }
              }
            """
            assertStructurallyEqual(actual, expected)
          }
        }
      }
    }
    "when component has companion object" - {
      "and companion contains companion's Props definition" - {
        "and component doesn't render children" in {
          val actual = PreactComponentImpl.expand(
            cls = q"""
              class Test(props: Props) {
                def render() = Preact.raw.h("div", null, null)
              }
            """,
            companionOpt = Some(q"""
              object Test {
                case class Props(a: String, b: Int)
              }
            """),
            PreactComponentImpl.Params(
              stateType = t"Unit",
              withChildrenValue = false
            )
          )

          val expected = q"""
            @_root_.scala.scalajs.js.annotation.ScalaJSDefined
            class Test(props: Props) extends _root_.preact.Preact.Component[Props, Unit] {
              def render() = Preact.raw.h("div",null, null)
            }
            object Test {
              case class Props(a: String, b: Int)

              def apply(props: Props)(
                implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
              ): _root_.preact.Preact.VNode = {
                _root_.preact.Preact.raw.h(
                  ct.constructor, props.asInstanceOf[_root_.preact.Preact.raw.Attributes], null
                )
              }
              def apply(a: String, b: Int)(
                implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
              ): _root_.preact.Preact.VNode = {
                apply(Props(a, b))
              }
            }
          """
          assertStructurallyEqual(actual, expected)
        }
        "and component render children" in {
          val actual = PreactComponentImpl.expand(
            cls = q"""
              class Test(props: Props) {
                def render() = Preact.raw.h("div", null, children)
              }
            """,
            companionOpt = Some(q"""
              object Test {
                case class Props(a: String, b: Int)
              }
            """),
            PreactComponentImpl.Params(
              stateType = t"Unit",
              withChildrenValue = true
            )
          )

          val expected = q"""
            @_root_.scala.scalajs.js.annotation.ScalaJSDefined
            class Test(props: Props) extends _root_.preact.Preact.Component[Props, Unit] {
              def render() = Preact.raw.h("div",null, children)
            }
            object Test {
              case class Props(a: String, b: Int)

              def apply(props: Props)(
                implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
              ): _root_.preact.Preact.VNode = {
                _root_.preact.Preact.raw.h(
                  ct.constructor, props.asInstanceOf[_root_.preact.Preact.raw.Attributes], null
                )
              }
              def apply(props: Props, children: _root_.preact.Preact.Child*)(
                implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
              ): _root_.preact.Preact.VNode = {
                _root_.preact.Preact.raw.h(
                  ct.constructor, props.asInstanceOf[_root_.preact.Preact.raw.Attributes], children: _*
                )
              }
              def apply(a: String, b: Int)(
                implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
              ): _root_.preact.Preact.VNode = {
                apply(Props(a, b))
              }
              def apply(a: String, b: Int, children: _root_.preact.Preact.Child*)(
                implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
              ): _root_.preact.Preact.VNode = {
                apply(Props(a, b), children: _*)
              }
            }
          """
          assertStructurallyEqual(actual, expected)
        }
      }
    }
  }

  "PreactComponentImpl.extractAnnotationParams" - {
    "should extract parameters from annotation definition" - {
      "when State type is defined as Unit" - {
        "and no parameters is passed to the constructor" in {
          val actual = PreactComponentImpl.extractAnnotationParams(q"new PreactComponent[Unit]()")
          val expected = PreactComponentImpl.Params(t"Unit", withChildrenValue = false)
          assertStructurallyEqual(actual.stateType, expected.stateType)
          assert(actual.withChildrenValue == expected.withChildrenValue)
        }
        "and withChildren is passed to the constructor" - {
          "with argument name" in {
            val actual = PreactComponentImpl.extractAnnotationParams(q"new PreactComponent[Unit](withChildren = true)")
            val expected = PreactComponentImpl.Params(t"Unit", withChildrenValue = true)
            assertStructurallyEqual(actual.stateType, expected.stateType)
            assert(actual.withChildrenValue == expected.withChildrenValue)
          }
          "without argument name" in {
            val actual = PreactComponentImpl.extractAnnotationParams(q"new PreactComponent[Unit](true)")
            val expected = PreactComponentImpl.Params(t"Unit", withChildrenValue = true)
            assertStructurallyEqual(actual.stateType, expected.stateType)
            assert(actual.withChildrenValue == expected.withChildrenValue)
          }
        }
      }
      "when State type is defined as Props" in {
        val actual = PreactComponentImpl.extractAnnotationParams(q"new PreactComponent[Props]()")
        val expected = PreactComponentImpl.Params(t"Props", withChildrenValue = false)
        assertStructurallyEqual(actual.stateType, expected.stateType)
        assert(actual.withChildrenValue == expected.withChildrenValue)
      }
    }
  }
}
