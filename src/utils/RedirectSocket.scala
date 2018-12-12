package utils

import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.net.{ServerSocket, Socket}

class RedirectSocket {

    var serverSocket: ServerSocket = _
    var clientSocket : Socket = _
    var out : PrintWriter = _
    var in : BufferedReader = _
    var cAddress : String = _

    def start(): Unit = {
      serverSocket = new ServerSocket(37644)
      clientSocket = serverSocket.accept()
      out = new PrintWriter(clientSocket.getOutputStream, true)
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream))
      cAddress = clientSocket.getInetAddress.toString
    }

    def send(data: String) : Unit = {
      out.println(data)
    }

    def close(): Unit = {
      out.close()
      in.close()
      clientSocket.close()
      serverSocket.close()
    }

}
