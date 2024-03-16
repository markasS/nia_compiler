package lt.artsysoft.compiler.semantic_analizer.semantic_programs;

import javax.naming.directory.InvalidAttributesException;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.semantic_analizer.SemanticsAnalysisHelper;
import lt.artsysoft.compiler.semantic_analizer.SemanticsCheckResult;

public class FunctionSemanticProgram implements SemanticProgram {

	@Override
	public SemanticsCheckResult checkSemantics(SyntaxTreeItem item) throws InvalidAttributesException {
		//inFunction set while checking header
		SemanticsCheckResult checkFuncHead = SemanticsAnalysisHelper.checkItem(item.getDescendants().getFirst());
		if (!checkFuncHead.isCorrect()) {
			SemanticsAnalysisHelper.inFunction = null;
			return checkFuncHead;
		}
		SyntaxTreeItem body = item.getDescendants().get(2);
		SemanticsCheckResult checkFuncBody = SemanticsAnalysisHelper.createSuccessCheckResult();
		if (body.getRule().getName().equals("func_body")) {
			checkFuncBody = SemanticsAnalysisHelper.checkItem(body);
		}
		SemanticsAnalysisHelper.inFunction = null;
		if (!checkFuncBody.isCorrect()) {
			return checkFuncBody;
		}
		return SemanticsAnalysisHelper.createSuccessCheckResult();
	}

	@Override
	public String getRuleName() {
		return "function";
	}

}
