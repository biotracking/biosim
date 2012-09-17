package app.Predator_Prey;

import core.run.BioSimWithUI;

public class Hunt extends BioSimWithUI {
	public Hunt() {}

	public static void main(String[] args) {
		HuntConfig sim = new HuntConfig();
		Hunt UI = new Hunt();
		UI.startSimulation(sim);
	}

}