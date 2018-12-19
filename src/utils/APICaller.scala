package utils

import com.danielasfregola.twitter4s.TwitterStreamingClient
import com.danielasfregola.twitter4s.entities.streaming.CommonStreamingMessage
import com.danielasfregola.twitter4s.entities.{AccessToken, ConsumerToken, Tweet}

class APICaller {

  val consumerToken = ConsumerToken(key = "xQ75rk7rKYBLFOkWzLJRT8Bmq", secret = "svqY6LfIZSh3S7fI0AJ00vPzEvlecTH5zqkjNguuzGH1ZBKJ0X")
  val accessToken = AccessToken(key = "1073564600312971266-EaLhIkkxb7w7gsLcBtLQcFJm3hj4RZ", secret = "kmjoN9Oc50jlBD2BHy3OyGLtCQMdLXYzg5w7WhRPgssAq")
  val streamingClient:TwitterStreamingClient = TwitterStreamingClient(consumerToken, accessToken)
  val tracking: Seq[String] = Seq("twitter")

  var socket: RedirectSocket = _

  def startTwitterStream(): Unit = {
    if (socket == null) throw new Exception("APICaller: Connection was not opened (RedirectSocket is null)")
    streamingClient.filterStatuses(tracks = tracking)(processTweet)

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
    case tweet: Tweet => socket.send(tweet.text)
    //case tweet: Tweet => println(tweet.text)
  }


}