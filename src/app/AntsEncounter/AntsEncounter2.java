package app.AntsEncounter;

import core.run.BioSimWithUI;
import core.tools.SimFromLog;

public class AntsEncounter2 extends BioSimWithUI {
	public AntsEncounter2() {
		
	}

	public static void main(String[] args) {
		
		SimFromLog sim = new SimFromLog(System.currentTimeMillis(), 0.5, 0.5, 0.01, 5, 0.3, 10);
		//AntsEncounter2 UI = new AntsEncounter2();
		//UI.startSimulation(sim);
	}

}