import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import java.util.Arrays;

public class BoardState {

	public static int numGenerated = 0;

	public boolean[] whitesbool;
	public boolean[] blacksbool;
	//public ArrayList<int[]> legalMoves;
	public int evaluation;

	public boolean whitePlaying;

	public BoardState(boolean[] _whitesbool, boolean[] _blacksbool, boolean _whitePlaying ) {
		this.whitesbool = _whitesbool;
		this.blacksbool = _blacksbool;
		this.whitePlaying = _whitePlaying;
		//this.legalMoves = this.legalMoves();
		this.evaluation = this.evaluate();
		BoardState.numGenerated += 1;
		return;
	}

	public boolean isGoal() {
		return (whiteWinState() || blackWinState() || !hasMoves());
	}

	public boolean whiteWinState() {

		for(int x = 0; x < SmartAgent.width; x++) {
			if(whitesbool[x+(SmartAgent.width*(SmartAgent.height-1))]) {
				return true;
			}
		}

		return false;
	}

	public boolean blackWinState() {

		for(int x = 0; x < SmartAgent.width; x++) {
			if(blacksbool[x]) {
				return true;
			}
		}

		return false;
	}

	public boolean hasMoves() {

		if(legalMoves().size() > 0){
			 return true;
		}

		return false;
	}

	public ArrayList<int[]> legalMoves() {

		ArrayList<int[]> legalMoves = new ArrayList<int[]>();
		int y;
		int x;

		if(this.whitePlaying) {
			for(y = 0; y < SmartAgent.height-1; y++) { // only go up to height - 2 to not calculate moves for whites on toprow
				for(x = 0; x < SmartAgent.width; x++) {
					if(whitesbool[x+(y*SmartAgent.width)] == false) {
						continue;
					}
					if(!blacksbool[x+((y+1)*SmartAgent.width)] && !whitesbool[x+((y+1)*SmartAgent.width)]) {
						int[] move = new int[4];
						move[0] = x+1;
						move[1] = y+1;
						move[2] = x+1;
						move[3] = y+2;
						legalMoves.add(move);
					}

					if(x != SmartAgent.width-1) {
						if(blacksbool[(x+1)+((y+1) * SmartAgent.width)]) {
							int[] move = new int[4];
							move[0] = x+1;
							move[1] = y+1;
							move[2] = x+2;
							move[3] = y+2;
							legalMoves.add(move);
						}
					}

					if(x!= 0) {
						if(blacksbool[(x-1)+((y+1) * SmartAgent.width)]) {
							int[] move = new int[4];
							move[0] = x+1;
							move[1] = y+1;
							move[2] = x;
							move[3] = y+2;
							legalMoves.add(move);
						}
					}
				}
			}

		}

		else{
			for(y = 1; y < SmartAgent.height; y++) { // start at 1 to not calculate moves for blacks in the bottom row
				for(x = 0; x < SmartAgent.width; x++) {
					if(blacksbool[x+(y*SmartAgent.width)] == false) {
						continue;
					}
					if(!blacksbool[x+((y-1)*SmartAgent.width)] && !whitesbool[x+((y-1)*SmartAgent.width)]) {
						int[] move = new int[4];
						move[0] = x+1;
						move[1] = y+1;
						move[2] = x+1;
						move[3] = y;
						legalMoves.add(move);
					}
					if(x != SmartAgent.width-1) {
						if(whitesbool[(x+1)+((y-1) * SmartAgent.width)]) {
							int[] move = new int[4];
							move[0] = x+1;
							move[1] = y+1;
							move[2] = x+2;
							move[3] = y;
							legalMoves.add(move);
						}
					}
					if(x != 0) {
						if(whitesbool[(x-1)+((y-1) * SmartAgent.width)]) {
							int[] move = new int[4];
							move[0] = x+1;
							move[1] = y+1;
							move[2] = x;
							move[3] = y;
							legalMoves.add(move);
						}
					}
					

				}
			}
		}
		return legalMoves;
	}

	public BoardState executeMove(int[] move) {
		boolean[] newWhitesBool = Arrays.copyOf(this.whitesbool, this.whitesbool.length);
		boolean[] newBlacksBool = Arrays.copyOf(this.blacksbool, this.blacksbool.length);

		int posFrom = (move[0]-1) + ((move[1]-1) * SmartAgent.width);
		int posTo = (move[2]-1) + ((move[3]-1) * SmartAgent.width);

		if(whitePlaying) {
			//System.out.println("blacks before possible remove " + newBlacks.size());
			if(newBlacksBool[posTo] == true) {
				//System.out.println("black removal");
				newBlacksBool[posTo] = false;
			}
			//System.out.println("blacks after possible remove " + newBlacks.size());
			newWhitesBool[posTo] = true;
			newWhitesBool[posFrom] = false;
		}
		else{
			if(newWhitesBool[posTo] == true) {
				newWhitesBool[posTo] = false;
			}
			newBlacksBool[posTo] = true;
			newBlacksBool[posFrom] = false;
		}
		return new BoardState(newWhitesBool, newBlacksBool, !whitePlaying);
	}

