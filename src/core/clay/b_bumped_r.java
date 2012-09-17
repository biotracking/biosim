package core.clay;

import java.awt.Color;

import core.basic_api.AbstractRobot;

public class b_bumped_r extends NodeBoolean {

	/**
	 * Turn debug printing on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	private AbstractRobot abstract_robot;

	public b_bumped_r(AbstractRobot ar) {
		if (DEBUG)
			System.out.println("i_InGripper_r: instantiated");
		abstract_robot = ar;
	}

	boolean last_val = false;
	double lasttime = 0;

	public boolean Value(double timestamp) {
		if (DEBUG)
			System.out.println("i_InGripper_r: Value()");

		if ((timestamp > lasttime) || (timestamp == -1)) {
			/*--- reset the timestamp ---*/
			if (timestamp > 0)
				lasttime = timestamp;
			/*--- get the info ---*/
			last_val = abstract_robot.getIsBumped();
		}
//		if (last_val)
//			abstract_robot.setDisplayColor(Color.green);
//		else
//			abstract_robot.setDisplayColor(Color.black);
		// System.out.print("b_bumped_r-> bumped!!"+last_val+"\n");
		return last_val;
	}

}
