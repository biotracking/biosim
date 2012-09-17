package app.AntsEncounter;

import core.run.BioSimWithUI;

public class AntsEncounter extends BioSimWithUI {
	public AntsEncounter() {
		
	}

	public static void main(String[] args) {
		
		AntsEncounterConfig sim = new AntsEncounterConfig();
		AntsEncounter UI = new AntsEncounter();
		UI.startSimulation(sim);
	}

}