package preact.dsl.tags

import java.net.URI

import preact.dsl.tags.Entry.Attribute

import scala.scalajs.js
import scala.scalajs.js.RegExp
import scala.util.matching.Regex

case class KeyValueAttribute(name: String, value: js.Any) extends Attribute

trait EmptyAttribute extends Attribute {
  def name: String
  def value: js.Any = ""
}

class StringAttributeBuilder(val name: String) {
  def :=(value: String): Attribute = KeyValueAttribute(name, value)
}

class CallbackAttributeBuilder(val name: String) {
  def :=(value: js.Function): Attribute = KeyValueAttribute(name, value)
}

case class HtmlId(value: String)

trait CustomAttributeValue {
  def name: String
}
object CustomAttributeValue {

  trait ListOfKeyLabels extends CustomAttributeValue {
    def :=(value: Set[Char]): Attribute = {
      KeyValueAttribute(name, value.mkString(","))
    }
  }

  trait SpaceSeparatedSet extends CustomAttributeValue {
    def :=(value: Set[String]): Attribute = {
      KeyValueAttribute(name, value.mkString(" "))
    }
  }

  trait CommaSeparatedStringsSet extends CustomAttributeValue {
    def :=(value: Set[String]): Attribute = {
      KeyValueAttribute(name, value.mkString(","))
    }
  }

  trait BooleanValue extends CustomAttributeValue {
    def :=(value: Boolean): Attribute = {
      KeyValueAttribute(name, value.toString)
    }
  }

  trait HtmlIdReference extends CustomAttributeValue {
    def :=(value: HtmlId): Attribute = {
      KeyValueAttribute(name, value.value)
    }
  }

  trait IntValue extends CustomAttributeValue {
    def :=(value: Int): Attribute = {
      KeyValueAttribute(name, value)
    }
  }

  trait Url extends CustomAttributeValue {
    def :=(value: URI): Attribute = {
      KeyValueAttribute(name, value.toString)
    }
  }

  trait IntList extends CustomAttributeValue {
    def :=(values: Int*): Attribute = {
      KeyValueAttribute(name, values.mkString(","))
    }
  }

  trait TargetValues extends CustomAttributeValue {
    def _blank: Attribute = KeyValueAttribute(name, "_blank")
    def _self: Attribute = KeyValueAttribute(name, "_self")
    def _parent: Attribute = KeyValueAttribute(name, "_parent")
    def _top: Attribute = KeyValueAttribute(name, "_top")
  }

  trait HttpMethod extends CustomAttributeValue {
    def get: Attribute = KeyValueAttribute(name, "get")
    def post: Attribute = KeyValueAttribute(name, "post")
  }

  trait EncMimeType extends CustomAttributeValue {
    def appFormUrlEncoded: Attribute = KeyValueAttribute(name, "application/x-www-form-urlencoded")
    def multipartFormData: Attribute = KeyValueAttribute(name, "multipart/form-data")
    def textPlain: Attribute = KeyValueAttribute(name, "text/plain")
  }

  trait RegexpValue extends CustomAttributeValue {
    def :=(value: RegExp): Attribute = KeyValueAttribute(name, value.source)
    def :=(value: Regex): Attribute = KeyValueAttribute(name, value.pattern.pattern())
  }
}
