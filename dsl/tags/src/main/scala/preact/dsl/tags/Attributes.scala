package preact.dsl.tags

import preact.dsl.tags.CustomAttributeValue._
import preact.dsl.tags.Entry.Attribute

trait CoreAttributes {

  lazy val accesskey = new StringAttributeBuilder("accesskey") with ListOfKeyLabels

  lazy val `class` = new StringAttributeBuilder("class") with SpaceSeparatedSet

  lazy val contenteditable = new StringAttributeBuilder("contenteditable") with BooleanValue with EmptyAttribute

  lazy val contextmenu = new StringAttributeBuilder("contextmenu") with HtmlIdReference

  lazy val dir = new StringAttributeBuilder("dir") with CustomAttributeValue {
    def ltr: Attribute = KeyValueAttribute(name, "ltr")
    def rtl: Attribute = KeyValueAttribute(name, "rtl")
    def auto: Attribute = KeyValueAttribute(name, "auto")
  }

  lazy val draggable = new StringAttributeBuilder("draggable") with BooleanValue

  lazy val dropzone = new StringAttributeBuilder("dropzone") // in progress in spec

  lazy val hidden = new StringAttributeBuilder("hidden") with EmptyAttribute with CustomAttributeValue {
    def hidden: Attribute = KeyValueAttribute(name, "hidden")
  }

  lazy val id = new StringAttributeBuilder("id")

  lazy val lang = new StringAttributeBuilder("lang")

  lazy val spellcheck = new StringAttributeBuilder("spellcheck") with BooleanValue with EmptyAttribute

  lazy val styleA = new StringAttributeBuilder("style") // conflict with tag

  lazy val tabindex = new StringAttributeBuilder("tabindex") with IntValue

  lazy val titleA = new StringAttributeBuilder("title") // conflict with tag

  lazy val translate = new StringAttributeBuilder("translate") with CustomAttributeValue {
    def yes: Attribute = KeyValueAttribute(name, "yes")
    def no: Attribute = KeyValueAttribute(name, "no")
  }
}

trait EventHandlerAttributes {
  lazy val onabort = new CallbackAttributeBuilder("onabort")
  lazy val onblur = new CallbackAttributeBuilder("onblur")
  lazy val oncanplay = new CallbackAttributeBuilder("oncanplay")
  lazy val oncanplaythrough = new CallbackAttributeBuilder("oncanplaythrough")
  lazy val onchange = new CallbackAttributeBuilder("onchange")
  lazy val onclick = new CallbackAttributeBuilder("onclick")
  lazy val oncontextmenu = new CallbackAttributeBuilder("oncontextmenu")
  lazy val ondblclick = new CallbackAttributeBuilder("ondblclick")
  lazy val ondrag = new CallbackAttributeBuilder("ondrag")
  lazy val ondragend = new CallbackAttributeBuilder("ondragend")
  lazy val ondragenter = new CallbackAttributeBuilder("ondragenter")
  lazy val ondragleave = new CallbackAttributeBuilder("ondragleave")
  lazy val ondragover = new CallbackAttributeBuilder("ondragover")
  lazy val ondragstart = new CallbackAttributeBuilder("ondragstart")
  lazy val ondrop = new CallbackAttributeBuilder("ondrop")
  lazy val ondurationchange = new CallbackAttributeBuilder("ondurationchange")
  lazy val onemptied = new CallbackAttributeBuilder("onemptied")
  lazy val onended = new CallbackAttributeBuilder("onended")
  lazy val onerror = new CallbackAttributeBuilder("onerror")
  lazy val onfocus = new CallbackAttributeBuilder("onfocus")
  lazy val oninput = new CallbackAttributeBuilder("oninput")
  lazy val oninvalid = new CallbackAttributeBuilder("oninvalid")
  lazy val onkeydown = new CallbackAttributeBuilder("onkeydown")
  lazy val onkeypress = new CallbackAttributeBuilder("onkeypress")
  lazy val onkeyup = new CallbackAttributeBuilder("onkeyup")
  lazy val onload = new CallbackAttributeBuilder("onload")
  lazy val onloadeddata = new CallbackAttributeBuilder("onloadeddata")
  lazy val onloadedmetadata = new CallbackAttributeBuilder("onloadedmetadata")
  lazy val onloadstart = new CallbackAttributeBuilder("onloadstart")
  lazy val onmousedown = new CallbackAttributeBuilder("onmousedown")
  lazy val onmousemove = new CallbackAttributeBuilder("onmousemove")
  lazy val onmouseout = new CallbackAttributeBuilder("onmouseout")
  lazy val onmouseover = new CallbackAttributeBuilder("onmouseover")
  lazy val onmouseup = new CallbackAttributeBuilder("onmouseup")
  lazy val onmousewheel = new CallbackAttributeBuilder("onmousewheel")
  lazy val onpause = new CallbackAttributeBuilder("onpause")
  lazy val onplay = new CallbackAttributeBuilder("onplay")
  lazy val onplaying = new CallbackAttributeBuilder("onplaying")
  lazy val onprogress = new CallbackAttributeBuilder("onprogress")
  lazy val onratechange = new CallbackAttributeBuilder("onratechange")
  lazy val onreadystatechange = new CallbackAttributeBuilder("onreadystatechange")
  lazy val onreset = new CallbackAttributeBuilder("onreset")
  lazy val onscroll = new CallbackAttributeBuilder("onscroll")
  lazy val onseeked = new CallbackAttributeBuilder("onseeked")
  lazy val onseeking = new CallbackAttributeBuilder("onseeking")
  lazy val onselect = new CallbackAttributeBuilder("onselect")
  lazy val onshow = new CallbackAttributeBuilder("onshow")
  lazy val onstalled = new CallbackAttributeBuilder("onstalled")
  lazy val onsubmit = new CallbackAttributeBuilder("onsubmit")
  lazy val onsuspend = new CallbackAttributeBuilder("onsuspend")
  lazy val ontimeupdate = new CallbackAttributeBuilder("ontimeupdate")
  lazy val onvolumechange = new CallbackAttributeBuilder("onvolumechange")
  lazy val onwaiting = new CallbackAttributeBuilder("onwaiting")
}

