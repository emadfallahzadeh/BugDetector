package core.visitor.common;

public class VariableTrack {
	boolean hasAssignment = false;
	public String lineOfCode;

	
	public VariableTrack(String lineOfCode) {
		this.lineOfCode=lineOfCode;
		
	}

	public VariableTrack() {
		
		
	}

	
	
	public void DeclareAssignemnt() {
		hasAssignment = true;
	}
	public boolean HasAssignment() {
		return hasAssignment;
	}
}