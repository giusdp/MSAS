package main

object Main extends App {

  override def main(args: Array[String]): Unit = {
    if (! (args.length == 1)) {
      println("Error: You have to give an hashtag! Exiting now.")
      return
    }
    val sparkTry = new SparkStreaming()
    sparkTry.startStreaming(args.head)
  }
}

