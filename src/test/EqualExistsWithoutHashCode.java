package test;

import static org.junit.Assert.*;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import core.visitor.common.EqualHashCode;

public class EqualExistsWithoutHashCode {
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
				"	@Override\n" + 
				"	public boolean equals(Object obj)\n" + 
				"	{\n" + 
				"		return true;\n" + 
				"	}	\n" + 
				"}";
		
		parser.setSource(javaSource.toCharArray());
		CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);

		// Act
		EqualHashCode eualHashCodeVisitor = new EqualHashCode(null, null, compilationUnit, null); 
		compilationUnit.accept(eualHashCodeVisitor);

		// Assert
	    assertEquals(true, eualHashCodeVisitor.hasEqual==true && eualHashCodeVisitor.hasHashcode==false);
	}
	
	@After
	public void tearDown() {
		parser = null;
	}
}
