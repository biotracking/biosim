/*
 * b_Close_vv.java
 */

package core.clay;

import core.basic_api.AbstractRobot;
import core.util.Vec2;

/**
 * Return true if the values of two embedded nodes are close to one another.
 * <P>
 * For detailed information on how to configure behaviors, see the <A
 * HREF="../clay/docs/index.html">Clay page</A>.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A> (c)1997, 1998 Tucker Balch
 * 
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class b_Close_vr extends NodeBoolean {
	/**
	 * Turn debug printing on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	// private NodeVec2 embedded1;
	private NodeVec2 embedded2;
	private double range;
	private AbstractRobot abstract_robot;

	/**
	 * Instantiate a b_Close_vv schema.
	 * 
	 * @param r
	 *            double, how close they must be.
	 * @param im1
	 *            NodeVec2, the embedded node that generates a vector to be
	 *            detected.
	 * @param im2
	 *            NodeVec2, the embedded node that generates a vector to be
	 *            detected.
	 */
	public b_Close_vr(double r, NodeVec2 im2, AbstractRobot ab) // NodeVec2 im1,
	{
		if (DEBUG)
			System.out.println("b_Close_vv: instantiated.");
		range = r;
		// embedded1 = im1;
		embedded2 = im2;
		abstract_robot = ab;
	}

	boolean last_val = false;
	double lasttime = 0;

	/**
	 * Return a boolean indicating if the two embedded schemas are close to each
	 * other.
	 * 
	 * @param timestamp
	 *            double, only get new information if timestamp > than last call
	 *            or timestamp == -1.
	 * @return true if non-zero, false if zero.
	 */
	public boolean Value(double timestamp) {
		if (DEBUG)
			System.out.println("b_Close_vv: Value()");

		if ((timestamp > lasttime) || (timestamp == -1)) {
			/*--- reset the timestamp ---*/
			if (timestamp > 0)
				lasttime = timestamp;

			/*--- compute the output ---*/
			// Vec2 tmp1 = embedded1.Value(timestamp);
			Vec2 tmp2 = embedded2.Value(timestamp);
			if (tmp2.r == 0)
				return false;

			double tmpr = abstract_robot.getDistanceToLoc(tmp2.x, tmp2.y);
			// System.out.print("b_Close_vr: "+tmpr+"\n");
			if (tmpr < range)
				last_val = true;
			else
				last_val = false;

			// tmp1.sub(tmp2);
			// if (Math.abs(tmp1.r) < range)
			// last_val = true;
			// else
			// last_val = false;
			// if(abstract_robot.getAgentType()==1)
			// System.out.print("b_Close_vr-> "+tmpr+"\t"+range+"\t"+tmp2.x+"\t"+tmp2.y+"\n");
		}
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
