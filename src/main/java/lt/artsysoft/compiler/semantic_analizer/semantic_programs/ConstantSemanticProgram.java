package lt.artsysoft.compiler.semantic_analizer.semantic_programs;

import javax.naming.directory.InvalidAttributesException;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.semantic_analizer.SemanticsAnalysisHelper;
import lt.artsysoft.compiler.semantic_analizer.SemanticsCheckResult;

public class ConstantSemanticProgram implements SemanticProgram {

	@Override
	public SemanticsCheckResult checkSemantics(SyntaxTreeItem item)
			throws InvalidAttributesException {
		return SemanticsAnalysisHelper.createSuccessCheckResult();
	}

	@Override
	public String getRuleName() {
		return "constant";
	}

}
