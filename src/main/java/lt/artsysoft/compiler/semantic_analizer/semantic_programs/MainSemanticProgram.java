package lt.artsysoft.compiler.semantic_analizer.semantic_programs;

import javax.naming.directory.InvalidAttributesException;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.semantic_analizer.SemanticsAnalysisHelper;
import lt.artsysoft.compiler.semantic_analizer.SemanticsCheckResult;
import lt.artsysoft.compiler.semantic_analizer.tables.FunctionDefinitionsTable;
import lt.artsysoft.compiler.semantic_analizer.tables.VariablesTable;

public class MainSemanticProgram implements SemanticProgram {

	@Override
	public SemanticsCheckResult checkSemantics(SyntaxTreeItem item) throws InvalidAttributesException {
		SemanticsAnalysisHelper.inFunction = "main";
		VariablesTable.mapFunction(SemanticsAnalysisHelper.inFunction);
		SyntaxTreeItem mainBody = item.getDescendants().get(1);
		SemanticsCheckResult checkMain = SemanticsAnalysisHelper.createSuccessCheckResult();
		if (mainBody.getRule().getName().equals("func_body")) {
			 checkMain = SemanticsAnalysisHelper.checkItem(mainBody);
		}
		SemanticsAnalysisHelper.inFunction = null;
		return checkMain;
	}

	@Override
	public String getRuleName() {
		return "main";
	}

}