trait XmlAttributes {

  lazy val `xml:lang` = new StringAttributeBuilder("xml:lang")

  lazy val `xml:space` = new StringAttributeBuilder("xml:space") with CustomAttributeValue {
    def preserve: Attribute = KeyValueAttribute(name, "preserve")
    def default: Attribute = KeyValueAttribute(name, "default")
  }

  lazy val `xml:base` = new StringAttributeBuilder("xml:base") with Url
}

trait GlobalAttributes extends CoreAttributes with EventHandlerAttributes with XmlAttributes

/**
  * Without native `|` it's difficult to make typesafe links between attributes and tags.
  * For now, any tag will be able to have any attribute at compile time.
  */
trait TagAttributes {

  lazy val href = new StringAttributeBuilder("href") with Url

  lazy val target = new StringAttributeBuilder("target") with TargetValues

  lazy val rel = new StringAttributeBuilder("rel") with SpaceSeparatedSet

  lazy val hreflang = new StringAttributeBuilder("hreflang")

  lazy val media = new StringAttributeBuilder("media")

  lazy val `type` = new StringAttributeBuilder("type")

  lazy val alt = new StringAttributeBuilder("alt")

  lazy val shape = new StringAttributeBuilder("shape") with CustomAttributeValue {
    def rect: Attribute = KeyValueAttribute(name, "rect")
    def circle: Attribute = KeyValueAttribute(name, "circle")
    def poly: Attribute = KeyValueAttribute(name, "poly")
    def default: Attribute = KeyValueAttribute(name, "default")
  }

  lazy val coords = new StringAttributeBuilder("coords") with IntList

  lazy val autoplay = new StringAttributeBuilder("autoplay") with EmptyAttribute

  lazy val preload = new StringAttributeBuilder("preload") with EmptyAttribute with CustomAttributeValue {
    def none: Attribute = KeyValueAttribute(name, "none")
    def metadata: Attribute = KeyValueAttribute(name, "metadata")
    def auto: Attribute = KeyValueAttribute(name, "auto")
  }

  lazy val controls = new StringAttributeBuilder("controls") with EmptyAttribute

  lazy val loop = new StringAttributeBuilder("loop") with EmptyAttribute

