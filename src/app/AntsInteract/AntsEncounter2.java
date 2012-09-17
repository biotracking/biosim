package app.AntsInteract;

import core.run.BioSimWithUI;
import core.tools.SimFromATF;
import core.tools.SimFromLog;

public class AntsEncounter2 extends BioSimWithUI {
	public AntsEncounter2() {
		
	}

	public static void main(String[] args) {
		
		//SimFromLog sim = new SimFromLog(long random long, xmax, ymax, interact_distance, interact_time_length, start_time, end_time);
		SimFromLog sim = new SimFromLog(System.currentTimeMillis(), 0.5, 0.5, 0.004, 1, 0, 132);
		//AntsEncounter2 UI = new AntsEncounter2();
		//UI.startSimulation(sim);
	}

}