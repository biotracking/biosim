/*
 * v_Avoid_va.java
 */

package core.clay;

import core.basic_api.AbstractRobot;
import core.util.Units;
import core.util.Vec2;

/**
 * This node (motor schema) generates a vector away from the items detected by
 * its embedded perceptual schema. Magnitude varies from 0 to 1.
 * <P>
 * This version works differently than Arkin's original formulation. In the
 * original, a repulsion vector is computed for each detected obstacle, with the
 * result being the sum of these vectors. The impact is that several hazards
 * grouped closely together are more repulsive than a single hazard. This causes
 * problems when each sonar return is treated as a separate hazard --- walls for
 * instance are more repulsive than a small hazard.
 * <P>
 * This version computes the direction of the repulsive vector as in the
 * original, but the returned magnitude is the largest of the vectors, not the
 * sum.
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

public class v_Avoid_va extends NodeVec2 {
	public static final boolean DEBUG = Node.DEBUG;
	private NodeVec2Array embedded1;
	private double sphere;// = 1.0;
	private double safety;// = 0.0;
	private AbstractRobot abstract_robot;

	/**
	 * Instantiate a v_Avoid_va schema.
	 * 
	 * @param soe
	 *            double, the sphere of influence beyond which the hazards are
	 *            not considered.
	 * @param s
	 *            double, the safety zone, inside of which a maximum repulsion
	 *            from the object is generated.
	 * @param im1
	 *            NodeVec2Array, the embedded node that generates a list of
	 *            items to avoid.
	 */
	public v_Avoid_va(double soe, double s, NodeVec2Array im1, AbstractRobot ar) {
		if (DEBUG)
			System.out.println("v_Avoid_va: instantiated.");
		embedded1 = im1;
		if ((soe < s) || (soe < 0) || (s < 0)) {
			System.out.println("v_Avoid_va: illegal parameters");
			return;
		}
		sphere = soe;
		safety = s;
		abstract_robot = ar;
	}

	public v_Avoid_va() {
	}

	public void setembedded(NodeVec2Array embedded1) {
		this.embedded1 = embedded1;
	}

	Vec2 last_val = new Vec2();
	double lasttime = 0;

	/**
	 * Return a Vec2 representing the direction to go away from the detected
	 * hazards.
	 * 
	 * @param timestamp
	 *            double, only get new information if timestamp > than last call
	 *            or timestamp == -1.
	 * @return the movement vector.
	 */
	public Vec2 Value(double timestamp) {
		double tempmag;
		double max_mag = 0;

		if ((timestamp > lasttime) || (timestamp == -1)) {
			/*--- reset the timestamp ---*/
			if (timestamp > 0)
				lasttime = timestamp;
			

			/*--- reset output ---*/
			last_val.setr(0);

			/*--- get the list of obstacles ---*/
			Vec2[] obs = embedded1.Value(timestamp);
			Vec2[] obs2 = embedded1.Value(timestamp);
			
			/*--- change to % unit ---*/
			double maxR = abstract_robot.getDistanceOfPerception();
			// System.out.print("va_Obstacles_r: "+last_val[i].x+"\t"+last_val[i].y+"\n");
			
			/*--- consider each obstacle ---*/
			for (int i = 0; i < obs.length; i++) {
				obs2[i].normalize(obs[i].r / maxR);
				if (obs2[i] != null) {
//					 System.out.println("v_Avoid_va-> obs2[ "+i+"]:"+ obs2[i].r);

					/*--- too close ? ---*/
					if (obs2[i].r < safety) {
						tempmag = -1 * Units.HUGE;
					}
					/*--- controlled zone ---*/
					else if (obs2[i].r < sphere)
						tempmag = -1 * (sphere - obs[i].r) / (sphere - safety);
					/*--- outside sphere ---*/
					else
						tempmag = 0;

					/*--- set the repulsive vector ---*/
					// disObs = tempmag;
					obs2[i].setr(tempmag);

					/*--- check if maximum value ---*/
					if (Math.abs(tempmag) > max_mag)
						max_mag = Math.abs(tempmag);

					/*--- set t between +1.57 to 3.14 and -3.14 to -1.57---*/
					double tempf;
					double tempt = obs2[i].t % (2 * Math.PI); // normalize
					if (tempt > Math.PI)
						tempt -= 2 * Math.PI;
					if (tempt < -Math.PI)
						tempt += 2 * Math.PI;
					if (tempt > (-1 * Math.PI / 2))
						tempf = -1 * tempt + (Math.PI / 2);
					else
						tempf = -1 * tempt - 3 * (Math.PI / 2);
					obs[i].sett(tempf);
					/*--- add it to the sum ---*/
					if (DEBUG)
						System.out.println(obs2[i]);
					last_val.add(obs2[i]);
					if (max_mag > 1)
						max_mag = 1;
					// System.out.println("v_Avoid_va-> last_val:"+last_val.x+"\t"+last_val.y+"\t"+last_val.t+"\t"
					// + last_val.r);
					// System.out.print("v_Avoid_va-> Math.atan2(y,x)3:" +
					// Math.atan2(obs[i].y,obs[i].x)+"\n"+"\n");
				}
			}// */
			if (last_val.r > 3.0)
				last_val.setr(max_mag);
			if (DEBUG)
				System.out.println("v_Avoid_va.Value: " + obs.length + " obstacles " + "output " + last_val);
		}
		// System.out.println("Avoid: "+
		// last_val.x+"\t"+last_val.y+"\t"+last_val.t+"\n");
		// System.out.print("Avoid-> Math.atan2(y,x)2:" +
		// Math.atan2(last_val.y,last_val.x)+"\n");
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
