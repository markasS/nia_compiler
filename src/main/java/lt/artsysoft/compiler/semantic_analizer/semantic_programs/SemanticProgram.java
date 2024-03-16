package lt.artsysoft.compiler.semantic_analizer.semantic_programs;

import javax.naming.directory.InvalidAttributesException;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.semantic_analizer.SemanticsCheckResult;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.11.29
 * Time: 13.41
 * To change this template use File | Settings | File Templates.
 */
public interface SemanticProgram {

    /**
     * Checks if semantics of the rule, represented by class <Rule_name>SemanticProgram, is correct.
     * @return a {@link SemanticsCheckResult} object, describing what problem was found and where
     * @throws InvalidAttributesException if the rule currently getting analyzed doent's have a 
     * semantic program recorded for it
     */
    public SemanticsCheckResult checkSemantics(SyntaxTreeItem item) throws InvalidAttributesException;
    /**
     * Returns name of associated rule
     * @return the name of the rule this checker is used for (key in checkers map)
     */
    public String getRuleName();
}
