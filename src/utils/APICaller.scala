package utils

import java.net.SocketTimeoutException

import scalaj.http.{Http, HttpResponse}

class APICaller {

  val baseURL: String = "https://mastodon.xyz"
  val endpoint: String = "/api/v1/streaming/public"

  def manageStream(): Unit = {
    while (true) {
      try {
        val request = Http(baseURL + endpoint)
        request.execute(is => {
          scala.io.Source.fromInputStream(is).getLines.foreach(println)
        })
      } catch {
        case e: SocketTimeoutException => println(e.getMessage)
        case e: NullPointerException => println(e.getMessage)
      }
    }
    println("Finito")
  }

}
