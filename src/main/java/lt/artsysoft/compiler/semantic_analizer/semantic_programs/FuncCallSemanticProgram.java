package lt.artsysoft.compiler.semantic_analizer.semantic_programs;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.InvalidAttributesException;

import lt.artsysoft.compiler.beans.Function;
import lt.artsysoft.compiler.beans.Lexem;
import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.semantic_analizer.SemanticAnalizer;
import lt.artsysoft.compiler.semantic_analizer.SemanticAnalysisErrors;
import lt.artsysoft.compiler.semantic_analizer.SemanticsAnalysisHelper;
import lt.artsysoft.compiler.semantic_analizer.SemanticsCheckResult;
import lt.artsysoft.compiler.semantic_analizer.tables.FunctionDefinitionsTable;

public class FuncCallSemanticProgram implements SemanticProgram {

	@Override
	public SemanticsCheckResult checkSemantics(SyntaxTreeItem item) throws InvalidAttributesException {
		boolean isRead = false;
		boolean isWrite = false;
		boolean isToString = false;
		Lexem funcName = SemanticAnalizer.getLexemAtTreeItem(item.getDescendants().getFirst());
		//function call is for read/write
		if (!funcName.getUid().equals("$id")) {
			isRead = funcName.getUid().equals("$read");
			isWrite = funcName.getUid().equals("$write");
			isToString = funcName.getUid().equals("$to_string");
		}
		Function definedFunc = FunctionDefinitionsTable.getTable().get(funcName.getValue());
		
		if (definedFunc == null) {
			return SemanticsAnalysisHelper.createFailureCheckResult(
					SemanticAnalysisErrors.raiseFunctionUndefinedError(SemanticAnalizer.getSyntaxItemStartLine(item),
							funcName.getValue()),
					item.getRule());
					
		}
		//analyze function params
		SyntaxTreeItem funcParamsList = item.getDescendants().get(2);
		if (funcParamsList.getRule().getName().equals("value")) {
			funcParamsList = SemanticsAnalysisHelper.createTreeItemWrapper(funcParamsList);
			funcParamsList.getRule().setName("param_list");
		} else if (funcParamsList.getRule().getName().equals("value_par")) {
			funcParamsList.getRule().setName("param_list");
		}
		List<String> callParamTypes = new ArrayList<String>();
		List<Integer> badIndices = new ArrayList<Integer>();
		if ("param_list".equals(funcParamsList.getRule().getName())) {
			for (int i = 0; i < funcParamsList.getDescendants().size(); i+= 2) {
				SyntaxTreeItem valueItem = funcParamsList.getDescendants().get(i);
				SemanticsCheckResult valueCheck = SemanticsAnalysisHelper.checkItem(valueItem);
				if (!valueCheck.isCorrect()) {
					return valueCheck;
				}
				String type = SemanticsAnalysisHelper.extractValueReturnType(valueItem);
				if (type.contains(" ")) {
					return SemanticsAnalysisHelper.createFailureCheckResult(type, valueItem.getRule());
				}
				Lexem[] lexemsInValue = SemanticAnalizer.getLexemsInItem(valueItem);
				if(!(lexemsInValue[0].getUid().equals("$id") && FunctionDefinitionsTable.getTable().get(lexemsInValue[0].getValue()) == null)) {
					badIndices.add(i);
				}
				callParamTypes.add(type);
			}
		}
		//analyze params count and type matches
		if (!callParamTypes.isEmpty()) {
			if (isRead || isWrite || isToString) {
				if (isRead && !badIndices.isEmpty()) {
					return SemanticsAnalysisHelper.createFailureCheckResult(
							SemanticAnalysisErrors.raiseReadParamNonVariableError(SemanticAnalizer.getSyntaxItemStartLine(funcParamsList), 
									badIndices),
							funcParamsList.getRule());
				}
				if (isToString && !(callParamTypes.size() == 1)) {
					return SemanticsAnalysisHelper.createFailureCheckResult(
							SemanticAnalysisErrors.raiseFunctionParamsCountMismatchError(SemanticAnalizer.getSyntaxItemStartLine(funcParamsList), 
									definedFunc.getName(), 1, callParamTypes.size()), 
								item.getRule());
				}
				
				return SemanticsAnalysisHelper.createSuccessCheckResult();
			}
			
			if (definedFunc.getParamTypes().size() != callParamTypes.size()) {
				return SemanticsAnalysisHelper.createFailureCheckResult(
						SemanticAnalysisErrors.raiseFunctionParamsCountMismatchError(
								SemanticAnalizer.getSyntaxItemStartLine(funcParamsList),
								definedFunc.getName(),
								definedFunc.getParamTypes().size(),
								callParamTypes.size()),
						funcParamsList.getRule());
			}
			 
			for (int i = 0; i < callParamTypes.size(); i++) {
				if (!callParamTypes.get(i).equals(definedFunc.getParamTypes().get(i).toString())) {
					return SemanticsAnalysisHelper.createFailureCheckResult(
							SemanticAnalysisErrors.raiseFunctionParamTypeMismatchError(
									SemanticAnalizer.getSyntaxItemStartLine(funcParamsList),
									definedFunc.getName(),
									definedFunc.getParamTypes().get(i).toString(),
									i + 1,
									callParamTypes.get(i)
									),
							funcParamsList.getRule());
				}
			}
			
			return SemanticsAnalysisHelper.createSuccessCheckResult();
			
			
		} else {
			SemanticsCheckResult result = (isRead || isWrite) || definedFunc.getParamTypes().isEmpty()?
					SemanticsAnalysisHelper.createSuccessCheckResult():
						SemanticsAnalysisHelper.createFailureCheckResult(
								SemanticAnalysisErrors.raiseFunctionParamsCountMismatchError(
										SemanticAnalizer.getSyntaxItemStartLine(funcParamsList),
										definedFunc.getName(),
										definedFunc.getParamTypes().size(),
										0),
								funcParamsList.getRule());
			return result;
		}
		
		

	}

	@Override
	public String getRuleName() {
		return "func_call";
	}

}
