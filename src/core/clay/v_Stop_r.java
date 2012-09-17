package core.clay;

import core.util.Vec2;

public class v_Stop_r extends NodeVec2 {
	/**
	 * Turn debug printing on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;

	// private NodeVec2 embedded1;

	/*
	 * Instantiate a v_Stop_r schema.
	 */
	public v_Stop_r() {
		if (DEBUG)
			System.out.println("v_FixedPoint_: instantiated");
		// embedded1 = im1;
	}

	Vec2 last_val = new Vec2();

	/**
	 * Return a constant Vec2.
	 */
	public Vec2 Value(double timestamp) {
		if (DEBUG)
			System.out.println("v_FixedPoint_: Value()");

		// last_val = embedded1.Value(timestamp);
		last_val.setr(0);
		// last_val.sett(embedded1.Value(timestamp).t);
		// System.out.print("v_Stop_r-> "+last_val.x+"  "+last_val.y+"\n");
		return (last_val);
	}
}
