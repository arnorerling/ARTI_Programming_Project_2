import java.util.Arrays;
import java.util.Collections;

public class AlphaBetaThread implements Runnable {

	private Node rootNode;
	private Node nextRoot;
	private boolean isWhite;
	private int statesGenerated;
	private int nodesExamined;
	private boolean isDead;

	public AlphaBetaThread(Node _rootNode, boolean _isWhite) {
		this.rootNode = _rootNode;
		this.nextRoot = null;
		this.isWhite = _isWhite;
		this.statesGenerated = 0;
		this.nodesExamined = 0;
		this.isDead = false;
	}

	public void run() {

		int currentDepth = 1;

		while(true) {

			try {

				this.rootNode.value = AlphaBeta(this.rootNode, currentDepth, Integer.MIN_VALUE+1, Integer.MAX_VALUE-1, this.isWhite);

				for(Node child : this.rootNode.children) {
					if(child.value == this.rootNode.value) {
						this.nextRoot = child;
						System.out.println("at depth: " + currentDepth);
						break;
					}
				}

				if(currentDepth > 50) {
					break;
					// Not going to hit this depth in our current maps
				}
				currentDepth += 1;

			} catch(OutOfMemoryError e) {
				System.out.println("Caught OutOfMemoryError, returning what we have so far. Stack trace follows");
				e.printStackTrace();
				break;

			} catch (InterruptedException e) {
				System.out.println("InterruptedException caught in thread. Gooodbye.");
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
			return n.state.evaluation;
		}

		if(n.state.isGoal()) {
			return n.state.evaluation;
		}

		int v;

		if(maxPlayer) {

			v = Integer.MIN_VALUE + 10;
			if(!n.expandChildren()) {
				this.statesGenerated += n.children.length;
			}

			Arrays.sort(n.children, Collections.reverseOrder());
			for(Node s : n.children) {

				s.value = AlphaBeta(s, depth-1, alpha, beta, false);
				//System.out.println(s);
				v = Math.max(v, s.value);
				alpha = Math.max(alpha, s.value);

				if(beta <= alpha) {
					//System.out.print("max player prune");
					break;
				}
			}

		} else {

			v = Integer.MAX_VALUE-10;
			if(!n.expandChildren()) {
				this.statesGenerated += n.children.length;
			}

			Arrays.sort(n.children);
			for(Node s : n.children) {

				s.value = AlphaBeta(s, depth-1, alpha, beta, true);
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