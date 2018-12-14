package utils

import java.net._

import com.danielasfregola.twitter4s.TwitterStreamingClient
import com.danielasfregola.twitter4s.entities.streaming.CommonStreamingMessage
import com.danielasfregola.twitter4s.entities.{AccessToken, ConsumerToken, Tweet}
import scalaj.http.Http

class APICaller {

//  val baseURL: String = "https://mastodon.technology"
//  val endpoint: String = "/api/v1/streaming/public"

//  Keywords to track. Phrases of keywords are specified by a comma-separated list.
  val twitterUrl: String = "https://stream.twitter.com/1.1/statuses/filter.json?track=twitter"

  val consumerToken = ConsumerToken(key = "SaobWJCoaLTeQt2ZejDyp8Cqb", secret = "0U35QrWEM8o4ayWqnxUsvQO49DjTg0nkzNfaGHmo0kj3vJvLkT")
  val accessToken = AccessToken(key = "1073564600312971266-d2R4xPHboqjBFq8GXyFolR1aQvM8R8", secret = "XLNzDjZZF1oT7lvyHhvKCa6qp5q2zjVwuIPqfISBhEAc7")
  val streamingClient:TwitterStreamingClient = TwitterStreamingClient(consumerToken, accessToken)

  val tracking: Seq[String] = Seq("twitter")

//  val token: String = "9ae77419135fc815ec25c3d16b4ed9ca1cf440fa3119c0fa9f68e4341df5ff46"
//  val url: String = baseURL + endpoint + "?access_token Mastodon=" + token + "&stream=public"

  var socket: RedirectSocket = _

  def manageMastodonStream(cleaner: StatusExtractor): Unit = {
    if (socket == null) throw new Exception("Connection was not opened (RedirectSocket is null)")
    streamingClient.filterStatuses(tracks = tracking)(processTweet)
    /*while (true) {
      try {
        // val request = Http(baseURL + endpoint)
        val request = Http(twitterUrl)
        request.execute(is => {
          scala.io.Source.fromInputStream(is).getLines.foreach(e => {
            println(e)
            /*val status = cleaner.takeText(cleaner.cleanString(e))
            if (status != null) socket.send(status)*/
          })
        })
      } catch {
        case e: SocketTimeoutException => println(e.getMessage)
        case e: NullPointerException => println(e.getMessage)
      }
    }*/
    println("Connection established with Twitter")
  }

  def openConnection(): Unit = {
    socket = new RedirectSocket()
    socket.start()
  }

  def closeConnection(): Unit = {
    if (socket == null) throw new Exception("Connection was not opened (RedirectSocket is null)")
    socket.close()
  }

  def processTweet: PartialFunction[CommonStreamingMessage, Unit] = {
    case tweet: Tweet => socket.send(tweet.text)
  }


}
