/*
 * TBVersion.java
 */

package core.util;

/**
 * Report the current TeamBots version.
 * <P>
 * Copyright (c)2000 Tucker Balch
 * 
 * @author Tucker Balch
 * @version $Revision: 1.7 $
 */

public final class TBVersion {
	/**
	 * Return a short message about this version of TeamBots.
	 */
	public static String shortReport() {
		return ("TeamBots 2.0e (c)2000 Tucker Balch, CMU and GTRC");
	}

	/**
	 * Return a long message about this version of TeamBots.
	 */
	public static String longReport() {
		return (shortReport() + "\n" + "compiled in the Iron City\n" + "\n" + "by Tucker Balch (trb@cs.cmu.edu)");
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
