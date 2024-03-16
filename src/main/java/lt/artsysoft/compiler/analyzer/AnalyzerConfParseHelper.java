package lt.artsysoft.compiler.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lt.artsysoft.compiler.beans.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lt.artsysoft.compiler.exceptions.NoSuchLexemException;

/**
 * Utility class created to help the analyzer have its initial BNF configuration parsed
 * into the data structures
 * @author Anatolij Grigorjev (grigorjev.anatolij@gmail.com)
 *
 */
public class AnalyzerConfParseHelper {
	
	private final static String ELEMENT_ALTERNATIVES = "alternative";
	private final static String ELEMENT_REPEATABLE = "repeat";
	private final static String ATTRIBUTE_ROOT_SYMBOL = "root";
	private final static String ATTRIBUTE_COMES_ONCE = "once";
	private final static String ATTRIBUTE_ERROR_MSG = "error";
	private final static String ELEMENT_LEXEM = "lexem";
	private final static String ATTRIBUTE_LEXEM_VALUE = "value";
	private final static String RULE_REFERENCE_PREFIX = "REF-";
	
	
	private static HashMap<String, Lexem> lexemBank;
	private static StandardRule rootSymbol;
	
	/**
	 * Initialize the syntax parser by providing it with the list of terms it 
	 * will consider {@link Lexem}s - that is, atomic symbols of which all parsed rules
	 * must consist.
	 * @param langLexems the parser's new dictionary
	 */
	public static void init(List<Lexem> langLexems) {
		lexemBank = annotateLexemsList(langLexems);
	}
	
	public static HashMap<String, Lexem> annotateLexemsList(List<Lexem> langLexems) {
		HashMap<String, Lexem> map = new HashMap<String, Lexem>();
		for(Lexem aLexem: langLexems) {
			map.put(aLexem.getUid(), aLexem);
		}
		return map;
	}

	private static HashMap<String, StandardRule> performInitialBNFScan(Document xml) {
		HashMap<String, StandardRule> result = new HashMap<String, StandardRule>();
		
		Node bnfRoot = xml.getFirstChild();
		NodeList ruleNodes = bnfRoot.getChildNodes();
		
		for (int i = 0; i < ruleNodes.getLength(); i++) {
			if (ruleNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				try {
					StandardRule rule = processNewStandardRule(ruleNodes.item(i));
					result.put(rule.getName(), rule);
				} catch (NoSuchLexemException e) {
					System.out.println("A malformed rule referred to a bad lexem! Error: " + e.getMessage());
				}
				
			}
		}
		
		
		return result;
	}


	private static StandardRule processNewStandardRule(Node newRule) throws NoSuchLexemException {
		
		String ruleName = newRule.getNodeName();
		String errorMsg = getErrorMessage(newRule);
		List<Rule> alternatives = new ArrayList<Rule>();
		
		NodeList alternativesNodes = newRule.getChildNodes();
		
		for (int i = 0; i < alternativesNodes.getLength(); i++) {
			Node alternativeNode = alternativesNodes.item(i);
			if (alternativeNode.getNodeType() == Node.ELEMENT_NODE) {
				alternatives.add(createAlternativeRule(alternativeNode, ruleName, errorMsg));
			}
		}
		
		StandardRule finalRule = new StandardRule(ruleName, alternatives, errorMsg);
		if (isRootWord(newRule)) {
			rootSymbol = finalRule;
		}
		return finalRule;
	}

	private static Rule createAlternativeRule(Node alternativeNode, String ruleName, String error) throws NoSuchLexemException {
		
		NodeList chunks = alternativeNode.getChildNodes();
		StandardRule theAlternative = new StandardRule(null, error);
		for (int i = 0; i < chunks.getLength(); i++) {
			Node item = chunks.item(i);
			if (item.getNodeType() == Node.ELEMENT_NODE) {
				theAlternative.getAlternatives().add(parseChildRuleNode((Element) item, ruleName, error));
			}
			
		}
		
		return theAlternative;
	}
	
	private static Rule parseChildRuleNode(Element item, String parentName, String parentError) throws NoSuchLexemException {
		if (item.getTagName().equals(ELEMENT_LEXEM)) {
			return createLexemRule(item);
		}
		if (item.getTagName().equals(ELEMENT_REPEATABLE)) {
			return processNewRepeatRule(item, null, parentError);
		}
		if (item.getTagName().equals(ELEMENT_ALTERNATIVES)) {
			return createAlternativeRule(item, null, parentError);
		}
		
		String name = RULE_REFERENCE_PREFIX + item.getTagName();
		return new StandardRule(name);
	}

	private static RepeatRule processNewRepeatRule(Node repeatRuleNode, String parentName, String parentError) throws NoSuchLexemException {
		NamedNodeMap repeatNodeAttributes = repeatRuleNode.getAttributes();
		boolean repeatsOnce = false;
		if (repeatNodeAttributes.getNamedItem(ATTRIBUTE_COMES_ONCE) != null) {
			repeatsOnce = true;
		}
		List<Rule> alts = new ArrayList<Rule>();
		NodeList chunks = repeatRuleNode.getChildNodes();
		for (int i = 0; i < chunks.getLength(); i++) {
			Node item = chunks.item(i);
			if (item.getNodeType() == Node.ELEMENT_NODE) {
				alts.add(parseChildRuleNode((Element) item, parentName, parentError));
			}
			
		}
		
		return new RepeatRule(repeatsOnce, alts);
	}

