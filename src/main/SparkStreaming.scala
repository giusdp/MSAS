package main

import java.io._

import com.typesafe.config.ConfigFactory
import org.apache.commons.io.FileUtils
import org.apache.log4j.{Level, Logger}
import org.apache.spark._
import org.apache.spark.streaming._
import utils.{APICaller, SentimentProcessor}

class SparkStreaming {

  def startStreaming(hashtag : String): Unit = {

    val tracking: Seq[String] = Seq(hashtag)
    val d = new File(tracking.head)
    if (!(d.exists && d.isDirectory)) {
      if(d.mkdir()) println("Directory " + tracking.head + " created!")
    }

    val conf = new SparkConf().setMaster("local[2]")
    conf.setAppName("TSAS")
    conf.set("spark.testing.memory", "471859200")
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    val sparkStreamingContext = new StreamingContext(conf, Seconds(1))

    val config = ConfigFactory.load("twitter4s-streaming")
    val apiCaller: APICaller = new APICaller

    val r = scala.util.Random

    apiCaller.openConnection()

    val streamingSocket = sparkStreamingContext.socketTextStream("localhost", 37644)

    val dstream = streamingSocket.map(tweet => {
      var s = SentimentAnalyzer.getMainSentiment(tweet)
      s -> tweet
    })

    dstream.foreachRDD(rdd => rdd.foreach(c => {
      if (c._1 == "POSITIVE" || c._1 == "NEGATIVE" || c._1 == "NEUTRAL") {
        val file = new File(tracking.head + "/Sentiment" + r.nextInt(1000000000).toString)
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

    println("Stopping spark.")
    streamingSocket.stop()
    sparkStreamingContext.sparkContext.stop()
    sparkStreamingContext.stop(stopSparkContext = false, stopGracefully = true)

    println("Spark stopped. Analysing sentiments.")
    val analysis = new SentimentProcessor
    analysis.analyzeFiles(tracking.head)

    println("Chart made.")

    println("Deleting files.")

    FileUtils.deleteDirectory(d)
    sys.exit(0)
  }
}


