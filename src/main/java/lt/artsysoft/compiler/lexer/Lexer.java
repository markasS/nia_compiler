package lt.artsysoft.compiler.lexer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import lt.artsysoft.compiler.beans.Lexem;
import lt.artsysoft.compiler.beans.classifiers.LexemTypes;
import lt.artsysoft.compiler.exceptions.NoSuchLexemException;

import org.w3c.dom.Document;

/**
 * The main code-reading component that performs the initial scan of it before the language syntax is 
 * analyzed. The purpose of this preprocessor is to determine if the available language library of 
 * words used to write the code is valid. This is accomplished by scanning the entire source for 
 * valid {@link Lexem}s and throwing errors if not all of the text is covered by them. 
 * 
 * @author Anatolij Grigorjev
 * @author Mark Shishlo
 */
public class Lexer {

	private static int maxSymbolLength;
    public static int linesOfCodeCounter = 1;
	private static HashMap<String, Lexem> continuousLexemsDictionary;
	private static HashMap<String, Lexem> reservedSymbolsDictionary;
	private static HashMap<String, Lexem> reservedKeywordsDictionary;
	private static boolean languageHasIntegers;
	private static boolean languageHasDecimals;
    private static String charIdentifier;
    private static String lineCommentIdentifier;
	private static Integer overallParseNextChar;

    public static void init(InputStream xmlFile) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);
        doc.getDocumentElement().normalize();
        languageHasIntegers = LexerConfParseHelper.parseBooleanFlag(doc, LexerConfParseHelper.ATTR_HAS_INTEGERS);
        languageHasDecimals = LexerConfParseHelper.parseBooleanFlag(doc, LexerConfParseHelper.ATTR_HAS_DECIMALS);
        reservedSymbolsDictionary = LexerConfParseHelper.parseSymbolicLexems(doc);
        reservedKeywordsDictionary = LexerConfParseHelper.parseKeywordsLexems(doc);
        continuousLexemsDictionary = LexerConfParseHelper.parseContinuousLexems(doc);
        LexerConfParseHelper.parseNumberLexem(doc);
        maxSymbolLength = LexerConfParseHelper.maxSymbolLength;
        charIdentifier = LexerConfParseHelper.getCharIdentifier();
        lineCommentIdentifier = LexerConfParseHelper.getSingleLineCommentIdentifier();
    }

	public static Lexem[] getSourceLexems(String source) throws Exception {
		
		List<Lexem> results = new ArrayList<Lexem>();
		
		overallParseNextChar = 0;
		
// 		source = stripSourceOfWhiteSpaces(source);
		
		while (overallParseNextChar < source.length()) {
			try
			{
				Lexem newLex = getNextLexem(source.substring(overallParseNextChar));
                if (overallParseNextChar < source.length() && source.charAt(overallParseNextChar) == '\n')
                    linesOfCodeCounter++;
                if (newLex != null && !newLex.getUid().equalsIgnoreCase("$comment")
                		&& !newLex.getUid().equalsIgnoreCase("$line_comment")) {
                    Lexem uniqueLexem = newLex.copyLexem();
                    uniqueLexem.setLineInCode(linesOfCodeCounter);
				    results.add(uniqueLexem);
                }
			} catch (NoSuchLexemException e) {
				e.printStackTrace();
				System.out.print(e.getMessage());
				break;
			}
		}
		
		return results.toArray(new Lexem[0]);
	}

//	private static String stripSourceOfWhiteSpaces(String source) {
//		Pattern whiteSpacers = Pattern.compile("\\s");
//		Matcher match = whiteSpacers.matcher(source);
//		return match.replaceAll("");
//	}

	private static Lexem getNextLexem(String source) throws Exception {
		
		int currenCharIndex = 0;
		char currentChar = source.charAt(currenCharIndex);
		Lexem nextLex;
		//NUMBERS
		nextLex = checkForNumbers(source, currenCharIndex, currentChar);
		if (nextLex != null) {
			overallParseNextChar += nextLex.getValue().length();
			return nextLex;
		} else {
			//KEYWORDS
			nextLex = LexerParseHelper.parseReservedSymbol(reservedSymbolsDictionary, source.substring(currenCharIndex), maxSymbolLength);
			if (nextLex != null) {
				currenCharIndex += nextLex.getValue().length();
				overallParseNextChar += currenCharIndex;
				return nextLex;
			} else {
				
				String name = LexerParseHelper.parseIdentifier(source, currenCharIndex);
                if (name != null) {
                    nextLex = LexerParseHelper.parseReservedKeyword(reservedKeywordsDictionary, name);
                    if (nextLex != null) {
                    	//we got a keyword
                    } else {
                    	nextLex = new Lexem(name, "Identifikatorius", "$id", LexemTypes.IDENTIFIER);
                    }
                    currenCharIndex += nextLex.getValue().length();
                    overallParseNextChar += currenCharIndex;
                	return nextLex;
                    	
                } else {

					//CONTINUOUS SYMBOLS
					currenCharIndex += LexerParseHelper.parseContinuousLexem(continuousLexemsDictionary, source, charIdentifier, lineCommentIdentifier);
					if (LexerParseHelper.getLastParsedOutput() != null) {
						String innerValue = LexerParseHelper.getLastParsedOutput();
						overallParseNextChar += currenCharIndex;
						return LexerConfParseHelper.generateContinuousLexem(pickContinuousLexem(innerValue), innerValue, false);
					} else {
	                        overallParseNextChar++;
		                    return null;
		                }

					}
				}
			}
	}

	private static Lexem pickContinuousLexem(String innerValue) {
       String starts = innerValue.substring(0,1);
       if (continuousLexemsDictionary.containsKey(starts)) {
           return continuousLexemsDictionary.get(starts);
       }
	   return null;
	}

	private static Lexem checkForNumbers(String source, int currenCharIndex,
			char currentChar) throws Exception {
		if (LexerParseHelper.isNumeric(currentChar)) {
			if (languageHasDecimals || languageHasIntegers) {
				Lexem numLex = extractNumberLexem(source, currenCharIndex);
				return numLex;
			} else {
				throw new NoSuchLexemException("ANY_NUMBER");
			}
		} else {
			return null; //NaN
		}
	}

	private static Lexem extractNumberLexem(String source, int currenCharIndex)
			throws Exception {
			currenCharIndex = currenCharIndex + LexerParseHelper.parseNumber(source.substring(currenCharIndex));
			String number = LexerParseHelper.getLastParsedOutput();
			if (number == null || (!languageHasDecimals && number.contains(".")) || (!languageHasIntegers && !number.contains("."))) {
				throw new NoSuchLexemException(number);
			} else {
				return LexerConfParseHelper.generateNumberLexem(number);
			}
	}

	public static List<Lexem> getAllLanguageLexems() throws Exception {
		ArrayList<Lexem> lexems = new ArrayList<>();
		
		lexems.addAll(continuousLexemsDictionary.values());
		lexems.addAll(reservedKeywordsDictionary.values());
		lexems.addAll(reservedSymbolsDictionary.values());
		if (languageHasDecimals || languageHasIntegers) {
			lexems.add(LexerConfParseHelper.generateNumberLexem(""));
		}
		
		return lexems;
	}
}
