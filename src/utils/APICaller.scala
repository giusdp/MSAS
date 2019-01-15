package utils

import com.danielasfregola.twitter4s.TwitterStreamingClient
import com.danielasfregola.twitter4s.entities.streaming.CommonStreamingMessage
import com.danielasfregola.twitter4s.entities.{AccessToken, ConsumerToken, Tweet}
import com.danielasfregola.twitter4s.http.clients.streaming.TwitterStream

import scala.concurrent.Future

class APICaller {

  val consumerToken = ConsumerToken(key = "9zAEZKIiGrofrdOdd9b8IYRzv", secret = "iUcWw9lg30UBshHOzXpnkwPJfL32IURcavHrmiwd4IupFncwaA")
  val accessToken = AccessToken(key = "1073564600312971266-3feLqOewNenRpFwxRsfmR1BdWQ6FRq", secret = "gpF7yx6VuiulCri02Un1cN2Ac300YxnqoKJQMt9VHZugy")
  val streamingClient:TwitterStreamingClient = TwitterStreamingClient(consumerToken, accessToken)

  var socket: RedirectSocket = _

  def startTwitterStream(tracking: Seq[String]): Unit = {
    if (socket == null) throw new Exception("APICaller: Connection was not opened (RedirectSocket is null)")
    var ts : Future[TwitterStream] = streamingClient.filterStatuses(tracks = tracking)(processTweet)


    println("APICaller: Connection established with Twitter")
  }

  def openConnection(): Unit = {
    socket = new RedirectSocket()
    socket.start()
  }

  def closeConnection(): Unit = {
    if (socket == null) throw new Exception("APICaller: Connection was not opened (RedirectSocket is null)")
    socket.close()
  }

  def processTweet: PartialFunction[CommonStreamingMessage, Unit] = {
    // case tweet: Tweet => socket.send(tweet.text)
    case tweet: Tweet =>
      if (!tweet.text.contains("https://") && !tweet.text.startsWith("RT") && tweet.lang.getOrElse("it").equals("en"))
        // println(tweet.text)
        socket.send(tweet.text)
  }


}