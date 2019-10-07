package antipattern.detection.run;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map.Entry;

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

import core.visitor.common.UselessConditionVisitor;
import core.visitor.common.VariableTrack;
import core.helper.HelperClass;
import core.visitor.common.CloseStream;
import core.visitor.common.EqualHashCode;
import core.visitor.common.SameCatchVisitor;


public class Application implements IApplication {
	
	public static int aa=0;
	public int cont=0;
	private String fileName = "empty-catch-Kafka.txt"; //Name of the output file that you wish to have
	

	@Override
	public Object start(IApplicationContext context) throws Exception {

		long startTime = System.nanoTime();
		//String[] args = (String[]) context.getArguments().get(IApplicationContext.APPLICATION_ARGS);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = root.getProjects();

		root = ResourcesPlugin.getWorkspace().getRoot();
		projects = root.getProjects();

		for (IProject project : projects) {

			if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
				System.out.println("analyzing " + project.getName());
				analyzeProject(project); 
			}
		}

		System.out.println("Done.");
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("It took " + duration / 1000000 / 1000 + " seconds");
		return null;
	}

	@Override
	public void stop() {
	}

	private void setup() {

	}

	/**
	 * Perform static analysis on the Java project
	 * 
	 * @param project
	 * @throws JavaModelException
	 */
	private void analyzeProject(IProject project) throws JavaModelException {
		IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();
		for (IPackageFragment mypackage : packages) {
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				if (mypackage.getElementName().toLowerCase().contains("test")) {
					continue;
				}
				analyze(mypackage);
			}
		}
	}

	/**
	 * Analyze data usage of each Spring entry function
	 * 
	 * @param mypackage
	 * @throws JavaModelException
	 */
	private void analyze(IPackageFragment mypackage) throws JavaModelException {
		for (ICompilationUnit unit : mypackage.getCompilationUnits()) {

			if (unit.getElementName().contains("test")
					|| (unit.getElementName().contains("IT") && !unit.getElementName().contains("ITenant")
							|| (unit.getElementName().contains("Test"))))
				continue;

			CompilationUnit parsedUnit = parse(unit);
			EqualHashCode exVisitor = new EqualHashCode(mypackage, unit, parsedUnit,this); //this is for 1th pattern
			UselessConditionVisitor uselessConditionVisitor = new UselessConditionVisitor(mypackage, unit, parsedUnit);// this is for 3th pattern
			SameCatchVisitor sameCatchVisitor = new SameCatchVisitor(mypackage, unit, parsedUnit,this); //this is for 4th pattern 
			CloseStream closeStream= new CloseStream(mypackage, unit, parsedUnit,this); //this is for 2th pattern


			parsedUnit.accept(exVisitor);
			parsedUnit.accept(uselessConditionVisitor);			
			
			if(exVisitor.hasEqual==true && exVisitor.hasHashcode==false) { //this is for 1th pattern
		        System.out.println("EqualHashCodeBug");   //this is for 1th pattern
		        System.out.println(exVisitor.str);    //this is for 1th pattern
				try {
					HelperClass.fileAppendMethod(fileName, exVisitor.str);
				} catch (IOException e) {
					e.printStackTrace();
				}
		      }//this is for 1th pattern
			
			for(Entry<IVariableBinding, VariableTrack> entry:closeStream.booleanVariablesMap.entrySet()) {
				if(!entry.getValue().HasAssignment()) { //this is for 2th pattern
					System.out.println(entry.getValue().lineOfCode); //this is for 2th pattern
					cont++;
					System.out.println(cont);
				} //this is for 2th pattern

			}
			
			
			
			
			
			
		}

	}

	/**
	 * Reads a ICompilationUnit and creates the AST DOM for manipulating the Java
	 * source file
	 * 
	 * @param unit
	 * @return
	 */

	private CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}
}