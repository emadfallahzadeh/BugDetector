package test;

import static org.junit.Assert.*;

import java.security.KeyStore.Entry;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.Before;
import org.junit.Test;

import core.visitor.common.CloseStream;
import core.visitor.common.SameCatchVisitor;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IVariableBinding;

import core.visitor.common.CloseStream;
import core.visitor.common.EqualHashCode;
import core.visitor.common.SameCatchVisitor;
import core.visitor.common.VariableTrack;


public class StreamOpendAndClosedButNotInFinalyClass {
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
				"			\n" + 
				"	          FileReader fr;\n" + 
				"			try {\n" + 
				"				fr = new FileReader(\"D:\\\\testout.txt\");\n" + 
				"		        BufferedReader br=new BufferedReader(fr);  \n" + 
				"		        \n" + 
				"		        \n" + 
				"		        try {\n" + 
				"					br.close();\n" + 
				"				} catch (IOException e) {\n" + 
				"					// TODO Auto-generated catch block\n" + 
				"					e.printStackTrace();\n" + 
				"				}\n" + 
				"\n" + 
				"			} catch (FileNotFoundException e) {\n" + 
				"				// TODO Auto-generated catch block\n" + 
				"				e.printStackTrace();\n" + 
				"			}    \n" + 
				"			\n" + 
				"			\n" + 
				"\n" + 
				"			\n" + 
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
		assertEquals(true, flag);
		

		
		
		
		

		

		
		
	}

}
