package preact.dsl

import preact.Preact

import scala.scalajs.js

package object symbol extends EntryImplicits {

  implicit class SymbolOps(val self: Symbol) extends AnyVal {

    def apply(): Preact.VNode = {
      Preact.raw.h(self.name, null)
    }

    def apply(entries: Entry*): Preact.VNode = {
      val zeroAcc = (js.Dictionary.empty[js.Any], Seq.empty[Preact.raw.Child])
      val (attributes, children) = entries
        .foldLeft(zeroAcc) { (acc, entry) =>
          entry match {
            case Entry.Attribute((key, value)) =>
              acc._1.update(key, value)
              acc

            case Entry.Child(child) =>
              (acc._1, acc._2 :+ child)

            case Entry.Children(entryChildren) =>
              (acc._1, acc._2 ++ entryChildren)

            case Entry.EmptyAttribute =>
              acc

            case Entry.EmptyChild =>
              // https://github.com/developit/preact/issues/540
              (acc._1, acc._2 :+ null)
          }
        }
      val vnodeAttributes = if (attributes.isEmpty) null else attributes
      if (children.isEmpty) {
        Preact.raw.h(self.name, vnodeAttributes)
      } else {
        Preact.raw.h(self.name, vnodeAttributes, children: _*)
      }
    }
  }

}
