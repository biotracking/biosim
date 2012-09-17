/*
 * v_Subdued_va.java
 */
package core.clay;

import core.util.Vec2;

/*
 * be subdued to the closest one,
 * Return a Vec2 representing the Subdued object, or 0,0 if none are visible.
 * 
 * 2011.08.10
 * 
 */

public class v_Subdued_v extends NodeVec2 {
	/**
	 * Turns debugging on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	private NodeVec2 embedded1;
	private double timeOut = 5.0;
	private double accum = Math.random() * timeOut;
	private double probibility = 0;

	/**
	 * Instantiate a v_Closest_va node.
	 * 
	 * @param im1
	 *            NodeVec2, the embedded node that generates a list of items to
	 *            scan.
	 */
	public v_Subdued_v(NodeVec2 im1, double p) {
		if (DEBUG)
			System.out.println("v_Subdued_v: instantiated.");
		embedded1 = im1;
		probibility = p;
	}

	Vec2 last_val = new Vec2();
	double lasttime = 0;

	/**
	 * Return a Vec2 representing the Subdued object, or 0,0 if none are
	 * visible.
	 * 
	 * @param timestamp
	 *            double, only get new information if timestamp > than last call
	 *            or timestamp == -1.
	 * @return the vector.
	 */
	public Vec2 Value(double timestamp) {
		double val = Math.random();
		Vec2 last_val = new Vec2();
		if ((timestamp > lasttime) || (timestamp == -1)) {
			/*--- get the list of prey ---*/
			Vec2 prey = embedded1.Value(timestamp);
			/*--- reset the timestamp ---*/
			double time_incd = (double) (timestamp - lasttime) / 100;
			if (timestamp > 0)
				lasttime = timestamp;
			else
				timestamp = lasttime + 1;
			accum += time_incd;
			/*--- reset output ---*/
			if (accum > timeOut) {
				accum = 0;
				System.out.print("Not subdued" + "\n");
				// return(new Vec2(0,0));
			}
			if (probibility > val) {
				last_val = new Vec2(prey.x, prey.y);
				System.out.print("subdued" + "\n");
			}
			/*---subdued prey ---*/
			if (DEBUG)
				System.out.println("v_Subdued_v: " + last_val);
		}
		System.out.println("v_Subdued_v: " + last_val);
		return (last_val);
	}
}
