package example

import preact.Preact

object FunctionComponents {

  import preact.dsl.symbol._

  case class Props(age: Int)

  val simple = () => {
    'div("Function component without props, state and children")
  }

  val withProps = Preact.FunctionFactory.withProps[Props] { props =>
    'div(s"Function component with props. Age: ${props.age}")
  }

  val withChildren = Preact.FunctionFactory.withChildren { children =>
    'div(
      "Function component with children",
      'div(children)
    )
  }

  val withPropsAndChildren = Preact.FunctionFactory.withPropsAndChildren[Props] { (props, children) =>
    'div(
      s"Function component with props and children. Age: ${props.age}",
      'div(children)
    )
  }
}
