/*
 * i_InGripper_r.java
 */

package core.clay;

import core.basic_api.AbstractRobot;

/**
 * Report the type of object in the gripper. If nothing is there, return 0.
 * <P>
 * For detailed information on how to configure behaviors, see the <A
 * HREF="../clay/docs/index.html">Clay page</A>.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A> (c)1997, 1998 Tucker Balch
 * 
 * @author Tucker Balch
 * @version $Revision: 1.2 $
 */

public class i_InGripper_r extends NodeInt {
	/**
	 * Turn debug printing on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	private AbstractRobot abstract_robot;

	/**
	 * Instantiate an i_InGripper_r schema.
	 * 
	 * @param ar
	 *            GripperActuator, the abstract_robot object that provides
	 *            hardware support.
	 */
	public i_InGripper_r(AbstractRobot ar) {
		if (DEBUG)
			System.out.println("i_InGripper_r: instantiated");
		abstract_robot = ar;
	}

	int last_val = 0;
	double lasttime = 0;
	int temp_last = 1;

	/**
	 * Return an int representing the type of object in the robot's gripper, -1
	 * if empty.
	 * 
	 * @param timestamp
	 *            double, only get new information if timestamp > than last call
	 *            or timestamp == -1.
	 * @return the type of the object, 0 if empty.
	 */
	public int Value(double timestamp) {
		if (DEBUG)
			System.out.println("i_InGripper_r: Value()");

		if ((timestamp > lasttime) || (timestamp == -1)) {
			/*--- reset the timestamp ---*/
			if (timestamp > 0)
				lasttime = timestamp;
			/*--- get the info ---*/

			boolean val_2 = abstract_robot.getAntThingInGripper();
			if (val_2 == true)
				last_val = abstract_robot.getTypeInGripper();
			else
				last_val = 0;
			// int temp_val = abstract_robot.succcusssTagging(2);
			// last_val = temp_val;
		}
		// if(last_val!=temp_last)
		// System.out.print("i_InGripper_r-> last_val: " + last_val + "\n");
		temp_last = last_val;
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
