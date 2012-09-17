/*
 * v_Noise_.java
 */

package core.clay;

import java.awt.Color;
import java.util.Random;

import core.basic_api.AbstractRobot;
import core.util.Vec2;

/**
 * Generates a vector in a random direction for a specified time. This software
 * module is based on the motor schema formulation developed by Ronald C. Arkin
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
 * <A HREF="../COPYRIGHT.html">Copyright</A> (c)1997 Georgia Tech Research
 * Corporation
 * 
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class v_Noise_ extends NodeVec2 {
	/**
	 * Turns debug printing on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	private double TIMEOUT = 5.0;
	private double accum = Math.random() * TIMEOUT;
	private Random r = new Random(0); // constant seed
	private AbstractRobot abstract_robot;

	/**
	 * Instantiate an v_Noise_ schema.
	 * 
	 * @param t
	 *            double, how double the random direction should persist in
	 *            seconds.
	 * @param s
	 *            double, the random number seed.
	 */
	public v_Noise_(double t, AbstractRobot ab) {
		if (DEBUG)
			System.out.println("v_Noise_: instantiated.");
		// _val.x = -1; _val.y = 0;
		_val.sett(Math.random() * 2 * Math.PI);
		TIMEOUT = t;
		r.setSeed(ab.getAgentId()+System.currentTimeMillis());
		abstract_robot = ab;
	}

	Vec2 last_val = new Vec2();
	Vec2 _val = new Vec2();
	double lasttime = 0;
	double tmp_val_angle = 0;

	/**
	 * Return a Vec2 representing a random direction to go for a period of time.
	 * @param timestamp only get new information if timestamp > than last call or timestamp == -1.
	 * @return the movement vector.
	 */
	public Vec2 Value(double timestamp) {

		if ((timestamp > lasttime) || (timestamp == -1)) {
			/*--- reset the timestamp ---*/
			double time_incd = abstract_robot.getTimeStampSize();
			if (timestamp > 0)
				lasttime = timestamp;
			else
				timestamp = lasttime + 1;
			accum += time_incd;
			/*--- reset output ---*/
			if (accum > TIMEOUT) {
				accum = 0;
				_val.sett(r.nextDouble() * 2 * Math.PI);
			}
			last_val.setr(1);
			double angleBetweenDirection = Math.atan2(_val.y, _val.x);
			angleBetweenDirection = angleBetweenDirection - abstract_robot.getDirAngle();
			if (angleBetweenDirection > Math.PI)
				angleBetweenDirection = angleBetweenDirection - 2 * Math.PI;
			if (angleBetweenDirection < -Math.PI)
				angleBetweenDirection = angleBetweenDirection + 2 * Math.PI;
			// last_val.sett(angleBetweenDirection);
			last_val.x = last_val.r * Math.cos(angleBetweenDirection);
			last_val.y = last_val.r * Math.sin(angleBetweenDirection);
//			abstract_robot.setDisplayText(last_val.x + "\t" + last_val.y + "\t" + last_val.t, 0.05, 0.05, Color.black);
			// System.out.println("Noise: "+ last_val.x +"\t"+ last_val.y+"\t"+
			// last_val.r + "\n");
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
