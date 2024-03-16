package lt.artsysoft.compiler.semantic_analizer.semantic_programs;

import javax.naming.directory.InvalidAttributesException;

import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.beans.classifiers.VariableTypes;
import lt.artsysoft.compiler.semantic_analizer.SemanticAnalizer;
import lt.artsysoft.compiler.semantic_analizer.SemanticAnalysisErrors;
import lt.artsysoft.compiler.semantic_analizer.SemanticsAnalysisHelper;
import lt.artsysoft.compiler.semantic_analizer.SemanticsCheckResult;

public class TermSemanticProgram implements SemanticProgram {

	@Override
	public SemanticsCheckResult checkSemantics(SyntaxTreeItem item) throws InvalidAttributesException {
		SemanticsCheckResult valueParCheck = SemanticsAnalysisHelper.createSuccessCheckResult();
		boolean isString = false;
		for (int i = 0; i < item.getDescendants().size(); i+= 2) {
			SyntaxTreeItem varPar = item.getDescendants().get(i);
			if (!varPar.getRule().getName().contains("$id")) {
				valueParCheck = SemanticsAnalysisHelper.checkItem(varPar);
				if (!valueParCheck.isCorrect()) {
					return valueParCheck;
				}
			}
			String type = SemanticsAnalysisHelper.exractValueParReturnType(varPar);
			if (type.contains(" ")) {
				return SemanticsAnalysisHelper.createFailureCheckResult(type, varPar.getRule());
			}
			if (!(VariableTypes.DOUBLE.toString().equals(type)
					|| VariableTypes.INTEGER.toString().equals(type)
					|| VariableTypes.STRING.toString().equals(type)
					|| VariableTypes.SYMBOL.toString().equals(type)
					|| VariableTypes.BOOLEAN.toString().equals(type))) {
				return SemanticsAnalysisHelper.createFailureCheckResult(SemanticAnalysisErrors.raiseTypeError(
						SemanticAnalizer.getSyntaxItemStartLine(varPar), 
						VariableTypes.DOUBLE + "> or <"
						+ VariableTypes.INTEGER + "> or <"
						+ VariableTypes.STRING + "> or <"
						+ VariableTypes.SYMBOL, type), item.getRule());
			}
			isString = VariableTypes.valueOf(type).equals(VariableTypes.STRING);
			if (isString && item.getDescendants().size() > 1) {
				String operation = SemanticAnalizer.getLexemAtTreeItem(item.getDescendants().get(1)).getValue();
				return SemanticsAnalysisHelper.createFailureCheckResult(
						SemanticAnalysisErrors.raiseInvalidOperationError(
								SemanticAnalizer.getSyntaxItemStartLine(item), operation, "+"),
								item.getRule());
			}
		}
		
		return valueParCheck;
	}

	@Override
	public String getRuleName() {
		return "term";
	}

}
