package utils

import scalaj.http.{Http, HttpResponse}

class APICaller {

  val baseURL:String = "https://mastodon.social"
  val endpoint:String = "/api/v1/streaming/local"
  val token:String = "829f7ee5d29f3cc15ef545b4bdfb0ea3dabcce01038c3f1ca5b36703749f883e"

  def manageStream() = {
    val request = Http(baseURL + endpoint)
    request.headers(("bearer_token", token))
    request.execute(is => {
      scala.io.Source.fromInputStream(is).getLines.foreach(println)
    })
  }

}
