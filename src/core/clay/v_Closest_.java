/*
 * v_Closest_va.java
 */

package core.clay;

import sim.util.Double2D;
import core.basic_api.AbstractRobot;
import core.util.Vec2;

/**
 * Finds the closest in a list of Vec2s. Assumes the vectors point
 * egocentrically to the objects, so that the closest one has the shortest r
 * value.
 * <P>
 * For detailed information on how to configure behaviors, see the <A
 * HREF="../clay/docs/index.html">Clay page</A>.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A> (c)1997, 1998 Tucker Balch
 * 
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class v_Closest_ extends NodeVec2 {
	/**
	 * Turns debugging on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	private NodeVec2Array embedded1;
	private int closestType;
	private AbstractRobot abstract_robot;

	/**
	 * Instantiate a v_Closest_va node.
	 * 
	 * @param im1
	 *            NodeVec2, the embedded node that generates a list of items to
	 *            scan.
	 */
	public v_Closest_(int type, AbstractRobot ab)// NodeVec2Array im1)
	{
		if (DEBUG)
			System.out.println("v_Closest_va: instantiated.");
		closestType = type;
		abstract_robot = ab;
		// embedded1 = im1;
	}

	Vec2 last_val = new Vec2();
	double lasttime = 0;

	/**
	 * Return a Vec2 representing the closest object, or 0,0 if none are
	 * visible.
	 * 
	 * @param timestamp
	 *            double, only get new information if timestamp > than last call
	 *            or timestamp == -1.
	 * @return the vector.
	 */
	public Vec2 Value(double timestamp) {
		double tempmag;

		if ((timestamp > lasttime) || (timestamp == -1)) {
			/*--- reset the timestamp ---*/
			if (timestamp > 0)
				lasttime = timestamp;

			/*--- reset output ---*/
			last_val.setr(0);

			// /*--- get the list of object ---*/
			// Vec2[] objs = embedded1.Value(timestamp);
			// //System.out.print("v_Closest_va: "+objs.length);
			//
			// /*--- consider each obstacle ---*/
			// double closest = 99999999;
			// for(int i = 0; i<objs.length; i++)
			// {
			// if(objs[i]!=null)
			// {
			// if (objs[i].r < closest)
			// {
			// //System.out.print("v_Closest_va->objs[i]: "+last_val.x+"\t"+last_val.y+"\n");
			// closest = objs[i].r;
			// last_val = objs[i];
			// }
			// }
			// }
			if (closestType == 2)
				last_val = new Vec2(abstract_robot.nearestPetry().x,
						abstract_robot.nearestPetry().y);
			else if (closestType == 1){
				Double2D tv = abstract_robot.nearestAnt();
				last_val = new Vec2(tv.x, tv.y);
			}
			if (DEBUG)
				System.out.println("v_Closest_va.Value: " + " things to see "
						+ "output " + last_val);
		}
		// System.out.print("v_Closest_: "+closestType+"\t"+last_val.x+"\t"+last_val.y+"\n");

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
