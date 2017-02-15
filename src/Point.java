import java.util.ArrayList;

public class Point implements Comparable
{
	public int x;
	public int y;

	public Point(int _x, int _y) 
	{
		x = _x;
		y = _y;
		return;
	}

	@Override
	public int hashCode() {
		long result = (x * 1001 + y * 5003) % Integer.MAX_VALUE;
		return (int) result;
	}

	@Override
	public boolean equals(Object obj) 
	{
		Point other = (Point) obj;
		return x == other.x && y == other.y;
	}

	@Override
	public String toString() 
	{
		return x + " " + y;
	}

	@Override 
	public int compareTo(Object obj) {
		Point that = (Point) obj;
		
		if(this.y == that.y && this.x == that.x) {
    		return 0;
    	}
    	
    	if((this.y < that.y) || (this.y == that.y && this.x < that.x)) {
    		return -1;
    	}
    	
        return 1;
	}
}