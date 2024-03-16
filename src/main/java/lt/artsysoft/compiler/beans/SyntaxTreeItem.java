package lt.artsysoft.compiler.beans;

import java.util.Collections;
import org.w3c.dom.Document;
import java.util.LinkedList;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.10.27
 * Time: 18.20
 * To change this template use File | Settings | File Templates.
 */
public class SyntaxTreeItem implements Comparable {

    public static StandardRule lastReadRule;

    private StandardRule rule;

    private int fromLexemNr;

    private int toLexemNr;

    private int difference;

    private LinkedList<SyntaxTreeItem> descendants;

    private String lexemValue;

    private int position;


    public boolean isHasFather() {
        return hasFather;
    }

    public void setHasFather(boolean hasFather) {
        this.hasFather = hasFather;
    }

    private boolean hasFather;

	private static int turn = 0;

    public SyntaxTreeItem(StandardRule rule, int fromLexemNr, int toLexemNr) {
        this.rule = rule;
        this.fromLexemNr = fromLexemNr;
        this.toLexemNr = toLexemNr;
        this.difference = toLexemNr-fromLexemNr;
        descendants = new LinkedList<>();
        hasFather = false;
    }

    public StandardRule getRule() {
        return rule;
    }

    public void setRule(StandardRule rule) {
        this.rule = rule;
    }

    public int getFromLexemNr() {
        return fromLexemNr;
    }

    public void setFromLexemNr(int fromLexemNr) {
        this.fromLexemNr = fromLexemNr;
    }

    public int getToLexemNr() {
        return toLexemNr;
    }

    public void setToLexemNr(int toLexemNr) {
        this.toLexemNr = toLexemNr;
    }

    public int getDifference() {
        return difference;
    }

    public boolean hasFather() {
        return hasFather;
    }

    public String printItem (int levelOfDepth) {
        return printItem(levelOfDepth, null);
    }

    public LinkedList<SyntaxTreeItem> getDescendants() {
        return descendants;
    }

    public void setDescendants(LinkedList<SyntaxTreeItem> descendants) {
        this.descendants = descendants;
    }

    private static int sameNumbers = 0;
    private int sameNumbersFromIndex;
    @Override
    public int compareTo(Object o) {
        int diff = difference - ((SyntaxTreeItem)o).difference;
        if (diff == 0) {
           //lexems must be first
           if (rule.getName().startsWith("$"))
               return -1;
           else if (((SyntaxTreeItem)o).getRule().getName().startsWith("$"))
               return 1;
           else if (((SyntaxTreeItem)o).getFromLexemNr() == fromLexemNr) {
               if (((SyntaxTreeItem)o).position > position)  return 1;
               else return -1;
           }
        }
       return diff;
    }

    public boolean equals(Object o) {
        SyntaxTreeItem syntaxTreeItem = ((SyntaxTreeItem)o);
        boolean log= syntaxTreeItem.getRule().getName().equals(rule.getName());

        return (log && (((SyntaxTreeItem)o).fromLexemNr == this.fromLexemNr) && (((SyntaxTreeItem)o).toLexemNr == this.toLexemNr));
    }

    @Override
    public int hashCode() {
        return super.hashCode();    //To change body of overridden methods use File | Settings | File Templates.
    }

	public String printItem(int levelOfDepth, Lexem[] lexemsArray) {
		lastReadRule = rule;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0; i < levelOfDepth; i++) {
            stringBuilder.append((i+1) + ((i < levelOfDepth - 1)? "." : ": "));
        }
        String appendee = "";
        if (rule.name != null) {
        	if (rule.name.startsWith("$") && lexemsArray != null) {
        		((Lexem) rule.alternatives.get(0)).setValue(lexemsArray[turn].getValue());
                lexemValue =  ((Lexem)rule.alternatives.get(0)).getValue();
        		appendee = lexemValue + "#";
        		turn++;
        	}
        	appendee += rule.name;
            appendee += " " + this.getFromLexemNr() + " " + this.getToLexemNr();
        	appendee += '\n';
        }
        stringBuilder.append(appendee);
        Collections.sort(descendants, new SyntaxTreeItemComparator());
        for (SyntaxTreeItem descendant : descendants) {
            stringBuilder.append(descendant.printItem(levelOfDepth + 1, lexemsArray));
        }
        if (!descendants.isEmpty()) {
           // stringBuilder.append("\n");
        }
        return stringBuilder.toString();
	}

    public void addToXML (Document doc, Lexem[] lexemsArray, Element parent) {
        if (rule.name != null) {
            String ruleName = rule.name;
            if (ruleName.startsWith("$")) {
                ruleName = rule.name.substring(1);
            }
            Element ruleEl = doc.createElement(ruleName);
            if (parent != null)
                parent.appendChild(ruleEl);
            else {
                doc.appendChild(ruleEl);
            }
            if (rule.name.startsWith("$") && lexemsArray != null) {
                ((Lexem) rule.alternatives.get(0)).setValue(lexemsArray[turn].getValue());
                String lexemVal = ((Lexem)rule.alternatives.get(0)).getValue();
                ruleEl.appendChild(doc.createTextNode(lexemVal));
                turn++;
            }
            Collections.sort(descendants, new SyntaxTreeItemComparator());
            for (SyntaxTreeItem descendant : descendants) {
                descendant.addToXML(doc, lexemsArray, ruleEl);
            }
        }
    }

    public static void resetLexemsCounter() {
        turn = 0;
    }

    public String getLexemValue() {
        return lexemValue;
    }

    public void setLexemValue(String lexemValue) {
        this.lexemValue = lexemValue;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
