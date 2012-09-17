package core.tools;

/**
 * @author Hai Shang
 * @author hshang9@gatech.edu
 * 
 */
public class Interaction {
	private int type;
	private double toX, toY;

	public Interaction() {
		type = -1;
		toX = -1;
		toY = -1;
	}

	public Interaction(int t, double x, double y) {
		type = t;
		toX = x;
		toY = y;
	}

	public int getType() {
		return this.type;
	}

	public double getToX() {
		return this.toX;
	}

	public double getToY() {
		return this.toY;
	}

}// end of the class Interaction
