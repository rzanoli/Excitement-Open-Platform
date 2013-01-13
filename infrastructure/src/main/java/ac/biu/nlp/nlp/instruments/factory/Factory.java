package ac.biu.nlp.nlp.instruments.factory;

import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Dictionary;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;
import eu.excitementproject.eop.lap.biu.en.ner.NamedEntityRecognizer;
import eu.excitementproject.eop.lap.biu.en.ner.NamedEntityRecognizerException;
import eu.excitementproject.eop.lap.biu.en.parser.BasicParser;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.en.sentencesplit.SentenceSplitter;
import eu.excitementproject.eop.lap.biu.en.sentencesplit.SentenceSplitterException;

public interface Factory
{
	public SentenceSplitter newSentenceSplitter() throws SentenceSplitterException;
	
	public BasicParser newParser() throws ParserRunException;
	
	public NamedEntityRecognizer newNamedEntityRecognizer() throws NamedEntityRecognizerException;
	
	
	
	
	
	/**
	 * Returns a WordNet {@linkplain Dictionary}, either a new one, or
	 * an existing one, if that {@linkplain Dictionary} is thread-safe.
	 * 
	 * @return
	 * @throws WordNetException
	 */
	public Dictionary getWordnetDictionary() throws WordNetException;
	
	
	

}
