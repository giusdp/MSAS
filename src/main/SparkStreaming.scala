package main

import java.io._

import org.apache.log4j.{Level, Logger}
import org.apache.spark._
import org.apache.spark.streaming._
import utils.{APICaller, SentimentProcessor}

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

    val sparkStreamingContext = new StreamingContext(conf, Seconds(1))
    val apiCaller: APICaller = new APICaller
    val counter = List("POSITIVE" ->0, "NEGATIVE" -> 0, "NEUTRAL" ->0)

    val direct:mutable.Buffer[String] = mutable.Buffer[String]()
    val directPar = sparkStreamingContext.sparkContext.parallelize(direct)
    val r = scala.util.Random

    val sentimentCounter = sparkStreamingContext.sparkContext.parallelize(counter, 3)

    apiCaller.openConnection()

    val streamingSocket = sparkStreamingContext.socketTextStream("localhost", 37644)

    val dstream = streamingSocket.map(tweet => SentimentAnalyzer.getMainSentiment(tweet).toString -> tweet)

    dstream.foreachRDD(rdd => rdd.foreach(c => {
      if (c._1 == "POSITIVE" || c._1 == "NEGATIVE" || c._1 == "NEUTRAL") {
        val file = new File("Sens/" + tracking.head + "/Sentiment" + r.nextInt(1000000000).toString)
        val bw = new BufferedWriter(new FileWriter(file))
        bw.write(c._1)
        bw.close()
      } else println("Empty RDD")
    })
    )

    sparkStreamingContext.start()
    apiCaller.startTwitterStream(tracking)
    sparkStreamingContext.awaitTerminationOrTimeout(30000)

    apiCaller.closeConnection()

    //streamingSocket.stop()
    //sparkStreamingContext.stop()
    sparkStreamingContext.stop(stopSparkContext = true, stopGracefully = true)

    val analysis = new SentimentProcessor
    analysis.analyzeFiles(tracking.head)

    println("Done.")
  }
}


