import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import java.util.Arrays;

public class BoardState {

	public int height;
	public int width;
	public boolean[] whitesbool;
	public boolean[] blacksbool;
	public ArrayList<int[]> legalMoves;

	public ArrayList<Point> blacks;
	public boolean whitePlaying;

	public BoardState(int _width, int _height, boolean[] _whitesbool, boolean[] _blacksbool, boolean _whitePlaying ) {
		this.width = _width;
		this.height = _height;
		this.whitesbool = _whitesbool;
		this.blacksbool = _blacksbool;
		this.whitePlaying = _whitePlaying;
		this.legalMoves = this.legalMoves();
		return;
	}

	/*public BoardState(BoardState _state) {
		this.width = _state.width;
		this.height = _state.height;
		//this.whites = copyArrayList(whites);
		//this.blacks = copyArrayList(blacks);

		this.whitePlaying = _state.whitePlaying;
	}*/

	public Boolean isGoal() {
		for(int x = 0; x < this.width; x++) {
			if(whitesbool[x+(this.width*(this.height-1))] || blacksbool[x]) {
				return true;
			}
		}
		if(legalMoves().size() == 0){
			 return true;
		}
		
		return false;
	}

	public ArrayList<int[]> legalMoves() {

		ArrayList<int[]> legalMoves = new ArrayList<int[]>();
		if(this.whitePlaying) {
			for(int y = 0; y < this.height-1; y++) { // only go up to height - 2 to not calculate moves for whites on toprow
				for(int x = 0; x < this.width; x++) {
					if(whitesbool[x+(y*this.width)] == false) {
						continue;
					}
					if(!blacksbool[x+((y+1)*this.width)] && !whitesbool[x+((y+1)*this.width)]) {
						int[] move = new int[4];
						move[0] = x+1;
						move[1] = y+1;
						move[2] = x+1;
						move[3] = y+2;
						legalMoves.add(move);
					}

					if(x != this.width-1) {
						if(blacksbool[(x+1)+((y+1) * this.width)]) {
							int[] move = new int[4];
							move[0] = x+1;
							move[1] = y+1;
							move[2] = x+2;
							move[3] = y+2;
							legalMoves.add(move);
						}
					}

					if(x!= 0) {
						if(blacksbool[(x-1)+((y+1) * this.width)]) {
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
			for(int y = 1; y < this.height; y++) { // start at 1 to not calculate moves for blacks in the bottom row
				for(int x = 0; x < this.width; x++) {
					if(blacksbool[x+(y*this.width)] == false) {
						continue;
					}
					if(!blacksbool[x+((y-1)*this.width)] && !whitesbool[x+((y-1)*this.width)]) {
						int[] move = new int[4];
						move[0] = x+1;
						move[1] = y+1;
						move[2] = x+1;
						move[3] = y;
						legalMoves.add(move);
					}
					if(x != this.width-1) {
						if(whitesbool[(x+1)+((y-1) * this.width)]) {
							int[] move = new int[4];
							move[0] = x+1;
							move[1] = y+1;
							move[2] = x+2;
							move[3] = y;
							legalMoves.add(move);
						}
					}
					if(x != 0) {
						if(whitesbool[(x-1)+((y-1) * this.width)]) {
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

		int posFrom = (move[0]-1) + ((move[1]-1) * this.width);
		int posTo = (move[2]-1) + ((move[3]-1) * this.width);

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
		return new BoardState(width, height, newWhitesBool, newBlacksBool, !whitePlaying);
	}


	public int evaluate(){
		int distOfMostAdvancedWhite = this.height;
		int distOfMostAdvancedBlack = 0;
		int onWinState = 0;

		boolean foundPawn = false;

		for(int y = this.height-1; y > 0; y--) { // 

				for(int x = 0; x < this.width; x++) {

					if(this.whitesbool[x+(y*this.width)] == true) {

						if(y == this.height-1) {
							//onWinState = 100;
						}

						distOfMostAdvancedWhite = this.height - y - 1;
						foundPawn = true;
						break;

					}
				}

				if(foundPawn) {
					break;
				}

			}

		foundPawn = false;

		for(int y = 0; y < this.height-2; y++) {
			for(int x = 0; x < this.width; x++) {
				if(this.blacksbool[x+(y*this.width)] == true) {
					if(y == 0) {
						//onWinState = 100;
					}
					distOfMostAdvancedBlack = y;
					foundPawn = true;
					break;
				}
			}
			if(foundPawn) {
				break;
			}
		}

		// System.out.println("size of whites is " + whites.size());
		// System.out.println("size of blacks is " + blacks.size());
		// System.out.println("distOfMostAdvancedWhite is " + distOfMostAdvancedWhite);
		// System.out.println("distOfMostAdvancedBlack is " + distOfMostAdvancedBlack);

		return 50 - distOfMostAdvancedWhite + distOfMostAdvancedBlack + onWinState;
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
		return true;
	}

	public String toString() {
		return "whitePlaying";
	}
}