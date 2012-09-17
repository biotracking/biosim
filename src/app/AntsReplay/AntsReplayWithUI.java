package app.AntsReplay;

import core.run.BioSimWithUI;

public class AntsReplayWithUI extends BioSimWithUI {
	public AntsReplayWithUI() {
		
	}

	public static void main(String[] args) {
		
		AntsReplay sim = new AntsReplay();
		AntsReplayWithUI UI = new AntsReplayWithUI();
		UI.startSimulation(sim);
	}

}
