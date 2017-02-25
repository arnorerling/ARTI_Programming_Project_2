import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class Node implements Comparable {

	public static HashMap<BoardState, Node[]> stateChildren;

	//Node parent;
	int value;
	int[] moveTo;
	BoardState state;
	Node[] children;

	private boolean value_set = false;
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
			this.state.legalMoves();
			this.children = new Node[this.state.legalMovesCache.size()];
			for(int i = 0; i < this.state.legalMovesCache.size(); i++) {
				this.children[i] = new Node(0, this.state.legalMovesCache.get(i), this.state.executeMove(this.state.legalMovesCache.get(i)));
			}
			Node.stateChildren.put(this.state, this.children);
			return false;
		}

		return true;
	}

	public void setValue(int _value) {
		value_set = true;
		this.value = _value;
	}

	@Override
	public int compareTo(Object obj) {
		Node other = (Node) obj;

		if(this.value_set && other.value_set) {
			return Integer.compare(this.value, other.value);
		}

		if(this.value_set) {
			return Integer.compare(this.value, other.state.evaluation);
		}
		
		return Integer.compare(this.state.evaluation, other.state.evaluation);
	}

	public String toString() {
		return "value: " + this.value + " State: " + state.toString() + " location (" + moveTo[2] + ", " + moveTo[3] + ")";
	}

}
