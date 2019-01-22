package main

import java.io._

import org.apache.commons.io.FileUtils
import org.apache.log4j.{Level, Logger}
import org.apache.spark._
import org.apache.spark.streaming._
import utils.{APICaller, SentimentProcessor}

class SparkStreaming {

  /**
    * launchApp:
    * Prepara la cartella per accogliere i sentimenti derivanti dall'analisi dei tweets.
    * Inizializza Spark per lo streaming (SparkConf e StreamingContext).
    * Crea connessione con Twitter e la collega a Spark.
    * Ogni tweet ricevuto nel DStream viene collezionato per la Sentiment Analysis.
    *
    * @param hashtag
    * @param duration
    */
  def launchApp(hashtag : String, duration: Long): Unit = {

    val tracking: Seq[String] = Seq(hashtag)
    val d = new File(tracking.head)
    if (!(d.exists && d.isDirectory)) {
      if(d.mkdir()) println("Directory " + tracking.head + " created!")
    }

    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    println("Starting Spark.")
    val conf = new SparkConf().setMaster("local[2]")
    conf.setAppName("TSA S/S")
    conf.set("spark.testing.memory", "471859200")
    val sparkStreamingContext = new StreamingContext(conf, Seconds(1))
    val apiCaller: APICaller = new APICaller

    val r = scala.util.Random
    apiCaller.openConnection()
    val streamingSocket = sparkStreamingContext.socketTextStream("localhost", 37644)
    val dstream = streamingSocket.map(tweet => SentimentAnalyzer.getMainSentiment(tweet) -> tweet)
    dstream.foreachRDD(rdd => rdd.foreach(c => {
      println(c)
      if (c._1 == "POSITIVE" || c._1 == "NEGATIVE" || c._1 == "NEUTRAL") {
        val file = new File(tracking.head + "/Sentiment" + r.nextInt(1000000000).toString)
        val bw = new BufferedWriter(new FileWriter(file))
        bw.write(c._1)
        bw.close()
      }
    })
    )

    println("Running.")
    sparkStreamingContext.start()
    apiCaller.startTwitterStream(tracking)

    // ****** Running ******
    sparkStreamingContext.awaitTerminationOrTimeout(duration)
    // ****** Closing ******

    println("Stopping Spark.")
    apiCaller.closeConnection()
    streamingSocket.stop()
    sparkStreamingContext.sparkContext.stop()
    sparkStreamingContext.stop(stopSparkContext = false, stopGracefully = true)

    // ****** Sentiment Analysis ******
    println("Analysing tweets.")
    val analysis = new SentimentProcessor
    analysis.analyzeFiles(tracking.head)

    // ****** Finished ******
    FileUtils.deleteDirectory(d)
    println("Finished.")
    sys.exit(0)
  }
}