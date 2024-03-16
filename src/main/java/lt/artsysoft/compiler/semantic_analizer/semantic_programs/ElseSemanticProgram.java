package lt.artsysoft.compiler.semantic_analizer.semantic_programs;

import javax.naming.directory.InvalidAttributesException;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.semantic_analizer.SemanticsAnalysisHelper;
import lt.artsysoft.compiler.semantic_analizer.SemanticsCheckResult;

public class ElseSemanticProgram implements SemanticProgram {

	@Override
	public SemanticsCheckResult checkSemantics(SyntaxTreeItem item) throws InvalidAttributesException {
		SyntaxTreeItem decisivePart = item.getDescendants().get(1);
		if (decisivePart.getRule().getName().equals("if")) {
			return SemanticsAnalysisHelper.checkItem(decisivePart);
		}
		SemanticsCheckResult checkStatements = item.getDescendants().size() > 3?
				SemanticsAnalysisHelper.checkDescendants(item.getDescendants().subList(2, item.getDescendants().size() - 1))
				: SemanticsAnalysisHelper.createSuccessCheckResult();
		if (!checkStatements.isCorrect()) {
			return checkStatements;
		}
		return SemanticsAnalysisHelper.createSuccessCheckResult();
		
	}

	@Override
	public String getRuleName() {
		return "else";
	}

}
