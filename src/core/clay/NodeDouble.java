/*
 * NodeDouble.java
 */

package core.clay;

/**
 * A Node that returns double values. Since it is an extension of NodeScalar, it
 * can return boolean and int values also.
 * <P>
 * For detailed information on how to configure behaviors, see the <A
 * HREF="../clay/docs/index.html">Clay page</A>.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A> (c)1997, 1998 Tucker Balch
 * 
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public abstract class NodeDouble extends NodeScalar {
	/**
	 * Provides the value of the node. If you implement a NodeDouble, you need
	 * to define this method.
	 * 
	 * @param timestamp
	 *            double indicates time of the request
	 * @return the value
	 */
	public abstract double Value(double timestamp);

	/**
	 * Get the double value.
	 * 
	 * @param timestamp
	 *            double indicates time of the request
	 * @return the double value
	 */
	public double doubleValue(double timestamp) {
		return Value(timestamp);
	}

	/**
	 * Convert double output to int.
	 * 
	 * @param timestamp
	 *            double indicates time of the request
	 * @return the int value
	 */
	public int intValue(double timestamp) {
		return (int) doubleValue(timestamp);
	}

	/**
	 * Convert double output to boolean.
	 * 
	 * @param timestamp
	 *            double indicates time of the request
	 * @return the boolean value (true if non-zero).
	 */
	public boolean booleanValue(double timestamp) {
		if (Value(timestamp) != 0)
			return (true);
		else
			return (false);
	}
}
