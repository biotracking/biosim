package core.clay;

import core.basic_api.AgentBody;

public class b_probabilitySubduedPrey_r extends NodeBoolean {

	private double lim;
	AgentBody abstract_robt;

	public b_probabilitySubduedPrey_r(AgentBody ab) {
		abstract_robt = ab;
	}

	boolean last_val = false;
	double lasttime = 0;
	boolean temp_last = false;

	public boolean Value(double timestamp) {
		double val;

		if (DEBUG)
			System.out.println("b_Probability_s: Value()");

		/*--- compute the output ---*/
		if ((timestamp > lasttime) || (timestamp == -1)) {
			lim = abstract_robt.getSubduedProbility();
			val = Math.random();
			if (lim > val) {
				last_val = true;
				abstract_robt.setSubdueAgent(2, true);
			} else {
				last_val = false;
				// abstract_robt.setSubdueAgent(2, false);
			}
		}

		// if(last_val!=temp_last)
		// System.out.print("b_probabilitySubduedPrey_r-> last_val: " + last_val
		// + "\n");
		// temp_last = last_val;

		return last_val;
	}

}
