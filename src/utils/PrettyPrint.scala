package utils

object PrettyPrint extends App {

  override def main(args: Array[String]): Unit = {
    var ac:APICaller = new APICaller
    ac.manageStream()
  }
}
