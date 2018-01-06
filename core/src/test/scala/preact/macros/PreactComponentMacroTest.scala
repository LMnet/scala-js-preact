package preact.macros

import org.scalatest.{Assertion, FreeSpec}

import scala.meta._
import scala.meta.internal.inline.AbortException

class PreactComponentMacroTest extends FreeSpec {

  import PreactComponentImpl._

  val unitType = t"Unit"

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
                  propsType = unitType,
                  stateType = unitType,
                  withChildrenValue = false
                )
              )

              val expected = q"""
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
                  propsType = unitType,
                  stateType = unitType,
                  withChildrenValue = true
                )
              )

              val expected = q"""
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
                propsType = unitType,
                stateType = t"State",
                withChildrenValue = false
              )
            )

            val expected = q"""
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
          "and State is a generic type" in {
            val actual = expand(
              cls = q"""
                class Test {
                  def render() = Preact.raw.h("div", null, null)
                }
              """,
              companionOpt = None,
              Params(
                propsType = unitType,
                stateType = t"Option[Int]",
                withChildrenValue = false
              )
            )

            val expected = q"""
              class Test extends _root_.preact.Preact.Component[Unit, Option[Int]] {
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
                    propsType = t"Props",
                    stateType = unitType,
                    withChildrenValue = false
                  )
                )

                val expected = q"""
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
                    propsType = t"Props",
                    stateType = unitType,
                    withChildrenValue = true
                  )
                )

                val expected = q"""
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
                  propsType = t"Props",
                  stateType = t"State",
                  withChildrenValue = false
                )
              )

              val expected = q"""
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
            "and State is a generic type" in {
              val actual = expand(
                cls =q"""
                  class Test {
                    def render() = Preact.raw.h("div", null, null)
                  }
                """,
                companionOpt = None,
                Params(
                  propsType = t"Props",
                  stateType = t"Option[Int]",
                  withChildrenValue = false
                )
              )

              val expected = q"""
                class Test extends _root_.preact.Preact.Component[Props, Option[Int]] {
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
          "and component doesn't render children" - {
            "and Props has one argument list in the constructor" in {
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
                    propsType = t"Props",
                    stateType = unitType,
                    withChildrenValue = false
                  )
                )

                val expected = q"""
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
            "and Props has more than one argument list in the constructor" in {
              val actual = expand(
                cls = q"""
                  class Test {
                    def render() = Preact.raw.h("div", null, null)
                  }
                """,
                companionOpt = Some(q"""
                  object Test {
                    case class Props(a: String, b: Int)(val c: Long)(d: Option[String])
                  }
                """),
                Params(
                  propsType = t"Test.Props",
                  stateType = unitType,
                  withChildrenValue = false
                )
              )

              val expected = q"""
                class Test extends _root_.preact.Preact.Component[Test.Props, Unit] {
                  def render() = Preact.raw.h("div",null, null)
                }
                object Test {
                  case class Props(a: String, b: Int)(val c: Long)(d: Option[String])

                  def apply(props: Test.Props)(
                    implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                  ): _root_.preact.Preact.VNode = {
                    _root_.preact.Preact.raw.h(
                      ct.constructor, props.asInstanceOf[_root_.preact.Preact.raw.Attributes], null
                    )
                  }
                  def apply(a: String, b: Int)(c: Long)(d: Option[String])(
                    implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                  ): _root_.preact.Preact.VNode = {
                    apply(Props(a, b)(c)(d))
                  }
                }
              """
              assertStructurallyEqual(actual, expected)
            }
            "and Props has implicits argument list in the constructor" - {
              "and implicit is not val" in {
                val actual = expand(
                  cls = q"""
                    class Test {
                      def render() = Preact.raw.h("div", null, null)
                    }
                  """,
                  companionOpt = Some(q"""
                    object Test {
                      case class Props(a: String, b: Int)(implicit c: Long)
                    }
                  """),
                  Params(
                    propsType = t"Test.Props",
                    stateType = unitType,
                    withChildrenValue = false
                  )
                )

                val expected = q"""
                  class Test extends _root_.preact.Preact.Component[Test.Props, Unit] {
                    def render() = Preact.raw.h("div",null, null)
                  }
                  object Test {
                    case class Props(a: String, b: Int)(implicit c: Long)

                    def apply(props: Test.Props)(
                      implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                    ): _root_.preact.Preact.VNode = {
                      _root_.preact.Preact.raw.h(
                        ct.constructor, props.asInstanceOf[_root_.preact.Preact.raw.Attributes], null
                      )
                    }
                    def apply(a: String, b: Int)(
                      implicit c: Long, ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                    ): _root_.preact.Preact.VNode = {
                      apply(Props(a, b)(c))
                    }
                  }
                """
                assertStructurallyEqual(actual, expected)
              }
              "and implicit is val" in {
                val actual = expand(
                  cls = q"""
                    class Test {
                      def render() = Preact.raw.h("div", null, null)
                    }
                  """,
                  companionOpt = Some(q"""
                    object Test {
                      case class Props(a: String, b: Int)(implicit val c: Long)
                    }
                  """),
                  Params(
                    propsType = t"Test.Props",
                    stateType = unitType,
                    withChildrenValue = false
                  )
                )

                val expected = q"""
                  class Test extends _root_.preact.Preact.Component[Test.Props, Unit] {
                    def render() = Preact.raw.h("div",null, null)
                  }
                  object Test {
                    case class Props(a: String, b: Int)(implicit val c: Long)

                    def apply(props: Test.Props)(
                      implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                    ): _root_.preact.Preact.VNode = {
                      _root_.preact.Preact.raw.h(
                        ct.constructor, props.asInstanceOf[_root_.preact.Preact.raw.Attributes], null
                      )
                    }
                    def apply(a: String, b: Int)(
                      implicit c: Long, ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                    ): _root_.preact.Preact.VNode = {
                      apply(Props(a, b)(c))
                    }
                  }
                """
                assertStructurallyEqual(actual, expected)
              }
            }


            "and Props has more than one argument list in the constructor and the last list is implicit" in {
              val actual = expand(
                cls = q"""
                  class Test {
                    def render() = Preact.raw.h("div", null, null)
                  }
                """,
                companionOpt = Some(q"""
                  object Test {
                    case class Props(a: String, b: Int)(val c: Long)(d: Double)(implicit e: Option[String])
                  }
                """),
                Params(
                  propsType = t"Test.Props",
                  stateType = unitType,
                  withChildrenValue = false
                )
              )

              val expected = q"""
                class Test extends _root_.preact.Preact.Component[Test.Props, Unit] {
                  def render() = Preact.raw.h("div",null, null)
                }
                object Test {
                  case class Props(a: String, b: Int)(val c: Long)(d: Double)(implicit e: Option[String])

                  def apply(props: Test.Props)(
                    implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                  ): _root_.preact.Preact.VNode = {
                    _root_.preact.Preact.raw.h(
                      ct.constructor, props.asInstanceOf[_root_.preact.Preact.raw.Attributes], null
                    )
                  }
                  def apply(a: String, b: Int)(c: Long)(d: Double)(
                    implicit e: Option[String], ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                  ): _root_.preact.Preact.VNode = {
                    apply(Props(a, b)(c)(d)(e))
                  }
                }
              """
              assertStructurallyEqual(actual, expected)
            }
          }

          "and component render children" - {
            "and Props constructor has one argument list in the constructor" in {
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
                  propsType = t"Props",
                  stateType = unitType,
                  withChildrenValue = true
                )
              )

              val expected = q"""
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
            "and Props has more than one argument list in the constructor" in {
              val actual = expand(
                cls = q"""
                  class Test {
                    def render() = Preact.raw.h("div", null, null)
                  }
                """,
                companionOpt = Some(q"""
                  object Test {
                    case class Props(a: String, b: Int)(c: Long)(d: Option[String])
                  }
                """),
                Params(
                  propsType = t"Test.Props",
                  stateType = unitType,
                  withChildrenValue = true
                )
              )

              val expected = q"""
                class Test extends _root_.preact.Preact.Component[Test.Props, Unit] {
                  def render() = Preact.raw.h("div",null, null)
                }
                object Test {
                  case class Props(a: String, b: Int)(c: Long)(d: Option[String])

                  def apply(props: Test.Props)(
                    implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                  ): _root_.preact.Preact.VNode = {
                    _root_.preact.Preact.raw.h(
                      ct.constructor, props.asInstanceOf[_root_.preact.Preact.raw.Attributes], null
                    )
                  }
                  def apply(props: Test.Props, children: _root_.preact.Preact.Child*)(
                    implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                  ): _root_.preact.Preact.VNode = {
                    _root_.preact.Preact.raw.h(
                      ct.constructor, props.asInstanceOf[_root_.preact.Preact.raw.Attributes], children: _*
                    )
                  }
                  def apply(a: String, b: Int)(c: Long)(d: Option[String])(
                    implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                  ): _root_.preact.Preact.VNode = {
                    apply(Props(a, b)(c)(d))
                  }
                  def apply(a: String, b: Int)(c: Long)(d: Option[String])(children: _root_.preact.Preact.Child*)(
                    implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                  ): _root_.preact.Preact.VNode = {
                    apply(Props(a, b)(c)(d), children: _*)
                  }
                }
              """
              assertStructurallyEqual(actual, expected)
            }
            "and Props has implicits argument list in the constructor" - {
              "and implicit is not val" in {
                val actual = expand(
                  cls = q"""
                    class Test {
                      def render() = Preact.raw.h("div", null, null)
                    }
                  """,
                  companionOpt = Some(q"""
                    object Test {
                      case class Props(a: String, b: Int)(implicit c: Long)
                    }
                  """),
                  Params(
                    propsType = t"Test.Props",
                    stateType = unitType,
                    withChildrenValue = true
                  )
                )

                val expected = q"""
                  class Test extends _root_.preact.Preact.Component[Test.Props, Unit] {
                    def render() = Preact.raw.h("div",null, null)
                  }
                  object Test {
                    case class Props(a: String, b: Int)(implicit c: Long)

                    def apply(props: Test.Props)(
                      implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                    ): _root_.preact.Preact.VNode = {
                      _root_.preact.Preact.raw.h(
                        ct.constructor, props.asInstanceOf[_root_.preact.Preact.raw.Attributes], null
                      )
                    }
                    def apply(props: Test.Props, children: _root_.preact.Preact.Child*)(
                      implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                    ): _root_.preact.Preact.VNode = {
                      _root_.preact.Preact.raw.h(
                        ct.constructor, props.asInstanceOf[_root_.preact.Preact.raw.Attributes], children: _*
                      )
                    }
                    def apply(a: String, b: Int)(
                      implicit c: Long, ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                    ): _root_.preact.Preact.VNode = {
                      apply(Props(a, b)(c))
                    }
                    def apply(a: String, b: Int, children: _root_.preact.Preact.Child*)(
                      implicit c: Long, ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                    ): _root_.preact.Preact.VNode = {
                      apply(Props(a, b)(c), children: _*)
                    }
                  }
                """
                assertStructurallyEqual(actual, expected)
              }
              "and implicit is val" in {
                val actual = expand(
                  cls = q"""
                    class Test {
                      def render() = Preact.raw.h("div", null, null)
                    }
                  """,
                  companionOpt = Some(q"""
                    object Test {
                      case class Props(a: String, b: Int)(implicit val c: Long)
                    }
                  """),
                  Params(
                    propsType = t"Test.Props",
                    stateType = unitType,
                    withChildrenValue = true
                  )
                )

                val expected = q"""
                  class Test extends _root_.preact.Preact.Component[Test.Props, Unit] {
                    def render() = Preact.raw.h("div",null, null)
                  }
                  object Test {
                    case class Props(a: String, b: Int)(implicit val c: Long)

                    def apply(props: Test.Props)(
                      implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                    ): _root_.preact.Preact.VNode = {
                      _root_.preact.Preact.raw.h(
                        ct.constructor, props.asInstanceOf[_root_.preact.Preact.raw.Attributes], null
                      )
                    }
                    def apply(props: Test.Props, children: _root_.preact.Preact.Child*)(
                      implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                    ): _root_.preact.Preact.VNode = {
                      _root_.preact.Preact.raw.h(
                        ct.constructor, props.asInstanceOf[_root_.preact.Preact.raw.Attributes], children: _*
                      )
                    }
                    def apply(a: String, b: Int)(
                      implicit c: Long, ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                    ): _root_.preact.Preact.VNode = {
                      apply(Props(a, b)(c))
                    }
                    def apply(a: String, b: Int, children: _root_.preact.Preact.Child*)(
                      implicit c: Long, ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                    ): _root_.preact.Preact.VNode = {
                      apply(Props(a, b)(c), children: _*)
                    }
                  }
                """
                assertStructurallyEqual(actual, expected)
              }
            }
            "and Props has more than one argument list in the constructor and the last list is implicit" in {
              val actual = expand(
                cls = q"""
                  class Test {
                    def render() = Preact.raw.h("div", null, null)
                  }
                """,
                companionOpt = Some(q"""
                  object Test {
                    case class Props(a: String, b: Int)(val c: Long)(d: Double)(implicit e: Option[String])
                  }
                """),
                Params(
                  propsType = t"Test.Props",
                  stateType = unitType,
                  withChildrenValue = true
                )
              )

              val expected = q"""
                class Test extends _root_.preact.Preact.Component[Test.Props, Unit] {
                  def render() = Preact.raw.h("div",null, null)
                }
                object Test {
                  case class Props(a: String, b: Int)(val c: Long)(d: Double)(implicit e: Option[String])

                  def apply(props: Test.Props)(
                    implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                  ): _root_.preact.Preact.VNode = {
                    _root_.preact.Preact.raw.h(
                      ct.constructor, props.asInstanceOf[_root_.preact.Preact.raw.Attributes], null
                    )
                  }
                  def apply(props: Test.Props, children: _root_.preact.Preact.Child*)(
                    implicit ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                  ): _root_.preact.Preact.VNode = {
                    _root_.preact.Preact.raw.h(
                      ct.constructor, props.asInstanceOf[_root_.preact.Preact.raw
                      .Attributes], children: _*
                    )
                  }
                  def apply(a: String, b: Int)(c: Long)(d: Double)(
                    implicit e: Option[String], ct: _root_.scala.scalajs.js.ConstructorTag[Test]
                  ): _root_.preact.Preact.VNode = {
                    apply(Props(a, b)(c)(d)(e))
                  }
                  def apply(a: String, b: Int)(c: Long)(d: Double)(children: _root_.preact.Preact.Child*)(
                    implicit e: Option[String], ct: _root_.scala.scalajs.js
                    .ConstructorTag[Test]
                  ): _root_.preact.Preact.VNode = {
                    apply(Props(a, b)(c)(d)(e), children: _*)
                  }
                }
              """
              assertStructurallyEqual(actual, expected)
            }
          }
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
                propsType = t"Test.Props",
                stateType = unitType,
                withChildrenValue = false
              )
            )

            val expected = q"""
              class Test extends _root_.preact.Preact.Component[Test.Props, Unit] {
                def render() = Preact.raw.h("div",null, null)
              }
              object Test {
                case class Props(a: String, b: Int)

                def apply(props: Test.Props)(
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
      "when component's constructor contains single argument with non-'props' name" in {
        val actual = expand(
          cls = q"""
            class Test(initialProps: Props) {
              def render() = Preact.raw.h("div", null, null)
            }
          """,
          companionOpt = None,
          Params(
            propsType = t"Props",
            stateType = unitType,
            withChildrenValue = false
          )
        )

        val expected = q"""
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
              propsType = t"Props",
              stateType = unitType,
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
          val expected = Params(unitType, unitType, withChildrenValue = false)
          assertParams(actual, expected)
        }
        "and withChildren is passed to the constructor" - {
          "with argument name" in {
            val actual = extractAnnotationParams(q"new PreactComponent[Unit, Unit](withChildren = true)")
            val expected = Params(unitType, unitType, withChildrenValue = true)
            assertParams(actual, expected)
          }
          "without argument name" in {
            val actual = extractAnnotationParams(q"new PreactComponent[Unit, Unit](true)")
            val expected = Params(unitType, unitType, withChildrenValue = true)
            assertParams(actual, expected)
          }
        }
      }
      "when State type is defined as" - {
        "'State'" in {
          val actual = extractAnnotationParams(q"new PreactComponent[Unit, State]()")
          val expected = Params(unitType, t"State", withChildrenValue = false)
          assertParams(actual, expected)
        }
        "'Foo.State'" in {
          val actual = extractAnnotationParams(q"new PreactComponent[Unit, Foo.State]()")
          val expected = Params(unitType, t"Foo.State", withChildrenValue = false)
          assertParams(actual, expected)
        }
      }
      "when State type is defined as" - {
        "'Props'" in {
          val actual = extractAnnotationParams(q"new PreactComponent[Props, Unit]()")
          val expected = Params(t"Props", unitType, withChildrenValue = false)
          assertParams(actual, expected)
        }
        "'Foo.Props'" in {
          val actual = extractAnnotationParams(q"new PreactComponent[Foo.Props, Unit]()")
          val expected = Params(t"Foo.Props", unitType, withChildrenValue = false)
          assertParams(actual, expected)
        }
      }
    }
  }

  private def assertParams(actual: Params, expected: Params): Assertion = {
    assertStructurallyEqual(actual.propsType, expected.propsType)
    assertStructurallyEqual(actual.stateType, expected.stateType)
    assert(actual.withChildrenValue == expected.withChildrenValue)
  }
}
