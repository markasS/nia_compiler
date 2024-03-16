package lt.artsysoft.compiler.lexer;

import java.util.HashMap;

import lt.artsysoft.compiler.beans.Lexem;
import lt.artsysoft.compiler.beans.classifiers.LexemTypes;
import lt.artsysoft.compiler.exceptions.NoSuchLexemException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Helper class for parsing Lexer configuration XML file content to data structures suitable for the lexer
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.10.3
 * Time: 22.47
 */
public class LexerConfParseHelper {

    private static final String EL_LEXEM_VALUE = "value";
    private static final String EL_LEXEM_NAME = "name";
    private static final String EL_LEXEM_UID = "uid";
    private static final String EL_SYMBOLS = "symbols";
    private static final String EL_KEYWORDS = "keywords";
    private static final String EL_CONTINUOUS = "continuous";
    private static final String EL_NUMBERS = "numbers";
    public static final String ATTR_HAS_INTEGERS = "hasIntegers";
    public static final String ATTR_HAS_DECIMALS = "hasDecimals";
    public static final String ATTR_HAS_SINGLE_CHAR= "singleCharacter";
    public static final String ATTR_IS_TO_LINE_END = "toLineEnd";
    public static int maxSymbolLength;

    private static String numbersLexemName = "";
    private static String numberLexemUid = "";
    private static String charIdentifier = "";
	private static String lineCommentIdentifier = "";

    /**
     *  Parses lexer configuration DOM document to Map of lexems represented
     *  by pairs of symbol strings and {@link Lexem} objects
     *
     * @param lexerConfXML - DOM document, which contains lexer configuration
     * @return map that contains lexem symbol and {@link Lexem} object pairs
     * @throws Exception
     */
    public static HashMap<String, Lexem> parseSymbolicLexems(Document lexerConfXML) throws Exception{
    	maxSymbolLength = 0;
        HashMap<String, Lexem> symbolicLexems = new HashMap<String, Lexem>();
        NodeList symbolsLexList = getLexemsNodeList(lexerConfXML, EL_SYMBOLS);
        for (int i=0; i < symbolsLexList.getLength(); i++)  {
             Node symbolicLexemNode = symbolsLexList.item(i);
             if (symbolicLexemNode.getNodeType() ==  Node.ELEMENT_NODE) {
                 Lexem lexem = generateLexem(symbolicLexemNode, LexemTypes.RESERVED_SYMBOL);
                 symbolicLexems.put(lexem.getValue(),lexem);
                 maxSymbolLength = maxSymbolLength < lexem.getValue().length() ? lexem.getValue().length() : maxSymbolLength;
             }
        }
        return symbolicLexems;
    }

    /**
     * Parses lexer configuration DOM document to List of continuous lexems
     * @param lexerConfXML - DOM document, which contains lexer configuration
     * @return  - list of continuous lexems
     * @throws Exception
     */
    public static HashMap<String, Lexem> parseContinuousLexems(Document lexerConfXML) throws Exception{
        HashMap<String, Lexem> continuousLexems = new HashMap<String, Lexem>();
        NodeList continuousLexList = getLexemsNodeList(lexerConfXML, EL_CONTINUOUS);
        for (int i=0; i < continuousLexList.getLength(); i++)  {
            Node continuousLexNode = continuousLexList.item(i);
            if (continuousLexNode.getNodeType() ==  Node.ELEMENT_NODE) {
                if (continuousLexNode.hasAttributes()) {
                	NodeList nodeElements = continuousLexNode.getChildNodes();
                	String identifier = "";
                    for (int j = 0; j < nodeElements.getLength(); j++)  {
                        String nodeName = nodeElements.item(j).getNodeName();
                        if (nodeName.equals(EL_LEXEM_VALUE))
                            identifier = new String(nodeElements.item(j).getFirstChild().getNodeValue());
                    }
                	if (continuousLexNode.getAttributes().getNamedItem(ATTR_HAS_SINGLE_CHAR) != null) {
                		charIdentifier = identifier;
                	} else {
                		lineCommentIdentifier = identifier;
                	}
                }
                Lexem lexem = generateLexem(continuousLexNode, LexemTypes.CONTINUOUS_LEXEM);
                continuousLexems.put(lexem.getValue(),lexem);
            }
        }
        return continuousLexems;
    }

    public static String getCharIdentifier() {
        return charIdentifier;
    }
    public static String getSingleLineCommentIdentifier() {
    	return lineCommentIdentifier ;
    }

    /**
     *  Parses lexer configuration DOM document to
     *  Map of nested states, where each lexem states refers to some character of the lexem.      *
     * @param lexerConfXML DOM document, which contains lexer configuration
     * @return HashMap which contains chain of HashMaps, which ends with the Lexem object
     * @throws Exception
     */
    public static HashMap<String, Lexem> parseKeywordsLexems(Document lexerConfXML) throws Exception{
        HashMap<String, Lexem> keywordsLexems = new HashMap<String, Lexem>();
        NodeList keywordsLexList = getLexemsNodeList(lexerConfXML, EL_KEYWORDS);
        for (int i=0; i < keywordsLexList.getLength(); i++)  {
            Node symbolicLexemNode = keywordsLexList.item(i);
            if (symbolicLexemNode.getNodeType() ==  Node.ELEMENT_NODE) {
                Lexem lexem = generateLexem(symbolicLexemNode, LexemTypes.RESERVED_KEYWORD);
                keywordsLexems.put(lexem.getValue(),lexem);
            }
        }
        return keywordsLexems;
    }

