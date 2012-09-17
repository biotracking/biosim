/*
 * v_Noise2_.java
 */

package core.clay;

import java.awt.Color;
import java.util.Random;

import core.basic_api.AbstractRobot;
import core.basic_api.BoundChecker;
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

public class v_Noise2_ extends NodeVec2 {
	/**
	 * Turns debug printing on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	private AbstractRobot abstract_robot;
	private BoundChecker bc;
	double lasttime = 0;
	private double timer_change = 0;
	private double timer_changing = 0;
	private Vec2 last_val;
	private Random randomer = new Random();
	private Vec2 changing_direction = new Vec2();
	
	/**
	 * Instantiate an v_Noise2_ schema.
	 * 
	 * @param t
	 *            double, how double the random direction should persist in
	 *            seconds.
	 * @param s
	 *            double, the random number seed.
	 */
	public v_Noise2_(double t, AbstractRobot ab) {
		if (DEBUG)
			System.out.println("v_Noise2_: instantiated.");
		// _val.x = -1; _val.y = 0;
		abstract_robot = ab;
		bc = new BoundChecker();
		lasttime = 0;
		timer_change = 0;
		timer_changing = 0;
		last_val = new Vec2(0,0);
		randomer.setSeed(System.currentTimeMillis()+abstract_robot.getAgentId());
		changing_direction = new Vec2(0, 0);
	}

	
	/**
	 * Return a Vec2 representing a random direction to go for a period of time.
	 * 
	 * @param timestamp
	 *            only get new information if timestamp > than last call or
	 *            timestamp == -1.
	 * @return the movement vector.
	 */
	public Vec2 Value(double timestamp) {
		if ((timestamp > lasttime) || (timestamp == -1)) {
			/*--- reset the timestamp ---*/
			if (timestamp > 0)
				lasttime = timestamp;
			if(timestamp >= timer_change){
				timer_changing = timestamp + randomer.nextDouble()*30*abstract_robot.getTimeStampSize();
				timer_change = timestamp + (40 + 8 * randomer.nextDouble()) * abstract_robot.getTimeStampSize();
				double tx = 2*(randomer.nextDouble()-0.5);
				double ty = Math.sqrt(1-tx*tx)*(randomer.nextDouble()>0.5?1:-1);
				changing_direction.setr(1);
				changing_direction.sett(Math.atan2(ty, tx));
			}
			if(timestamp >= timer_changing){
				changing_direction.setr(0);
				changing_direction.sett(0);
				double tt = abstract_robot.getDirAngle();
				last_val.setr(1);
				last_val.sett(0);
				
			} else {
				if(bc.checkBound(this.abstract_robot.getLoc().x, this.abstract_robot.getLoc().y, 0.01)){
					double angleBetweenDirection = changing_direction.t;
					// double angleAntHeadding =
					// Math.atan2(abstract_robot.getDir().y,abstract_robot.getDir().x);

					angleBetweenDirection = angleBetweenDirection
							- abstract_robot.getDirAngle();
					if (angleBetweenDirection > Math.PI)
						angleBetweenDirection = angleBetweenDirection - 2 * Math.PI;
					if (angleBetweenDirection < -Math.PI)
						angleBetweenDirection = angleBetweenDirection + 2 * Math.PI;
					// last_val.sett(angleBetweenDirection);
					last_val.setr(1);
					last_val.sett(angleBetweenDirection);
				} else {
					last_val.setr(0);
				}
			}
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
