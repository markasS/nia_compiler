package lt.artsysoft.compiler.semantic_analizer.semantic_programs;

import javax.naming.directory.InvalidAttributesException;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.semantic_analizer.SemanticsAnalysisHelper;
import lt.artsysoft.compiler.semantic_analizer.SemanticsCheckResult;

public class ConditionSemanticProgram implements SemanticProgram {

	@Override
	public SemanticsCheckResult checkSemantics(SyntaxTreeItem item) throws InvalidAttributesException {
		
		SyntaxTreeItem ifExpression = item.getDescendants().get(0);
		SemanticsAnalysisHelper.inCondition = true;
		SemanticsCheckResult checkIf = SemanticsAnalysisHelper.checkItem(ifExpression);
		if (!checkIf.isCorrect()) {
			SemanticsAnalysisHelper.inCondition = false;
			return checkIf;
		}
		SemanticsCheckResult checkElse;
		for (int i = 1; i < item.getDescendants().size(); i++) {
			SyntaxTreeItem elseItem = item.getDescendants().get(i);
			checkElse = SemanticsAnalysisHelper.checkItem(elseItem);
			if (!checkElse.isCorrect()) {
				SemanticsAnalysisHelper.inCondition = false;
				return checkElse;
			}
		}
		SemanticsAnalysisHelper.inCondition = false;
		return SemanticsAnalysisHelper.createSuccessCheckResult();
	}

	@Override
	public String getRuleName() {
		return "condition";
	}

}
