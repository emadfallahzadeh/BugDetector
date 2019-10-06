package test;

import static org.junit.Assert.*;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import core.visitor.common.EmptyCatchVisitor;

public class IfWithPredeterminedVariable {

	ASTParser parser;
	
	@Before
	public void setUp() throws Exception {
		parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setEnvironment( // apply classpath
                new String[] { "" }, //
                null, null, true);
        parser.setUnitName("any_name");	
	}

	@Test
	public void test() {
		// Arrange
		String javaSource = 
				"package MyProj;\n" + 
				"public class MyProj {\n" + 
				"	public static void main(String[] args) {\n" + 
				"		boolean a = true;\n" + 
				"		if (a)\n" + 
				"			return;\n" + 
				"		if (a == false)\n" + 
				"			return;\n" + 
				"	}\n" + 
				"}";
		
		parser.setSource(javaSource.toCharArray());
		CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);

		// Act
		EmptyCatchVisitor ecVisitor = new EmptyCatchVisitor(
				null, null, compilationUnit);
		compilationUnit.accept(ecVisitor);
		
		// Assert
	    assertEquals(2, ecVisitor.getUselessConditionCount());
	}
	
	@After
	public void tearDown() {
		parser = null;
	}
}
