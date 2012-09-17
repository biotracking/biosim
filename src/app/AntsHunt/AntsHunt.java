package app.AntsHunt;

import core.run.BioSimWithUI;


public class AntsHunt extends BioSimWithUI {

	public AntsHunt() {
	}

	public static void main(String[] args) {

		AntsHuntConfig sim = new AntsHuntConfig();
		AntsHunt UI = new AntsHunt();
		UI.startSimulation(sim);
	}

}
