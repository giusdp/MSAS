package utils

import scalaj.http.{Http, HttpResponse}

class APICaller {

  val baseURL:String = "https://mastodon.social"
  val endpoint:String = "/api/v1/streaming/local"

  def manageStream() = {
    val response = Http(baseURL + endpoint).execute(is => {
      scala.io.Source.fromInputStream(is).getLines.foreach(println)
    })

  }

}
