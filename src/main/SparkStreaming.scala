package main

import java.util.concurrent.atomic.AtomicInteger

import org.apache.commons.lang.mutable.Mutable
import org.apache.spark._
import org.apache.spark.streaming._
import utils.APICaller
import utils.SentimentProcessor
import org.apache.log4j.Logger
import org.apache.log4j.Level
import java.io._

import scala.collection.mutable

class SparkStreaming {

  def startStreaming(): Unit = {

    val tracking: Seq[String] = Seq("NATO")
    val d = new File("Sens/" + tracking.head)
    if (!(d.exists && d.isDirectory)) {
      if(d.mkdir()) println("Directory " + tracking.head + " created!")
    }

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
    val r = scala.util.Random
    //
    val sentimentCounter = sparkStreamingContext.sparkContext.parallelize(counter, 3)

    apiCaller.openConnection()

    val streamingSocket = sparkStreamingContext.socketTextStream("localhost", 37644)

    val dstream = streamingSocket.map(tweet => SentimentAnalyzer.mainSentiment(tweet).toString -> tweet)

    dstream.foreachRDD(rdd => rdd.foreach(c => {
      if (c._1 == "POSITIVE" || c._1 == "NEGATIVE" || c._1 == "NEUTRAL") {
        val file = new File("Sens/" + tracking.head + "/Sentiment" + r.nextInt(1000000000).toString)
        val bw = new BufferedWriter(new FileWriter(file))
        bw.write(c._1)
        bw.close()
      } else println("Empty RDD")
    })
    )
    /*sentiments.foreachRDD(r => r.foreach( c => {
      println(c._1)
      val sentiment = c._2.toString
      println(sentiment)
      sentimentCollection.update(sentiment, sentimentCollection(sentiment) + 1)
    }))*/

    //dstream.saveAsTextFiles("dstreamTwitter", "txt")

    sparkStreamingContext.start()
    apiCaller.startTwitterStream(tracking)
    sparkStreamingContext.awaitTerminationOrTimeout(30000)

    //Thread.sleep(20000)
    streamingSocket.stop()
    apiCaller.closeConnection()

    // sentimentPlotting.makeSentimentsChart("Title 1", sentimentCollection)
    sparkStreamingContext.stop()
    println("Done.")

    val analysis = new SentimentProcessor
    analysis.analyzeFiles(tracking.head)
  }
}


