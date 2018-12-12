package utils

import argonaut._ , Argonaut._

class PrettyPrint {

  def cleanString(rawString:String):Option[String] = rawString match {

    case s if s.startsWith("event") => None
    case s if s.startsWith("data") => Some(s.substring(6, s.length))
    case _ => None

  }

  def takeText(filteredString:Option[String]): String = filteredString match {
    case None => null
    case Some(s) =>
      val obj: Option[Json] = Parse.parseOption(s)
      var pretty = obj.orNull
      if (pretty == null) "Parsed string failed: " + s else filteredJson(pretty)
  }

  def filteredJson(retrieved:Json) : String = {
    retrieved.fieldOrNull("content").toString()
  }

}

