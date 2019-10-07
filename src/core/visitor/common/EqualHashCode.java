package core.visitor.common;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import antipattern.detection.run.Application;

import org.eclipse.jdt.core.IPackageFragment;


import core.datastructure.impl.Method;

import core.helper.ObjectCreationHelper;

public class EqualHashCode extends ASTVisitor {
	
	
	private String sysName = "cloudstack"; //Name of the project
	private String className = "";
	private CompilationUnit parsedunit;
	public Application app;
	public boolean hasEqual=false;
	public boolean hasHashcode=false;
	public String str;
		
	public EqualHashCode(IPackageFragment packageFrag, ICompilationUnit unit, CompilationUnit parsedunit,Application app ) {
		
		this.app=app;
		//className = unit.getElementName().split("\\.")[0];
		this.parsedunit = parsedunit;
	}
	
	public boolean visit(MethodDeclaration method) {
		IMethodBinding binding = method.resolveBinding();
        if (binding != null) {
            ITypeBinding type = binding.getDeclaringClass();
            if (type != null) {
                className = type.getName();
            }
        }
		final Method callsite = ObjectCreationHelper.createMethodFromMethodDeclaration(method, className);
		
		String ar=method.getName().toString();
		if(ar.equals("equals")) {
			hasEqual=true;
			int lineNumber = parsedunit.getLineNumber(method.getStartPosition());

			 str = "<system>" + sysName + "</system>" + "<callsite>" + callsite
					+ "</callsite>" + "<line>" + lineNumber + "</line>";
		}
		
		if(ar.equals("hashCode")) {
			hasHashcode=true;			
		}		
		return false;
	}


	
	

	

}
