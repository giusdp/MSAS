package main

import argonaut._, Argonaut._
import scalaj.http.{Http, HttpResponse}

object Main extends App {

  override def main(args: Array[String]): Unit = {

    val response: HttpResponse[String] = Http("https://mastodon.social/api/v1/instance").asString
    val obj:Option[Json] = Parse.parseOption(response.body)
    var pretty = obj.get
    if (obj.get == null) println("No Response") else pretty = obj.get
    println(pretty.asJson.spaces4)
  }
}
