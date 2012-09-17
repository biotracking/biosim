package app.Predator_Prey;

public class BatchRun {
	public static void main(String args[]){
		
		HuntConfig hc = new HuntConfig();
		double simulationTime = Double.valueOf((args[0]));
		double simulationRound = Double.valueOf((args[1]));
		hc.setEndingTime(simulationTime);
		hc.start();
		for(int i = 0; i < simulationRound; i++)
		hc.doLoop(HuntConfig.class, args);
	}
	
}
