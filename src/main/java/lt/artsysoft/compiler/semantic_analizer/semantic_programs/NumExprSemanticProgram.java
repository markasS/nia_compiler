package lt.artsysoft.compiler.semantic_analizer.semantic_programs;

import javax.naming.directory.InvalidAttributesException;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.semantic_analizer.SemanticsAnalysisHelper;
import lt.artsysoft.compiler.semantic_analizer.SemanticsCheckResult;

public class NumExprSemanticProgram implements SemanticProgram {

	@Override
	public SemanticsCheckResult checkSemantics(SyntaxTreeItem item) throws InvalidAttributesException {
		SemanticsCheckResult termsChecks = SemanticsAnalysisHelper.createSuccessCheckResult();
		for (int i = 0; i < item.getDescendants().size(); i++) {
			termsChecks = SemanticsAnalysisHelper.checkItem(item.getDescendants().get(i));
			if (!termsChecks.isCorrect()) {
				return termsChecks;
			}
		}
		
		return termsChecks;
	}

	@Override
	public String getRuleName() {
		return "numExpr";
	}

}
