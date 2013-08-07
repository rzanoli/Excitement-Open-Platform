package eu.excitementproject.eop.distsim.application;

import java.util.List;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.distsim.storage.DefaultSimilarityStorage;
import eu.excitementproject.eop.distsim.storage.RedisBasedStringListBasicMap;
import eu.excitementproject.eop.distsim.storage.SimilarityNotFoundException;
import eu.excitementproject.eop.distsim.storage.SimilarityStorage;
import eu.excitementproject.eop.distsim.storage.SimilarityStorageBasedLexicalResource;

/**
 * A program which demonstrates how to access a given distributional similarity model via Lexical interface, 
 * stored in Redis dbs, given by the hosts and the ports of the element db, and the l2r and r-2l similarity dbs
 * 
 * 
 * @author Meni Adler
 * @since 07/01/2013
 *
 */
public class TestLemmaPosSimilarity2 {
	public static void main(String[] args) throws SimilarityNotFoundException, LexicalResourceException, UnsupportedPosTagStringException {
		
		if (args.length != 4) {
			System.out.println("Usage: eu.excitementproject.eop.distsim.application.TestLemmaPosSimilarity " +
					" <l2r similarity redis host> <l2r similarity redis port>" + 
			        " <r2l similarity redis host> <r2l similarity redis port>");
			System.exit(0);
		}
		
		String l2rRedisHost = args[0];
		int l2rRedisPort = Integer.parseInt(args[1]);
		String r2lRedisHost = args[2];
		int r2lRredisPort = Integer.parseInt(args[3]);
		
		SimilarityStorage similarityStorage = new DefaultSimilarityStorage(
				new RedisBasedStringListBasicMap(l2rRedisHost,l2rRedisPort),
				new RedisBasedStringListBasicMap(r2lRedisHost,r2lRredisPort),				
				"lin-dist-sim","eu.excitementproject.eop.distsim.items.LemmaPosBasedElement");
		
		LexicalResource<? extends RuleInfo> resource = new SimilarityStorageBasedLexicalResource(similarityStorage,20);
		List<? extends LexicalRule<? extends RuleInfo>> similarities = resource.getRulesForRight(
				"affect",null/*new ByCanonicalPartOfSpeech(CanonicalPosTag.V.name())*/);
		
		for (LexicalRule<? extends RuleInfo> similarity : similarities)
			System.out.println("<" + similarity.getLLemma() + "," + similarity.getLPos() + ">" + " --> " + "<" + similarity.getRLemma() + "," + similarity.getRPos() + ">" + ": " + similarity.getConfidence());
	}
}
