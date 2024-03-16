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

public class DeclarationSemanticProgram implements SemanticProgram {

	@Override
	public SemanticsCheckResult checkSemantics(SyntaxTreeItem item) throws InvalidAttributesException {
		SyntaxTreeItem declarationType = item.getDescendants().get(0);
		SyntaxTreeItem declarationName = item.getDescendants().get(1);
		Lexem varName = SemanticAnalizer.getLexemAtTreeItem(declarationName);
		Lexem varType = SemanticAnalizer.getLexemAtTreeItem(declarationType);
		
		if (SemanticsAnalysisHelper.inCondition) {
			return SemanticsAnalysisHelper.createFailureCheckResult(
					SemanticAnalysisErrors.raiseDeclareVariableInConditionalStatementError(
							SemanticAnalizer.getSyntaxItemStartLine(declarationName)),
							item.getRule()
					);
		}
		if (SemanticsAnalysisHelper.inLoop) {
			return SemanticsAnalysisHelper.createFailureCheckResult(
					SemanticAnalysisErrors.raiseDeclareVariableInLoopStatementError(
							SemanticAnalizer.getSyntaxItemStartLine(declarationName)),
							item.getRule()
					);
		}
		
		
		if (VariablesTable.getTable().get(SemanticsAnalysisHelper.inFunction).get(varName.getValue()) != null) {
			return SemanticsAnalysisHelper.createFailureCheckResult(
					SemanticAnalysisErrors.raiseVariableExistsError(
							SemanticAnalizer.getSyntaxItemStartLine(declarationName),
							varName.getValue(),
							SemanticsAnalysisHelper.inFunction),
					declarationName.getRule());
		}
		
		Variable var = new Variable();
		var.setInFunction(SemanticsAnalysisHelper.inFunction);
		var.setName(varName.getValue());
		var.setType(SemanticsAnalysisHelper.convertLexemTypeToEnum(varType.getUid()));
		var.setParam(false);
		
		VariablesTable.getTable().get(SemanticsAnalysisHelper.inFunction).put(var.getName(), var);
		
		return SemanticsAnalysisHelper.createSuccessCheckResult();
		
	}

	@Override
	public String getRuleName() {
		return "declaration";
	}

}
