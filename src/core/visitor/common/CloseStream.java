package core.visitor.common;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import antipattern.detection.run.Application;

import org.eclipse.jdt.core.IPackageFragment;


import core.datastructure.impl.Method;

import core.helper.ObjectCreationHelper;


public class CloseStream extends ASTVisitor {
	
	
	private String sysName = "cloud-stack"; //Name of the project
	private String className = "";
	private CompilationUnit parsedunit;
	public Application app;

	
	
	public Map<IVariableBinding, VariableTrack> booleanVariablesMap = new HashMap<IVariableBinding, VariableTrack>();

	
	 public CloseStream(IPackageFragment packageFrag, ICompilationUnit unit, CompilationUnit parsedunit,Application app ) {
		 
			this.app=app;
	//		className = unit.getElementName().split("\\.")[0];
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

			if (method.getBody() != null) {
					
						
				method.getBody().accept(new ASTVisitor() {
					
									
					@Override

					public boolean visit(VariableDeclarationStatement variableDeclaration) {
						
						String ar=variableDeclaration.getType().toString();
						
						if(ar.equals("DataInputStream")||ar.equals("DataOutputStream")||ar.equals("FileDescriptor")||ar.equals("FileInputStream")||ar.equals("FileOutputStream")||ar.equals("FilePermission")||ar.equals("FileReader")||ar.equals("FileWriter")||ar.equals("FilterInputStream")||ar.equals("FilterOutputStream")||ar.equals("FilterReader")||ar.equals("FilterWriter")||ar.equals("InputStream")||ar.equals("InputStreamReader")||ar.equals("LineNumberInputStream")||ar.equals("LineNumberReader")||ar.equals("ObjectInputStream")||ar.equals("ObjectInputStream.GetField")||ar.equals("ObjectOutputStream")||ar.equals("ObjectOutputStream.PutField")||ar.equals("ObjectStreamClass")||ar.equals("ObjectStreamField")||ar.equals("OutputStreamWriter")||ar.equals("PipedInputStream")||ar.equals("PipedOutputStream")||ar.equals("PipedReader")||ar.equals("PipedWriter")||ar.equals("PrintStream")||ar.equals("PrintWriter")||ar.equals("PushbackInputStream")||ar.equals("PushbackReader")||ar.equals("RandomAccessFile")||ar.equals("Reader")||ar.equals("SequenceInputStream")||ar.equals("SerializablePermission")||ar.equals("StreamTokenizer")||ar.equals("StringBufferInputStream")||ar.equals("StringReader")||ar.equals("StringWriter")||ar.equals("Writer")||ar.equals("BufferedWriter")||ar.equals("ByteArrayInputStream")||ar.equals("ByteArrayOutputStream")||ar.equals("CharArrayReader")||ar.equals("CharArrayWriter")||ar.equals("BufferedWriter")||ar.equals("BufferedReader")||ar.equals("BufferedOutputStream")||ar.equals("BufferedInputStream")) { 

							for (Iterator iterator = variableDeclaration.fragments().iterator(); iterator.hasNext();) {
								VariableDeclarationFragment declarationFragment = (VariableDeclarationFragment) iterator.next();
								IVariableBinding variableBinding = declarationFragment.resolveBinding();
								int lineNumber = parsedunit.getLineNumber(variableDeclaration.getStartPosition());
								String str = "<system>" + sysName + "</system>" + "<callsite>" + callsite
									+ "</callsite>" + "<line>" + lineNumber + "</line>";

								booleanVariablesMap.put(variableBinding, new VariableTrack(str));

								
							}				
						}

						


						return true;
					}
					
					
					
					
					@Override
					public boolean visit(SimpleName method) {
				
						return true;
					}		
					
					

								
								
								@Override
								public boolean visit(MethodInvocation method) {

									
									String w=(method.getName().getFullyQualifiedName().toString());
									if(w.equals("close")) {
										if(method.getExpression() instanceof SimpleName) {
											IBinding binding = ((SimpleName)method.getExpression()).resolveBinding();	
											if(booleanVariablesMap.containsKey(binding)) {

												booleanVariablesMap.get(binding).DeclareAssignemnt();
											}	


											
											
										}

									}
									
									return true;
								}		

	
					
					
					

				});

			}
			

			
			return false;
		}


	
	
	
	

}
