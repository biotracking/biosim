package app.AntsSimulatedTracks;

import core.run.BioSimWithUI;

public class AntsSimulatedTracksWithUI extends BioSimWithUI {
	public AntsSimulatedTracksWithUI() {
		
	}

	public static void main(String[] args) {
		
		AntsSimulatedTracks sim = new AntsSimulatedTracks();
		AntsSimulatedTracksWithUI UI = new AntsSimulatedTracksWithUI();
		UI.startSimulation(sim);
	}

}
