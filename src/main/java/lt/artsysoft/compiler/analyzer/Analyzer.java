package lt.artsysoft.compiler.analyzer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import lt.artsysoft.compiler.beans.ComplexRule;
import lt.artsysoft.compiler.beans.Lexem;
import lt.artsysoft.compiler.beans.NamedElement;
import lt.artsysoft.compiler.beans.RepeatRule;
import lt.artsysoft.compiler.beans.Rule;
import lt.artsysoft.compiler.beans.StandardRule;
import lt.artsysoft.compiler.beans.SyntaxTreeItem;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class Analyzer {
	
	
	private static HashMap<String, StandardRule> rules;

    private static Lexem[] lexemsArray;

    private static ComplexRule potentialErrorRule;

    private static int lexemsCounter = 0;

    private static int globalNrOfLexemsRead = 0;

    private static LinkedList<SyntaxTreeItem> syntaxTreeItems;

    private static boolean repeatRuleIncorrect = false;

    public static void setLexemsArray(Lexem[] lexemsArray) {
        Analyzer.lexemsArray = lexemsArray;
    }

    public static void init(InputStream xmlFileStream, List<Lexem> wordBank) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFileStream);
        doc.getDocumentElement().normalize();
        AnalyzerConfParseHelper.init(wordBank);
        
        rules = AnalyzerConfParseHelper.parseDocBNF(doc);
        syntaxTreeItems = new LinkedList<>();
        if (rules.get(AnalyzerConfParseHelper.getRootSymbol().getName()) instanceof ComplexRule) {
            analyzeRule(rules.get(AnalyzerConfParseHelper.getRootSymbol().getName()), 0);
        }
        System.out.println();
        printSyntaxAnalysisTree();
        if (lexemsCounter != lexemsArray.length) {
            System.out.println(SyntaxTreeItem.lastReadRule.getErrorMessage() + " lexem. Value: \"" + lexemsArray[globalNrOfLexemsRead-1].getValue() + "\". "
            + "On " + lexemsArray[globalNrOfLexemsRead-1].getLineInCode() + " line.");
        } else {
        	
        }
	}
    
    public static boolean hasFailed() {
    	return lexemsCounter != lexemsArray.length;
    }

    /**
     *
     * @param currentRule
     * @param lexemPosition
     * @return   If at least one alternative is accepted - then rule is accepted
     */
    public static boolean analyzeRule(ComplexRule currentRule, int lexemPosition) {

        boolean accepted = false;
        Iterator<Rule> alternativesIterator = currentRule.getAlternatives().iterator();
        if (currentRule.getAlternatives().size() == 1) {
            Rule alternative = alternativesIterator.next();
            if (alternative instanceof Lexem) {
                alternative = wrapLexem((Lexem)alternative);
            }
            if (currentRule instanceof RepeatRule) {
                accepted = analyzeRule((ComplexRule) alternative, lexemPosition);
            }
            else accepted = analyzeAlternative((ComplexRule) alternative, lexemPosition);
        }
        else if (!isTrueAlternatives(currentRule.getAlternatives())){
            accepted = analyzeAlternative((ComplexRule) currentRule, lexemPosition);
        }
        else {
            while (!accepted && alternativesIterator.hasNext())  {
                Rule alternative =  alternativesIterator.next();
                if (alternative instanceof Lexem) {
                    alternative = wrapLexem((Lexem)alternative);
                }
                //analyze alternative as new rule
                if (analyzeAlternative((ComplexRule) alternative, lexemPosition))
                    accepted = true;
            }
        }

        // repeating rule analysis if rule is RepeatRule type
        // and equal lexems where found on alternative analysis.
        if ((currentRule instanceof RepeatRule) && lexemsCounter != lexemPosition
                && !((RepeatRule)currentRule).doesRepeatOnce() && lexemsCounter < lexemsArray.length) {
            int lexemPosBefore = lexemsCounter;
            boolean repeatAccepted = analyzeRule(currentRule, lexemsCounter);
           if (lexemPosBefore < lexemsCounter && !repeatAccepted && accepted && repeatRuleIncorrect)  {
                accepted = false;
            }
            //if two in a row repeat rules validations failed, then, the whole rule - failed
            if (!repeatAccepted && !accepted) {
                accepted = false;
                repeatRuleIncorrect = true;
            }

        }
        if (currentRule instanceof StandardRule && ((StandardRule) currentRule).getName() != null) {
            //accepted rule eliminates potential error.
            if (accepted) {
                potentialErrorRule = null;
                //if rule is accepted, add it to syntaxTreeItems collection
                StandardRule stdVersion = (StandardRule) currentRule;
                if (stdVersion.getName().startsWith("$")) {
                	//prepTheLexem
                	Lexem unwrap = (Lexem) stdVersion.getAlternatives().get(0);
                	unwrap.setValue(lexemsArray[lexemPosition].getValue());
//                	System.out.println("Gave value: " + ((Lexem) stdVersion.getAlternatives().get(0)).getValue());
                }
                SyntaxTreeItem syntaxTreeItem = new SyntaxTreeItem(stdVersion, lexemPosition, globalNrOfLexemsRead -1);
                if (syntaxTreeItems.contains(syntaxTreeItem))
                    syntaxTreeItems.remove(syntaxTreeItem);
                syntaxTreeItems.addFirst(syntaxTreeItem );

            }
            else {
                //if rule is not accepted, gathering syntax Tree from the start
                //syntaxTreeItems.clear();
                if (potentialErrorRule == null)
                    potentialErrorRule = currentRule;
            }
        }
        return accepted;
    }

    /**
     *
     * @param alternative
     * @param lexemPosition
     * @return if all parts of alternative are accepted, then alternative is also accepted
     */

    private static boolean analyzeAlternative(ComplexRule alternative, int lexemPosition) {

        //iterating through alternative elements,  the method getAlternatives should return alternative parts
        //wrap lexem
        Iterator<Rule> alternativePartIterator =  alternative.getAlternatives().iterator();
        while (alternativePartIterator.hasNext()) {
             Rule alternativePart = alternativePartIterator.next();
             if (alternativePart instanceof Lexem) {
                 Lexem alternativePartLexem = (Lexem) alternativePart;
                 //found equal lexems
                 Lexem currentLexem = lexemsArray[lexemPosition];
                 if (alternativePartLexem.getUid().equals(currentLexem.getUid())) {
                      //System.out.println(lexemsArray[lexemPosition]+ " " + ((Integer) lexemPosition).toString());
                     //increasing global number of lexems read only if it current lexem wasn't read recently
                      lexemsCounter = (++lexemPosition > lexemsCounter) ? ++lexemsCounter : lexemsCounter;
                      if (lexemsCounter > lexemPosition) {
                          lexemsCounter = lexemPosition;
                      }
                      globalNrOfLexemsRead = lexemsCounter;
                      //globalNrOfLexemsRead = (lexemsCounter > globalNrOfLexemsRead) ? lexemsCounter : globalNrOfLexemsRead;
                      if (lexemPosition == lexemsArray.length)
                         //if all lexems read and alternative is not accepted
                         if (alternativePartIterator.hasNext()) return false;
                 }
                 //alternative is not suitable, returning to next alternative
                 else return false;
             }
             else if (alternativePart instanceof ComplexRule && lexemPosition < lexemsArray.length) {
                 if (!analyzeRule((ComplexRule) alternativePart, lexemPosition)) {
                     //if rule repeats from 0 to 1 times, we can ignore it
                     if (!((alternativePart instanceof RepeatRule) /*&& ((RepeatRule) alternativePart).doesRepeatOnce())*/)) {
                         //situation, when after repeat rule was accepted - higher rule failed
                         //if (globalNrOfLexemsRead > lexemsCounter)
                            // removeSyntaxTreeItems(lexemPosition, globalNrOfLexemsRead-1);
                         return false;
                     }
                     else {
                         //if repeat rule validation failed - pass current lexem validation to higher rule
                         lexemsCounter = lexemPosition;
                     }
                 }
                 //if inner rule was accepted, assigning last read lexem position, to current analyzing position in alternative
                 else lexemPosition = lexemsCounter;
             }
        }
        return true;
    }

    private static StandardRule wrapLexem(Lexem lexem) {
            StandardRule lexemWrapperRule = new StandardRule();
            ArrayList<Rule> wrappedLexem = new ArrayList<>();
            wrappedLexem.add(lexem);
            lexemWrapperRule.setAlternatives(wrappedLexem);
            return lexemWrapperRule;
    }

    private static boolean isTrueAlternatives (List<Rule> alternatives ) {
         for (Rule alternative : alternatives) {
             if (!(alternative instanceof StandardRule && ((StandardRule)alternative).getName()== null)) {
                 return false;
             }
         }
        return true;
    }

    private static void setSyntaxTreeItemPositions() {
        for (int i = 0; i < syntaxTreeItems.size(); i++) {
            syntaxTreeItems.get(i).setPosition(i);
        }
    }

    @SuppressWarnings("unchecked")
	private static void printSyntaxAnalysisTree() {
        /*for (SyntaxTreeItem st : syntaxTreeItems) {
            System.out.println(st.getFromLexemNr() + " " + st.getToLexemNr() + st.getRule().getName());
        }   */
        eliminateIncorrectItems ();
        for (int i=1; i < syntaxTreeItems.size(); i++) {
            SyntaxTreeItem syntaxTreeItemOut = syntaxTreeItems.get(i);
            //System.out.println(getPossibleDescendantsNamesOut (syntaxTreeItemOut.getRule()));
            //System.out.println( syntaxTreeItemOut.getRule().getName()+ " "+ syntaxTreeItemOut.getFromLexemNr() + " " + syntaxTreeItemOut.getToLexemNr());
            for (int j=0; j < i; j++) {
                SyntaxTreeItem syntaxTreeItemIn = syntaxTreeItems.get(j);
                if ((syntaxTreeItemOut.getFromLexemNr() <= syntaxTreeItemIn.getFromLexemNr())
                        &&((syntaxTreeItemOut.getToLexemNr() >= syntaxTreeItemIn.getToLexemNr()))
                       /* && isRuleConditionAccepted(syntaxTreeItemOut, syntaxTreeItemIn)*/) {
                    if (!syntaxTreeItemIn.hasFather()) {
                        syntaxTreeItemOut.getDescendants().add(syntaxTreeItemIn);
                        syntaxTreeItemIn.setHasFather(true);
                    }
                }
            }
        }
        System.out.println(syntaxTreeItems.getLast().printItem(0, lexemsArray));
    }

    private static void eliminateIncorrectItems () {
        setSyntaxTreeItemPositions();
        Collections.sort(syntaxTreeItems);
        LinkedList<SyntaxTreeItem> newSyntaxTreeItems = new LinkedList<>();
        for (int i=1; i < syntaxTreeItems.size() ; i++) {
            SyntaxTreeItem syntaxTreeItemRight = syntaxTreeItems.get(i);
            SyntaxTreeItem syntaxTreeItemLeft = syntaxTreeItems.get(i-1);
            //System.out.println( syntaxTreeItemRight.getRule().getName()+ " "+ syntaxTreeItemRight.getFromLexemNr() + " " + syntaxTreeItemRight.getToLexemNr());
           //two neighbour rules, which define the same lexem range, should be included one into another
            if (syntaxTreeItemRight.getDifference() == 0 && syntaxTreeItemLeft.getDifference() == 0 &&
                    syntaxTreeItemRight.getFromLexemNr() == syntaxTreeItemLeft.getFromLexemNr())
            {
                if (isRuleConditionAccepted(syntaxTreeItemRight, syntaxTreeItemLeft)) {
                    newSyntaxTreeItems.addLast(syntaxTreeItemLeft);
                }
            }
            else {
                newSyntaxTreeItems.addLast(syntaxTreeItemLeft);
            }
        }
        newSyntaxTreeItems.addLast(syntaxTreeItems.getLast());
        syntaxTreeItems = newSyntaxTreeItems;
    }

    private static boolean isRuleConditionAccepted(SyntaxTreeItem father, SyntaxTreeItem descendant) {
       {
            StandardRule fatherRule = father.getRule();
            StandardRule descendantRule = descendant.getRule();
            ArrayList<String> fathersPossibleDescendants = getPossibleDescendantsNamesOut(fatherRule);
            return fathersPossibleDescendants.contains(descendantRule.getName());
        }
    }


    private static ArrayList<String> getPossibleDescendantsNamesOut (StandardRule standardRule) {
        ArrayList<String> result = new ArrayList<>();
        Iterator<Rule> alternativesIterator = standardRule.getAlternatives().iterator();
        if (!isTrueAlternatives(standardRule.getAlternatives())){
            result.addAll(getPossibleAlternativeNames(standardRule));
        }
        else {
            while (alternativesIterator.hasNext())  {
                Rule alternative =  alternativesIterator.next();
                if (alternative instanceof Lexem) {
                    alternative = wrapLexem((Lexem)alternative);
                }
                result.addAll(getPossibleAlternativeNames((ComplexRule)alternative));
            }
        }
        return  result;
    }

    private static ArrayList<String> getPossibleAlternativeNames(ComplexRule alternative) {
        ArrayList<String> result = new ArrayList<>();
        Iterator<Rule> alternativePartIterator =  alternative.getAlternatives().iterator();
        while (alternativePartIterator.hasNext()) {
            Rule alternativePart = alternativePartIterator.next();
            if (alternativePart instanceof  RepeatRule) {
                result.addAll(getPossibleAlternativeNames((RepeatRule)alternativePart));
            }
            else if (alternativePart instanceof NamedElement) {
                result.add(((NamedElement)alternativePart).getName());
            }
        }
        return result;
    }

    public static void saveSyntaxTreeToXML (File xmlFile) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        SyntaxTreeItem.resetLexemsCounter();
        syntaxTreeItems.getLast().addToXML(doc, lexemsArray, null);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(xmlFile);
        transformer.transform(source, result);
    }

    public static SyntaxTreeItem getRootSyntaxTreeItem() {
        return syntaxTreeItems.getLast();
    }
}
