package utils

import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.net.{ServerSocket, Socket}

class RedirectSocket {

  var serverSocket: ServerSocket = _
  var clientSocket: Socket = _
  var out: PrintWriter = _
  var in: BufferedReader = _
  var cAddress: String = _

  var creationThread: Thread = _

  def start(): Unit = {

    serverSocket = new ServerSocket(37644)

    creationThread = new Thread(() => {
      println("RedirectSocket: Thread lanciato in parallelo. In attesa di clients...")
      clientSocket = serverSocket.accept()
      println("RedirectSocket: Client accettato.")
      out = new PrintWriter(clientSocket.getOutputStream, true)
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream))
      cAddress = clientSocket.getInetAddress.toString
    })

    creationThread.start()
  }

  def send(data: String): Unit = {
    if (clientSocket == null) return
    out.println(data)
  }

  def close(): Unit = {
    if (!creationThread.isInterrupted) creationThread.interrupt()
    out.close()
    in.close()
    clientSocket.close()
    serverSocket.close()
  }

}