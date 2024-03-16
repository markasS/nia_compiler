package lt.artsysoft.compiler.beans;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.11.4
 * Time: 02.39
 * To change this template use File | Settings | File Templates.
 */
public class SyntaxTreeItemComparator implements Comparator<SyntaxTreeItem> {
    @Override
    public int compare(SyntaxTreeItem o1, SyntaxTreeItem o2) {
        return ((Integer)o1.getFromLexemNr()).compareTo((Integer)o2.getFromLexemNr());
    }
}
