import java.util.ArrayList;
import java.util.HashMap;
public class SmartAgent implements Agent
{

	private String role; // the name of this agent's role (white or black)
	private int playclock; // this is how much time (in seconds) we have before nextAction needs to return a move
	private boolean myTurn; // whether it is this agent's turn or not
	private int width, height; // dimensions of the board
	private Node rootNode;
	private boolean isWhite;
	private int cutoffTime;
	/* irssi
		init(String role, int playclock) is called once before you have to select the first action. Use it to initialize the agent. role is either "white" or "black" and playclock is the number of seconds after which nextAction must return.
	*/
    public void init(String role, int width, int height, int playclock) {
		this.role = role;
		this.playclock = playclock;
		myTurn = !role.equals("white");
		isWhite = role.equals("white");
		this.width = width;
		this.height = height;
		BoardState.width = width;
		BoardState.height = height;
		Node.stateChildren = new HashMap<BoardState, Node[]>();
		cutoffTime = (int)(playclock * 1000 * 0.2);
		// TODO: add your own initialization code here

		// intiializes all points of blacks and whites
		ArrayList<Point> white = new ArrayList<Point>();
		ArrayList<Point> black = new ArrayList<Point>();
		boolean[] whitesbool = new boolean[width * height];
		boolean[] blacksbool = new boolean[width * height];
		for(int i = 0; i < width; i++) {
			whitesbool[i] = true;
			whitesbool[i + width] = true;
			blacksbool[i + (width * (height-1))] = true;
			blacksbool[i + ((width) * (height-2))] = true;
		}
		// pass in true because whiteplaying starts as true
		System.out.println("init1");
		this.rootNode = new Node(0, null, new BoardState(whitesbool, blacksbool, true));
		System.out.println("init2");
    }

	// lastMove is null the first time nextAction gets called (in the initial state)
    // otherwise it contains the coordinates x1,y1,x2,y2 of the move that the last player did
    public String nextAction(int[] lastMove) {

    	System.out.println("NEXT ACTION------------------");

    	long timeTilReturn = System.currentTimeMillis() + (playclock * 1000); // playclock is in seconds so * 1000 for milliseconds.


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
    		
    		this.rootNode.expandChildren();
    		BoardState temp = this.rootNode.state.executeMove(lastMove);
    		//System.out.println(this.rootNode);
    		// System.out.println("lastmove map");
    		// asciiWorld(temp);
    		// System.out.println("");
    		// System.out.println("rootNode");
    		// asciiWorld(this.rootNode.state);
    		// System.out.println("");

    		if(temp == null) {
    			System.out.println("temp became null");
    		}

    		for(Node child: this.rootNode.children)
    		{
    			if(child.state.equals(temp)){
    				this.rootNode = child;
    				System.out.println("assigned new root after other played moved");
    				//this.rootNode.parent = null;
    				break;
    			}
    		}
			asciiWorld(this.rootNode.state);

    		System.out.println(this.rootNode);
    	}
		
    	// update turn (above that line it myTurn is still for the previous state)
	
		myTurn = !myTurn;
		if (myTurn) {
			// TODO: 2. run alpha-beta search to determine the best move
			
			//for(int i = 1; i < 5; i++) {

			int[] moveToTake = new int[4];
			long currentTime = System.currentTimeMillis();
			AlphaBetaThread abt = new AlphaBetaThread(this.rootNode, isWhite);
			Thread t = new Thread(abt);

			t.start();
			while(currentTime + cutoffTime < timeTilReturn) {

				try {
					
					Thread.sleep(500);
					if(abt.isDead()) {
						break;
					}

				}catch(InterruptedException e) {
					// Someone woke us up during sleep, that's OK
				} catch(Exception e) {
					System.out.println("Caught an exception we weren't expecting: " + e.getClass().getCanonicalName());
					e.printStackTrace();
				}

				currentTime = System.currentTimeMillis();
			}
			t.interrupt();

			try {
				t.join(1);
			} catch(InterruptedException e) {
				System.out.println("interruption during join");
			}
				
			if(abt.getNextRoot() == null) {
				System.out.println("nextRoot became null");
			}
				
			System.out.println(this.rootNode.value);
			this.rootNode = abt.getNextRoot();
			moveToTake = this.rootNode.moveTo;

			//this.rootNode.parent = null;
			System.out.println("Value found was: " + this.rootNode.value);
			System.out.println("States Generated: " + abt.getStatesGenerated());
			System.out.println("States Examined: " + abt.getStatesExamined());
			System.out.println("Nodes Generated: " + BoardState.numGenerated);
			System.out.println("HashMap size: " + Node.stateChildren.size());

			t = null;
			abt = null;
			Node.stateChildren.clear();

			return "(move "+moveToTake[0]+" "+moveToTake[1]+" "+moveToTake[2]+" "+moveToTake[3]+")";

		} else {
			asciiWorld(this.rootNode.state);
			return "noop";
		}
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

			for(int j = 0; j < width; j++) {

				if(state.whitesbool[j+((i-1)* this.width)]) {
					System.out.print("W");
					continue;
				}

				if(state.blacksbool[j+((i-1)* this.width)]) {
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

