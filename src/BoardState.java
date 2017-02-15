import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

public class BoardState {

	public int height;
	public int width;
	public ArrayList<Point> whites;
	public ArrayList<Point> blacks;
	public boolean whitePlaying;

	public BoardState(int _width, int _height, ArrayList<Point> _whites, ArrayList<Point> _blacks, boolean _whitePlaying ) {
		this.width = _width;
		this.height = _height;
		this.whites = _whites;
		this.blacks = _blacks;
		this.whitePlaying = _whitePlaying;
		return;
	}

	public BoardState(BoardState _state) {
		this.width = _state.width;
		this.height = _state.height;
		this.whites = copyArrayList(whites);
		this.blacks = copyArrayList(blacks);
		this.whitePlaying = _state.whitePlaying;
	}

	private ArrayList<Point> copyArrayList(ArrayList<Point> cpy) {
		ArrayList<Point> out = new ArrayList<Point>();
		for(Point p : cpy) {
			out.add(new Point(p.x, p.y));
		}
		return out;
	}

	public Boolean isGoal() {
		for(Point p : whites) {
			if(p.y == height) {
				return true;
			}
		}
		for(Point p : blacks) {
			if(p.y == 1) {
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

		if(whitePlaying) {

			for(Point pawn : whites) {

				if(pawn.y == height) {
					continue;
				}

				Point front = new Point(pawn.x, pawn.y+1);
				Point diagonal_left = new Point(pawn.x-1, pawn.y+1);
				Point diagonal_right = new Point(pawn.x+1, pawn.y+1);
				if(!blacks.contains(front) && !whites.contains(front)) {
					int[] move = new int[4];
					move[0] = pawn.x;
					move[1] = pawn.y;
					move[2] = front.x;
					move[3] = front.y;
					legalMoves.add(move);
				}
				if(blacks.contains(diagonal_left)) {
					int[] move = new int[4];
					move[0] = pawn.x;
					move[1] = pawn.y;
					move[2] = diagonal_left.x;
					move[3] = diagonal_left.y;
					legalMoves.add(move);
				}
				if(blacks.contains(diagonal_right)) {
					int[] move = new int[4];
					move[0] = pawn.x;
					move[1] = pawn.y;
					move[2] = diagonal_right.x;
					move[3] = diagonal_right.y;
					legalMoves.add(move);
				}
			}
		}
		else{
			for(Point pawn : blacks) {

				if(pawn.y == 1) {
					continue;
				}

				Point front = new Point(pawn.x, pawn.y-1);
				Point diagonal_left = new Point(pawn.x-1, pawn.y-1);
				Point diagonal_right = new Point(pawn.x+1, pawn.y-1);
				if(!blacks.contains(front) && !whites.contains(front)) {
					int[] move = new int[4];
					move[0] = pawn.x;
					move[1] = pawn.y;
					move[2] = front.x;
					move[3] = front.y;
					legalMoves.add(move);
				}
				if(whites.contains(diagonal_left)) {
					int[] move = new int[4];
					move[0] = pawn.x;
					move[1] = pawn.y;
					move[2] = diagonal_left.x;
					move[3] = diagonal_left.y;
					legalMoves.add(move);
				}
				if(whites.contains(diagonal_right)) {
					int[] move = new int[4];
					move[0] = pawn.x;
					move[1] = pawn.y;
					move[2] = diagonal_right.x;
					move[3] = diagonal_right.y;
					legalMoves.add(move);
				}
			}
		}
		return legalMoves;
	}

	public BoardState executeMove(int[] move) {
		Point pawn_original = new Point(move[0], move[1]);
		Point pawn_move_to = new Point(move[2], move[3]);
		ArrayList<Point> newWhites = copyArrayList(whites);
		ArrayList<Point> newBlacks = copyArrayList(blacks);

		if(whitePlaying) {
			//System.out.println("blacks before possible remove " + newBlacks.size());
			if(newBlacks.contains(pawn_move_to)) {
				//System.out.println("black removal");
				newBlacks.remove(pawn_move_to);
			}
			//System.out.println("blacks after possible remove " + newBlacks.size());
			newWhites.add(pawn_move_to);
			newWhites.remove(pawn_original);
		}
		else{
			if(newWhites.contains(pawn_move_to)) {
				newWhites.remove(pawn_move_to);
			}
			newBlacks.add(pawn_move_to);
			newBlacks.remove(pawn_original);
		}
		return new BoardState(width, height, newWhites, newBlacks, !whitePlaying);
	}


	public int evaluate(){
		int distOfMostAdvancedWhite = Integer.MAX_VALUE;
		int distOfMostAdvancedBlack = Integer.MAX_VALUE;

		for(Point pawn : whites) {
			distOfMostAdvancedWhite = Math.min(distOfMostAdvancedWhite, height - pawn.y);
		}

		for(Point pawn : blacks) {
			distOfMostAdvancedBlack = Math.min(distOfMostAdvancedBlack, pawn.y-1);
		}

		// System.out.println("size of whites is " + whites.size());
		// System.out.println("size of blacks is " + blacks.size());
		// System.out.println("distOfMostAdvancedWhite is " + distOfMostAdvancedWhite);
		// System.out.println("distOfMostAdvancedBlack is " + distOfMostAdvancedBlack);

		return 50 - distOfMostAdvancedWhite + distOfMostAdvancedBlack;
	}


	@Override
	public boolean equals(Object obj){

		BoardState other = (BoardState) obj;
		if(this.whites.size() != other.whites.size()) {
			return false;
		}
		if(this.blacks.size() != other.blacks.size()) {
			return false;
		}
		for(int i = 0; i < whites.size(); i++) {
			if(!this.whites.get(i).equals(other.whites.get(i))) {
				return false;
			}
		}
		for(int i = 0; i < blacks.size(); i++) {
			if(!this.blacks.get(i).equals(other.blacks.get(i))) {
				return false;
			}
		}
		if(width != other.width || height != other.height){
			return false;
		}
		return true;
	}

	public String toString() {
		return "whitePlaying " + Boolean.toString(whitePlaying);
	}
}