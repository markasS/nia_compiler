package lt.artsysoft.compiler.semantic_analizer;

import java.util.Arrays;
import java.util.HashMap;

import javax.activity.InvalidActivityException;
import javax.naming.directory.InvalidAttributesException;

import lt.artsysoft.compiler.beans.Lexem;
import lt.artsysoft.compiler.beans.SyntaxTreeItem;
import lt.artsysoft.compiler.semantic_analizer.semantic_programs.*;

/**
 * Created with IntelliJ IDEA.
 * User: Marik
 * Date: 13.11.29
 * Time: 13.40
 * To change this template use File | Settings | File Templates.
 */
public class SemanticAnalizer {
	
	public static HashMap<String, SemanticProgram> semanticCheckers = new HashMap<String, SemanticProgram>();
	private static Lexem[] source;
	
	public static void init(Lexem[] sourceLexems) {
		source = sourceLexems;
		SemanticProgram aProgram;
		
		aProgram = new AssignmentSemanticProgram();
		semanticCheckers.put(aProgram.getRuleName(), aProgram);
		
		aProgram = new ConditionSemanticProgram();
		semanticCheckers.put(aProgram.getRuleName(), aProgram);
		
		aProgram = new ConjOrDisjSemanticProgram();
		semanticCheckers.put(aProgram.getRuleName(), aProgram);
		
		aProgram = new ConstantSemanticProgram();
		semanticCheckers.put(aProgram.getRuleName(), aProgram);
		
		aProgram = new ControlStatementSemanticProgram();
		semanticCheckers.put(aProgram.getRuleName(), aProgram);
		
		aProgram = new DeclarationSemanticProgram();
		semanticCheckers.put(aProgram.getRuleName(), aProgram);
		
		aProgram = new ElseSemanticProgram();
		semanticCheckers.put(aProgram.getRuleName(), aProgram);
		
		aProgram = new ExpressionSemanticProgram();
		semanticCheckers.put(aProgram.getRuleName(), aProgram);
		
		aProgram = new FuncBodySemanticProgram();
		semanticCheckers.put(aProgram.getRuleName(), aProgram);
		
		aProgram = new FuncCallSemanticProgram();
		semanticCheckers.put(aProgram.getRuleName(), aProgram);
		
		aProgram = new FuncHeadSemanticProgram();
		semanticCheckers.put(aProgram.getRuleName(), aProgram);
		
		aProgram = new FunctionSemanticProgram();
		semanticCheckers.put(aProgram.getRuleName(), aProgram);
		
		aProgram = new IfSemanticProgram();
		semanticCheckers.put(aProgram.getRuleName(), aProgram);
		
		aProgram = new InitializationSemanticProgram();
		semanticCheckers.put(aProgram.getRuleName(), aProgram);
		
		aProgram = new LoopSemanticProgram();
		semanticCheckers.put(aProgram.getRuleName(), aProgram);
		
		aProgram = new MainSemanticProgram();
		semanticCheckers.put(aProgram.getRuleName(), aProgram);
		
		aProgram = new NumExprSemanticProgram();
		semanticCheckers.put(aProgram.getRuleName(), aProgram);
		
		aProgram = new PlusOrMinusSemanticProgram();
		semanticCheckers.put(aProgram.getRuleName(), aProgram);
		
		aProgram = new ProgramSemanticProgram();
		semanticCheckers.put(aProgram.getRuleName(), aProgram);
		
		aProgram = new ReturnSemanticProgram();
		semanticCheckers.put(aProgram.getRuleName(), aProgram);
		
		aProgram = new StatementSemanticProgram();
		semanticCheckers.put(aProgram.getRuleName(), aProgram);
		
		aProgram = new TermSemanticProgram();
		semanticCheckers.put(aProgram.getRuleName(), aProgram);
		
		aProgram = new ValueOrNotSemanticProgram();
		semanticCheckers.put(aProgram.getRuleName(), aProgram);
		
		aProgram = new ValueParSemanticProgram();
		semanticCheckers.put(aProgram.getRuleName(), aProgram);
		
		aProgram = new ValueSemanticProgram();
		semanticCheckers.put(aProgram.getRuleName(), aProgram);
		
		
	}
	
	public static Lexem getLexemAtTreeItem(SyntaxTreeItem item) throws InvalidAttributesException {
		if (item.getFromLexemNr() != item.getToLexemNr()) {
			throw new InvalidAttributesException("Item too abstract! Covers " + item.getDifference() + " lexems of source, needs to cover 1!");
		}
		
		return source[item.getFromLexemNr()];
	}

	public static Lexem[] getLexemsRange(int from, int to) { 
		return Arrays.asList(source).subList(from, to + 1).toArray(new Lexem[0]);
	}
	
	public static Lexem[] getLexemsInItem(SyntaxTreeItem item) {
		return getLexemsRange(item.getFromLexemNr(), item.getToLexemNr());
	}
	
	
	public static SemanticsCheckResult analyzeTree(SyntaxTreeItem root) throws InvalidAttributesException, InvalidActivityException {
		if (semanticCheckers.isEmpty()) {
			throw new InvalidActivityException("init() must be called first!");
		}
		
		return semanticCheckers.get(root.getRule().getName()).checkSemantics(root);
	}
	
	public static int getSyntaxItemStartLine(SyntaxTreeItem item) {
		Lexem firstLex = source[item.getFromLexemNr()];
		
		return firstLex.getLineInCode();
	}
	
	
	

	
	
}
