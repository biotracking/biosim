package app.test;

import core.run.BioSimWithUI;

public class Test extends BioSimWithUI {
	public Test() {
		
	}

	public static void main(String[] args) {
		
		TestConfig sim = new TestConfig();
		Test UI = new Test();
		UI.startSimulation(sim);
	}

}