  lazy val mediagroup = new StringAttributeBuilder("mediagroup")

  lazy val muted = new StringAttributeBuilder("muted") with EmptyAttribute

  lazy val src = new StringAttributeBuilder("src") with Url

  lazy val citeA = new StringAttributeBuilder("cite") with Url // conflict with tag

  lazy val onafterprint = new CallbackAttributeBuilder("onafterprint")
  lazy val onbeforeprint = new CallbackAttributeBuilder("onbeforeprint")
  lazy val onbeforeunload = new CallbackAttributeBuilder("onbeforeunload")
  lazy val onhashchange = new CallbackAttributeBuilder("onhashchange")
  lazy val onmessage = new CallbackAttributeBuilder("onmessage")
  lazy val onoffline = new CallbackAttributeBuilder("onoffline")
  lazy val ononline = new CallbackAttributeBuilder("ononline")
  lazy val onpagehide = new CallbackAttributeBuilder("onpagehide")
  lazy val onresize = new CallbackAttributeBuilder("onresize")
  lazy val onstorage = new CallbackAttributeBuilder("onstorage")
  lazy val onunload = new CallbackAttributeBuilder("onunload")

  lazy val name = new StringAttributeBuilder("name")

  lazy val disabled = new StringAttributeBuilder("disabled") with EmptyAttribute

  lazy val formA = new StringAttributeBuilder("form") with HtmlIdReference // conflict with tag

  lazy val value = new StringAttributeBuilder("value")

  lazy val formaction = new StringAttributeBuilder("formaction") with Url

  lazy val autofocus = new StringAttributeBuilder("autofocus") with EmptyAttribute

  lazy val formenctype = new StringAttributeBuilder("formenctype") with EncMimeType

  lazy val formmethod = new StringAttributeBuilder("formmethod") with HttpMethod

  lazy val formtarget = new StringAttributeBuilder("formtarget") with TargetValues

  lazy val formnovalidate = new StringAttributeBuilder("formnovalidate") with EmptyAttribute

  lazy val height = new StringAttributeBuilder("height") with IntValue

  lazy val width = new StringAttributeBuilder("width") with IntValue

  lazy val spanA = new StringAttributeBuilder("span") with IntValue // conflict with tag

  lazy val labelA = new StringAttributeBuilder("label") // conflict with tag

  lazy val icon = new StringAttributeBuilder("icon") with Url

  lazy val radiogroup = new StringAttributeBuilder("radiogroup")

  lazy val checked = new StringAttributeBuilder("checked") with EmptyAttribute

  lazy val datetime = new StringAttributeBuilder("datetime") // TODO: use types from java.time?

  lazy val open = new StringAttributeBuilder("open") with EmptyAttribute

  lazy val action = new StringAttributeBuilder("action") with Url

  lazy val method = new StringAttributeBuilder("method") with HttpMethod

  lazy val enctype = new StringAttributeBuilder("formenctype") with EncMimeType

  lazy val `accept-charset` = new StringAttributeBuilder("accept-charset") with SpaceSeparatedSet

  lazy val novalidate = new StringAttributeBuilder("novalidate") with EmptyAttribute

  lazy val autocomplete = new StringAttributeBuilder("autocomplete") with CustomAttributeValue {
    def on: Attribute = KeyValueAttribute(name, "on")
    def off: Attribute = KeyValueAttribute(name, "off")
  }

  lazy val manifest = new StringAttributeBuilder("manifest") with Url

  lazy val srcdoc = new StringAttributeBuilder("srcdoc")

  lazy val sandbox = new StringAttributeBuilder("sandbox") with EmptyAttribute with SpaceSeparatedSet

  lazy val seamless = new StringAttributeBuilder("seamless") with EmptyAttribute

  lazy val usemap = new StringAttributeBuilder("usemap")

  lazy val ismap = new StringAttributeBuilder("ismap") with EmptyAttribute

  lazy val border = new StringAttributeBuilder("border") with CustomAttributeValue {
    def zero: Attribute = KeyValueAttribute(name, "0")
 }

  lazy val maxlength = new StringAttributeBuilder("maxlength") with IntValue

  lazy val readonly = new StringAttributeBuilder("readonly") with EmptyAttribute

