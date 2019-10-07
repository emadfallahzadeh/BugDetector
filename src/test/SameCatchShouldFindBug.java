package test;

import static org.junit.Assert.*;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import core.visitor.common.SameCatchVisitor;

public class SameCatchShouldFindBug {
	
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
		String jaString="package core.visitor.common;\n" + 
				"\n" + 
				"import java.io.BufferedReader;\n" + 
				"import java.io.ByteArrayOutputStream;\n" + 
				"import java.io.File;\n" + 
				"import java.io.FileInputStream;\n" + 
				"import java.io.FileNotFoundException;\n" + 
				"import java.io.IOException;\n" + 
				"import java.io.StringReader;\n" + 
				"import java.util.Scanner;\n" + 
				"\n" + 
				"public class hello {\n" + 
				"	\n" + 
				"		public static void main(String[] args) {\n" + 
				"			\n" + 
				"\n" + 
				"	           try{    \n" + 
				"	                int a[]=new int[5];    \n" + 
				"	                a[5]=30/0;    \n" + 
				"	               }    \n" + 
				"	               catch(ArithmeticException e)  \n" + 
				"	                  {  \n" + 
				"	                   System.out.println(\"Hello\");  \n" + 
				"	                  }    \n" + 
				"	               catch(ArrayIndexOutOfBoundsException e)  \n" + 
				"	                  {  \n" + 
				"	                   System.out.println(\"Hello\");  \n" + 
				"	                  }    \n" + 
				"	               System.out.println(\"rest of the code\");    \n" + 
				"	    }  \n" + 
				"\n" + 
				"			\n" + 
				"			\n" + 
				"		\n" + 
				"		\n" + 
				"		\n" + 
				"		public void testingmethod(int a) {\n" + 
				"			\n" + 
				"			System.out.println(\"salam\");\n" + 
				"		}\n" + 
				"\n" + 
				"\n" + 
				"\n" + 
				"\n" + 
				"}\n" + 
				"";
		
		parser.setSource(jaString.toCharArray());
		CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
		
		// Act
		SameCatchVisitor sameCatchVisitor = new SameCatchVisitor(null, null, compilationUnit, null); 
		compilationUnit.accept(sameCatchVisitor);
		
		// Assert
		assertEquals(1, sameCatchVisitor.getCount());
		
		

	}
	
	@After
	public void tearDown() {
		parser = null;
	}


}
