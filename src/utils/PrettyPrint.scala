import scalaj.http._
import argonaut._
import Argonaut._
import utils.APICaller


object PrettyPrint extends App {

  override def main(args: Array[String]): Unit = {
    var ac:APICaller = new APICaller
    ac.manageStream()
  }
}
