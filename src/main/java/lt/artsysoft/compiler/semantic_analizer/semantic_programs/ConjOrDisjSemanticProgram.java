package lt.artsysoft.compiler.semantic_analizer.semantic_programs;

import javax.naming.directory.InvalidAttributesException;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.semantic_analizer.SemanticsAnalysisHelper;
import lt.artsysoft.compiler.semantic_analizer.SemanticsCheckResult;

public class ConjOrDisjSemanticProgram implements SemanticProgram {

	@Override
	public SemanticsCheckResult checkSemantics(SyntaxTreeItem item) throws InvalidAttributesException {
		SyntaxTreeItem valueOrNot = item.getDescendants().get(1);
		
		return SemanticsAnalysisHelper.checkItem(valueOrNot);
	}

	@Override
	public String getRuleName() {
		return "conj_or_disj";
	}

}
