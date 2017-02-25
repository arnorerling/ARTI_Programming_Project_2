import java.util.Arrays;
import java.util.Collections;

public class AlphaBetaThread implements Runnable {

	private Node rootNode;
	private Node nextRoot;
	private boolean isWhite;
	private int statesGenerated;
	private int nodesExamined;
	private boolean isDead;
	private int currentDepth = 1;

	public AlphaBetaThread(Node _rootNode, boolean _isWhite) {
		this.rootNode = _rootNode;
		this.nextRoot = null;
		this.isWhite = _isWhite;
		this.statesGenerated = 0;
		this.nodesExamined = 0;
		this.isDead = false;
	}

	public void run() {

		currentDepth = 1;
		int alpha = -SmartAgent.winBonus;
		int beta = SmartAgent.winBonus;


		while(true) {

			try {

				this.rootNode.setValue(AlphaBeta(this.rootNode, currentDepth, alpha, beta, this.isWhite));

				for(Node child : this.rootNode.children) {
					if(child.value == this.rootNode.value) {
						this.nextRoot = child;
						System.out.println("Iterative deepening at depth: " + currentDepth);
						break;
					}
				}

				if((this.isWhite && this.rootNode.value ==  SmartAgent.winBonus) || (!this.isWhite && this.rootNode.value == -SmartAgent.winBonus)) {
					// we found a win condition, we won't do another round of iterative deepening
					break;
				}

				if(currentDepth > 50) {
					// Not going to hit this depth in our current maps, so we exit
					break;
				}
				currentDepth += 1;

			} catch(OutOfMemoryError e) {
				System.out.println("Caught OutOfMemoryError in AlphaBetaThread. Returning what we have so far. Error message: " + e.getMessage());
				//e.printStackTrace();
				break;

			} catch (InterruptedException e) {
				System.out.println("InterruptedException caught in AlphaBetaThread. Most likely out of time. Error message: " + e.getMessage());
				break;
				//e.printStackTrace();
			}
		}

		this.isDead = true;
		return;
	}

	public int AlphaBeta(Node n, int depth, int alpha, int beta, boolean maxPlayer) throws InterruptedException {

		this.nodesExamined += 1;

		if(Thread.currentThread().isInterrupted()) {
			throw new InterruptedException("AlphaBeta stopping, out of time!");
		}

		if(depth == 0) {
			n.setValue(n.state.evaluation);
			return n.state.evaluation;
		}

		if(n.state.isGoal()) {
			n.setValue(n.state.evaluation);
			return n.state.evaluation;
		}

		int v;

		if(maxPlayer) {

			v = -SmartAgent.winBonus;
			if(!n.expandChildren()) {
				this.statesGenerated += n.children.length;
			}

			Arrays.sort(n.children, Collections.reverseOrder());
			for(Node s : n.children) {

				s.setValue(AlphaBeta(s, depth-1, alpha, beta, false));
				//System.out.println(s);
				v = Math.max(v, s.value);
				alpha = Math.max(alpha, s.value);

				if(beta <= alpha) {
					//System.out.print("max player prune");
					break;
				}
			}

		} else {

			v = SmartAgent.winBonus;
			if(!n.expandChildren()) {
				this.statesGenerated += n.children.length;
			}

			Arrays.sort(n.children);
			for(Node s : n.children) {

				//System.out.println("in min and node is " + s);
				s.setValue(AlphaBeta(s, depth-1, alpha, beta, true));
				//System.out.println(s);
				v = Math.min(v, s.value);
				beta = Math.min(beta, v);

				if(beta <= alpha) {
					//System.out.print("max player prune");
					break;
				}
			}
		}

		return v;
	}

	public Node getNextRoot() {
		return this.nextRoot;
	}

	public int getStatesGenerated() {
		return this.statesGenerated;
	}

	public int getNodesExamined() {
		return this.nodesExamined;
	}

	public boolean isDead() {
		return this.isDead;
	}

}