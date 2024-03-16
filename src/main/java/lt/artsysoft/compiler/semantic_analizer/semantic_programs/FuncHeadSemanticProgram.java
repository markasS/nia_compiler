package lt.artsysoft.compiler.semantic_analizer.semantic_programs;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.InvalidAttributesException;

import lt.artsysoft.compiler.beans.Function;
import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.beans.classifiers.VariableTypes;
import lt.artsysoft.compiler.semantic_analizer.SemanticAnalizer;
import lt.artsysoft.compiler.semantic_analizer.SemanticAnalysisErrors;
import lt.artsysoft.compiler.semantic_analizer.SemanticsAnalysisHelper;
import lt.artsysoft.compiler.semantic_analizer.SemanticsCheckResult;
import lt.artsysoft.compiler.semantic_analizer.tables.FunctionDefinitionsTable;
import lt.artsysoft.compiler.semantic_analizer.tables.VariablesTable;

public class FuncHeadSemanticProgram implements SemanticProgram {

	@Override
	public SemanticsCheckResult checkSemantics(SyntaxTreeItem item) throws InvalidAttributesException {
		VariableTypes returnType = SemanticsAnalysisHelper.extractTypeName(item.getDescendants().get(0));
		String funcName = SemanticsAnalysisHelper.extractIdValue(item.getDescendants().get(1));
		//also finds 
		List<String> funcParamTypes = SemanticsAnalysisHelper.exctractTypesList(item.getDescendants().get(3));
		if (FunctionDefinitionsTable.getTable().get(funcName) != null) {
			return SemanticsAnalysisHelper.createFailureCheckResult(SemanticAnalysisErrors.raiseFunctionExistsError(SemanticAnalizer.getSyntaxItemStartLine(item), 
					funcName), item.getRule());
		}
		FunctionDefinitionsTable.getTable().put(funcName, new Function(returnType, funcName, convertToVarTypes(funcParamTypes)));
		VariablesTable.mapFunction(funcName);
		SemanticsAnalysisHelper.inFunction = funcName;
		SemanticsCheckResult result = SemanticsAnalysisHelper.writeParamsToVariableDefs(item.getDescendants().get(3));
        if (!result.isCorrect()) {
            return result;
        }
		return result;
	}

	private List<VariableTypes> convertToVarTypes(List<String> types) {
		List<VariableTypes> typesEnumed = new ArrayList<VariableTypes>();
		for (String aType: types) {
			typesEnumed.add(VariableTypes.valueOf(aType));
		}
		return typesEnumed;
	}

	@Override
	public String getRuleName() {
		return "func_head";
	}

}