  lazy val size = new StringAttributeBuilder("size") with IntValue

  lazy val list = new StringAttributeBuilder("list") with HtmlIdReference

  lazy val pattern = new StringAttributeBuilder("pattern") with RegexpValue

  lazy val required = new StringAttributeBuilder("required") with EmptyAttribute

  lazy val placeholder = new StringAttributeBuilder("placeholder")

  lazy val dirname = new StringAttributeBuilder("dirname")

  lazy val accept = new StringAttributeBuilder("accept") with CommaSeparatedStringsSet

  lazy val multiple = new StringAttributeBuilder("multiple") with EmptyAttribute

  lazy val min = new StringAttributeBuilder("min")

  lazy val max = new StringAttributeBuilder("max")

  lazy val step = new StringAttributeBuilder("step")

  lazy val challenge = new StringAttributeBuilder("challenge")

  lazy val keytype = new StringAttributeBuilder("keytype") with CustomAttributeValue {
    def rsa: Attribute = KeyValueAttribute(name, "rsa")
  }

  lazy val `for` = new StringAttributeBuilder("for") with HtmlIdReference

  lazy val sizes = new StringAttributeBuilder("sizes") with CustomAttributeValue {
    def any: Attribute = KeyValueAttribute(name, "any")
  }

  lazy val content = new StringAttributeBuilder("content")

  lazy val `http-equiv` = new StringAttributeBuilder("http-equiv") with CustomAttributeValue {
    def refresh: Attribute = KeyValueAttribute(name, "refresh")
  }

  lazy val charset = new StringAttributeBuilder("charset")

  lazy val low = new StringAttributeBuilder("low")

  lazy val high = new StringAttributeBuilder("high")

  lazy val optimum = new StringAttributeBuilder("optimum")

  lazy val data = new StringAttributeBuilder("data") with Url

  lazy val start = new StringAttributeBuilder("start") with IntValue

  lazy val reversed = new StringAttributeBuilder("reversed") with EmptyAttribute

  lazy val selected = new StringAttributeBuilder("selected") with EmptyAttribute

  lazy val language = new StringAttributeBuilder("language")

  lazy val defer = new StringAttributeBuilder("defer") with EmptyAttribute

  lazy val async = new StringAttributeBuilder("async") with EmptyAttribute

  lazy val scoped = new StringAttributeBuilder("scoped") with EmptyAttribute

  lazy val colspan = new StringAttributeBuilder("colspan") with IntValue

  lazy val rowspan = new StringAttributeBuilder("rowspan") with IntValue

  lazy val headers = new StringAttributeBuilder("headers") with CustomAttributeValue {
    def :=(value: Set[HtmlId]): Attribute = KeyValueAttribute(name, value.mkString(" "))
  }

  lazy val rows = new StringAttributeBuilder("rows") with IntValue

  lazy val cols = new StringAttributeBuilder("cols") with IntValue

  lazy val wrap = new StringAttributeBuilder("wrap") with CustomAttributeValue {
    def soft: Attribute = KeyValueAttribute(name, "soft")
  }

  lazy val scope = new StringAttributeBuilder("scope") with CustomAttributeValue {
    def row: Attribute = KeyValueAttribute(name, "row")
    def col: Attribute = KeyValueAttribute(name, "col")
    def rowgroup: Attribute = KeyValueAttribute(name, "rowgroup")
    def colgroup: Attribute = KeyValueAttribute(name, "colgroup")
  }

  lazy val kind = new StringAttributeBuilder("kind") with CustomAttributeValue {
    def subtitles: Attribute = KeyValueAttribute(name, "subtitles")
    def captions: Attribute = KeyValueAttribute(name, "captions")
    def descriptions: Attribute = KeyValueAttribute(name, "descriptions")
    def chapters: Attribute = KeyValueAttribute(name, "chapters")
    def metadata: Attribute = KeyValueAttribute(name, "metadata")
  }

  lazy val srclang = new StringAttributeBuilder("srclang")

  lazy val default = new StringAttributeBuilder("default") with EmptyAttribute

  lazy val poster = new StringAttributeBuilder("poster") with Url
}

trait AllAttributes extends GlobalAttributes with TagAttributes
