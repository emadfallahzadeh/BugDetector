package core.visitor.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.IPackageFragment;


import core.datastructure.impl.Method;

import core.helper.HelperClass;
import core.helper.ObjectCreationHelper;

public class EmptyCatchVisitor extends ASTVisitor {

	private String fileName = "empty-catch-Kafka.txt"; //Name of the output file that you wish to have
	private String sysName = "Kafka-2.3.0"; //Name of the project
	private String className = "";
	private CompilationUnit parsedunit;

	private Map<IVariableBinding, VariableTrack> booleanVariablesMap = new HashMap<IVariableBinding, VariableTrack>();
	private Method callsite;
	
	private int emptyCatchBlockCount = 0;
	private int uselessConditionCount = 0;
	
	public int getUselessConditionCount() {
		return uselessConditionCount;
	}
	
	public EmptyCatchVisitor(IPackageFragment packageFrag, ICompilationUnit unit, CompilationUnit parsedunit) {

		
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
		callsite = ObjectCreationHelper.createMethodFromMethodDeclaration(method, className);

		if (method.getBody() != null) {
			method.getBody().accept(new ASTVisitor() {
				@Override
				public boolean visit(VariableDeclarationStatement variableDeclaration) {
					if(variableDeclaration.getType().toString().equals("boolean")) { 
						for (Iterator iterator = variableDeclaration.fragments().iterator(); iterator.hasNext();) {
							VariableDeclarationFragment declarationFragment = (VariableDeclarationFragment) iterator.next();
							IVariableBinding variableBinding = declarationFragment.resolveBinding();
							booleanVariablesMap.put(variableBinding, new VariableTrack());
						}				
					}
					return false;
				}				
				@Override
				public boolean visit(Assignment assignment) {
					if(assignment.getLeftHandSide() instanceof SimpleName) {
						IBinding binding = ((SimpleName)assignment.getLeftHandSide()).resolveBinding();	
						if(booleanVariablesMap.containsKey(binding)) {
							VariableTrack variableTrack = booleanVariablesMap.get(binding);
							variableTrack.DeclareAssignemnt();
						}	
					}
					return false;
				}				
				@Override
				public boolean visit(IfStatement ifS) { 
					Expression ifExpression = ifS.getExpression();
					int lineNumber = parsedunit.getLineNumber(ifS.getStartPosition());
					if(ifExpression instanceof BooleanLiteral) //if(true) if(false)   
						saveBug(BugType.UselessCondition, lineNumber);					
					else if(ifExpression instanceof SimpleName) {  
						IBinding binding = ((SimpleName)ifS.getExpression()).resolveBinding(); //if(a) [a=true/false]
						if(booleanVariablesMap.containsKey(binding) && !booleanVariablesMap.get(binding).HasAssignment())
							saveBug(BugType.UselessCondition, lineNumber);					
					}					
					else if(ifExpression instanceof InfixExpression) { 
						  Expression leftOperand = ((InfixExpression)ifS.getExpression()).getLeftOperand();
						  Expression rightOperand = ((InfixExpression)ifS.getExpression()).getRightOperand();
						  if(leftOperand instanceof SimpleName && rightOperand instanceof BooleanLiteral) { //if(a == true)
							  IBinding binding = ((SimpleName)leftOperand).resolveBinding();
							  if(booleanVariablesMap.containsKey(binding) && !booleanVariablesMap.get(binding).HasAssignment())
								  saveBug(BugType.UselessCondition, lineNumber);									  
						  }	
						  else if(leftOperand instanceof BooleanLiteral && rightOperand instanceof SimpleName) { //if(true == a)
							  IBinding binding = ((SimpleName)rightOperand).resolveBinding();
							  if(booleanVariablesMap.containsKey(binding) && !booleanVariablesMap.get(binding).HasAssignment())
								  saveBug(BugType.UselessCondition, lineNumber);								  
						  }	
					}					  
					
					return true;		
				}
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

	private void saveBug(BugType bugType, int lineNumber) {
		System.out.println(bugType + "  " + callsite + "  " + lineNumber);
		String str = "\n<bugType>"+ bugType + "</bugType>"
				   + "<system>" + sysName + "</system>" 
				   + "<callsite>" + callsite + "</callsite>" 
				   + "<line>" + lineNumber + "</line>\n";
		try {
			HelperClass.fileAppendMethod(fileName, str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		switch(bugType) {
			case UselessCondition: uselessConditionCount++;
			break;
		}
	}
}

enum BugType{
	EmptyCatchBlock,
	UselessCondition
}
