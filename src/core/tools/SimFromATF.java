package core.tools;

import java.util.ArrayList;

import sim.field.continuous.Continuous2D;
import sim.util.Double2D;
import core.basic_api.Sim;
import core.basic_api.body.AgentFromLogging;

/**
 * @author Hai Shang
 * @author hshang6@gatech.edu This is an example of Simulation from log file
 */

public class SimFromATF extends Sim {
	public double xMin = 0;
	public double xMax = 1200.0;
	public double yMin = 0;
	public double yMax = 680.0;
	public double endTime = 60, startTime = 0;

	private ArrayList<ArrayList<SimInfo>> history = null;

	public SimFromATF(long seed) {
		this(seed, 0.5, 0.5);// 1200 and 680 can be modified to prefered size
	}

	public SimFromATF(long seed, double width, double height) {
		super(seed, width, height);
		xMax = width;
		yMax = height;
		//forest = new Continuous2D(1, (xMax - xMin), (yMax - yMin));
		history = new ArrayList<ArrayList<SimInfo>>();

		ATFAnalysiser ia = new ATFAnalysiser();
		ia.analysis();
	}
	
	public SimFromATF(long seed, double width, double height, double interacting_distance, double interacting_time_length, double startTime, double endTime){
		super(seed, width, height);
		xMax = width;
		yMax = height;
		this.startTime = startTime;
		this.endTime = endTime;
		//forest = new Continuous2D(1, (xMax - xMin), (yMax - yMin));
		history = new ArrayList<ArrayList<SimInfo>>();

		ATFAnalysiser ia = new ATFAnalysiser();
		ia.setInteractingDistance(interacting_distance);
		ia.setInteractingTimeLength(interacting_time_length);
		ia.setStartTime(startTime);
		ia.setEndTime(endTime);
		ia.analysis();
		System.out.println("total num of frame: "+ ia.frameNum);
		System.out.println("total agents' num: " + ia.history.size());
		System.out.println("total interacting count: " + ia.interacting_count);
		System.out.println("interacting count per second: " + ia.interacting_count/(endTime - startTime));
		System.out.println("interacting count per ant: " + ia.interacting_count/(double)ia.interacting.size());
		System.out.println("interacting count per centi-meter square: " + ia.interacting_count/(10000* width* height));
		System.out.println("maxSpeed: " + ia.maxSpeed);
		System.out.println("minSpeed: " + ia.minSpeed);
		System.out.println("maxThetaOff: " + ia.maxThetaOff);
		System.out.println("minThetaOff: " + ia.minThetaOff);
		System.out.println("speedHistogram: ");
		for(int i = 0; i < (int)(ia.guessedMaxSpeed/ia.speedWidth); i++){
			//System.out.println("speed histogram["+i+"]: "+ ia.speedHistogram[i]);
			System.out.println(ia.speedHistogram[i]);
		}
		System.out.println("speed zero: " + ia.speedZero);
		System.out.println("speed exceed: " + ia.speedOutBound);
		System.out.println("speed lost: " + ia.speedLost);
		System.out.println("thetaHistogram: ");
		for(int i = 0; i < (int)(Math.PI/ia.thetaWidth); i++){
			System.out.println("theta histogram["+i+"]: "+ ia.thetaHistogram[i]);
		}
		System.out.println("theta zero: " + ia.thetaZero);
		System.out.println("theta exceed: " + ia.thetaOutBound);
		System.out.println("theta lost: " + ia.thetaLost);
	}

	/**
	 * in the start function, initiate all the agents and the environment
	 */
	public void start() {
		forest.clear(); // before start new sim. clear the field
		schedule.reset(); // before start new sim. clear the schedule
		// Analysiser analysiser = new Analysiser();
		// // analysis the log and generate history
		// history = analysiser.analysis();

		//InteractionAnalysiser interactionAnalysiser = new InteractionAnalysiser();
		//interactionAnalysiser.analysis();
		
		ATFAnalysiser atfAnalysiser = new ATFAnalysiser();
		atfAnalysiser.analysis();
		
		//history = interactionAnalysiser.getHistory();
		history = atfAnalysiser.getHistory();
		
		double px, py, dx, dy;

		// Seeding the agents
		int numOfAgents = history.size();
		double ranTemp1 = 0, ranTemp2 = 0;
		SimInfo tempSI = null;
		for (int i = 0; i < numOfAgents; i++) {
			tempSI = history.get(i).get(0);
			px = tempSI.getPX()*core.basic_api.DrawAgentBody.scaler;
			py = tempSI.getPY()*core.basic_api.DrawAgentBody.scaler;
			dx = tempSI.getDX();
			dy = tempSI.getDY();
			AgentFromLogging ant = new AgentFromLogging(px, py, dx, dy, i, 1);
			ant.setHistory(history.get(i));
			//ant.scale = this.screen;
			forest.setObjectLocation(ant, new Double2D(px, py));
			schedule.scheduleRepeating(ant);
		}
	}

}// end of the class SimFromLog
