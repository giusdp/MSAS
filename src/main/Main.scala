package main

object Main extends App {

  override def main(args: Array[String]): Unit = {
    val sparkTry = new SparkStreaming()
    sparkTry.startStreaming()
  }
}
