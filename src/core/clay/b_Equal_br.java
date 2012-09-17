package core.clay;

import core.basic_api.AbstractRobot;

public class b_Equal_br extends NodeBoolean {
	/**
	 * Turn debug printing on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	private NodeBoolean embedded1;
	private NodeBoolean embedded2;
	private AbstractRobot abstract_robot;

	/**
	 * Instantiate a b_Equal_bb schema.
	 * 
	 * @param im1
	 *            NodeBoolean, the embedded node.
	 * @param im2
	 *            NodeBoolean, the embedded node that generates an boolean to be
	 *            detected if equal to im1.
	 */
	public b_Equal_br(NodeBoolean im1, NodeBoolean im2, AbstractRobot ab) {
		if (DEBUG)
			System.out.println("b_Equal_i: instantiated.");
		embedded1 = im1;
		embedded2 = im2;
		abstract_robot = ab;
	}

	boolean last_val = false;
	double lasttime = 0;

	/**
	 * Return a boolean indicating if the embedded schema output is equal to a
	 * desired value.
	 * 
	 * @return true if equal, false if not.
	 */
	public boolean Value(double timestamp) {
		if (DEBUG)
			System.out.println("b_Equal_bb: Value()");

		if ((timestamp > lasttime) || (timestamp == -1)) {
			/*--- reset the timestamp ---*/
			if (timestamp > 0)
				lasttime = timestamp;
			/*--- compute the output ---*/
			boolean tmp1 = embedded1.Value(timestamp);
			boolean tmp2 = embedded2.Value(timestamp);

			if (tmp2 == tmp1) {
				last_val = true;
				abstract_robot.setSubdueAgent(2, false);
			} else
				last_val = false;
		}
		return (last_val);
	}

}
