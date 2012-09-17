/*
 * v_LinearAttraction_v.java
 */

package core.clay;

import core.basic_api.AbstractRobot;
import core.util.Vec2;

/**
 * Generates a vector towards a goal location that varies with distance from the
 * goal. The attraction is increased linearly at greater distances. Based on
 * Arkin's formulation.
 * <P>
 * Arkin's original formulation is described in "Motor Schema Based Mobile Robot
 * Navigation," <I>International Journal of Robotics Research</I>, vol. 8, no 4,
 * pp 92-112.
 * <P>
 * The source code in this module is based on "first principles" (e.g. published
 * papers) and is not derived from any previously existing software.
 * <P>
 * For detailed information on how to configure behaviors, see the <A
 * HREF="../clay/docs/index.html">Clay page</A>.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A> (c)1997, 1998 Tucker Balch
 * 
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class v_LinearAttraction_v extends NodeVec2 {
	/**
	 * Turns debug printing on or off.
	 */
	public static final boolean DEBUG = /* true; */Node.DEBUG;
	private NodeVec2 embedded1;
	private double controlled_zone = 1.0;
	private double dead_zone = 0.0;
	private AbstractRobot abstract_robot;

	/**
	 * Instantiate a v_LinearAttraction_v schema.
	 * 
	 * @param czr
	 *            double, controlled zone radius.
	 * @param dzr
	 *            double, dead zone radius.
	 * @param im1
	 *            double, the node that generates an egocentric vector to the
	 *            goal.
	 */
	public v_LinearAttraction_v(AbstractRobot ar, double czr, double dzr,
			NodeVec2 im1) {
		if (DEBUG)
			System.out.println("v_LinearAttraction_v: instantiated.");
		embedded1 = im1;
		abstract_robot = ar;
		if ((czr < dzr) || (czr < 0) || (dzr < 0)) {
			System.out.println("v_LinearAttraction_v: illegal parameters");
			return;
		}
		controlled_zone = czr;
		dead_zone = dzr;
	}

	public v_LinearAttraction_v() {
	}

	Vec2 last_val = new Vec2();
	double lasttime = 0;

	/**
	 * Return a Vec2 representing the direction to go towards the goal.
	 * Magnitude varies with distance.
	 * 
	 * @param timestamp
	 *            double, only get new information if timestamp > than last call
	 *            or timestamp == -1.
	 * @return the movement vector.
	 */
	public Vec2 Value(double timestamp) {
		double mag;
		Vec2 goal = new Vec2();

		if ((timestamp > lasttime) || (timestamp == -1)) {
			if (DEBUG)
				System.out.println("v_LinearAttraction_v:");

			/*--- reset the timestamp ---*/
			if (timestamp > 0)
				lasttime = timestamp;

			/*--- get the goal ---*/
			goal = embedded1.Value(timestamp);
			Vec2 tv = new Vec2(this.abstract_robot.getLoc().x, this.abstract_robot.getLoc().y);
			//goal.sub(tv);
			// if(abstract_robot.getAgentType()==1)
			// System.out.print("v_LineraAttraction_-> goal: "+goal.x+"\t"+goal.y+"\n");

			/*--- consider the magnitude ---*/
			// inside dead zone?
			if (goal.r < dead_zone)
				mag = 0;
			// inside control zone?
			else if (goal.r < controlled_zone)
				mag = (goal.r - dead_zone) / (controlled_zone - dead_zone);
			// outside control zone
			else
				mag = 1.0;
			if (DEBUG)
				System.out.println(mag + " " + goal.r);

			/*--- set the new vector ---*/
			goal.setr(mag);
			last_val = goal;
		}

		last_val.setr(2);
		if (DEBUG)
			System.out.println(last_val.r + " " + goal.r);

		double angleBetweenDirection = Math.atan2(last_val.y, last_val.x);
		// double angleAntHeadding = Math.atan2(abstract_robot.getDir().y,abstract_robot.getDir().x);

		angleBetweenDirection = angleBetweenDirection - abstract_robot.getDirAngle();
		if (angleBetweenDirection > Math.PI)
			angleBetweenDirection = angleBetweenDirection - 2 * Math.PI;
		if (angleBetweenDirection < -Math.PI)
			angleBetweenDirection = angleBetweenDirection + 2 * Math.PI;
		// last_val.sett(angleBetweenDirection);
		last_val.x = last_val.r * Math.cos(angleBetweenDirection);
		last_val.y = last_val.r * Math.sin(angleBetweenDirection);

		// System.out.println("v_LinearAttraction_v2: "+ last_val.x +"\t"+ last_val.y + "\n");
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
