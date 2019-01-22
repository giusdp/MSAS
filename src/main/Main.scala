package main

import com.typesafe.config.ConfigFactory

object Main extends App {

  /**
    * Main:
    * L'applicazione prende in input 2 argomenti:
    * - L'hashtag per filtrare i tweets da ricevere in streaming
    * - La durata dell'ascolto dello streaming in secondi, minuti o ore (s, m, h).
    * Carica il file di configurazione (application.conf) e lancia l'applicazione.
    *
    * @param args
    */
  override def main(args: Array[String]): Unit = {

    if (! (args.length == 2)) {
      println("Error: You have to give a hashtag and the time as #{s,m,h}. Exiting now.")
      return
    }

    var time = args.last.dropRight(1).toLong
    val unit: String = args.last.drop(args.last.length - 1)

    unit match {
      case "s" => time *= 1000
      case "m" => time *= 60000
      case "h" => time *= 3600000
      case _ =>
        println("Error: You have given the time in a wrong format. It must be #{s,m,h}. Exiting now.")
        return
    }

    println("Running with hashtag " + args.head + " for "+ args.last.dropRight(1) + " "+ unit + ".")

    ConfigFactory.load("application.conf")
    val application = new SparkStreaming()
    application.launchApp(args.head, time)
  }
}

