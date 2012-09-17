/*
 * v_GlobalToEgo_rv.java
 */

package core.clay;

import sim.util.Double2D;
import core.basic_api.AbstractRobot;
import core.util.Vec2;

/**
 * Convert a global Vec2 to egocentric coordinates based on the positional
 * information provided by a SimpleInterface robot.
 * <P>
 * For detailed information on how to configure behaviors, see the <A
 * HREF="../clay/docs/index.html">Clay page</A>.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A> (c)1997, 1998 Tucker Balch
 * 
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class v_GlobalToEgo_rv extends NodeVec2 {
	/**
	 * Turn debug printing on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	private AbstractRobot abstract_robot;
	private NodeVec2 embedded1;

	/**
	 * Instantiate a v_GlobalToEgo_rv schema.
	 * 
	 * @param ar
	 *            SimpleInterface, the abstract_robot object that provides
	 *            hardware support.
	 * @param im1
	 *            NodeVec2, the embedded node.
	 */
	public v_GlobalToEgo_rv(AbstractRobot ar, NodeVec2 im1) {
		if (DEBUG)
			System.out.println("v_GlobalToEgo_rv: instantiated");
		abstract_robot = ar;
		embedded1 = im1;
	}

	public v_GlobalToEgo_rv() {
	}

	Vec2 last_val = new Vec2();
	double lasttime = 0;

	/**
	 * Return a Vec2 representing the egocentric coordinate of the embedded
	 * global schema.
	 * 
	 * @param timestamp
	 *            double, only get new information if timestamp > than last call
	 *            or timestamp == -1.
	 * @return the egocentric coordinate.
	 */
	public Vec2 Value(double timestamp) {
		if (DEBUG)
			System.out.println("v_GlobalToEgo_rv: Value()");

		if ((timestamp > lasttime) || (timestamp == -1)) {
			/*--- reset the timestamp ---*/
			if (timestamp > 0)
				lasttime = timestamp;

			/*--- get the position ---*/
			last_val = embedded1.Value(timestamp);
			Double2D temp = abstract_robot.getNewDirection(last_val);
			Vec2 tmp_val = new Vec2(temp.x, temp.y);
			last_val = tmp_val;
			// tmp_val.x = abstract_robot.getLoc().x;
			// tmp_val.y = abstract_robot.getLoc().y;
			// last_val.sub(tmp_val);

			/*--- get the position ---*/
			// last_val = embedded1.Value(timestamp);
			// Vec2 tmp_val = new Vec2();
			// tmp_val.x = abstract_robot.getLoc().x;
			// tmp_val.y = abstract_robot.getLoc().y;
			// last_val.x = last_val.x - tmp_val.x;
			// last_val.y = last_val.y - tmp_val.y;
			// last_val.rotate(Math.PI/2);
			// last_val.setr(1);
		}
		// System.out.print("v_GlobalToEgo_rv2:" + last_val.x +"\t"+ last_val.y
		// + "\n");
		return (new Vec2(last_val.x, last_val.y));
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
