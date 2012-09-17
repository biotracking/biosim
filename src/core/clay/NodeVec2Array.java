/*
 * NodeVec2Array.java
 */

package core.clay;

import core.util.Vec2;

/**
 * A Node that returns an array of Vec2 values.
 * <P>
 * For detailed information on how to configure behaviors, see the <A
 * HREF="../clay/docs/index.html">Clay page</A>.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A> (c)1997, 1998 Tucker Balch
 * 
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public abstract class NodeVec2Array extends Node {
	/**
	 * Get the Vec2 array output. If you implement a NodeVec2Array, you need to
	 * define this method.
	 * 
	 * @param timestamp
	 *            double, the time of the request
	 */
	public abstract Vec2[] Value(double timestamp);
}
