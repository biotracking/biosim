package core.clay;

import core.basic_api.AgentBody;

/**
 * Return true if the probability is smaller then setting value.
 * 
 * @author
 * @version
 * @since 2011 07 29
 */

public class b_Probability_s extends NodeBoolean {

	private double lim;
	AgentBody abstract_robt;

	public b_Probability_s(double Limit)// , AgentBody ab)
	{
		lim = Limit;
		// abstract_robt = ab;
	}

	boolean last_val = false;
	double lasttime = 0;

	public boolean Value(double timestamp) {
		double val;

		if (DEBUG)
			System.out.println("b_Probability_s: Value()");

		/*--- compute the output ---*/
		if ((timestamp > lasttime) || (timestamp == -1)) {
			val = Math.random();
			if (lim > val) {
				last_val = true;
//				 System.out.println("b_Probability_s-> start!!" + timestamp + " " + val+"\n");
			} else
				last_val = false;
		}
//		 System.out.println("b_Probability_s-> start!!" + last_val +"\n");
		return last_val;
	}

}
