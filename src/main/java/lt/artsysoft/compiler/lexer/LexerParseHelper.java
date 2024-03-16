package lt.artsysoft.compiler.lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lt.artsysoft.compiler.beans.Lexem;

/**
 * A (mostly) stateless helper to encapsulate parsing-related logic from the main {@link Lexer} class.
 * @author Anatolij Grigorjev (grigorjev.anatolij@gmail.com)
 *
 */
public class LexerParseHelper {
	
	private static String lastParseOutput;
	
	/**
	 * Checks if the specified char is a number literal, i.e is in the range 0..9
	 * @param symbol the char to check
	 * @return <code>true</code> if the specified symbols is a number in ASCII, <code>false</code> otherwise
	 */
	public static boolean isNumeric(char symbol) {
		return (symbol >= '0' && symbol <= '9');
	}
	
	/**
	 * Parses a number form the start of this sequence to a return point in it
	 * @param sequence sequence to rip a number out of
	 * @return the index at which a character was no longer read, relative to the start of the input sequence
	 */
	public static int parseNumber(String sequence) {
		int parseIndex = 0;
		StringBuilder buffer = new StringBuilder();
		parseIndex = readNums(sequence, parseIndex, buffer);
		if (parseIndex < sequence.length() && sequence.charAt(parseIndex) == '.') {
			parseIndex++;
			int postDot = parseIndex;
			buffer.append('.');
			parseIndex = readNums(sequence, parseIndex, buffer);
			if (postDot == parseIndex) {
				parseIndex = 0;
			}
		}
		if (parseIndex > 0) {
			lastParseOutput = buffer.toString();
		} else {
			lastParseOutput = null;
		}
		
		return parseIndex;
	}
	
	
	public static Lexem parseReservedSymbol(HashMap<String, Lexem> dictionary, String source, int maxSymbolLength) {
		int currentCharIndex = 0;
		int currSymbolLength = maxSymbolLength;
		
		for (currSymbolLength = maxSymbolLength; currSymbolLength > 0; currSymbolLength--) {
			if (source.length() >= currSymbolLength) {
				String symbol = source.substring(currentCharIndex, currSymbolLength);
				if (dictionary.containsKey(symbol)){
					return dictionary.get(symbol);
				}
			}
		}
		
		return null;
		
	}
	
	/**
	 * Parses a language keyword for the Lexer. 
	 * @param dictionary the dictionary of reserved keywords to check source against
	 * @param word the source word to perform checks on
	 * @return Parsed Lexem if all went well, <code>null</code> otherwise
	 */
	public static Lexem parseReservedKeyword(HashMap<String, Lexem> dictionary, String word) {
		if (dictionary.containsKey(word)) {
			return dictionary.get(word);
		} else {
			return null;
		}
		
		
	}
	

	private static int readNums(String sequence, int parseIndex,
			StringBuilder buffer) {
		while (parseIndex < sequence.length() && isNumeric(sequence.charAt(parseIndex))) {
			buffer.append(sequence.charAt(parseIndex));
			parseIndex++;
		}
		return parseIndex;
	}
	
	
	

	public static String getLastParsedOutput() {
		return lastParseOutput;
	}
	
	/**
	 * Parses a single character out of the input. It is assumed that the first symbol
	 * in the input sequence is the character start/end denominator
	 * @param sequence the sequence to rip a character out of
	 * @return the index at which symbol was no longer read, relative to the start of the input sequence
	 */
	public static int parseCharacter(String sequence) {
		int parseIndex = 0;
		char charSymbol = sequence.charAt(parseIndex);
		StringBuilder buffer = new StringBuilder();
		buffer.append(sequence.charAt(parseIndex));
		parseIndex++;
		buffer.append(sequence.charAt(parseIndex));
		if (sequence.charAt(parseIndex) == charSymbol) {
			lastParseOutput = ""; //empty symbol
			return parseIndex + 1;
		} else {
			parseIndex++;
			buffer.append(sequence.charAt(parseIndex));
			if (sequence.charAt(parseIndex) == charSymbol) {
				lastParseOutput = buffer.toString();
				return parseIndex + 1;
			} else {
				lastParseOutput = null;
				return parseIndex;
			}
		}
		
	}
	
	
	
