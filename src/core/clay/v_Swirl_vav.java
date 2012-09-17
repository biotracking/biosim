/*
 * v_Swirl_vav.java
 */

package core.clay;

import core.util.Vec2;

/**
 * Generate a vector that swirls to one side or the other of detected hazards.
 * One embedded node provides a list of hazards, the other points in the
 * reference direction (typically a goal location).
 * <P>
 * The "swirl" behavior was originally developed by Andy Henshaw and Tom Collins
 * at the Georgia Tech Research Institute. It is also similar to Marc Slack's
 * NATs.
 * <P>
 * For detailed information on how to configure behaviors, see the <A
 * HREF="../clay/docs/index.html">Clay page</A>.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A> (c)1997, 1998 Tucker Balch
 * 
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class v_Swirl_vav extends NodeVec2 {
	/**
	 * Turn debug printing on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	private NodeVec2Array embedded1;
	private NodeVec2 embedded2;
	private double sphere = 1.0;
	private double safety = 0.0;

	/**
	 * Instantiate a v_Swirl_vav schema.
	 * 
	 * @param soe
	 *            double, the sphere of influence beyond which the hazards are
	 *            not considered.
	 * @param s
	 *            double, the safety zone, inside of which a maximum repulsion
	 *            from the object is generated.
	 * @param im1
	 *            NodeVec2Array, the embedded perceptual schema that generates a
	 *            list of items to avoid.
	 * @param im2
	 *            NodeVec2, the embedded perceptual schema that generates a
	 *            pointer to the goal (must be egocentric).
	 */
	public v_Swirl_vav(double soe, double s, NodeVec2Array im1, NodeVec2 im2) {
		if (DEBUG)
			System.out.println("v_Swirl_vav: instantiated.");
		embedded1 = im1;
		embedded2 = im2;
		if ((soe < s) || (soe < 0) || (s < 0)) {
			System.out.println("v_Swirl_vav: illegal parameters");
			return;
		}
		sphere = soe;
		safety = s;
	}

	public v_Swirl_vav() {
	}

	Vec2 last_val = new Vec2();
	double lasttime = 0;

	/**
	 * Return a Vec2 representing the direction to go.
	 * 
	 * @param timestamp
	 *            double, only get new information if timestamp > than last call
	 *            or timestamp == -1.
	 * @return the movement vector.
	 */
	public Vec2 Value(double timestamp) {
		double tempmag;
		double max_mag = 0;
		double tempdir;
		double refheading;

		if ((timestamp > lasttime) || (timestamp == -1)) {
			/*--- reset the timestamp ---*/
			if (timestamp > 0)
				lasttime = timestamp;

			/*--- reset output ---*/
			last_val.setr(0);

			/*--- get the list of obstacles and ref dir ---*/
			Vec2[] obs = embedded1.Value(timestamp);
			refheading = embedded2.Value(timestamp).t;

			/*--- consider each obstacle ---*/
			for (int i = 0; i < obs.length; i++) {
				if (obs[i] != null) {

					// only swirl around the obs if it is in front of us.
					// tempdir = -Units.BestTurnRad(refheading, obs[i].t);
					// tempdir = Units.BestTurnRad(refheading, obs[i].t);
					if (obs[i].t < Math.PI / 2)
						tempdir = 1;
					else
						tempdir = -1;
					// System.out.print("v_Swirl_va-> tempdir: "+tempdir+"\n");
					obs[i].sett(obs[i].t + (tempdir * Math.PI / 2));

					/*--- set t between +1.57, 0 to -1.57---*/
					// System.out.print("v_Swirl_va-> obs[i]: "+obs[i].t+"\n");
					double tempf;
					double tempt = obs[i].t % (2 * Math.PI); // normalize
					while (tempt > Math.PI)
						tempt -= 2 * Math.PI;
					while (tempt < -Math.PI)
						tempt += 2 * Math.PI;
					tempf = -1 * tempt + (Math.PI / 2);
					// System.out.print("v_Swirl_va-> tempf: "+tempf+"\n");
					obs[i].sett(tempf);
					// System.out.print("v_Swirl_va-> obs[i]: "+obs[i].t+"\n");

					// if (Math.abs(tempdir)<Math.PI/2)
					// {
					// System.out.println("v_Swirl_va-> obs2[ "+i+"]:"+obs[i].t+"\t"+
					// obs[i].r);
					/*--- compute direction of swirl ---*/
					// first decide left or right, negative is left
					// if (tempdir < 0)
					// obs[i].sett(obs[i].t - Math.PI/2);
					// else
					// obs[i].sett(obs[i].t + Math.PI/2);

					/*--- compute magnitude of swirl ---*/
					// inside saftey zone, set full magnitude
					if (obs[i].r < safety) {
						tempmag = 1;
					}
					// in controlled zone
					else if (obs[i].r < sphere)
						tempmag = (sphere - obs[i].r) / (sphere - safety);
					// outside sphere of influence, ignore
					else
						tempmag = 0;
					// set the magnitude
					obs[i].setr(tempmag);

					/*--- check if it is the biggest one ---*/
					if (Math.abs(tempmag) > max_mag)
						max_mag = Math.abs(tempmag);

					//
					// /*--- set t between +1.57 to -1.57---*/
					// tempt = obs[i].t%(2*Math.PI); //normalize
					// while(tempt > Math.PI) tempt -= 2*Math.PI;
					// while(tempt < -Math.PI) tempt += 2*Math.PI;
					// tempf = -1*tempt + (Math.PI/2);
					// obs[i].sett(tempf);
					//						
					/*--- add it to the sum ---*/
					if (DEBUG)
						System.out.println(obs[i]);
					last_val.add(obs[i]);

					// System.out.println("v_Swirl_va-> obs3[ "+i+"]:"+obs[i].t+"\t"+
					// obs[i].r);
					// }
				}
			}
			/*--- normalize ---*/
			// NOT!
			// if (last_val.r > 0)
			// last_val.setr(0.0);
			last_val.setr(max_mag);
			if (DEBUG)
				System.out.println("v_Swirl_vav.Value: " + obs.length
						+ " obstacles " + "output " + last_val);
		}
		// System.out.println("v_Swirl_va-> last:"+ last_val.x+"\t"+last_val.y +
		// "\t"+last_val.r);
		// System.out.print("v_Swirl_va-> Math.atan2(y,x)3:" +
		// Math.atan2(last_val.y,last_val.x)+"\n"+"\n");
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
