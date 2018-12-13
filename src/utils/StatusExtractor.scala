package utils

import argonaut._ , Argonaut._

class StatusExtractor {

  def cleanString(rawString:String):Option[String] = rawString match {
    case s if s.startsWith("data") => Some(s.substring(6, s.length))
    case _ => None
  }

  def takeText(filteredString:Option[String]): String = filteredString match {
    case Some(s) =>
      val obj: Option[Json] = Parse.parseOption(s)
      val json = obj.orNull
      if (json == null) "Parsed string failed: " + s else getStatus(json)
    case _ => null
  }

  def getStatus(retrieved:Json) : String = Option(retrieved.fieldOrNull("content")) match {
    case Some(s) => if ( s != null) s.toString() else null
    case _ => null
  }

}

