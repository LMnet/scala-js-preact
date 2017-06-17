package preact.macros

import org.scalatest.{Assertion, FreeSpec}

import scala.meta._
import scala.meta.internal.inline.AbortException

class PreactComponentMacroTest extends FreeSpec {

  import PreactComponentImpl._

  val simpleUnitType = SimpleType(Left(t"Unit"))

  "PreactComponent macro annotation" - {
    "should correctly expand" - {
      "when there is no companion object" - {
        "and Props type is Unit" - {
          "and State type is Unit" - {
            "and component doesn't render children" in {
              val actual = expand(
                cls = q"""
                  class Test {
                    def render() = Preact.raw.h("div", null, null)
                  }
                """,
                companionOpt = None,
                Params(
                  propsType = simpleUnitType,
                  stateType = simpleUnitType,
                  withChildrenValue = false
                )
              )

              val expected = q"""
                @_root_.scala.scalajs.js.annotation.ScalaJSDefined
                class Test extends _root_.preact.Preact.Component[Unit, Unit] {
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
              val actual = expand(
                cls =
                  q"""
                  class Test {
                    def render() = Preact.raw.h("div", null, children)
                  }
                """,
                companionOpt = None,
                Params(
                  propsType = simpleUnitType,
                  stateType = simpleUnitType,
                  withChildrenValue = true
                )
              )

              val expected = q"""
                @_root_.scala.scalajs.js.annotation.ScalaJSDefined
                class Test extends _root_.preact.Preact.Component[Unit, Unit] {
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
          "and State is a non-Unit type" in {
            val actual = expand(
              cls = q"""
                class Test {
                  def render() = Preact.raw.h("div", null, null)
                }
              """,
              companionOpt = None,
              Params(
                propsType = simpleUnitType,
                stateType = SimpleType(Left(t"State")),
                withChildrenValue = false
              )
            )

            val expected = q"""
              @_root_.scala.scalajs.js.annotation.ScalaJSDefined
              class Test extends _root_.preact.Preact.Component[Unit, State] {
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
        "and Props is a non-Unit type" - {
            "and State type is Unit" - {
              "and component doesn't render children" in {
                val actual = expand(
                  cls = q"""
                    class Test {
                      def render() = Preact.raw.h("div", null, null)
                    }
                  """,
                  companionOpt = None,
                  Params(
                    propsType = SimpleType(Left(t"Props")),
                    stateType = simpleUnitType,
                    withChildrenValue = false
                  )
                )

                val expected = q"""
                  @_root_.scala.scalajs.js.annotation.ScalaJSDefined
                  class Test extends _root_.preact.Preact.Component[Props, Unit] {
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
                val actual = expand(
                  cls = q"""
                    class Test {
                      def render() = Preact.raw.h("div", null, children)
                    }
                  """,
                  companionOpt = None,
                  Params(
                    propsType = SimpleType(Left(t"Props")),
                    stateType = simpleUnitType,
                    withChildrenValue = true
                  )
                )

                val expected = q"""
                  @_root_.scala.scalajs.js.annotation.ScalaJSDefined
                  class Test extends _root_.preact.Preact.Component[Props, Unit] {
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
              val actual = expand(
                cls =q"""
                  class Test {
                    def render() = Preact.raw.h("div", null, null)
                  }
                """,
                companionOpt = None,
                Params(
                  propsType = SimpleType(Left(t"Props")),
                  stateType = SimpleType(Left(t"State")),
                  withChildrenValue = false
                )
              )

              val expected = q"""
                @_root_.scala.scalajs.js.annotation.ScalaJSDefined
                class Test extends _root_.preact.Preact.Component[Props, State] {
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
      "when component has companion object" - {
        "and companion contains companion's Props definition" - {
          "and component doesn't render children" in {
            val actual = expand(
              cls = q"""
                class Test {
                  def render() = Preact.raw.h("div", null, null)
                }
              """,
              companionOpt = Some(q"""
                object Test {
                  case class Props(a: String, b: Int)
                }
              """),
              Params(
                propsType = SimpleType(Left(t"Props")),
                stateType = simpleUnitType,
                withChildrenValue = false
              )
            )

            val expected = q"""
              @_root_.scala.scalajs.js.annotation.ScalaJSDefined
              class Test extends _root_.preact.Preact.Component[Props, Unit] {
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
            val actual = expand(
              cls = q"""
                class Test {
                  def render() = Preact.raw.h("div", null, children)
                }
              """,
              companionOpt = Some(q"""
                object Test {
                  case class Props(a: String, b: Int)
                }
              """),
              Params(
                propsType = SimpleType(Left(t"Props")),
                stateType = simpleUnitType,
                withChildrenValue = true
              )
            )

            val expected = q"""
              @_root_.scala.scalajs.js.annotation.ScalaJSDefined
              class Test extends _root_.preact.Preact.Component[Props, Unit] {
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
          "and Props is passed to annotation with companion name" in {
            val actual = expand(
              cls = q"""
                class Test {
                  def render() = Preact.raw.h("div", null, null)
                }
              """,
              companionOpt = Some(q"""
                object Test {
                  case class Props(a: String, b: Int)
                }
              """),
              Params(
                propsType = SimpleType(Right(t"Test.Props")),
                stateType = simpleUnitType,
                withChildrenValue = false
              )
            )

            val expected = q"""
              @_root_.scala.scalajs.js.annotation.ScalaJSDefined
              class Test extends _root_.preact.Preact.Component[Test.Props, Unit] {
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
        }
      }
      "when component's constructor contains single argument with non-'props' name" in {
        val actual = expand(
          cls = q"""
            class Test(initialProps: Props) {
              def render() = Preact.raw.h("div", null, null)
            }
          """,
          companionOpt = None,
          Params(
            propsType = SimpleType(Left(t"Props")),
            stateType = simpleUnitType,
            withChildrenValue = false
          )
        )

        val expected = q"""
          @_root_.scala.scalajs.js.annotation.ScalaJSDefined
          class Test(initialProps: Props) extends _root_.preact.Preact.Component[Props, Unit] {
            def render() = Preact.raw.h("div",null, null)
          }
          object Test {
            def apply(props: Props)(
              implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
            ): _root_.preact.Preact.VNode = {
              _root_.preact.Preact.raw.h(ct.constructor, props.asInstanceOf[_root_.preact.Preact.raw.Attributes], null)
            }
          }
        """
        assertStructurallyEqual(actual, expected)
      }
    }
    "should fail" - {
      "when constructor has argument with name 'props'" in {
        assertThrows[AbortException] {
          expand(
            cls = q"""
              class Test(props: Props) {
                def render() = Preact.raw.h("div", null, children)
              }
            """,
            companionOpt = None,
            Params(
              propsType = SimpleType(Left(t"Props")),
              stateType = simpleUnitType,
              withChildrenValue = false
            )
          )
        }
      }
    }
  }

  "PreactComponentImpl.extractAnnotationParams" - {
    "should extract parameters from annotation definition" - {
      "when State and Props types is defined as 'Unit'" - {
        "and no parameters is passed to the constructor" in {
          val actual = extractAnnotationParams(q"new PreactComponent[Unit, Unit]()")
          val expected = Params(simpleUnitType, simpleUnitType, withChildrenValue = false)
          assertParams(actual, expected)
        }
        "and withChildren is passed to the constructor" - {
          "with argument name" in {
            val actual = extractAnnotationParams(q"new PreactComponent[Unit, Unit](withChildren = true)")
            val expected = Params(simpleUnitType, simpleUnitType, withChildrenValue = true)
            assertParams(actual, expected)
          }
          "without argument name" in {
            val actual = extractAnnotationParams(q"new PreactComponent[Unit, Unit](true)")
            val expected = Params(simpleUnitType, simpleUnitType, withChildrenValue = true)
            assertParams(actual, expected)
          }
        }
      }
      "when State type is defined as" - {
        "'State'" in {
          val actual = extractAnnotationParams(q"new PreactComponent[Unit, State]()")
          val expected = Params(simpleUnitType, SimpleType(Left(t"State")), withChildrenValue = false)
          assertParams(actual, expected)
        }
        "'Foo.State'" in {
          val actual = extractAnnotationParams(q"new PreactComponent[Unit, Foo.State]()")
          val expected = Params(simpleUnitType, SimpleType(Right(t"Foo.State")), withChildrenValue = false)
          assertParams(actual, expected)
        }
      }
      "when State type is defined as" - {
        "'Props'" in {
          val actual = extractAnnotationParams(q"new PreactComponent[Props, Unit]()")
          val expected = Params(SimpleType(Left(t"Props")), simpleUnitType, withChildrenValue = false)
          assertParams(actual, expected)
        }
        "'Foo.Props'" in {
          val actual = extractAnnotationParams(q"new PreactComponent[Foo.Props, Unit]()")
          val expected = Params(SimpleType(Right(t"Foo.Props")), simpleUnitType, withChildrenValue = false)
          assertParams(actual, expected)
        }
      }
    }
  }

  private def assertParams(actual: Params, expected: Params): Assertion = {
    assertStructurallyEqual(actual.propsType.originalType, expected.propsType.originalType)
    assertStructurallyEqual(actual.stateType.originalType, expected.stateType.originalType)
    assert(actual.withChildrenValue == expected.withChildrenValue)
  }
}
