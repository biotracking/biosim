/*
 * va_Obstacles_r.java
 */

package core.clay;

import java.util.ArrayList;

import sim.util.Double2D;
import core.basic_api.AbstractRobot;
import core.util.Vec2;

/**
 * Report a list of Vec2s pointing to obstacles detected by the robot.
 * <P>
 * For detailed information on how to configure behaviors, see the <A
 * HREF="../clay/docs/index.html">Clay page</A>.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A> (c)1998 Tucker Balch
 * 
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class va_Obstacles_r extends NodeVec2Array {
	/**
	 * Turn debug printing on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	private AbstractRobot abstract_robot;
	private int agent_type = 0;

	/**
	 * Instantiate a va_Obstacles_r schema.
	 * 
	 * @param ar
	 *            SimpleInterface, the abstract_robot object that provides
	 *            hardware support.
	 */
	public va_Obstacles_r(AbstractRobot ar, int type) {
		if (DEBUG)
			System.out.println("va_Obstacles_r: instantiated");
		abstract_robot = ar;
		agent_type = type;
	}

	/**
	 * Return an array of Vec2s pointing from the center of the robot to the
	 * detected obstacles.
	 * 
	 * @param timestamp
	 *            double, only get new information if timestamp > than last call
	 *            or timestamp == -1.
	 * @return the sensed obstacles
	 */

	public Vec2[] Value(double timestamp) {
		if (DEBUG)
			System.out.println("va_Obstacles_r: Value()");

		ArrayList<Double2D> avoid_object = abstract_robot.getAgents(agent_type);//

		Vec2[] last_val = new Vec2[avoid_object.size()];
		for (int i = 0; i < avoid_object.size(); i++) {
			last_val[i] = new Vec2(avoid_object.get(i).x, avoid_object.get(i).y);
		}

		// System.out.print("va_Obstacles_r: "+avoid_object.size()+"\n");
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
