/*
 * v_GlobalPosition_r.java
 */

package core.clay;

import core.basic_api.AgentBody;
import core.util.Vec2;

/**
 * Report a Vec2 representing the robot's position in global coordinates.
 * <P>
 * For detailed information on how to configure behaviors, see the <A
 * HREF="../clay/docs/index.html">Clay page</A>.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A> (c)1998 Tucker Balch
 * 
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class v_GlobalPosition_r extends NodeVec2 {
	/**
	 * Turn debug printing on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	private AgentBody abstract_robot;

	/**
	 * Instantiate a v_GlobalPosition_r schema.
	 * 
	 * @param ar
	 *            SimpleInterface, the abstract_robot object that provides
	 *            hardware support.
	 */
	public v_GlobalPosition_r(AgentBody ar) {
		if (DEBUG)
			System.out.println("v_GlobalPosition_r: instantiated");
		abstract_robot = ar;
	}

	Vec2 last_val = new Vec2();
	double lasttime = 0;

	/**
	 * Return a Vec2 representing the robot's position in global coordinates.
	 * 
	 * @param timestamp
	 *            double, only get new information if timestamp > than last call
	 *            or timestamp == -1.
	 * @return the heading
	 */
	public Vec2 Value(double timestamp) {
		if (DEBUG)
			System.out.println("v_GlobalPosition_r: Value()");

		if ((timestamp > lasttime) || (timestamp == -1)) {
			/*--- reset the timestamp ---*/
			if (timestamp > 0)
				lasttime = timestamp;

			/*--- get the position ---*/
			last_val.x = abstract_robot.getLoc().x;
			last_val.y = abstract_robot.getLoc().y;
		}
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
