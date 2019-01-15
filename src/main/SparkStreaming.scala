package main

import java.util.concurrent.atomic.AtomicInteger

import org.apache.commons.lang.mutable.Mutable
import org.apache.spark._
import org.apache.spark.streaming._
import utils.APICaller
import utils.SentimentProcessor
import org.apache.log4j.Logger
import org.apache.log4j.Level

import scala.collection.mutable

class SparkStreaming {

  def startStreaming(): Unit = {

    val conf = new SparkConf().setMaster("local[2]")
    conf.setAppName("TSAS")
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    // val sentimentPlotting = new SentimentProcessor
    // val sentimentCollection:mutable.Map[String, Int] = scala.collection.mutable.Map("POSITIVE" -> 0, "NEGATIVE" -> 0, "NEUTRAL" -> 0)

    val ssc = new StreamingContext(conf, Seconds(1))
    val ac: APICaller = new APICaller
    val counter = List("POSITIVE" ->0, "NEGATIVE" -> 0, "NEUTRAL" ->0)
    //
    val direct:mutable.Buffer[String] = mutable.Buffer[String]()
    val directPar = ssc.sparkContext.parallelize(direct)
    //
    val sentimentCounter = ssc.sparkContext.parallelize(counter, 3)

    ac.openConnection()

    val streamingSocket = ssc.socketTextStream("localhost", 37644)

    val dstream = streamingSocket.map(tweet => SentimentAnalyzer.mainSentiment(tweet).toString -> tweet)

    dstream.foreachRDD(rdd => rdd.foreach(c => {
        directPar + c._1
        // println(direct)
    }))
    /*sentiments.foreachRDD(r => r.foreach( c => {
      println(c._1)
      val sentiment = c._2.toString
      println(sentiment)
      sentimentCollection.update(sentiment, sentimentCollection(sentiment) + 1)
    }))*/

    ssc.start()
    ssc.awaitTermination
    ac.startTwitterStream()



    Thread.sleep(20000)
    ssc.stop()
    ac.closeConnection()
    println("elem")
    directPar.foreach(x => println(x))


    // sentimentPlotting.makeSentimentsChart("Title 1", sentimentCollection)
  }
}


