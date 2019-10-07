package test;

import static org.junit.Assert.*;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.junit.Before;
import org.junit.Test;

import core.visitor.common.CloseStream;
import core.visitor.common.VariableTrack;

public class StreamOpenedAndClosedInFinalyBlock {
	
	boolean flag=false;
	
	
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
		String jString="package core.visitor.common;\n" + 
				"\n" + 
				"import java.io.BufferedReader;\n" + 
				"import java.io.ByteArrayOutputStream;\n" + 
				"import java.io.File;\n" + 
				"import java.io.FileInputStream;\n" + 
				"import java.io.FileNotFoundException;\n" + 
				"import java.io.FileReader;\n" + 
				"import java.io.IOException;\n" + 
				"import java.io.StringReader;\n" + 
				"import java.util.Scanner;\n" + 
				"\n" + 
				"public class hello {\n" + 
				"	\n" + 
				"		public static void main(String[] args) {\n" + 
				"			FileInputStream f = null;\n" + 
				"			try {\n" + 
				"				f=new FileInputStream(\"string\");\n" + 
				"			} catch (FileNotFoundException e) {\n" + 
				"				// TODO Auto-generated catch block\n" + 
				"				e.printStackTrace();\n" + 
				"			}\n" + 
				"			finally {\n" + 
				"				try {\n" + 
				"					f.close();\n" + 
				"				} catch (IOException e) {\n" + 
				"					// TODO Auto-generated catch block\n" + 
				"					e.printStackTrace();\n" + 
				"				}\n" + 
				"			}\n" + 
				"\n" + 
				"			\n" + 
				"			\n" + 
				"			\n" + 
				"			\n" + 
				"		}\n" + 
				"			\n" + 
				"			\n" + 
				"			\n" + 
				"		\n" + 
				"		\n" + 
				"		\n" + 
				"\n" + 
				"\n" + 
				"\n" + 
				"}\n" + 
				"";
		
		parser.setSource(jString.toCharArray());
		CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
		// Act
		CloseStream closeStream = new CloseStream(null, null, compilationUnit, null); 
		compilationUnit.accept(closeStream);
		
		
		for(java.util.Map.Entry<IVariableBinding, VariableTrack> entry:closeStream.booleanVariablesMap.entrySet()) {
					if(!entry.getValue().HasAssignment()) { //this is for 2th pattern
						flag=true;
					} //this is for 2th pattern
				} //this is for 2th pattern
						
		
		// Assert
		assertEquals(false, flag);
		

		
		
		
		

		

		
		
	}

}
