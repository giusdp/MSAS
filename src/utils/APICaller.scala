package utils

import com.danielasfregola.twitter4s.TwitterStreamingClient
import com.danielasfregola.twitter4s.entities.streaming.CommonStreamingMessage
import com.danielasfregola.twitter4s.entities.{AccessToken, ConsumerToken, Tweet}
import com.danielasfregola.twitter4s.http.clients.streaming.TwitterStream

import scala.concurrent.Future

class APICaller {

  val consumerToken = ConsumerToken(key = "9zAEZKIiGrofrdOdd9b8IYRzv", secret = "iUcWw9lg30UBshHOzXpnkwPJfL32IURcavHrmiwd4IupFncwaA")
  val accessToken = AccessToken(key = "1073564600312971266-3feLqOewNenRpFwxRsfmR1BdWQ6FRq", secret = "gpF7yx6VuiulCri02Un1cN2Ac300YxnqoKJQMt9VHZugy")
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