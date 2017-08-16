scala-js-preact
===============

[![Build Status](https://travis-ci.org/LMnet/scala-js-preact.svg?branch=master)](https://travis-ci.org/LMnet/scala-js-preact)

Scala.js facade for the [Preact](https://preactjs.com/) JavaScript library.

Quick start
-----------

Add the following lines into your `build.sbt` file:

```scala
libraryDependencies ++= Seq(
  "com.github.lmnet" %%% "scala-js-preact-core" % "0.2.0",
  "com.github.lmnet" %%% "scala-js-preact-dsl-tags" % "0.2.0"
)
addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M9" cross CrossVersion.full)
```

Now you can create Preact component:

```scala
import org.scalajs.dom.Event
import preact.Preact.VNode
import preact.macros.PreactComponent

object SomeComponent {
  case class Props(name: String)
}

@PreactComponent[SomeComponent.Props, Unit]
class SomeComponent {

  import preact.dsl.tags._

  def render(): VNode = {
    div(id := "foo",
      span(s"Hello, ${props.name}!"),
      button(onclick := { _: Event => println("hi!") }, "Click Me")
    )
  }
}
```

And render it:

```scala
import org.scalajs.dom
import preact.Preact

import scala.scalajs.js.JSApp

object App extends JSApp {

  def main(): Unit = {
    val appDiv = dom.document.getElementById("app")
    val component = SomeComponent(name = "scala-js-preact user")
    Preact.render(component, appDiv)
  }
}
```

You can find more examples in the [examples directory](examples).

Design goals and motivation
---------------------------

Why should you care about this "yet another Scala.js facade for React"? The current situation in the Scala.js ecosystem
forced developers to spend quite a lot of time to be able to write frontend applications with React facades.
Also, React is a pretty heavy library and Scala.js runtime also adds its own overhead.
Even after `fullOptJS` and with `gzip` it's common to have more than 1 MB of JavaScript code in the bundle.

`scala-js-preact` is trying to solve this issues:

1. Preact instead of React. Preact is a fast 3KB alternative to React, with the same API.
It helps to minimize js bundle size.
2. Minimalistic and simple. `scala-js-preact` contains a small amount of code and this also helps to prevent
your bundle overgrowth. Also, it doesn't contain any complex functional stuff in the API which is good for the newcomers.
3. Modular architecture. Use only parts you need in your application.
4. Familiar API. Scala API should be more or less like JavaScript API.
5. DSL agnostic. Scala is the very powerful language in terms of DSL. But this power also lead to a lot of debates.
`scala-js-preact` doesn't limit you with the single HTML DSL. It provides a few DSL out of the box,
but you can easily create your own DSL, or use third-party DSL.


Modules
-------

1. `scala-js-preact-raw` - raw Preact facade. Just some low-level typings.
You will need this module only if you want to create your own Preact library.
2. `scala-js-preact-core` - core module. Contains APIs for creating class and function components.
This is the main module of the library. To use this module you need to add macro-paradise compiler plugin
because `core` uses some macro magic to provide nice API for creating components.
3. `scala-js-preact-dsl-symbol` - minimalistic DSL module with dynamically typed tags and attributes.
Inspired by [levsha](https://github.com/fomkin/levsha).
3. `scala-js-preact-dsl-tags` - DSL module with typesafe tags and attributes.
Inspired by [scalatags](https://github.com/lihaoyi/scalatags).

Usage
-----

### Components

To create a class component you should annotate your class with `@PreactComponent[PropsType, StateType]` annotation.
You should set up `Props` and `State` types of the component:
```scala
case class Props(...)
case class State(...)

@PreactComponent[Props, State]
class SomeComponent {...}
```

If your component doesn't use `Props` or `State` you can set any of them as `Unit`:
```scala
@PreactComponent[Props, Unit]
class StatelessComponent {...}

@PreactComponent[Unit, State]
class PropslessComponent {...}

@PreactComponent[Unit, Unit]
class ConstantComponent {...}
```

The only one required method in the components is `render`:
```scala
@PreactComponent[Props, State]
class SomeComponent {
  def render(): VNode = ???
}
```

Here is the list of other `Component` methods:
* `props` - gives access to the read-only `props` object.
* `props` - state access to the read-only `state` object.
* `setState` - method to define a new state of the component. This is the only way to set up a new state.
* `initialState` - if you want to define the initial state of the component, you should do this with this method.
This method could be called only once.
* `children` - gives access to the component's children.
* `key` - gives access to the component's [key](https://facebook.github.io/react/docs/lists-and-keys.html).
* `base` - gives access to the component's DOM node.
* `componentWillMount` - [lifecycle method](https://preactjs.com/guide/getting-started#the-component-lifecycle).
* `componentDidMount` - [lifecycle method](https://preactjs.com/guide/getting-started#the-component-lifecycle).
* `componentWillUnmount` - [lifecycle method](https://preactjs.com/guide/getting-started#the-component-lifecycle).
* `componentWillReceiveProps` - [lifecycle method](https://preactjs.com/guide/getting-started#the-component-lifecycle).
* `shouldComponentUpdate` - [lifecycle method](https://preactjs.com/guide/getting-started#the-component-lifecycle).
* `componentWillUpdate` - [lifecycle method](https://preactjs.com/guide/getting-started#the-component-lifecycle).
* `componentDidUpdate` - [lifecycle method](https://preactjs.com/guide/getting-started#the-component-lifecycle).

#### Auto generated component's constructor

If your component use props, creating a component instance usually looks like this:

```scala
case class Props(foo: String, bar: Int)

@PreactComponent[Props, Unit]
class SomeComponent {
  def render(): VNode = ???
}

val instance = SomeComponent(Props("test", 15))
```

But, its a good practice to place local `Props` and `State` case classes inside companion object of the component.
If you do this, `@PreactComponent` macro will generate additional `apply` method with the same signature as `Props` case class has:

```scala
object SomeComponent {
  case class Props(foo: String, bar: Int)
}
@PreactComponent[SomeComponent.Props, Unit]
class SomeComponent {
  def render(): VNode = ???
}

val instance = SomeComponent("test", 15) // don't need to wrap argument in Props(...)
```
It makes your code a little more readable.

#### Getting props from component's constructor

There is some unobvious moment with getting props from the component's constructor.
This is the common pattern to set up initial state from the props from the constructor. Let's try to write some code with this pattern:
```scala
case class Props(foo: Int)
case class State(bar: String)

@PreactComponent[Props, State]
class SomeComponent(props: Props) {

  initialState(State(
    bar = props.foo.toString
  ))

  def render(): VNode = {
    div(props.foo.toString, state.bar)
  }
}
```
At first glance, everything looks good. But, what about `props` method? In the component above we have a naming conflict:
`props` object from the constructor and `props` method in the same scope. To prevent this issue we should rename
our constructor argument, for example like this:
```scala
@PreactComponent[Props, State]
class SomeComponent(initialProps: Props) {

  initialState(State(
    bar = initialProps.foo.toString
  ))

  def render(): VNode = {
    div(props.foo.toString, state.bar)
  }
}
```
Now we don't have naming conflict and can use both `initialProps` and `props`.

Good thing to know: `@PreactComponent` macro checks this and if you got into this problem you will have
nice compile-time error.

### DSL

`scala-js-preact` got two optional DSLs: symbol DSL and tags DSL. You can use any of them, or any third party DSL,
or even create your own DSL. In the sections below you will be guided how to use default DSLs and create your own.

#### Symbol DSL

This DSL is inspired by [levsha](https://github.com/fomkin/levsha) DSL. Its goal is to be minimalistic and not typesafe
in terms of HTML tags or attributes. You can use any tags and attributes you want, like in normal HTML.
All under your control and responsibility.

To use this DSL add it to your dependencies:
```scala
"com.github.lmnet" %%% "scala-js-preact-dsl-symbol" % "0.2.0"
```

And import `preact.dsl.symbol._` into your source code:
```scala
import preact.dsl.symbol._

'section("class" -> "todoapp",
  'header("class" -> "header",
    'h1("todos")
  )
)
```

This DSL is very simple:
```scala
// Any `scala.Symbol` became tag with `apply` method:
'div()

// Tuples inside tags became attributes:
'div(
  "class" -> "foo"
)

// Functions works too:
def callback(event: Event): Unit = {
  ???
}
'div(
  "onclick" -> callback _
)

// You can pass any VNode, component or strings inside tags:
'div(someComponentInstance, "some text", 'b("some another tag"))

// Also, there are a few helpers:
'div(
  if (something) {
    Entry.Children(Seq(???)) // gives possibility to pass `Iterable[Preact.Child]` to any tag
  } else {
    Entry.EmptyChild // useful for conditional children
  },
  if (somethingElse) {
    "foo" -> "bar"
  } else {
    Entry.EmptyAttribute // useful for conditional attributes
  }
)
```

#### Tags DSL

This DSL is inspired by [scalatags](https://github.com/lihaoyi/scalatags) DSL. Its goal is to provide typesafe HTML DSL,
and at the same time be minimalistic as possible.

Why not just create `scalatags` backend for the `scala-js-preact`? Here are a few reasons:
1. Annoying `.render` on all fragments.
2. `scalatags` is small, but not enough small.
It contains some parts that I don't want to have in the `scala-js-preact` DSL, like styles.
3. Because of `scalatags` internals and rendering process, it will add some extra performance overhead.

But if you want you can create `scalatags` backend for the `scala-js-preact`. Contributions are welcome!

To use this DSL add it to your dependencies:
```scala
"com.github.lmnet" %%% "scala-js-preact-dsl-tags" % "0.2.0"
```

And import `preact.dsl.tags._` into your source code:
```scala
import preact.dsl.tags._

section(`class` := "todoapp",
  header(`class` := "header",
    h1("todos")
  )
)
```

The main idea is the same as in the `scalatags`. I suggest you to familiarize with its docs at first.

The main difference between `scalatags` and `scala-js-preact-dsl-tags`:
* You don't need to call `.render`method on your fragments.
* `Tag`'s `apply` method returns and receives `Preact.VNode`.
* Has `Children`, `EmptyAttribute` and `EmptyChild`, just like Symbol DSL.

#### Creating your own DSL

Every Preact DSL is based on the single `Preact.raw.h` function under the hood.
This function creates `VNode` instances.
It has the following signatures (in the real source code they are looking a little different):
```scala
type NodeType = js.Dynamic |        // for class components
  js.Function0[VNode] |             // for function components without arguments
  js.Function1[js.Dynamic, VNode] | // for function components with props argument
  String                            // for HTML tags

// for VNode with children
def h(
  node: NodeType,
  params: js.Dictionary[js.Any], // if params is empty you should pass null
  children: Child*
): VNode

// for VNode without children
def h(
  node: NodeType,
  params: js.Dictionary[js.Any] // if params is empty you should pass null
): VNode
```

_Important note_: in the Preact (and React) `null` is the correct child node. In your DSL you should correctly preserve
an order of all child nodes, including `null`. You can replace `null` with something else in your high-level DSL API,
but on the low-level `Preact.raw.h` call you should pass `null`. Look at the `Entry.EmptyChild` in the symbol or tags
DSLs for the example.

This is all you should know about creating DSLs for `scala-js-preact`.
You can check code of the built-in DSLs for the real examples.

Project status
==============

This project is in the pre-alpha state. Please, don't use it in the production!
