/*
 * NodeScalar.java
 */

package core.clay;

/**
 * A Node that returns int, double and boolean values.
 * <P>
 * For detailed information on how to configure behaviors, see the <A
 * HREF="../clay/docs/index.html">Clay page</A>.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A> (c)1997, 1998 Tucker Balch
 * 
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public abstract class NodeScalar extends Node {
	/**
	 * Get the double value.
	 * 
	 * @param timestamp
	 *            double indicates time of the request
	 * @return the double value
	 */
	public abstract double doubleValue(double timestamp);

	/**
	 * Get the int value.
	 * 
	 * @param timestamp
	 *            double indicates time of the request
	 * @return the int value
	 */
	public abstract int intValue(double timestamp);

	/**
	 * Get the boolean value.
	 * 
	 * @param timestamp
	 *            double indicates time of the request
	 * @return the boolean value
	 */
	public abstract boolean booleanValue(double timestamp);
}
