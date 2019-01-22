package utils

import java.io.File

import scalax.chart.module.ChartFactories
import scalax.chart.module.Charting._
import org.jfree.data.category.DefaultCategoryDataset
import scala.io.Source
import scala.collection.mutable

class SentimentProcessor  {

  /**
    * getListOfFiles:
    * Vengono presi tutti i file che rappresentano i sentimenti e si restituiscono in una lista.
    *
    * @param dir
    * @return
    */
  def getListOfFiles(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }

  /**
    * analyzeFiles:
    * Viene creata una mappa di contatori per ciascun sentimento.
    * Ottenuti i sentimenti dai files, viene aggiornata la mappa e infine si crea un istogramma.
    *
    * @param hashtag
    */
  def analyzeFiles(hashtag: String): Unit = {
    val sentimentMap:mutable.Map[String, Int] = mutable.Map("POSITIVE" -> 0, "NEUTRAL" -> 0, "NEGATIVE" -> 0)
    getListOfFiles(hashtag + "/").foreach(file => {
      for (line <- Source.fromFile(file).getLines) {
        sentimentMap.update(line, sentimentMap(line) + 1)
      }})
    makeSentimentsChart(hashtag, sentimentMap)
  }

  /**
    * makeSentimentsChart:
    * Con una mappa di contatori dei sentimenti, viene creato un istogramma dove il titolo e' l'hashtag utilizzato
    * e ciascuna colonna rappresenta un sentimento.
    *
    * @param hashtag
    * @param sentiments
    */
  def makeSentimentsChart(hashtag: String, sentiments: mutable.Map[String, Int]): Unit = {

    val ds = new DefaultCategoryDataset
    ds.addValue(sentiments("POSITIVE"), "Positives", "Positives")
    ds.addValue(sentiments("NEUTRAL"), "Neutrals", "Neutrals")
    ds.addValue(sentiments("NEGATIVE"), "Negatives", "Negatives")

    val chart = ChartFactories.BarChart(ds)
    chart.title = sentiments.foldLeft(0)(_+_._2) + " tweets about: " + hashtag
    chart.saveAsPNG( "Charts/" + hashtag + ".png")
    println("Chart made.")
  }
}