	public int evaluate() {

		if(whiteWinState()) {
			return SmartAgent.winBonus;
		}

		if(blackWinState()) {
			return -SmartAgent.winBonus;
		}

		int numWhite = 0;
		int placementWhite = 0;
		int safeWhite = 0;
		int breakawayWhite = 0;
		int numBlack = 0;
		int placementBlack = 0;
		int safeBlack = 0;
		int breakawayBlack = 0;

		// examine white perspective
		for(int y = SmartAgent.height-1; y >= 0; y--) { // 

			for(int x = 0; x < SmartAgent.width; x++) {

				int index = x+(y*SmartAgent.width);

				if(this.whitesbool[index] == true) {
					numWhite += SmartAgent.existenceBonus;
					placementWhite += SmartAgent.cellScores.get(SmartAgent.columnTypes[x])[y];

					int safe = SmartAgent.cellScores.get(SmartAgent.columnTypes[x])[y] / 2;
					
					if(isFoe(x-1, y+1, blacksbool) || isFoe(x+1, y+1, blacksbool)) {
						safe = 0;
					}

					safeWhite += safe;

					// we check for a "breakaway", 2x3 rectangle in front of the piece, and give a bonus for that
					int breakaway = 0;

					if(emptyOrFriend(x-1, y+1, whitesbool, blacksbool)) {
						breakaway += 1;
					}
					if(emptyOrFriend(x, y+1, whitesbool, blacksbool)) {
						breakaway += 1;
					}
					if(emptyOrFriend(x+1, y+1, whitesbool, blacksbool)) {
						breakaway += 1;
					}
					if(emptyOrFriend(x-1, y+2, whitesbool, blacksbool)) {
						breakaway += 1;
					}
					if(emptyOrFriend(x, y+2, whitesbool, blacksbool)) {
						breakaway += 1;
					}
					if(emptyOrFriend(x+1, y+2, whitesbool, blacksbool)) {
						breakaway += 1;
					}

					breakawayWhite += breakaway * SmartAgent.cellScores.get(SmartAgent.columnTypes[x])[y];
				}



			}

		}

		// examine black perspective
		for(int y = SmartAgent.height-1; y >= 0; y--) { // 

			for(int x = 0; x < SmartAgent.width; x++) {

				int index = x+(y*SmartAgent.width);

				if(this.blacksbool[index] == true) {
					numBlack += SmartAgent.existenceBonus;;
					placementBlack += SmartAgent.cellScores.get(SmartAgent.columnTypes[x] + 3)[y];

					int safe = SmartAgent.cellScores.get(SmartAgent.columnTypes[x] + 3)[y] / 2;
					
					if(isFoe(x-1, y-1, whitesbool) || isFoe(x+1, y-1, whitesbool)) {
						safe = 0;
					}

					safeBlack += safe;

					// we check for a "breakaway", 2x3 rectangle in front of the piece, and give a bonus for that
					int breakaway = 0;

					if(emptyOrFriend(x-1, y-1, blacksbool, whitesbool)) {
						breakaway += 1;
					}
					if(emptyOrFriend(x, y-1, blacksbool, whitesbool)) {
						breakaway += 1;
					}
					if(emptyOrFriend(x+1, y-1, blacksbool, whitesbool)) {
						breakaway += 1;
					}
					if(emptyOrFriend(x-1, y-2, blacksbool, whitesbool)) {
						breakaway += 1;
					}
					if(emptyOrFriend(x, y-2, blacksbool, whitesbool)) {
						breakaway += 1;
					}
					if(emptyOrFriend(x+1, y-2, blacksbool, whitesbool)) {
						breakaway += 1;
					}

					breakawayBlack += breakaway * SmartAgent.cellScores.get(SmartAgent.columnTypes[x] + 3)[y];
				}


			}

		}

		return (numWhite - numBlack) + (placementWhite - placementBlack) + (safeWhite - safeBlack) + (breakawayWhite - breakawayBlack);
	}

	private boolean isFoe(int x, int y, boolean[] foebool) {

		if(x < 0 || x >= SmartAgent.width || y < 0 || y >= SmartAgent.height) {
			return false;
		}

		if(x + (y*SmartAgent.width) < foebool.length) {

			if(foebool[x + (y*SmartAgent.width)] == true) {
				return true;
			}
			
		}

		return false;
	}

	private boolean emptyOrFriend(int x, int y, boolean[] friendbool, boolean[] foebool) {

		if(x < 0 || x >= SmartAgent.width || y < 0 || y >= SmartAgent.height) {
			return true;
		}

		if(x + (y*SmartAgent.width) < friendbool.length) {

			if(friendbool[x + (y*SmartAgent.width)] == true || foebool[x + (y*SmartAgent.width)] == false) {
				return true;
			}
			
		}

		return false;
	}


	@Override
	public boolean equals(Object obj){

		BoardState other = (BoardState) obj;
		if(this.whitesbool.length != other.whitesbool.length) {
			return false;
		}
		if(this.blacksbool.length != other.blacksbool.length) {
			return false;
		}
		for(int i = 0; i < whitesbool.length; i++) {
			if(!(this.whitesbool[i] == other.whitesbool[i])) {
				return false;
			}
		}
		for(int i = 0; i < blacksbool.length; i++) {
			if(!(this.blacksbool[i] == other.blacksbool[i])) {
				return false;

			}
		}
		if(whitePlaying != other.whitePlaying) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		// we pick a prime at random
		int prime = 10007;
		long hash = Arrays.hashCode(whitesbool) * (prime*prime) + Arrays.hashCode(blacksbool) * prime;
		if(whitePlaying) {
			hash += 1;
		}

		return (int) (hash % Integer.MAX_VALUE);
	}

	public String toString() {
		
		// could use some more detail if it becomes necessary
		if(whitePlaying) {
			return "whitePlaying";
		} else {
			return "blackPlaying";
		}

	}
}