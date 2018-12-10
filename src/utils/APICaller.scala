package utils

import java.net.SocketTimeoutException

import scalaj.http.{Http, HttpResponse}

import scala.collection.mutable.Queue

class APICaller {

  val baseURL: String = "https://mastodon.xyz"
  val endpoint: String = "/api/v1/streaming/public"

  def manageStream(cleaner:PrettyPrint): Unit = {
    while (true) {
      try {
        val request = Http(baseURL + endpoint)
        request.execute(is => {
          scala.io.Source.fromInputStream(is).getLines.foreach(e => {
             cleaner.takeText(cleaner.cleanString(e))
            //println(cleaner.cleanString(e))
          })
        })
      } catch {
        case e: SocketTimeoutException => println(e.getMessage)
        case e: NullPointerException => println(e.getMessage)
      }
    }
    println("Finito")
  }

}
