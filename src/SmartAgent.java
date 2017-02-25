import java.util.ArrayList;
import java.util.HashMap;
public class SmartAgent implements Agent
{

	private String role; // the name of this agent's role (white or black)
	private int playclock; // this is how much time (in seconds) we have before nextAction needs to return a move
	private boolean myTurn; // whether it is this agent's turn or not
	public static int width, height; // dimensions of the board
	public static ArrayList<int[]> cellScores;
	public static int[] columnTypes;
	private Node rootNode;
	private boolean isWhite;
	private int cutoffTime;
	public static final int incrementInitial = 5;
	public static final int existenceBonus = 10;
	public static final int winBonus = Integer.MAX_VALUE - 1;
	/* 
		init(String role, int playclock) is called once before you have to select the first action. Use it to initialize the agent.
		role is either "white" or "black" and playclock is the number of seconds after which nextAction must return.
	*/
    public void init(String role, int width, int height, int playclock) {
		this.role = role;
		this.playclock = playclock;
		myTurn = !role.equals("white");
		isWhite = role.equals("white");
		SmartAgent.width = width;
		SmartAgent.height = height;
		Node.stateChildren = new HashMap<BoardState, Node[]>();
		cutoffTime = (int)(playclock * 1000 * 0.2);

		// initializes cell scores and column types
		calculateCellScores();
		calculateColumnTypes();
		showCellScores();

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
    		
    		this.rootNode.expandChildren();
    		BoardState temp = this.rootNode.state.executeMove(lastMove);

    		if(temp == null) {
    			System.out.println("temp became null");
    		}

    		for(Node child: this.rootNode.children)
    		{
    			if(child.state.equals(temp)){
    				this.rootNode = child;
    				System.out.println("assigned new root after other played moved");
    				break;
    			}
    		}
			asciiWorld(this.rootNode.state);

    		System.out.println(this.rootNode);
    	}
		
    	// update turn (above that line it myTurn is still for the previous state)
	
		myTurn = !myTurn;
		if (myTurn) {

			int[] moveToTake = new int[4];
			long currentTime = System.currentTimeMillis();
			AlphaBetaThread abt = new AlphaBetaThread(this.rootNode, isWhite);
			Thread t = new Thread(abt);

			t.start();
			while(currentTime + cutoffTime < timeTilReturn) {

				try {
					
					Thread.sleep(100);
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

			this.rootNode = abt.getNextRoot();
			moveToTake = this.rootNode.moveTo;

			//this.rootNode.parent = null;
			System.out.println("Value found was: " + this.rootNode.value);
			System.out.println("States Generated: " + abt.getStatesGenerated());
			System.out.println("Nodes Examined: " + abt.getNodesExamined());
			System.out.println("All States Generated So Far: " + BoardState.numGenerated);
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

	private void asciiWorld(BoardState state) {

		for(int i = height; i > 0; i--) {

			for(int j = 0; j < width; j++) {

				if(state.whitesbool[j+((i-1)* SmartAgent.width)]) {
					System.out.print("W");
					continue;
				}

				if(state.blacksbool[j+((i-1)* SmartAgent.width)]) {
					System.out.print("B");
					continue;
				}

				System.out.print("_");
			}

			System.out.println("");
		}
	}

	private void calculateCellScores() {
		int[] white0 = new int[SmartAgent.height];
		int[] white1 = new int[SmartAgent.height];
		int[] white2 = new int[SmartAgent.height];
		int[] black0 = new int[SmartAgent.height];
		int[] black1 = new int[SmartAgent.height];
		int[] black2 = new int[SmartAgent.height];
		int increment = SmartAgent.incrementInitial;

		for(int i = 0; i < SmartAgent.height; i++) {

			if(i == 0) {
				white1[i] = increment;
			} else {
				white1[i] = white1[i-1] + increment;
			}

			white0[i] = white1[i] - (white1[i] / 4);

			if(i == 0) {
				white2[i] = white0[i];
			} else {
				white2[i] = white1[i];
			}

			increment += 5;
		}
		// adjust home row scores
		white0[0] = white0[SmartAgent.height / 3];
		white1[0] = white1[SmartAgent.height / 3];
		white2[0] = white0[0];
		
		// make goal row scores equal
		white0[SmartAgent.height-1] = white1[SmartAgent.height-1];

		for(int i = 0; i < SmartAgent.height; i++) {
			black0[i] = white0[SmartAgent.height-i-1];
			black1[i] = white1[SmartAgent.height-i-1];
			black2[i] = white2[SmartAgent.height-i-1];
		}

		SmartAgent.cellScores = new ArrayList<int[]>();
		SmartAgent.cellScores.add(white0);
		SmartAgent.cellScores.add(white1);
		SmartAgent.cellScores.add(white2);
		SmartAgent.cellScores.add(black0);
		SmartAgent.cellScores.add(black1);
		SmartAgent.cellScores.add(black2);
	}

	private void calculateColumnTypes() {

		if(SmartAgent.width == 2) {
			SmartAgent.columnTypes = new int[]{1, 1};
		}
		if(SmartAgent.width == 3) {
			SmartAgent.columnTypes = new int[]{0, 1, 0};
		}
		if(SmartAgent.width == 4) {
			SmartAgent.columnTypes = new int[]{0, 1, 1, 0};
		}
		if(SmartAgent.width == 5) {
			SmartAgent.columnTypes = new int[]{0, 1, 2, 1, 0};
		}
		if(SmartAgent.width == 6) {
			SmartAgent.columnTypes = new int[]{0, 1, 2, 2, 1, 0};
		}
		if(SmartAgent.width == 7) {
			SmartAgent.columnTypes = new int[]{0, 1, 1, 2, 1, 1, 0};
		}
		if(SmartAgent.width == 8) {
			SmartAgent.columnTypes = new int[]{0, 1, 1, 2, 2, 1, 1, 0};
		}
		if(SmartAgent.width == 9) {
			SmartAgent.columnTypes = new int[]{0, 1, 1, 2, 1, 2, 1, 1, 0};
		}
		if(SmartAgent.width == 10) {
			SmartAgent.columnTypes = new int[]{0, 1, 1, 2, 1, 1, 2, 1, 1, 0};
		}

	}

	private void showCellScores() {

		System.out.println("White Scores:");
		for(int y = SmartAgent.height-1; y >= 0; y--) {
			for(int x = 0; x < SmartAgent.width; x++) {

				System.out.printf("%3d ", SmartAgent.cellScores.get(SmartAgent.columnTypes[x])[y]);

			}
			System.out.println("");
		}

		System.out.println("");
		System.out.println("Black Scores:");
		for(int y = SmartAgent.height-1; y >= 0; y--) {
			for(int x = 0; x < SmartAgent.width; x++) {

				System.out.printf("%3d ", SmartAgent.cellScores.get(SmartAgent.columnTypes[x] + 3)[y]);

			}
			System.out.println("");
		}
	}

	// is called when the game is over or the match is aborted
	@Override
	public void cleanup() {
		// Cleanup not neaded as init reinitiates everything from scratch.
	}

}

