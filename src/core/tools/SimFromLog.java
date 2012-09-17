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

public class SimFromLog extends Sim {
	public double xMin = 0;
	public double xMax = 1200.0;
	public double yMin = 0;
	public double yMax = 680.0;
	public double endTime = 0, startTime = 60;

	private ArrayList<ArrayList<SimInfo>> history = null;

	public SimFromLog(long seed) {
		this(seed, 0.5, 0.5);// 1200 and 680 can be modified to prefered size
	}

	public SimFromLog(long seed, double width, double height) {
		super(seed, width, height);
		xMax = width;
		yMax = height;
		//forest = new Continuous2D(1, (xMax - xMin), (yMax - yMin));
		history = new ArrayList<ArrayList<SimInfo>>();

		InteractionAnalysiser ia = new InteractionAnalysiser();
		ia.analysis();
	}
	
	public SimFromLog(long seed, double width, double height, double interacting_distance, double interacting_time_length, double startTime, double endTime){
		super(seed, width, height);
		xMax = width;
		yMax = height;
		this.startTime = startTime;
		this.endTime = endTime;
		//forest = new Continuous2D(1, (xMax - xMin), (yMax - yMin));
		history = new ArrayList<ArrayList<SimInfo>>();

		InteractionAnalysiser ia = new InteractionAnalysiser();
		ia.setInteractingDistance(interacting_distance);
		ia.setInteractingTimeLength(interacting_time_length);
		ia.setStartTime(startTime);
		ia.setEndTime(endTime);
		ia.analysis();
		System.out.println("total / per sec / per ant / per cm2");
		System.out.println(ia.interacting_count);
		System.out.println(ia.interacting_count/(endTime - startTime));
		System.out.println(ia.interacting_count/(double)ia.interacting.size());
		System.out.println(ia.interacting_count/(10000* width* height));
		
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

		InteractionAnalysiser interactionAnalysiser = new InteractionAnalysiser();
		interactionAnalysiser.analysis();
		history = interactionAnalysiser.getHistory();

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
