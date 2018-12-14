package utils

import com.danielasfregola.twitter4s.TwitterStreamingClient
import com.danielasfregola.twitter4s.entities.streaming.CommonStreamingMessage
import com.danielasfregola.twitter4s.entities.{AccessToken, ConsumerToken, Tweet}

class APICaller {


  val consumerToken = ConsumerToken(key = "SaobWJCoaLTeQt2ZejDyp8Cqb", secret = "0U35QrWEM8o4ayWqnxUsvQO49DjTg0nkzNfaGHmo0kj3vJvLkT")
  val accessToken = AccessToken(key = "1073564600312971266-d2R4xPHboqjBFq8GXyFolR1aQvM8R8", secret = "XLNzDjZZF1oT7lvyHhvKCa6qp5q2zjVwuIPqfISBhEAc7")
  val streamingClient:TwitterStreamingClient = TwitterStreamingClient(consumerToken, accessToken)
  val tracking: Seq[String] = Seq("twitter")

  var socket: RedirectSocket = _

  def startTwitterStream(): Unit = {
    if (socket == null) throw new Exception("Connection was not opened (RedirectSocket is null)")
    streamingClient.filterStatuses(tracks = tracking)(processTweet)

    while (true) {
      Thread.sleep(500)
    }
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