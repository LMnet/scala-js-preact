package preact.dsl.symbol

import preact.Preact

import scala.scalajs.js

sealed trait Entry
object Entry {
  case class Attribute(value: (String, js.Any)) extends Entry
  case class Child(value: Preact.Child) extends Entry
  case class Children(value: Seq[Preact.Child]) extends Entry
}

trait EntryImplicits {

  implicit def tupleToEntry(tuple: (String, js.Any)): Entry = {
    Entry.Attribute(tuple)
  }

  implicit def stringTupleToEntry(tuple: (String, String)): Entry = {
    Entry.Attribute((tuple._1, tuple._2))
  }

  implicit def function0ToEntry[T](tuple: (String, Function0[T])): Entry = {
    Entry.Attribute((tuple._1, tuple._2))
  }

  implicit def function1ToEntry[T, U](tuple: (String, Function1[T, U])): Entry = {
    Entry.Attribute((tuple._1, tuple._2))
  }

  implicit def function2ToEntry[T1, T2, U](tuple: (String, Function2[T1, T2, U])): Entry = {
    Entry.Attribute((tuple._1, tuple._2))
  }

  implicit def childToEntry(child: Preact.Child): Entry = {
    Entry.Child(child)
  }

  implicit def childrenToEntry(children: Seq[Preact.Child]): Entry = {
    Entry.Children(children)
  }

  implicit def vnodeToEntry(vnode: Preact.VNode): Entry = {
    Entry.Child(vnode)
  }

  implicit def stringToEntry(x: String): Entry = {
    Entry.Child(x)
  }
}
