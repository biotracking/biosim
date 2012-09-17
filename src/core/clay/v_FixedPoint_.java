/*
 *  v_FixedPoint_.java
 */

package core.clay;

import core.util.Vec2;

/**
 * Always reports the same Vec2. This is useful for moving to a known location.
 * <P>
 * For detailed information on how to configure behaviors, see the <A
 * HREF="../clay/docs/index.html">Clay page</A>.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A> (c)1997, 1998 Tucker Balch
 * 
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class v_FixedPoint_ extends NodeVec2 {
	/**
	 * Turn debug printing on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	private final Vec2 point;

	/**
	 * Instantiate a v_FixedPoint_ schema.
	 * 
	 * @param x
	 *            double, the x coordinate of the point to return.
	 * @param y
	 *            double, the y coordinate of the point to return.
	 */
	public v_FixedPoint_(double x, double y) {
		if (DEBUG)
			System.out.println("v_FixedPoint_: instantiated");
		point = new Vec2(x, y);
	}

	/**
	 * Return a constant Vec2.
	 * 
	 * @param timestamp
	 *            double, not used but retained for compatibility.
	 * @return the point.
	 */
	public Vec2 Value(double timestamp) {
		if (DEBUG)
			System.out.println("v_FixedPoint_: Value()");
		// System.out.println("v_FixedPoint_: "+ point.x +"\t"+ point.y + "\n");
		return (new Vec2(point.x, point.y));
	}

	/**
	 * Test function for BioSim integration.
	 * 
	 * @return 0 if OK, 1 if there is a problem.
	 */
	public static int test() {
		return 0;
	}
}
