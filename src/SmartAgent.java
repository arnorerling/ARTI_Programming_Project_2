import java.util.ArrayList;
public class SmartAgent implements Agent
{

	private String role; // the name of this agent's role (white or black)
	private int playclock; // this is how much time (in seconds) we have before nextAction needs to return a move
	private boolean myTurn; // whether it is this agent's turn or not
	private int width, height; // dimensions of the board
	private Node rootnode;
	private boolean isWhite;
	/*
		init(String role, int playclock) is called once before you have to select the first action. Use it to initialize the agent. role is either "white" or "black" and playclock is the number of seconds after which nextAction must return.
	*/
    public void init(String role, int width, int height, int playclock) {
		this.role = role;
		this.playclock = playclock;
		myTurn = !role.equals("white");
		isWhite = role.equals("white");
		this.width = width;
		this.height = height;
		// TODO: add your own initialization code here

		// intiializes all points of blacks and whites
		ArrayList<Point> white = new ArrayList<Point>();
		ArrayList<Point> black = new ArrayList<Point>();
		for(int i = 1; i <= width; i++) {
			white.add(new Point(i,1));
			white.add(new Point(i,2));
			black.add(new Point(i,height));
			black.add(new Point(i,height-1));
		}


		// pass in true because whiteplaying starts as true
		this.rootnode = new Node(null, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, null, new BoardState(width, height, white, black, true));
		
    }

	// lastMove is null the first time nextAction gets called (in the initial state)
    // otherwise it contains the coordinates x1,y1,x2,y2 of the move that the last player did
    public String nextAction(int[] lastMove) {

    	System.out.println("NEXT ACTION------------------");

    	if (lastMove != null && !myTurn) {
    		int x1 = lastMove[0], y1 = lastMove[1], x2 = lastMove[2], y2 = lastMove[3];
    		String roleOfLastPlayer;
    		if (myTurn && role.equals("white") || !myTurn && role.equals("black")) {
    			roleOfLastPlayer = "white";
    		} else {
    			roleOfLastPlayer = "black";
    		}
   			System.out.println(roleOfLastPlayer + " moved from " + x1 + "," + y1 + " to " + x2 + "," + y2);
    		// TODO: 1. update your internal world model according to the action that was just executed
    		
    		this.rootnode.expandChildren();
    		BoardState temp = this.rootnode.state.executeMove(lastMove);
    		//System.out.println(this.rootnode);
    		// System.out.println("lastmove map");
    		// asciiWorld(temp);
    		// System.out.println("");
    		// System.out.println("rootnode");
    		// asciiWorld(this.rootnode.state);
    		// System.out.println("");

    		if(temp == null) {
    			System.out.println("temp became null");
    		}

    		for(Node child: this.rootnode.children)
    		{
    			if(child.state.equals(temp)){
    				this.rootnode = child;
    				System.out.println("assigned new root after other played moved");
    				this.rootnode.parent = null;
    				break;
    			}
    		}

    		System.out.println(this.rootnode);
    	}
		
    	// update turn (above that line it myTurn is still for the previous state)
    	
		myTurn = !myTurn;
		if (myTurn) {
			// TODO: 2. run alpha-beta search to determine the best move
			int[] moveToTake = new int[4];
			Node nextRoot = null;
			//for(int i = 1; i < 5; i++) {
				this.rootnode.value = AlphaBeta(this.rootnode, 8, Integer.MIN_VALUE+1, Integer.MAX_VALUE-1, isWhite);
				for(Node child : this.rootnode.children) {
					//System.out.println("child value is " + child.value);
					if(child.value == this.rootnode.value) {
						moveToTake = child.moveTo;
						nextRoot = child;
						break;
					}
				}
			//}
				System.out.println(this.rootnode.value);
				this.rootnode = nextRoot;
			System.out.println("Value found was: " + this.rootnode.value);
			asciiWorld(this.rootnode.state);
			return "(move "+moveToTake[0]+" "+moveToTake[1]+" "+moveToTake[2]+" "+moveToTake[3]+")";
		} else {
			//asciiWorld(this.rootnode.state);
			return "noop";
		}
	}

	public int AlphaBeta(Node n, int depth, int alpha, int beta, boolean maxPlayer) {

		if(depth == 0 || n.state.isGoal()) {

			if(!maxPlayer) {
				return n.state.evaluate();
			} else {
				return n.state.evaluate();
			}
		}

		int v;

		if(maxPlayer) {

			//System.out.println("maxplayer");

			v = Integer.MIN_VALUE + 10;
			n.expandChildren();

			for(Node s : n.children) {

				s.value = AlphaBeta(s, depth-1, alpha, beta, false);
				//System.out.println(s);
				v = Math.max(v, s.value);
				alpha = Math.max(alpha, s.value);

				if(beta <= alpha) {
					break;
				}
			}

		} else {

			//System.out.println("minplayer");

			v = Integer.MAX_VALUE-10;
			n.expandChildren();

			for(Node s : n.children) {

				s.value = AlphaBeta(s, depth-1, alpha, beta, true);
				//System.out.println(s);
				v = Math.min(v, s.value);
				beta = Math.min(beta, v);

				if(beta <= alpha) {
					break;
				}
			}
		}

		return v;
	}

	// public int AlphaBeta(int depth, Node n, int alpha, int beta) {

	// 	if(n.state.isGoal() || depth <= 0) {
	// 		System.out.println("EVALUATING" +  evaluate(n));
	// 		return evaluate(n);
	// 	}
	// 	int bestValue = Integer.MIN_VALUE+1;
	// 	n.expandChildren();
	// 	int value;
	// 	for(Node successor : n.children) {
	// 		value = -AlphaBeta(depth-1, successor, -beta, -alpha);
	// 		bestValue = Math.max(value, bestValue);

	// 		successor.value = value;

	// 		if(bestValue > alpha) {
	// 			alpha = bestValue;
	// 			//System.out.println("I am at depth " + depth + ", alpha " + alpha + ", beta " + beta);
	// 			if(alpha >= beta){
	// 				break;
	// 			}
	// 		} else {
	// 			//System.out.println("I am at depth " + depth + ", alpha " + alpha + ", beta " + beta);
	// 		}

	// 	}

	// 	return bestValue;
	// }

	private int evaluate(Node n){
		if((n.state.whitePlaying && isWhite) || (!n.state.whitePlaying && !isWhite)) {
			return n.state.evaluate();
		}
		return -n.state.evaluate();
	}

	private void asciiWorld(BoardState state) {

		for(int i = height; i > 0; i--) {

			for(int j = 1; j <= width; j++) {

				if(state.whites.contains(new Point(j, i))) {
					System.out.print("W");
					continue;
				}

				if(state.blacks.contains(new Point(j, i))) {
					System.out.print("B");
					continue;
				}

				System.out.print("_");
			}

			System.out.println("");
		}
	}

	// is called when the game is over or the match is aborted
	@Override
	public void cleanup() {
		// TODO: cleanup so that the agent is ready for the next match
	}

}
