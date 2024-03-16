package lt.artsysoft.compiler.semantic_analizer.semantic_programs;

import javax.naming.directory.InvalidAttributesException;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.beans.classifiers.VariableTypes;
import lt.artsysoft.compiler.semantic_analizer.SemanticAnalizer;
import lt.artsysoft.compiler.semantic_analizer.SemanticAnalysisErrors;
import lt.artsysoft.compiler.semantic_analizer.SemanticsAnalysisHelper;
import lt.artsysoft.compiler.semantic_analizer.SemanticsCheckResult;

public class ValueParSemanticProgram implements SemanticProgram {

	@Override
	public SemanticsCheckResult checkSemantics(SyntaxTreeItem item) throws InvalidAttributesException {
		SyntaxTreeItem firstChild = item.getDescendants().get(0);
		boolean shouldBeNumeric = false;
		if (firstChild.getRule().getName().contains("$lPar")) {
			firstChild = item.getDescendants().get(1);
		}
		if (firstChild.getRule().getName().contains("$minus")) {
			shouldBeNumeric = true;
			firstChild = item.getDescendants().get(2);
		}
		
		
		if (!firstChild.getRule().getName().contains("$id")) {
			SemanticsCheckResult checkNonId = SemanticsAnalysisHelper.checkItem(firstChild);
			if (!checkNonId.isCorrect()) {
				return checkNonId;
			}
		} else {
			SemanticsCheckResult existingVar = SemanticsAnalysisHelper.checkForExistingVar(firstChild);
			if (!existingVar.isCorrect()) {
				return existingVar;
			}
		}
		if (shouldBeNumeric) {
			String valueParReturnType = SemanticsAnalysisHelper.exractValueParReturnType(item);
			if (valueParReturnType.contains(" ")) {
				return SemanticsAnalysisHelper.createFailureCheckResult(valueParReturnType, item.getRule());
			}
			
			if (SemanticsAnalysisHelper.isNumericType(valueParReturnType)) {
				return SemanticsAnalysisHelper.createSuccessCheckResult();
			} else {
				return SemanticsAnalysisHelper.createFailureCheckResult(SemanticAnalysisErrors.raiseTypeError(
							SemanticAnalizer.getSyntaxItemStartLine(firstChild),
							VariableTypes.DOUBLE.toString() + " >or<" + VariableTypes.INTEGER.toString(),
							valueParReturnType), 
						firstChild.getRule());
			}
		} 
		
		
		return SemanticsAnalysisHelper.createSuccessCheckResult();
		
	}

	@Override
	public String getRuleName() {
		return "value_par";
	}

}
