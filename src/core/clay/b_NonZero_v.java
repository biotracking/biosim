/*
 * b_NonZero_v.java
 */

package core.clay;

import core.util.Vec2;

/**
 * Returns true if embedded schema Vec2 is is non-zero length.
 * <P>
 * For detailed information on how to configure behaviors, see the <A
 * HREF="../clay/docs/index.html">Clay page</A>.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A> (c)1997, 1998 Tucker Balch
 * 
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class b_NonZero_v extends NodeBoolean {
	/**
	 * Turn debug printing on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	private NodeVec2 embedded1;

	/**
	 * Instantiate a b_NonZero_v schema.
	 * 
	 * @param im1
	 *            NodeVec2, the embedded node that generates a vector to be
	 *            detected.
	 */
	public b_NonZero_v(NodeVec2 im1) {
		if (DEBUG)
			System.out.println("b_NonZero_v: instantiated.");
		embedded1 = im1;
	}

	public b_NonZero_v() {
	}

	boolean last_val = false;
	double lasttime = 0;

	/**
	 * Return a boolean indicating if the embedded schema is non-zero.
	 * 
	 * @param timestamp
	 *            double, only get new information if timestamp > than last call
	 *            or timestamp == -1.
	 * @return true if non-zero, false if zero.
	 */
	public boolean Value(double timestamp) {
		if (DEBUG)
			System.out.println("b_NonZero_v: Value()");

		if ((timestamp > lasttime) || (timestamp == -1)) {
			/*--- reset the timestamp ---*/
			if (timestamp > 0)
				lasttime = timestamp;

			/*--- compute the output ---*/
			Vec2 tmp = embedded1.Value(timestamp);
			if (tmp.r != 0)
				last_val = true;
			else
				last_val = false;
			// if (false)
			// System.out.println("non zero " +last_val);
		}
		// System.out.println("non_zero " +last_val);
		return (last_val);
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
