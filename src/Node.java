import java.util.List;
import java.util.ArrayList;

public class Node {

	Node parent;
	int value;
	int alpha;
	int beta;
	int[] moveTo;
	BoardState state;
	Node[] children;
	Boolean alreadyExpanded;

	public Node (Node _parent, int _value, int _alpha, int _beta, int[] _moveTo, BoardState _state) {
		this.parent = _parent;
		this.value = _value;
		this.alpha = _alpha;
		this.beta = _beta;
		this.moveTo = _moveTo;
		this.state = _state;
		this.alreadyExpanded = false;
		return;
	}

	public void expandChildren() {
		if(alreadyExpanded) {
			return;
		}
		alreadyExpanded = true;
		ArrayList<int[]> legalMoves = this.state.legalMoves();
		this.children = new Node[legalMoves.size()];
		for(int i = 0; i < legalMoves.size(); i++) {
			children[i] = new Node(this, 0, this.alpha, this.beta, legalMoves.get(i), this.state.executeMove(legalMoves.get(i)));
		}
		return;
	}

	public String toString() {
		return "value: " + this.value + "State: " + state.toString() + " location (" + moveTo[2] + ", " + moveTo[3] + ")";
	}

}
