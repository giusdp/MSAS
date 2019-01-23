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

  /**
    * start:
    * Crea una socket alla porta effimera 37644 e si mette in ascolto.
    */
  def start(): Unit = {

    serverSocket = new ServerSocket(37644)
    println("Socket created.")

    creationThread = new Thread(new Runnable {
      override def run(): Unit = {
        println("Waiting for clients...")
        clientSocket = serverSocket.accept()
        println("Client accepted.")
        out = new PrintWriter(clientSocket.getOutputStream, true)
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream))
        cAddress = clientSocket.getInetAddress.toString
      }
    })

    creationThread.start()
  }

  /**
    * send:
    * Invia i dati di input attraverso la socket.
    *
    * @param data
    */
  def send(data: String): Unit = {
    if (clientSocket == null) return
    out.println(data)
  }

  /**
    * close:
    * Chiude la socket.
    */
  def close(): Unit = {
    if (!creationThread.isInterrupted) creationThread.interrupt()
    out.close()
    in.close()
    clientSocket.close()
    serverSocket.close()
    println("Socket closed.")
  }
}