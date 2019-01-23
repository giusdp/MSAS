package utils

import com.danielasfregola.twitter4s.TwitterStreamingClient
import com.danielasfregola.twitter4s.entities.streaming.CommonStreamingMessage
import com.danielasfregola.twitter4s.entities.{AccessToken, ConsumerToken, Tweet}
import com.danielasfregola.twitter4s.http.clients.streaming.TwitterStream

import scala.concurrent.Future
import scala.io.Source

class APICaller {

  var streamingClient:TwitterStreamingClient = _
  var ts : Future[TwitterStream] = _
  var socket: RedirectSocket = _

  /**
    * startTwitterStream:
    * Apre connessione streaming con Twitter filtrando i tweets rispetto all'hashtag fornito,
    * utilizzando le credenziali dell'applicazione (registrata su Twitter).
    *
    * @param tracking
    */
  def startTwitterStream(tracking: Seq[String]): Unit = {
    if (socket == null) throw new Exception("APICaller: Connection was not opened (RedirectSocket is null)")

    val keys = Source.fromFile("SECRET.txt").getLines.toList
    val consumerToken = ConsumerToken(key = keys(0), secret = keys(1))
    val accessToken = AccessToken(key = keys(2), secret = keys(3))
    streamingClient = TwitterStreamingClient(consumerToken, accessToken)
    ts = streamingClient.filterStatuses(tracks = tracking)(sendTweet)

    println("Connection established with Twitter.")
  }

  /**
    * openConnection:
    * Crea una socket per mettere a disposizione i tweets a Spark.
    */
  def openConnection(): Unit = {
    socket = new RedirectSocket()
    socket.start()
  }

  /**
    * closeConnection:
    * Chiude la socket per Spark e la connessione a Twitter.
    */
  def closeConnection(): Unit = {
    if (socket == null) throw new Exception("Connection was not opened (RedirectSocket is null)")
    socket.close()
    ts.value.get.get.close()
    println("Twitter stream closed.")
  }

  /**
    * sendTweet:
    *  Inoltra i tweets alla socket che rispettano le seguenti condizioni:
    *  - Non contengono links;
    *  - Non sono retweets (non iniziano con RT);
    *  - Sono in lingua inglese.
    *
    * @return
    */
  def sendTweet: PartialFunction[CommonStreamingMessage, Unit] = {
    case tweet: Tweet =>
      if (!tweet.text.contains("https://") && !tweet.text.startsWith("RT") && tweet.lang.getOrElse("null").equals("en"))
        socket.send(tweet.text)
  }
}