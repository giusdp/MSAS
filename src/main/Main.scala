package main

import argonaut._
import Argonaut._
import scalaj.http.{Http, HttpResponse}
import utils.{APICaller, PrettyPrint}

import scala.collection.mutable.Queue

object Main extends App {

  override def main(args: Array[String]): Unit = {
    var sparkTry = new SparkStreaming
    sparkTry.startStreaming()
  }
}