	public static int parseContinuousLexem(HashMap<String, Lexem> dictionary, String source, String limitedOne, String singleLine) {
		
		int index = 0;
		String openCloseSymbol = source.charAt(index) + "";
		if (limitedOne.startsWith(openCloseSymbol)) {
			//SEEMS TO BE START OF LIMITED CHAR
			while (limitedOne.length() > openCloseSymbol.length()) {
				index++;
				openCloseSymbol += source.charAt(index);
			}
			if (openCloseSymbol.equals(limitedOne)) {
				//PARSE LIMITED CHAR
				StringBuffer limitedBuffer = new StringBuffer(openCloseSymbol);
				index++;
				limitedBuffer.append(source.charAt(index));
				index++;
				if (source.substring(index).startsWith(openCloseSymbol)) {
					//SUCCESS
					limitedBuffer.append(openCloseSymbol);
					lastParseOutput = limitedBuffer.toString();
					index += openCloseSymbol.length();
					return index;
				} else {
					//FAILURE
					lastParseOutput = null;
					return 0;
				}
			} else {
				//ONLY SEEMED TO BE START OF LIMITED
				index = 0;
				index = parseLongContinuous(new ArrayList<Lexem>(dictionary.values()), source, index);
				return index;
			}
		} else {
			//DOESN'T SEEM TO BE START OF LIMITED
			if (singleLine.startsWith(openCloseSymbol)) {
				//SEEMS TO BE START OF SINGLE-LINE COMMENT
				while (singleLine.length() > openCloseSymbol.length()) {
					index++;
					openCloseSymbol += source.charAt(index);
				}
				if (openCloseSymbol.equals(singleLine)) {
					//PARSE SINGLE-LINE COMMENT
					StringBuffer singleLineBuffer = new StringBuffer(openCloseSymbol);
					String endOfLineString = "";
					while (!endOfLineString.endsWith(System.lineSeparator()))
					{
						index++;
						singleLineBuffer.append(source.charAt(index));
						endOfLineString += source.charAt(index);
						
					}
					
					lastParseOutput = singleLineBuffer.toString();
					return index;
				} else {
					//ONLY SEEMED TO BE START OF SINGLE-LINE COMMENT
					index = 0;
					index = parseLongContinuous(new ArrayList<Lexem>(dictionary.values()), source, index);
					return index;
				}
			} else {
				//DOESN'T SEEM TO BE START OF SINGLE-LINE COMMENT
				index = parseLongContinuous(new ArrayList<Lexem>(dictionary.values()), source, index);
				return index;
			}
		}
	
	}
	
	
	private static int parseLongContinuous(List<Lexem> dictionary, String source, int startIndex) {
		String analysedFragment = source.substring(startIndex);
		
		for(Lexem lex : dictionary) {
			String halfValue = lex.getValue();
			if (analysedFragment.startsWith(halfValue)) {
				int currIndex = halfValue.length();
				StringBuilder buffer = new StringBuilder(halfValue);
				boolean lexemOver = false;
				while(currIndex < analysedFragment.length() && !lexemOver) {
					buffer.append(analysedFragment.charAt(currIndex));
					if (halfValue.startsWith(analysedFragment.charAt(currIndex) + "")) {
						lexemOver = true;
						for (int i = currIndex; i < halfValue.length() + currIndex; i++) {
							lexemOver = lexemOver && (analysedFragment.charAt(i) == halfValue.charAt(i - currIndex));
						}
						if (lexemOver) {
							buffer.deleteCharAt(buffer.length() - 1);
							buffer.append(halfValue);
							lastParseOutput = buffer.toString();
							return currIndex + halfValue.length();
						}
					}
					currIndex++;
				}
				if (!lexemOver) {
					lastParseOutput = null;
					return 0;
				}
			}
		}
		lastParseOutput = null;
		return 0;
	}


    public static String parseIdentifier(String source, int currentCharIndex) {
        char currentSymbol = source.charAt(currentCharIndex);
        if (isWordCharacter(currentSymbol)) {
            StringBuilder builder = new StringBuilder(currentSymbol + "");
            currentCharIndex++;
            currentSymbol = source.charAt(currentCharIndex);
            while(isNumeric(currentSymbol) || isWordCharacter(currentSymbol)){
                builder.append(currentSymbol);
                currentCharIndex++;
                currentSymbol = source.charAt(currentCharIndex);
            }

            lastParseOutput = builder.toString();
            return lastParseOutput;

        } else {
            lastParseOutput = null;
            return null;
        }
    }


    private static boolean isWordCharacter(char c) {
        String cs = c + "";
        Pattern nonW = Pattern.compile("\\W", Pattern.UNICODE_CHARACTER_CLASS);
        Matcher match = nonW.matcher(cs);
        return !(match.find());
    }
}
