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

    val sparkStreamingContext = new StreamingContext(conf, Seconds(1))
    val apiCaller: APICaller = new APICaller
    val counter = List("POSITIVE" ->0, "NEGATIVE" -> 0, "NEUTRAL" ->0)
    //
    val direct:mutable.Buffer[String] = mutable.Buffer[String]()
    val directPar = sparkStreamingContext.sparkContext.parallelize(direct)
    //
    val sentimentCounter = sparkStreamingContext.sparkContext.parallelize(counter, 3)

    apiCaller.openConnection()

    val streamingSocket = sparkStreamingContext.socketTextStream("localhost", 37644)

    val dstream = streamingSocket.map(tweet => SentimentAnalyzer.mainSentiment(tweet).toString -> tweet)

    dstream.foreachRDD(rdd =>
      rdd.saveAsTextFile("StreamingFiles")
    )
    /*sentiments.foreachRDD(r => r.foreach( c => {
      println(c._1)
      val sentiment = c._2.toString
      println(sentiment)
      sentimentCollection.update(sentiment, sentimentCollection(sentiment) + 1)
    }))*/

    //dstream.saveAsTextFiles("dstreamTwitter", "txt")

    sparkStreamingContext.start()
    apiCaller.startTwitterStream()
    sparkStreamingContext.awaitTerminationOrTimeout(60000)

    //Thread.sleep(20000)
    streamingSocket.stop()
    apiCaller.closeConnection()

    println("Saving files...")



    // sentimentPlotting.makeSentimentsChart("Title 1", sentimentCollection)
    sparkStreamingContext.stop()
    println("Done.")
  }
}


