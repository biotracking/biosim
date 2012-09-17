/*
 * b_between_d.java
 */

package core.clay;

import sim.util.Double2D;
import core.basic_api.AbstractRobot;
import core.util.Vec2;

/**
 * Return true if the value of input double is between the lower and upper bound
 * <P>
 * For detailed information on how to configure behaviors, see the <A
 * HREF="../clay/docs/index.html">Clay page</A>.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A> (c)1997, 1998 Tucker Balch
 * 
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class b_between_d extends NodeBoolean {
	/**
	 * Turn debug printing on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	private NodeInt embedded1;
	private int target;
	private double lower, upper;
	private NodeVec2 obj;
	private AbstractRobot robot;

	/**
	 * Instantiate a b_between_d schema.
	 * 
	 * @param t
	 *            int, the target value.
	 * @param im1
	 *            NodeInt, the embedded node that generates an int to be
	 *            detected if equal to target.
	 */
	public b_between_d(double l, double u, NodeVec2 o, AbstractRobot ab) {
		if (DEBUG)
			System.out.println("b_between_d: instantiated.");
		lower = l;
		upper = u;
		obj = o;
		robot = ab;
	}

	public b_between_d() {
	}

	boolean last_val = false;
	double lasttime = 0;

	/**
	 * Return a boolean indicating if the embedded schema output is equal to a
	 * desired value.
	 * 
	 * @param timestamp
	 *            double, only get new information if timestamp > than last call
	 *            or timestamp == -1.
	 * @return true if equal, false if not.
	 */
	public boolean Value(double timestamp) {
		if (DEBUG)
			System.out.println("b_between_d: Value()");

		if ((timestamp > lasttime) || (timestamp == -1)) {
			/*--- reset the timestamp ---*/
			if (timestamp > 0)
				lasttime = timestamp;
			/*--- compute the output ---*/
			Vec2 tmp = obj.Value(timestamp);
			Vec2 lo = new Vec2(this.robot.getLoc().x, this.robot.getLoc().y);
			tmp.sub(lo);
			
			if (tmp.r > lower && tmp.r < upper)
				last_val = true;
			else
				last_val = false;
		}
//		System.out.print("b_between_d: "+last_val+"\n");
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
