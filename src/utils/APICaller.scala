package utils

import java.io.{BufferedReader, DataOutputStream, InputStreamReader}
import java.net._

import scalaj.http.Http

class APICaller {

  val baseURL: String = "mastodon.technology"
  val endpoint: String = "/api/v1/streaming"
  val token: String = "9ae77419135fc815ec25c3d16b4ed9ca1cf440fa3119c0fa9f68e4341df5ff46"
  val url: String = baseURL + endpoint + "?access_token=" + token + "&stream=public"
  var socket: Socket = _
  var outToServer: DataOutputStream = _
  var inFromServer: BufferedReader = _

  def manageStream(cleaner: PrettyPrint): Unit = {
    while (true) {
      try {
        val request = Http(baseURL + endpoint)
        request.execute(is => {
          scala.io.Source.fromInputStream(is).getLines.foreach(e => {
            //cleaner.takeText(cleaner.cleanString(e))
            //println(cleaner.cleanString(e))
            outToServer.writeChars(e)
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
    val localPort = 37645
    val port = 80
    socket = new Socket()
    //socket.bind(new InetSocketAddress(localPort))
    socket.connect(new InetSocketAddress("localhost", 80))
    outToServer = new DataOutputStream(socket.getOutputStream)
    //inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream))
  }

  def sendGet(): Unit = {

    outToServer.writeChars(" GET www.google.com HTTP/1.1 \r\n")

    while(inFromServer.read() != -1)
    {
      //continues loop until we won't get int value as a -1
      println(inFromServer.readLine())
    }
  }

  def closeConnection(): Unit = {
    inFromServer.close()
    outToServer.close()
    socket.close()
  }

  def testSocket() : Unit = {



  }
}
