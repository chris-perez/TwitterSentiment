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


  val pipeline = new StanfordCoreNLP("MyPropFile.properties")

  /**
   * Gets the sentiment of a string
   * @param tweet string to do sentiment analysis on
   * @return sentiment score
   */
  def getSentiment(tweet : String) = {
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

//more info on the library and implimentation -> https://trainingthemachine.wordpress.com/2014/04/11/sentiment-analysis-using-stanford-corenlp-recursive-deep-learning-models/

  /*
  * The sentiment labels are:
  0 - negative
  1 - somewhat negative
  2 - neutral
  3 - somewhat positive
  4 - positive
  * */


}
