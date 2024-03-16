package lt.artsysoft.compiler.semantic_analizer.semantic_programs;

import javax.naming.directory.InvalidAttributesException;

import lt.artsysoft.compiler.beans.Lexem;
import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.beans.Variable;
import lt.artsysoft.compiler.beans.classifiers.VariableTypes;
import lt.artsysoft.compiler.semantic_analizer.SemanticAnalizer;
import lt.artsysoft.compiler.semantic_analizer.SemanticAnalysisErrors;
import lt.artsysoft.compiler.semantic_analizer.SemanticsAnalysisHelper;
import lt.artsysoft.compiler.semantic_analizer.SemanticsCheckResult;
import lt.artsysoft.compiler.semantic_analizer.tables.VariablesTable;

public class AssignmentSemanticProgram implements SemanticProgram {

	@Override
	public SemanticsCheckResult checkSemantics(SyntaxTreeItem item) throws InvalidAttributesException {
		//check if variable exists
		Lexem assignVar = SemanticAnalizer.getLexemAtTreeItem(item.getDescendants().get(0));
		Variable theVar = VariablesTable.getTable().get(SemanticsAnalysisHelper.inFunction).get(assignVar.getValue());
		if (theVar == null) {
			return SemanticsAnalysisHelper.createFailureCheckResult(
					SemanticAnalysisErrors.raiseVariableUndefinedError(
							SemanticAnalizer.getSyntaxItemStartLine(item),
							assignVar.getValue(),
							SemanticsAnalysisHelper.inFunction),
					item.getRule());
		}
		//ignore equals lexem
		//check if value ok to proceed
		SyntaxTreeItem assignVal = item.getDescendants().get(2);
		SemanticsCheckResult valCheck = SemanticsAnalysisHelper.checkItem(assignVal);
		if (!valCheck.isCorrect()) {
			return valCheck;
		}
		//time to check type mismatches
		VariableTypes varType = theVar.getType();
		String stringType = SemanticsAnalysisHelper.extractValueReturnType(assignVal);
		if (stringType.contains(" ")) {
			return SemanticsAnalysisHelper.createFailureCheckResult(stringType, assignVal.getRule());
		}
		VariableTypes valueType = VariableTypes.valueOf(stringType);
		
		if (!varType.equals(valueType)) {
			return SemanticsAnalysisHelper.createFailureCheckResult(
					SemanticAnalysisErrors.raiseTypeError(
							SemanticAnalizer.getSyntaxItemStartLine(assignVal),
							varType.toString(),
							valueType.toString()),
					assignVal.getRule());
		}
		
		return SemanticsAnalysisHelper.createSuccessCheckResult();
	}

	@Override
	public String getRuleName() {
		return "assignment";
	}

}
