/*
 * va_Subtract_vav.java
 */

package core.clay;

import core.util.Vec2;

/**
 * Subtract one vector from an array of others. Useful for converting ego to
 * global positions for an array of sensor readings.
 * <P>
 * For detailed information on how to configure behaviors, see the <A
 * HREF="../clay/docs/index.html">Clay page</A>.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A> (c)1997, 1998 Tucker Balch
 * 
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class va_Subtract_vav extends NodeVec2Array {
	/**
	 * Turn debug printing on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	private NodeVec2Array embedded1;
	private NodeVec2 embedded2;

	/**
	 * Instantiate an va_Subtract_vav schema.
	 * 
	 * @param im1
	 *            NodeVec2Array, an embedded node that generates an Array of
	 *            vectors to be subtracted.
	 * @param im2
	 *            NodeVec2, an embedded node that generates a vector to be
	 *            subtracted from the others.
	 */
	public va_Subtract_vav(NodeVec2Array im1, NodeVec2 im2) {
		if (DEBUG)
			System.out.println("va_Subtract_vav: instantiated.");
		embedded1 = im1;
		embedded2 = im2;
	}

	public va_Subtract_vav() {
	}

	Vec2[] last_val = new Vec2[0];
	double lasttime = -1;

	/**
	 * Return a Vec2Array that is the difference of the original values and the
	 * embedded schema.
	 * 
	 * @param timestamp
	 *            double, only get new information if timestamp > than last call
	 *            or timestamp == -1.
	 * @return the array of subtracted Vec2s.
	 */
	public Vec2[] Value(double timestamp) {
		if (DEBUG)
			System.out.println("va_Subtract_vav: Value()");

		if ((timestamp > lasttime) || (timestamp == -1)) {
			/*--- reset the timestamp ---*/
			if (timestamp > 0)
				lasttime = timestamp;

			/*--- get the embedded values ---*/
			last_val = embedded1.Value(timestamp);
			Vec2 tmp = embedded2.Value(timestamp);
			int tmpea;
			if (last_val.length != 0) {
				/*--- Sub the values ---*/
				for (int i = 0; i < last_val.length; i++)
				// last_val[i].sub(tmp);
				{
					if (last_val[i] != null) {
						// System.out.print("va_Subtract_vav: "+last_val[i].x+"\t"+last_val[i].y+"\n");
						last_val[i].x = last_val[i].x - tmp.x;
						last_val[i].y = last_val[i].y - tmp.y;
					}

				}
			}
		}
		Vec2[] retval = new Vec2[last_val.length];
		for (int i = 0; i < last_val.length; i++) {
			if (last_val[i] != null)
				retval[i] = new Vec2(last_val[i].x, last_val[i].y);
		}
		return (retval);
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
