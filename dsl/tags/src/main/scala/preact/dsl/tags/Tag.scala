package preact.dsl.tags

import preact.Preact

import scala.scalajs.js

sealed trait Entry
object Entry {

  trait Attribute extends Entry {
    def name: String
    def value: js.Any
  }

  case class Child(value: Preact.Child) extends Entry
  case class Children(value: Iterable[Preact.Child]) extends Entry
  case object EmptyAttribute extends Entry
  case object EmptyChild extends Entry
}

class Tag(val name: String) {

  def apply(): Preact.VNode = {
    Preact.raw.h(name, null, null)
  }

  def apply(entries: Entry*): Preact.VNode = {
    val zeroAcc = (js.Dictionary.empty[js.Any], Seq.empty[Preact.raw.Child])
    val (attributes, children) = entries
      .foldLeft(zeroAcc) { (acc, entry) =>
        entry match {
          case attr: Entry.Attribute =>
            acc._1.update(attr.name, attr.value)
            acc

          case Entry.Child(child) =>
            (acc._1, acc._2 :+ child)

          case Entry.Children(entryChildren) =>
            (acc._1, acc._2 ++ entryChildren)

          case Entry.EmptyAttribute =>
            acc

          case Entry.EmptyChild =>
            acc
        }
      }
    val vnodeAttributes = if (attributes.isEmpty) null else attributes
    Preact.raw.h(name, vnodeAttributes, children: _*)
  }
}

trait EntryImplicits {

  implicit def childToEntry(child: Preact.Child): Entry = {
    Entry.Child(child)
  }

  implicit def childrenToEntry(children: Iterable[Preact.Child]): Entry = {
    Entry.Children(children)
  }

  implicit def vnodeToEntry(vnode: Preact.VNode): Entry = {
    Entry.Child(vnode)
  }

  implicit def vnodesToEntry(vnodes: Iterable[Preact.VNode]): Entry = {
    Entry.Children(vnodes.asInstanceOf[Iterable[Preact.Child]])
  }

  implicit def stringToEntry(x: String): Entry = {
    Entry.Child(x)
  }
}
