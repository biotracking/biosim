package app.SimpleAnts;

import core.run.BioSimWithUI;

public class SimpleAnts extends BioSimWithUI {
	public SimpleAnts() {
	}
	
	public static void main(String[] args) {
		SimpleAntsConfig sim = new SimpleAntsConfig();
		SimpleAnts UI = new SimpleAnts();
		UI.startSimulation(sim);
	}
}
