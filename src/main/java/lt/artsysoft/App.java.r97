package lt.artsysoft;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import lt.artsysoft.compiler.analyzer.Analyzer;
import lt.artsysoft.compiler.beans.Lexem;
import lt.artsysoft.compiler.iform_generator.TACodeItemsList;
import lt.artsysoft.compiler.iform_generator.ThreeAddressCodeGenerator;
import lt.artsysoft.compiler.interpreter.Interpreter;
import lt.artsysoft.compiler.lexer.Lexer;
import lt.artsysoft.compiler.semantic_analizer.SemanticAnalizer;
import lt.artsysoft.compiler.semantic_analizer.SemanticsCheckResult;

import lt.artsysoft.compiler.semantic_analizer.tables.FunctionDefinitionsTable;
import lt.artsysoft.compiler.semantic_analizer.tables.VariablesTable;
import org.apache.commons.io.IOUtils;

/**
 * Main class for the lexer
 *
 *
 */
public class App {
	public static void main(String[] args) throws Exception {
		InputStream inLexer = App.class.getResourceAsStream("/NiaLangLexerConf.xml");
		InputStream inAnalyzer = App.class.getResourceAsStream("/NiaLangSyntaxBNF.xml");
        InputStream testProgram = App.class.getResourceAsStream("/inputSentence.nia");
		Lexer.init(inLexer);
		String source = readFile(testProgram);
		//String source = "-18.9 + 85 - 't' {} \"hot pocket\" variable ( * !&&|| ПОКА	ЕСЛИ##";
		Lexem[] lexems = Lexer.getSourceLexems(source);
		List<Lexem> allLexems= Lexer.getAllLanguageLexems();
        Analyzer.setLexemsArray(lexems);
		Analyzer.init(inAnalyzer, allLexems);
		
		if (!Analyzer.hasFailed()) {
			SemanticAnalizer.init(lexems);
			SemanticsCheckResult treeAnalysis = SemanticAnalizer.analyzeTree(Analyzer.getRootSyntaxTreeItem());
			System.err.println("Semantics report: " + treeAnalysis);
	        // Turn the resource into a File object
	        File dir = new File("testProgramTree.xml");
	        Analyzer.saveSyntaxTreeToXML(dir);
	        if (treeAnalysis.isCorrect()) {
		        ThreeAddressCodeGenerator.init(Analyzer.getRootSyntaxTreeItem());
		        ThreeAddressCodeGenerator.print();
		        System.out.println();
		        System.out.println("Program execution: ");
		        if (TACodeItemsList.getMainCodeItemIndex() != -1)
		        	Interpreter.interpretFunction(TACodeItemsList.getMainCodeItemIndex(), "main");
	        }
		}
        
	}

	private static void printLexems(Lexem[] lexems) {
		System.out.println("Lexems in source: ");
		int i = 1;
		if (lexems != null) {
			for (Lexem aLexem : lexems) {
				System.out.println(i + ") " + aLexem.getValue() + "(uid: " + aLexem.getUid() + ", name: " + aLexem.getName() + ")");
				i++;
			}
		}
		
	}

	private static String readFile(InputStream in)
			throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(in, writer, "UTF-8");
        return writer.toString();
	}
}
