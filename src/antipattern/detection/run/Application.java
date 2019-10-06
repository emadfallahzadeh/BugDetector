package antipattern.detection.run;

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


import core.visitor.common.EmptyCatchVisitor;
import core.visitor.common.EqualHashCode;


public class Application implements IApplication {
	
	public static int aa=0;
	public int cont=0;

	

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

			//EmptyCatchVisitor exVisitor = new EmptyCatchVisitor(mypackage, unit, parsedUnit);
			if(exVisitor.hasEqual==true && exVisitor.hasHashcode==false) { //this is for 1th pattern
		        System.out.println("bug report");   //this is for 1th pattern
		        System.out.println(exVisitor.str);    //this is for 1th pattern
		      }  //this is for 1th pattern
						
			

			parsedUnit.accept(exVisitor);

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
