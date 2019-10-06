package core.visitor.common;

public class VariableTrack {
	boolean hasAssignment = false;
	public void DeclareAssignemnt() {
		hasAssignment = true;
	}
	public boolean HasAssignment() {
		return hasAssignment;
	}
}