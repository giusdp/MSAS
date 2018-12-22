package main

import org.apache.spark._
import org.apache.spark.streaming._
import utils.APICaller

import org.apache.log4j.Logger
import org.apache.log4j.Level

class SparkStreaming {

  def startStreaming(): Unit = {

    val conf = new SparkConf().setMaster("local[2]")
    conf.setAppName("TSAS")
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    val ssc = new StreamingContext(conf, Seconds(1))
    val ac: APICaller = new APICaller

    ac.openConnection()

    val lines = ssc.socketTextStream("localhost", 37644)

    val sentiments = lines.map(l => (l, SentimentAnalyzer.mainSentiment(l)))

    sentiments.foreachRDD(r => r.foreach( c => {
      println(c._1)
      println(c._2.toString)
    }))

    ssc.start()
    ssc.awaitTerminationOrTimeout(10000)
    ac.startTwitterStream()


    while (true) {
      Thread.sleep(500)
    }
    ac.closeConnection()

  }
}


