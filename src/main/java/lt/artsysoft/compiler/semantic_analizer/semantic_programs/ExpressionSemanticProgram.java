package lt.artsysoft.compiler.semantic_analizer.semantic_programs;

import javax.naming.directory.InvalidAttributesException;

import lt.artsysoft.compiler.beans.Lexem;
import lt.artsysoft.compiler.beans.StandardRule;
import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.beans.Variable;
import lt.artsysoft.compiler.beans.classifiers.VariableTypes;
import lt.artsysoft.compiler.semantic_analizer.SemanticAnalizer;
import lt.artsysoft.compiler.semantic_analizer.SemanticAnalysisErrors;
import lt.artsysoft.compiler.semantic_analizer.SemanticsAnalysisHelper;
import lt.artsysoft.compiler.semantic_analizer.SemanticsCheckResult;
import lt.artsysoft.compiler.semantic_analizer.tables.VariablesTable;

public class ExpressionSemanticProgram implements SemanticProgram {

	@Override
	public SemanticsCheckResult checkSemantics(SyntaxTreeItem item) throws InvalidAttributesException {
		//check if parts are all good
		SemanticsCheckResult result = SemanticsAnalysisHelper.checkDescendants(item.getDescendants());
		if (!result.isCorrect()) {
			return result;
		}
		//check types to evaluate to boolean
		String allBool = areAllToBool(item);
		if (!allBool.isEmpty()) {
			if (allBool.contains(" ")) {
				//finished with undefined var error, raise it
				return SemanticsAnalysisHelper.createFailureCheckResult(allBool, item.getRule());
			} else {
				//regular ol' NOT_BOOL
				return SemanticsAnalysisHelper.createFailureCheckResult(
						SemanticAnalysisErrors.raiseTypeError(
								SemanticAnalizer.getSyntaxItemStartLine(item), 
								VariableTypes.BOOLEAN.toString(),
								allBool), item.getRule());
			}
			
		}
		
		return SemanticsAnalysisHelper.createSuccessCheckResult();
	}

	@Override
	public String getRuleName() {
		return "expression";
	}
	
	private String[] boolRules = {"expression", "value_or_not", "conj_or_disj", "logical_constant"};
	private String[] maybeBools = {"value", "value_par"};
	private String areAllToBool(SyntaxTreeItem item) throws InvalidAttributesException {
		
		StandardRule itemRule = item.getRule();
		boolean instaBool = false;
		int i = 0;
		while (i < boolRules.length && !instaBool) {
			instaBool = boolRules[i].equals(itemRule.getName());
			i++;
		}
		if (instaBool) {
			return "";
		}
		if (itemRule.getName().contains("$id")) {
			Lexem varRule = SemanticAnalizer.getLexemAtTreeItem(item);
			Variable definedVar = VariablesTable.getTable().get(SemanticsAnalysisHelper.inFunction).get(varRule.getValue());
			if (definedVar == null) {
				return SemanticAnalysisErrors.raiseVariableUndefinedError(SemanticAnalizer.getSyntaxItemStartLine(item), 
						varRule.getValue(), SemanticsAnalysisHelper.inFunction);
			}
			return VariableTypes.BOOLEAN.equals(definedVar.getType())? "" : "NOT_BOOL";
		}
		if (itemRule.getName().contains("$true") || itemRule.getName().contains("$false")) {
			return "";
		}
		i = 0;
		boolean maybeBool = false;
		while (i < maybeBools.length && !maybeBool) {
			instaBool = maybeBools[i].equals(itemRule.getName());
			i++;
		}
		if (!maybeBool) {
			return "NOT_BOOL";
		} else {
			String compoundBool = "";
			for (SyntaxTreeItem childItem: item.getDescendants()) {
				compoundBool = compoundBool + areAllToBool(childItem);
			}
			
			return compoundBool;
		}
		
	}

}
