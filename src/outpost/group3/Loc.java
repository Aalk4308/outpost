package outpost.group3;

public class Loc {
	public int x;
	public int y;
	
	Loc() {
		x = 0;
		y = 0;
	}
	
	Loc(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	Loc(Loc l) {
		x = l.x;
		y = l.y;
	}
	
	static public double distanceSquared(int x1, int y1, int x2, int y2) {
    	return (x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2);
    }
    
    static public double distance(int x1, int y1, int x2, int y2) {
    	return Math.sqrt(distanceSquared(x1, y1, x2, y2));
    }
    
    public double distanceSquared(Loc comparison) {
    	return distanceSquared(this, comparison);
    }
    
    public double distance(Loc comparison) {
    	return distance(this, comparison);
    }
    
    static public double distanceSquared(Loc l1, Loc l2) {
    	return distanceSquared(l1.x, l1.y, l2.x, l2.y);
    }
    
    static public double distance(int x, int y, Loc l) {
    	return distance(x, y, l.x, l.y);
    }
    
    static public double distanceSquared(int x, int y, Loc l) {
    	return distanceSquared(x, y, l.x, l.y);
    }
    
    static public double distance(Loc l1, Loc l2) {
    	return distance(l1.x, l1.y, l2.x, l2.y);
    }
    
    static public boolean equals(Loc l1, Loc l2) {
    	return l1.x == l2.x && l1.y == l2.y;
    }
}
