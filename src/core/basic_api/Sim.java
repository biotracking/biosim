package core.basic_api;

import java.util.ArrayList;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.util.Double2D;

/** Creates a Sim simulation with the given constant number predator and prey. */

public abstract class Sim extends SimState {
	public double screen;
	public Continuous2D forest;
	public double xMax, yMax;
	public double xMin, yMin;
	public static double timeStepSize = (1.0 / 30.0);
	public ArrayList<Integer> loggingList;
	DrawAgentBody dab = new DrawAgentBody();
	BoundChecker bc = new BoundChecker();
	public ObstacleCollisionChecker obstacleCollisionChecker = new ObstacleCollisionChecker();
	private double bigObstacleThreshold = 0.1;
	// public GUIState gui;
	// public Display2D display; //screen
	// public JFrame displayFrame; //screen
	// ContinuousPortrayal2D forestPortrayal = new ContinuousPortrayal2D();
	public Sim(long seed) {
		// this(seed, 1200, 680);//0929
		this(seed, 1.25, 0.68);
	}

	public Sim(long seed, double width, double height) {
		super(seed);
		xMin = 0;
		yMin = 0;
		xMax = width + xMin;
		yMax = height + yMin;
		this.bigObstacleThreshold = 0.1;
		// forest = new Continuous2D(1, (xMax - xMin), (yMax - yMin)); //0929
		if (width / height > 1250 / 680) {
			screen = 1250 / width;
		} else {
			screen = 680 / height;
		}
		forest = new Continuous2D(1, 1250, 680); // new Continuous2D(1,
		// (xMax*this.getScreen() -
		// xMin*this.getScreen()),
		// (yMax*this.getScreen() -
		// yMin*this.getScreen()));
		timeStepSize = (1.0 / 30.0);
		loggingList = new ArrayList<Integer>();
		loggingList.add(1);
		dab.setSize(xMin, yMin, xMax, yMax, screen);
		bc.setBound(xMin, yMin, xMax, yMax);
		obstacleCollisionChecker.clearObstacle();
	}

	public Sim(long seed, double width, double height, double d) {
		super(seed);
		xMin = 0;
		yMin = 0;
		xMax = width + xMin;
		yMax = height + yMin;
		this.bigObstacleThreshold = d;

		// forest = new Continuous2D(1, (xMax - xMin), (yMax - yMin)); //0929
		if (width / height > 1250 / 680) {
			screen = 1250 / width;
		} else {
			screen = 680 / height;
		}
		forest = new Continuous2D(1, 1250, 680); // new Continuous2D(1,
		// (xMax*this.getScreen() -
		// xMin*this.getScreen()),
		// (yMax*this.getScreen() -
		// yMin*this.getScreen()));
		timeStepSize = (1.0 / 30.0);
		loggingList = new ArrayList<Integer>();
		loggingList.add(1);
		dab.setSize(xMin, yMin, xMax, yMax, screen);
		bc.setBound(xMin, yMin, xMax, yMax);
		obstacleCollisionChecker.clearObstacle();
	}

	public double getTimeStepSize() {
		return this.timeStepSize;
	}

	public boolean inLoggingList(int agentType) {
		if (loggingList.contains(agentType))
			return true;
		return false;
	}

	public abstract void start();

	public static void main(String[] args) {
		doLoop(Sim.class, args);
		System.exit(0);
	}

	public double getScreen() {
		return screen;
	}

	public void setObjectLocation(Steppable obj, double x, double y) {
		forest.setObjectLocation(obj, new Double2D((xMin + x)
				* this.getScreen(), (yMin + y) * this.getScreen()));
		schedule.scheduleRepeating(obj);
		// if the obj is an big obstacle, then record it for obstacle collision check
		if(((AgentPortrayal)obj).getAgentType() == 3 && ((AgentPortrayal)obj).getAgentSize() >= this.bigObstacleThreshold){//
			obstacleCollisionChecker.recordObstacle(obj);
		}
	}
	
	public void setEndingTime(double secs){
		double ending = secs / this.timeStepSize;
		this.schedule.AFTER_SIMULATION = ending;
	}

	public void setBigObstacleThreshold(double t){
		this.bigObstacleThreshold = t;
	}

}// end of the class Sim
