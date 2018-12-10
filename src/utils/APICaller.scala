package utils

import scalaj.http.{Http, HttpResponse}

class APICaller {

  val baseURL: String = "https://mastodon.technology"
  val endpoint: String = "/api/v1/streaming/public"

  def manageStream(): HttpResponse[Unit] = {
    val request = Http(baseURL + endpoint)
    request.execute(is => {
      scala.io.Source.fromInputStream(is).getLines.foreach(println)
    })
  }

}
