package utils

import java.net._

import scalaj.http.Http

class APICaller {

//  val baseURL: String = "https://mastodon.technology"
//  val endpoint: String = "/api/v1/streaming/public"

//  Keywords to track. Phrases of keywords are specified by a comma-separated list.
  val twitterUrl: String = "https://stream.twitter.com/1.1/statuses/filter.json?track=twitter"

//  val token: String = "9ae77419135fc815ec25c3d16b4ed9ca1cf440fa3119c0fa9f68e4341df5ff46"
//  val url: String = baseURL + endpoint + "?access_token=" + token + "&stream=public"

  var socket: RedirectSocket = _

  def manageMastodonStream(cleaner: StatusExtractor): Unit = {
    if (socket == null) throw new Exception("Connection was not opened (RedirectSocket is null)")


    while (true) {
      try {
        // val request = Http(baseURL + endpoint)
        val request = Http(twitterUrl)
        request.execute(is => {
          scala.io.Source.fromInputStream(is).getLines.foreach(e => {
            val status = cleaner.takeText(cleaner.cleanString(e))
            if (status != null) socket.send(status)
          })
        })
      } catch {
        case e: SocketTimeoutException => println(e.getMessage)
        case e: NullPointerException => println(e.getMessage)
      }
    }
    println("Finito")
  }

  def openConnection(): Unit = {
    socket = new RedirectSocket()
    socket.start()
  }

  def closeConnection(): Unit = {
    if (socket == null) throw new Exception("Connection was not opened (RedirectSocket is null)")
    socket.close()
  }

}
