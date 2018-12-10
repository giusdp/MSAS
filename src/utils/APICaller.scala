package utils

import scalaj.http.{Http, HttpResponse}

class APICaller {

  val baseURL:String = "https://mastodon.social"
  val endpoint:String = "/api/v1/streaming/local"
  val token:String = "9ae77419135fc815ec25c3d16b4ed9ca1cf440fa3119c0fa9f68e4341df5ff46"
  val client_id:String = "170d143404860f0300cec84848602237dd5a858b22859d1a300ed0fb31273d40"
  val secret:String = "a74cf401511d7c631b75a92430e260880799520ca57d304c019a3bc538f5ea89"

  def manageStream() = {
    val request = Http(baseURL + endpoint)
   // request.headers("client_id", )
    request.headers(("Authorization", token))
    request.execute(is => {
      scala.io.Source.fromInputStream(is).getLines.foreach(println)
    })
  }

}