    /**
     *  Parses from lexer configuration DOM document numbers lexem name and uid
     * @param lexerConfXML DOM document, which contains lexer configuration
     * @throws Exception
     */
    public static void parseNumberLexem (Document lexerConfXML) throws Exception {
        NodeList numbersLexList = getLexemsNodeList(lexerConfXML, EL_NUMBERS);
        for (int i=0; i < numbersLexList.getLength(); i++)  {
            Node numberLexemNode = numbersLexList.item(i);
            if (numberLexemNode.getNodeType() ==  Node.ELEMENT_NODE) {
                NodeList nodeElements = numberLexemNode.getChildNodes();
                for (int j=0; j < nodeElements.getLength(); j++)  {
                    String nodeName = nodeElements.item(j).getNodeName();
                    if (nodeName.equals(EL_LEXEM_NAME))
                        numbersLexemName = new String(nodeElements.item(j).getFirstChild().getNodeValue());
                    else if (nodeName.equals(EL_LEXEM_UID))
                        numberLexemUid =new String(nodeElements.item(j).getFirstChild().getNodeValue());
                }
            }
        }
    }

    public static boolean parseBooleanFlag(Document lexerConfXML, String flagName) throws NoSuchLexemException {
        NodeList lexRootList = lexerConfXML.getElementsByTagName(EL_NUMBERS);
        if (lexRootList.getLength() == 1)  {
            Node lexRoot = lexRootList.item(0);
            String hasIntegersStr = lexRoot.getAttributes().getNamedItem(flagName).getNodeValue();
            return Boolean.parseBoolean(hasIntegersStr);
        }
        else throw new NoSuchLexemException(EL_NUMBERS);
    }

    public static Lexem generateNumberLexem(String value) throws Exception{
        if (numberLexemUid.isEmpty() || numbersLexemName.isEmpty()) {
            throw new Exception("numberLexemUid or numbersLexemName are not defined");
        }
        else return new Lexem(value,numbersLexemName,numberLexemUid,LexemTypes.NUMBER);
    }

    public static Lexem generateContinuousLexem(Lexem continuousLexem, String value) throws Exception{
        return generateContinuousLexem(continuousLexem, value, true);
    }
    
    public static Lexem generateContinuousLexem(Lexem continuousLexem, String value, boolean shouldWrap) throws Exception{
    	String generatedValue = "";
    	if (shouldWrap) {
    		String startCharacter = continuousLexem.getValue().substring(0,1);
        	generatedValue = startCharacter + value + startCharacter;
    	} else {
    		generatedValue = value;
    	}
        return new Lexem(generatedValue, continuousLexem.getName(), continuousLexem.getUid(), LexemTypes.CONTINUOUS_LEXEM);
    }

    /**
     * Return the list of lexem nodes for defined configuration XML and xml element lexem type
     *
     * @param lexerConfXML
     * @param lexemType
     * @return
     * @throws NoSuchLexemException if no lexems with defined type were found in confXML
     */
    private static NodeList getLexemsNodeList(Document lexerConfXML, String lexemType) throws NoSuchLexemException{
        NodeList lexRootList = lexerConfXML.getElementsByTagName(lexemType);
        if (lexRootList.getLength() == 1)  {
            Node lexRoot = lexRootList.item(0);
            NodeList lexList = lexRoot.getChildNodes();
            return  lexList;
        }
        else throw new NoSuchLexemException(lexemType);
    }

    /**
     * Creates {@link Lexem} object with defined type from DOM node.
     * @param node nods from DOM that contains all necessary Lexem info
     * @param lexemType type of lexem
     * @return lexem with filled type, name, value and uid attributes
     */
    private static Lexem generateLexem(Node node, LexemTypes lexemType) {
        Lexem lexem = new Lexem();
        lexem.setType(lexemType);
        NodeList nodeElements = node.getChildNodes();
        for (int i=0; i < nodeElements.getLength(); i++)  {
            String nodeName = nodeElements.item(i).getNodeName();
            if (nodeName.equals(EL_LEXEM_VALUE))
                lexem.setValue(new String(nodeElements.item(i).getFirstChild().getNodeValue()));
            else if (nodeName.equals(EL_LEXEM_NAME))
                lexem.setName(new String(nodeElements.item(i).getFirstChild().getNodeValue()));
            else if (nodeName.equals(EL_LEXEM_UID))
                lexem.setUid(new String(nodeElements.item(i).getFirstChild().getNodeValue()));
        }
        return lexem;
    }


}
