package main

import org.apache.log4j.{LogManager, Level}
import org.apache.spark._
import utils.{APICaller, PrettyPrint}
import org.apache.spark.streaming._

//import org.apache.spark.streaming.StreamingContext._ // not necessary since Spark 1.3

class SparkStreaming {

  def startStreaming() = {

    val conf = new SparkConf().setMaster("local[2]").setAppName("MastodonStreaming")
    conf.setAppName("MSAS")
    conf.set("spark.eventLog.enabled", "true")
    conf.set("spark.eventLog.dir", "/tmp/spark-events")
    LogManager.getRootLogger.setLevel(Level.ALL)
    System.setProperty("hadoop.home.dir", "/home/alessandro")

    val ssc = new StreamingContext(conf, Seconds(1))
    val lines = ssc.socketTextStream("localhost", 37644)

    var ac: APICaller = new APICaller
    val cleaner: PrettyPrint = new PrettyPrint
    ac.openConnection()
    ac.manageStream(cleaner)
    ac.closeConnection()

  }
}


