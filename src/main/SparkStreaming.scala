package main

import org.apache.log4j.{Level, LogManager}
import org.apache.spark._
import org.apache.spark.streaming._
import utils.{APICaller, StatusExtractor}

//import org.apache.spark.streaming.StreamingContext._ // not necessary since Spark 1.3

class SparkStreaming {

  def startStreaming(): Unit = {

    val conf = new SparkConf().setMaster("local[2]").setAppName("MastodonStreaming")
    conf.setAppName("MSAS")
    conf.set("spark.eventLog.enabled", "true")
    conf.set("spark.eventLog.dir", "/tmp/spark-events")
    LogManager.getRootLogger.setLevel(Level.OFF)
    System.setProperty("hadoop.home.dir", "/home/giuseppe")

    val ssc = new StreamingContext(conf, Seconds(1))
    var ac: APICaller = new APICaller

    val cleaner: StatusExtractor = new StatusExtractor
    ac.openConnection()
    //Thread.sleep(1000)

    val lines = ssc.socketTextStream("localhost", 37644)

    val sentiments = lines.map(l => (l, SentimentAnalyzer.mainSentiment(l)))

    sentiments.foreachRDD(r => r.foreach( c => {
      println(c._1)
      println(c._2.toString)
    }))

    //lines.foreachRDD(x => x.foreach(l => println(l)))

    ssc.start()
    ssc.awaitTerminationOrTimeout(10000)

    ac.manageMastodonStream(cleaner)
    ac.closeConnection()

  }
}

