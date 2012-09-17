package app.AntsLearned;

import core.run.BioSimWithUI;

public class AntsLearnedWithUI extends BioSimWithUI {
	public AntsLearnedWithUI() {
		
	}

	public static void main(String[] args) {
		
		AntsLearned sim = new AntsLearned();
		AntsLearnedWithUI UI = new AntsLearnedWithUI();
		UI.startSimulation(sim);
	}

}
