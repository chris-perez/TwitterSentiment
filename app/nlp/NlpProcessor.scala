package nlp

import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations
import edu.stanford.nlp.trees.Tree
import edu.stanford.nlp.util.CoreMap

/**
 * Created by lwald_000 on 12/9/2015.
 */
object NlpProcessor {

  def getSentiment(tweet : String) = {
    val pipeline = new StanfordCoreNLP("MyPropFile.properties")
    var sentimentScore : Int = 0
    if (tweet != null && tweet.length > 0) {
      var longest: Int = 0
      val annotation: Annotation = pipeline.process(tweet)
      val coreMapIterator = annotation.get(classOf[CoreAnnotations.SentencesAnnotation]).iterator()
      var sentence : CoreMap = null
      while (coreMapIterator.hasNext) {
        sentence = coreMapIterator.next()
        val tree: Tree = sentence.get(classOf[SentimentCoreAnnotations.SentimentAnnotatedTree])
        val sentiment: Int = RNNCoreAnnotations.getPredictedClass(tree)
        val partText: String = sentence.toString
        if (partText.length > longest) {
          sentimentScore = sentiment
          longest = partText.length
        }
      }
    }
    sentimentScore
  }

}
