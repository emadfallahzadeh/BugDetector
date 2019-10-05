package core.visitor.common;

import java.io.IOException;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.IPackageFragment;


import core.datastructure.impl.Method;

import core.helper.HelperClass;
import core.helper.ObjectCreationHelper;

public class EmptyCatchVisitor extends ASTVisitor {

	private String fileName = "empty-catch-Kafka.txt"; //Name of the output file that you wish to have
	private String sysName = "Kafka-2.3.0"; //Name of the project
	private String className = "";
	private CompilationUnit parsedunit;

	public EmptyCatchVisitor(IPackageFragment packageFrag, ICompilationUnit unit, CompilationUnit parsedunit) {

		className = unit.getElementName().split("\\.")[0];
		this.parsedunit = parsedunit;

	}

	public boolean visit(MethodDeclaration method) {

		final Method callsite = ObjectCreationHelper.createMethodFromMethodDeclaration(method, className);

		if (method.getBody() != null) {
			method.getBody().accept(new ASTVisitor() {
				@Override
				public boolean visit(CatchClause ca) {

					String catchBody = ca.getBody().toString().replaceAll("\\{", "").replaceAll("\\}", "")
							.replaceAll(" ", "").replaceAll("\r", "").replaceAll("\n", "");
					int lineNumber = parsedunit.getLineNumber(ca.getStartPosition());
					if (catchBody.equals("")) {

						//System.out.println("Empty Catch Block:" + callsite + " :" + lineNumber);
						String str = "<system>" + sysName + "</system>" + "<callsite>" + callsite
								+ "</callsite>" + "<line>" + lineNumber + "</line>";
						try {
							HelperClass.fileAppendMethod(fileName, str);
						} catch (IOException e) {
							e.printStackTrace();
						}

					}

					return true;
				}

			});

		}

		return true;
	}

}
