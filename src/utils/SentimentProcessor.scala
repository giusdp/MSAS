package utils

import scalax.chart.module.ChartFactories
import scalax.chart.module.Charting._
import org.jfree.data.category.DefaultCategoryDataset

import scala.collection.mutable

class SentimentProcessor  {

  def makeSentimentsChart(title: String, sentiments: mutable.Map[String, Int]): Unit = {

    val ds = new DefaultCategoryDataset
    ds.addValue(sentiments("POSITIVE"), "Positives", "Positives")
    ds.addValue(sentiments("NEUTRAL"), "Neutrals", "Neutrals")
    ds.addValue(sentiments("NEGATIVE"), "Negatives", "Negatives")

    val chart = ChartFactories.BarChart(ds)
    chart.title = title
    //chart.show()
    chart.saveAsPNG("chart.png")

  }

 // makeSentimentsChart("titolo", List(3,4,5))

}