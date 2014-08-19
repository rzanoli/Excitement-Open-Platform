/**
 * 
 */
package eu.excitementproject.eop.alignmentedas.p1eda;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;

import eu.excitement.type.entailment.Pair;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.ClassifierException;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.DecisionLabelWithDistribution;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.EDAClassifierAbstraction;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.FeatureValue;
import eu.excitementproject.eop.common.EDABasic;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.lap.LAPException;
import static eu.excitementproject.eop.lap.PlatformCASProber.probeCas; 

/**
 * This is a template, abstract class for P1 EDA. 
 * 
 * <H3> What this template class is for? </H3>
 * <P> 
 * It provides basic "flow" for process and training of alignment-based EDA. 
 * The template is an "abstract" class, and supposed to be extended to become 
 * an actual Entailment Decision Algorithm (EDA) class.  
 * 
 * <P> 
 * The following two methods *must* be extended (override) 
 * <UL>
 *   <LI> addAlignment() 
 *   <LI> evaluateAlignments() 
 * </UL>
 * 
 * <P>
 * The following methods are optional to be extended (EDA can work and classify 
 * Entailment, even though they are not overridden). They provide optional capabilities.   
 * 
 * <UL>
 *   <LI> TODO write 
 *   <LI> 
 * </UL>
 * 
 * <P> It is recommended to check actual example codes to see how you make 
 * an alignment-based EDA by extending this abstract class. Please check the following 
 * classes: {@link ClassName} TODO update 
 * 
 * <H3> Classifier capability is embedded within the template </H3> 
 * <P> 
 * Classification capability of this template is provided by using Weka 
 * classifier. Changing the classifier and option within Weka is simple: 
 * TODO change this, and this. 
 * 
 * If you want to use some other classifiers; 
 * TODO check and write this. (e.g. one with Rui's classifier) 
 * 
 * Please see the following document for more info: 
 * TODO fill in URL 
 * 
 * @author Tae-Gil Noh
 *
 */
public abstract class P1EDATemplate implements EDABasic<TEDecisionWithAlignment> {
	
	/**
	 * The constructor for this abstract class. Does nothing but initializing 
	 * two mandatory final fields. They are: 
	 * 
	 * @param classifier Pass one of EDAClassifierAbstraction, such as WekaLogisticRegression, etc. The given classifier will be used for training and classification. 
	 * @param logger Usual log4j logger to be used. 
	 */
	public P1EDATemplate(EDAClassifierAbstraction classifier, Logger logger) 
	{
		this.logger = logger; 
		this.classifier = classifier; 
	}
		
	public TEDecisionWithAlignment process(JCas eopJCas) throws EDAException 
	{
		// Here's the basic step of P1 EDA's process(), outlined by the template.  
		// Note that, the template assumes that you override each of the step-methods. 
		// (although you are free to override any, including this process()). 
		
		logger.info("process() has been called.");
		
		// Step 0. check JCas: a correct one with all needed annotations? 
		checkInputJCas(eopJCas); 
		String pairID = getTEPairID(eopJCas); 
		logger.info("processing pair with ID: " + pairID); 

		// Step 1. add alignments. The method will add various alignment.Link instances
		// Once this step is properly called, the JCas holds alignment.Link data in it. 
		addAlignments(eopJCas); 
				
		// Step 2. (this is an optional step.) The method will interact / output 
		// the added alignment links for debug / analysis purpose. (for Tracer)  
		visualizeAlignments(eopJCas); 
		
		// Step 3. 
		Vector<FeatureValue> featureValues = evaluateAlignments(); 
		
		// Step 4. (this is also an optional step.) 
		//
		visualizeEdaInternals(); 
		
		// Step 5. 
		// Classification. 
		DecisionLabelWithDistribution result = classifyEntailment(featureValues); 
		
		// Finally, return a TEDecision object with CAS (which holds alignments) 
		return new TEDecisionWithAlignment(result.getLabel(), result.getConfidence(), pairID, eopJCas); 
	}
	
	public void initialize(CommonConfig conf)
	{
		// TODO 
	}
	
	public void initialize(String modelPath)
	{
		// TODO 
	}

	public void startTraining(CommonConfig conf)
	{
		// TODO 
	}
	
	public void startTraining(String modelPath, String dirXmiTrainingData)
	{
		// TODO 
	}

	public void shutdown()
	{
		// TODO 
	}
			
	
	/*
	 * Mandatory methods (steps) that should be overridden. 
	 */
	
	public abstract void addAlignments(JCas input); 
		
	public abstract Vector<FeatureValue> evaluateAlignments(); 

	
	/* 
	 *  Optional methods (steps) that can be overridden.    
	 */  
	
	public void visualizeAlignments(JCas CASWithAlignments)
	{
		// Template default is doing nothing. 
	}
	
	public void visualizeEdaInternals() 
	{
		// Template default is doing nothing. 
	}
	
	/*
	 * 	Optional methods (steps) that can be overridden. --- but these 
	 *  methods provide default functionalities. You can extend if you want. 
	 *  But default implementations would also work as well.  
	 */
	
	/**
	 * This method will be used to check input CAS for P1EDA flow. 
	 * As is, it will do the basic check of CAS as EOP CAS input, via PlatformCASProber.probeCAS(). 
	 * 
	 * If you want to do additional checks, such as existence of specific LAP annotations, 
	 * You can extend this class to do the checks. 
	 * 
	 * EXTENSION of this method is optional, and not mandatory.  
	 * 
	 * @param input JCas that is given to your EDA  
	 * @throws EDAException If the given JCas was not well-formed for your EDA, you should throw this exception
	 */
	public void checkInputJCas(JCas input) throws EDAException 
	{
		try {
			probeCas(input, null); 
		}
		catch (LAPException e)
		{
			throw new EDAException("Input CAS is not well-formed CAS as EOP EDA input.", e); 
		}
	}

	public DecisionLabelWithDistribution classifyEntailment(Vector<FeatureValue> fValues) throws EDAException
	{		
		DecisionLabelWithDistribution dl = null; 
		try {
			dl = classifier.classifyInstance(fValues); 
		}
		catch (ClassifierException ce)
		{
			throw new EDAException("underlying classifier throw exception", ce); 
		}
		
		return dl; 
	}
	
	// private utility methods 
	private String getTEPairID(JCas aJCas) throws EDAException
	{
		String id = null; 
		
		// check entailment pair, 
		FSIterator<TOP> iter = aJCas.getJFSIndexRepository().getAllIndexedFS(Pair.type); 
		if (iter.hasNext())
		{
			Pair p = (Pair) iter.next(); 
			id = p.getPairID(); 
			
			if (iter.hasNext())
			{
				logger.warn("This JCas has more than one TE Pairs: This P1EDA template only processes single-pair inputs. Any additional pairs are being ignored, and only the first Pair will be processed.");
			}
			return id; 
		}
		else
		{
			throw new EDAException("Input CAS is not well-formed CAS as EOP EDA input: missing TE pair"); 
		}
	}
	
	// private final fields... 
	private final EDAClassifierAbstraction classifier; 
	protected final Logger logger; 
	
	
	
}
