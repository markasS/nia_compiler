package lt.artsysoft.compiler.semantic_analizer.semantic_programs;

import javax.naming.directory.InvalidAttributesException;

import lt.artsysoft.compiler.beans.Lexem;
import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.beans.Variable;
import lt.artsysoft.compiler.semantic_analizer.SemanticAnalizer;
import lt.artsysoft.compiler.semantic_analizer.SemanticAnalysisErrors;
import lt.artsysoft.compiler.semantic_analizer.SemanticsAnalysisHelper;
import lt.artsysoft.compiler.semantic_analizer.SemanticsCheckResult;
import lt.artsysoft.compiler.semantic_analizer.tables.VariablesTable;

public class ValueSemanticProgram implements SemanticProgram {

	@Override
	public SemanticsCheckResult checkSemantics(SyntaxTreeItem item) throws InvalidAttributesException {
		SyntaxTreeItem valueContents = item.getDescendants().getFirst();
		//the value is an id, check for existence of variable in function
		if (valueContents.getRule().getName().contains("$id")) {
			return SemanticsAnalysisHelper.checkForExistingVar(valueContents);
		}
		//rest of alternatives have individual checkers defined for them
		return SemanticsAnalysisHelper.checkItem(valueContents);
	}

	@Override
	public String getRuleName() {
		return "value";
	}

}
