/*
 * CircularBufferException.java
 */

package core.util;

/**
 * Signals that an execption of some sort has occured in a FunctionApproximator.
 * <P>
 * Copyright (c)2000 Tucker Balch
 * 
 * @author Tucker Balch (tucker@cc.gatech.edu)
 * @version $Revision: 1.2 $
 */

public class CircularBufferException extends Exception {

	/**
	 * Constructs a <code>CircularBufferException</code> with no detail message.
	 */
	public CircularBufferException() {
		super();
	}

	/**
	 * Constructs a <code>CircularBufferException</code> with a specified detail
	 * message.
	 * 
	 * @param s
	 *            the detail message.
	 */
	public CircularBufferException(String msg) {
		super(msg);
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
