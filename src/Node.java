import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class Node {

	public static HashMap<BoardState, Node[]> stateChildren;

	//Node parent;
	int value;
	int[] moveTo;
	BoardState state;
	Node[] children;
	//Boolean alreadyExpanded;

	public Node (int _value, int[] _moveTo, BoardState _state) {
		//this.parent = _parent;
		this.value = _value;
		this.moveTo = _moveTo;
		this.state = _state;
		this.children = null;
		return;
	}

	// returns true if children are already expanded
	public boolean expandChildren() {
		

		if(this.children != null) {
			return true;
		}

		this.children = Node.stateChildren.get(this.state);

		if(children == null) {
			ArrayList<int[]> legalMoves = this.state.legalMoves();
			this.children = new Node[legalMoves.size()];
			for(int i = 0; i < legalMoves.size(); i++) {
				children[i] = new Node(0, legalMoves.get(i), this.state.executeMove(legalMoves.get(i)));
			}
			Node.stateChildren.put(this.state, this.children);
			return false;
		}

		return true;
	}

	public String toString() {
		return "value: " + this.value + " State: " + state.toString() + " location (" + moveTo[2] + ", " + moveTo[3] + ")";
	}

}
