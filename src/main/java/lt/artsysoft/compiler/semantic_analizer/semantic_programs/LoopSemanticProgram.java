package lt.artsysoft.compiler.semantic_analizer.semantic_programs;

import javax.naming.directory.InvalidAttributesException;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.semantic_analizer.SemanticsAnalysisHelper;
import lt.artsysoft.compiler.semantic_analizer.SemanticsCheckResult;

public class LoopSemanticProgram implements SemanticProgram {

	@Override
	public SemanticsCheckResult checkSemantics(SyntaxTreeItem item) throws InvalidAttributesException {
		SyntaxTreeItem expression = item.getDescendants().get(2);
		SemanticsCheckResult expressionCheck = SemanticsAnalysisHelper.checkItem(expression);
		SemanticsAnalysisHelper.inLoop = expressionCheck.isCorrect();
		if (!expressionCheck.isCorrect()) {
			return expressionCheck;
		}
		SemanticsCheckResult checkStatements = item.getDescendants().size() > 6?
				SemanticsAnalysisHelper.checkDescendants(item.getDescendants().subList(5, item.getDescendants().size() - 1))
				: SemanticsAnalysisHelper.createSuccessCheckResult();
		if (!checkStatements.isCorrect()) {
			SemanticsAnalysisHelper.inLoop = false;
			return checkStatements;
		}
		SemanticsAnalysisHelper.inLoop = false;
		return SemanticsAnalysisHelper.createSuccessCheckResult();		
	}

	@Override
	public String getRuleName() {
		return "loop";
	}

}
