package core.visitor.common;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TryStatement;

import antipattern.detection.run.Application;
import core.datastructure.impl.Method;
import core.helper.ObjectCreationHelper;

public class SameCatchVisitor extends ASTVisitor {
	

	private String sysName = "cloudstack"; //Name of the project
	private String className = "";
	private CompilationUnit parsedunit;
	public Application app;
	public boolean hasEqual=false;
	public boolean hasHashcode=false;
	
	public Map<IVariableBinding, VariableTrack> booleanVariablesMap = new HashMap<IVariableBinding, VariableTrack>();
	
	
	public SameCatchVisitor(IPackageFragment packageFrag, ICompilationUnit unit, CompilationUnit parsedunit,Application app ) {
		
		
		this.app=app;

		className = unit.getElementName().split("\\.")[0];
		this.parsedunit = parsedunit;


	}

	
	
	public boolean visit(MethodDeclaration method) {

		final Method callsite = ObjectCreationHelper.createMethodFromMethodDeclaration(method, className);

		if (method.getBody() != null) {
			method.getBody().accept(new ASTVisitor() {
				@Override
				public boolean visit(TryStatement ts) {
			
				
					ts.accept(new ASTVisitor() {
						String[] bodies =new String[100];
						int conter=0;
						boolean ch=false;
						boolean cheking=false;
						

						
						
						@Override
						public boolean visit(CatchClause ca) {
							
							
							
							ca.getBody().accept(new ASTVisitor() {
								
								@Override
								public boolean visit(SimpleName s) {
									if(s.toString().equals("e") || s.toString().equals("ex")) {
										cheking=true;
										return false;
									}
									return false;
								}
								
								
								
								
							
								
								
								
							});
							
							String body = ca.getBody().toString().replaceAll("\\{", "").replaceAll("\\}", "")
									.replaceAll(" ", "").replaceAll("\r", "").replaceAll("\n", "");
							if(conter!=0) {
								for(int i=0;i<conter;i++) {
									if(bodies[i].equals(body) && ch==false && cheking==false) {
										System.out.println("This is a problem");
										ch=true;
										int lineNumber = parsedunit.getLineNumber(ca.getStartPosition());

										String str = "<system>" + sysName + "</system>" + "<callsite>" + callsite
												+ "</callsite>" + "<line>" + lineNumber + "</line>";
										
										System.out.println(str);
										System.out.println(cheking);
										app.aa++;
										System.out.println(app.aa);
										break;
										
										

									}
								}
							}
							bodies[conter]=body;
							conter++;

							return false;
						}

						
					});
					

					return false;
				}

			});

		}

		return true;
	}


	
	
	
	
	
	
	
	

}
