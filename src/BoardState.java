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

	public ArrayList<int[]> legalMoves() {
		ArrayList<int[]> legalMoves = new ArrayList<int[]>();
		if(whitePlaying) {
			for(Point pawn : whites) {
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

	public void executeMove(int[] move) {
		Point pawn_original = new Point(move[0], move[1]);
		Point pawn_move_to = new Point(move[2], move[3]);
		if(whitePlaying) {
			if(!whites.contains(pawn_original)){
				//something went wrong.
				return;
			}
			if(blacks.contains(pawn_move_to)) {
				blacks.remove(pawn_move_to);
			}
			whites.add(pawn_move_to);
			whites.remove(pawn_original);
		}
		else{
			if(!blacks.contains(pawn_original)){
				//something went wrong.
				return;
			}
			if(whites.contains(pawn_move_to)) {
				whites.remove(pawn_move_to);
			}
			blacks.add(pawn_move_to);
			blacks.remove(pawn_original);
		}
		whitePlaying = !whitePlaying;
		return;
	}

	public int evaluate(){
		int distOfMostAdvancedWhite = Integer.MIN_VALUE;
		int distOfMostAdvancedBlack = Integer.MAX_VALUE;
		for(Point pawn : whites) {
			distOfMostAdvancedWhite = Math.min(distOfMostAdvancedWhite, height - pawn.y);
		}
		for(Point pawn : blacks) {
			distOfMostAdvancedBlack = Math.min(distOfMostAdvancedBlack, pawn.y);
		}
		return 50 - distOfMostAdvancedBlack + distOfMostAdvancedWhite;
	}

	public String toString() {
		return "height: " + height + " width: " + width;
	}
}