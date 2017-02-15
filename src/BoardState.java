import java.util.List;
import java.util.ArrayList;

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

	public List<int[]> legalMoves() {
		List<int[]> legalMoves = new ArrayList<int[]>();
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
}