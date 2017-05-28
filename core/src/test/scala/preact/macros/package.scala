package preact

import org.scalatest.{Assertion, Assertions}

import scala.meta.Tree
import scala.meta.testkit.StructurallyEqual

package object macros {

  def assertStructurallyEqual(actual: Tree, expected: Tree): Assertion = {
    StructurallyEqual(actual, expected) match {
      case Left(diff) =>
        Assertions.fail(
          s"""Not Structurally equal:
             |$diff
             |Details:
             |${diff.detailed}""".stripMargin)
      case _ => Assertions.succeed
    }
  }
}
