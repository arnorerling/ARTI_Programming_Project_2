public class AlphaBetaThread implements Runnable {

	private Node rootNode;
	private Node nextRoot;
	private boolean isWhite;
	private int statesGenerated;
	private int statesExamined;
	private boolean isDead;

	public AlphaBetaThread(Node _rootNode, boolean _isWhite) {
		this.rootNode = _rootNode;
		this.nextRoot = null;
		this.isWhite = _isWhite;
		this.statesGenerated = 0;
		this.statesExamined = 0;
		this.isDead = false;
	}

	public void run() {

		int currentDepth = 1;

		while(true) {

			try {
				//System.out.println("TIME LEFT: " + (timeTilReturn - currentTime));
				// if(currentDepth > 50) {// if have more time than what last iteration took * 2 just quit out.
				// 	System.out.println("iterative deepening stopped at depth " + currentDepth);
				// 	break;
				// } 
				this.rootNode.value = AlphaBeta(this.rootNode, currentDepth, Integer.MIN_VALUE+1, Integer.MAX_VALUE-1, this.isWhite);
				
				//System.out.println("FINISHED A ITERATION IN ITERATIVE DEEPENING D: "+currentDepth);
				//System.out.println("LAST RUNTIME: " + lastRunTime);
				for(Node child : this.rootNode.children) {
					if(child.value == this.rootNode.value) {
						this.nextRoot = child;
						System.out.println("at depth: " + currentDepth);
						break;
					}
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

		this.statesExamined += 1;

		if(Thread.currentThread().isInterrupted()) {
			throw new InterruptedException("AlphaBeta stopping, out of time!");
		}

		if(depth == 0) {
			//System.out.println("depth has reached zero");
			return n.state.evaluation;
		}

		if(n.state.isGoal()) {
			//System.out.println("state goal reached");
			return n.state.evaluation;
		}

		int v;

		if(maxPlayer) {

			//System.out.println("maxplayer");

			v = Integer.MIN_VALUE + 10;
			if(!n.expandChildren()) {
				this.statesGenerated += n.children.length;
			}

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

			//System.out.println("minplayer");

			v = Integer.MAX_VALUE-10;
			if(!n.expandChildren()) {
				this.statesGenerated += n.children.length;
			}

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

	public int getStatesExamined() {
		return this.statesExamined;
	}

	public boolean isDead() {
		return this.isDead;
	}

}