	private static StandardRule createLexemRule(Node lexemNode) throws NoSuchLexemException {
		NamedNodeMap lexemNodeAttributes = lexemNode.getAttributes();
		if (lexemNodeAttributes.getNamedItem(ATTRIBUTE_LEXEM_VALUE) == null) {
			throw new NoSuchLexemException(ATTRIBUTE_LEXEM_VALUE + " attribute wasnt provided in lexem xml definition!");
		}
		String lexemUid = lexemNodeAttributes.getNamedItem(ATTRIBUTE_LEXEM_VALUE).getNodeValue();
		if (lexemBank.containsKey(lexemUid)) {
			Lexem theLexem = lexemBank.get(lexemUid);
			List<Rule> listForLexem = new ArrayList<Rule>();
			listForLexem.add(theLexem);
			StandardRule lexemRule = new StandardRule(lexemUid, listForLexem, "ERROR WITH " + lexemUid);
			return lexemRule;
		} else {
			throw new NoSuchLexemException(lexemUid + " was not found in the word bank!");
		}
	}

	private static boolean isRootWord(Node newRule) {
		NamedNodeMap ruleAttributes = newRule.getAttributes();
		
		return ruleAttributes.getNamedItem(ATTRIBUTE_ROOT_SYMBOL) != null;
	}

	private static String getErrorMessage(Node newRule) {
		NamedNodeMap ruleAttributes = newRule.getAttributes();
		Node errorAttribute = ruleAttributes.getNamedItem(ATTRIBUTE_ERROR_MSG);
		
		String message = errorAttribute == null? null : errorAttribute.getNodeValue();
		return message;
	}

	
	/**
	 * Parse the rules of language syntax described as an XML-optimized version of 
	 * eBNF into a Map in the program. Prints the rules while they are a simple parse 
	 * @param BNFXml the file in which the syntax rules are located (expressed as optimized eBNF)
	 * @return a {@link HashMap} of rules in the file, keyed to by their names
	 * @throws IllegalStateException if {@link #init(List)} wasn't called prior to this.
	 */
	public static HashMap<String, StandardRule> parseDocBNF(Document BNFXml) {
		if (lexemBank == null) {
			throw new IllegalStateException("No lexems in bank! init() must be called first!");
		}
		HashMap<String, StandardRule> bnfScan = performInitialBNFScan(BNFXml);
		
//		printLayer1Rules(bnfScan);
		
		selfReferenceBNF(bnfScan);
		
		return bnfScan;
	}
	
	
	
	private static void printLayer1Rules(HashMap<String, StandardRule> bnfScan) {
		System.out.println("The rules: ");
        Object[] justRules = bnfScan.values().toArray();
        for (int i = 0; i < justRules.length; i++) {
        	System.out.println((i + 1) + ") " + justRules[i].toString());
        }
        System.out.println();
		System.out.println();
		
	}

	private static void selfReferenceBNF(HashMap<String, StandardRule> bnfHash) {
		StandardRule[] stdRules = bnfHash.values().toArray(new StandardRule[0]);
		for(StandardRule aStdRule: stdRules) {
			crossReferenceRuleWithAlternatives(aStdRule, bnfHash);
		}
	}

	private static void crossReferenceRuleWithAlternatives(ComplexRule stdRule, HashMap<String, StandardRule> ruleLookup) {
		
		List<Rule> rules = stdRule.getAlternatives();
		
		for(Rule aRule: rules) {
			if (aRule instanceof RepeatRule) {
				crossReferenceRuleWithAlternatives((ComplexRule) aRule, ruleLookup);
			} else if (aRule instanceof StandardRule) {
				String name = ((StandardRule) aRule).getName();
				if (name != null)
				{
					if (name.startsWith(RULE_REFERENCE_PREFIX)) {
						name = name.substring(RULE_REFERENCE_PREFIX.length());
					}
					if (ruleLookup.containsKey(name)) {
						//borrow complete state
//						System.out.println("Rule definition found for " + name + "!");
						StandardRule rule = ruleLookup.get(name);
						StandardRule current = (StandardRule) aRule;
						
						current.setAlternatives(rule.getAlternatives());
						current.setName(rule.getName());
						current.setErrorMessage(rule.getErrorMessage());
					} else {
						if (!name.startsWith("$")) {
							System.err.println("No defeinition found for rule " + name + "!!!");
						}
					}
				} else {
					crossReferenceRuleWithAlternatives((ComplexRule) aRule, ruleLookup);
				}
			}
		}
		
	}
	
	
	/**
	 * Returns what is known to be the starting symbol within the current BNF grammar of
	 * this language. This is calculated during the initial BNF XML parsing and should
	 * be indicated within that document.
	 * @return
	 */
	public static StandardRule getRootSymbol() {
		return rootSymbol;
	}

	public static HashMap<String, Lexem> getLexemBank() {
		return lexemBank;
	}
	
	

}
