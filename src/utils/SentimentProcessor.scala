package utils

import scalax.chart.module.ChartFactories
import scalax.chart.module.Charting._
import org.jfree.data.category.DefaultCategoryDataset

class SentimentProcessor  {

  def makeSentimentsChart(title: String, sentiments: List[Int]): Unit = {

    val ds = new DefaultCategoryDataset
    ds.addValue(sentiments(0), "Positives", "Positives")
    ds.addValue(sentiments(1), "Neutrals", "Neutrals")
    ds.addValue(sentiments(2), "Negatives", "Negatives")

    val chart = ChartFactories.BarChart(ds)
    chart.title = title
    chart.show()
    //chart.saveAsPNG("chart.png")

  }

  makeSentimentsChart("titolo", List(3,4,5))

}