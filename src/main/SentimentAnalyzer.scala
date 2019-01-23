package main

import java.util.Properties

import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations
import main.Sentiment.Sentiment

import scala.collection.convert.wrapAll._

object SentimentAnalyzer {

  val props = new Properties()
  props.setProperty("annotators", "tokenize, ssplit, parse, sentiment")
  val pipeline: StanfordCoreNLP = new StanfordCoreNLP(props)

  /**
    * getMainSentiment:
    * Estrae un sentimento (positivo, neutrale, negativo) da una stringa (che sarà un tweet).
    *
    * @param input
    * @return
    */
  def getMainSentiment(input: String): String = Option(input) match {
    case Some(text) => extractSentiment(text).toString
  }

  /**
    * extractSentiment:
    * Estrae il sentimento predominante nella frase.
    * Se la frase genera un'eccezione, questa viene catturata e si crea un sentimento ad hoc (EMPTY) per gestire l'errore.
    *
    * @param text
    * @return
    */
  private def extractSentiment(text: String): Sentiment = {
    try {
      val (_, sentiment) = extractSentiments(text).maxBy { case (sentence, _) => sentence.length }
      sentiment
    }
    catch {
      case _: Exception => Sentiment.toSentiment(-1)
    }
  }

  /**
    * extractSentiments:
    * Si estraggono i sentimenti dal testo e vengono restituiti in una lista di coppie (testo, sentimento)
    *
    * @param text
    * @return
    */
  def extractSentiments(text: String): List[(String, Sentiment)] = {
    val annotation: Annotation = pipeline.process(text)
    val sentences = annotation.get(classOf[CoreAnnotations.SentencesAnnotation])
    sentences
      .map(sentence => (sentence, sentence.get(classOf[SentimentCoreAnnotations.SentimentAnnotatedTree])))
      .map { case (sentence, tree) => (sentence.toString, Sentiment.toSentiment(RNNCoreAnnotations.getPredictedClass(tree))) }
      .toList
  }
}

/**
  * Sentiment:
  * Oggetto che rappresenta un sentimento.
  */
object Sentiment extends Enumeration {
  type Sentiment = Value
  val POSITIVE, NEGATIVE, NEUTRAL, EMPTY = Value

  /**
    * toSentiment:
    * Mapping intero -> sentimento.
    *
    * @param sentiment
    * @return
    */
  def toSentiment(sentiment: Int): Sentiment = sentiment match {
    case x if x == 0 || x == 1 => Sentiment.NEGATIVE
    case 2 => Sentiment.NEUTRAL
    case x if x == 3 || x == 4 => Sentiment.POSITIVE
    case -1 => Sentiment.EMPTY
  }
}