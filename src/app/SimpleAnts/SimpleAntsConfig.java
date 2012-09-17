package app.SimpleAnts;

import core.basic_api.*;
import core.basic_api.body.*;
import core.util.Vec2;

/**
 * Creates a BioSim simulation with the given constant number of predators and preys.
 */

public class SimpleAntsConfig extends Sim {
	public int numFlys = 0; // fly(prey) number constant
	public int numagents = 20; // ant(predator) number constant
	public int numObstacles = 0;
	public double xMax = 0.5;
	public double yMax = 0.5;
	double predatorRadius = 0.003;
	double preyRadius = 0.003;
	double obstacleRadius = 0.002;

	public double pixel2CM;

	boolean verbose = false;
	double ending = Double.MAX_VALUE;

	private double maxSpeed = 0.003;
	private double maxAC = 2*maxSpeed;
	
	public SimpleAntsConfig() {
		this(System.currentTimeMillis());
	}

	public SimpleAntsConfig(long seed) {
		this(seed, 0.25, 0.25);
		this.ending = 132; //count in seconds
		this.setEndingTime(ending);
	}

	public SimpleAntsConfig(long seed, double width, double height) {
		super(seed, width, height);
		xMax = width;
		yMax = height;
		this.ending = 132;
		this.setEndingTime(ending);
		// super.screen = 800/(xMax- xMin);
		// forest = new Continuous2D(1, (xMax*this.getScreen() -
		// xMin*this.getScreen()), (yMax*this.getScreen() -
		// yMin*this.getScreen()));
		// predatorPositionArray = new double[numFlys][2];
		// preyPositionArray = new double[numagents][2];
		if (verbose) {
			System.out.println("Array ini...");
		}
	}

	public void printArray(double aRRAY[][]) // /print func.
	{
		for (int i = 0; i < aRRAY.length; i++) {
			for (int j = 0; j < aRRAY[i].length; j++) {
				// System.out.print(aRRAY[i][j] + "\t");
			}
			System.out.print("\n");
		}
	}

	public void start() {
		// ((SimState)this).start();

		// seed the agents separately

		forest.clear(); // before start new sim. clear the field
		schedule.reset();
		// double x_1 = 0.0;
		// double y_1 = 0.0;
		double locationX = 0, locationY = 0;
		double directionX = 0, directionY = 0;
		int obstacleCounter = 0;

		// Seeding the obstacles
		ObstacleBody obs;
		for (int i = 0; i < numObstacles; i++) {
			locationX = xMax / 2;// Math.random()*xMax/2 + xMin+0.03;
			locationY = yMax / 2;// Math.random()*yMax/2 + yMin+0.01;
			obs = new ObstacleBody(locationX, locationY, 0.008, i, 3);
			obstacleCounter++;
			setObjectLocation(obs, locationX, locationY);
		}

		double xoMin = xMax/40, yoMin = yMax/40, xoMax = xMax*39/40, yoMax = yMax*39/40;
		for (double i = xoMin - this.obstacleRadius/2; i <= xoMax + this.obstacleRadius/2; i = i + 0.001) {
			locationX = i;
			locationY = yoMin - this.obstacleRadius/2;
			obs = new ObstacleBody(locationX, locationY, this.obstacleRadius, obstacleCounter, 3);
			obstacleCounter++;
			setObjectLocation(obs, locationX, locationY);
			locationY = yoMax + this.obstacleRadius/2;
			ObstacleBody obs2 = new ObstacleBody(locationX, locationY, 0.002, obstacleCounter, 3);
			obstacleCounter++;
			setObjectLocation(obs2, locationX, locationY);
		}

		for (double i = yoMin - this.obstacleRadius/2; i <= yoMax + this.obstacleRadius/2; i = i + 0.001) {
			locationX = xoMin - this.obstacleRadius/2;
			locationY = i;
			// ObstacleBody
			obs = new ObstacleBody(locationX, locationY, this.obstacleRadius, obstacleCounter, 3);
			obstacleCounter++;
			setObjectLocation(obs, locationX, locationY);
			locationX = xoMax + this.obstacleRadius/2;
			ObstacleBody obs2 = new ObstacleBody(locationX, locationY, this.obstacleRadius, obstacleCounter, 3);
			obstacleCounter++;
			this.setObjectLocation(obs2, locationX, locationY);
		}

		if (verbose) {
			System.out.println("Obstacles Initial Position");
		}

		// Seeding the ants
		for (int i = 0; i < numagents; i++) {
			locationX = Math.random() * (xMax - xMax/10) + xMax/20;
			locationY = Math.random() * (yMax - yMax/10) + yMax/20;
			directionX = Math.random() * 2 - 1;
			directionY = Math.sqrt(1 - directionX * directionX);
			directionY *= Math.random() > 0.5 ? 1 : -1;
			AntBody agentBody = new AntBody(locationX, locationY, directionX, directionY, i, 1, predatorRadius);
			agentBody.setLogging(true);
			agentBody.setMaxSpeed(maxSpeed);
			agentBody.setMaxAc(maxAC);
			agentBody.setMaxTurnRate(Math.PI/6);
			agentBody.setMaxTAc(Math.PI/3);
			agentBody.setDistanceOfPerception(0.013);
			AntController agentC = new AntController(agentBody);
			agentBody.setController((AgentController) agentC);
			this.setObjectLocation(agentBody, locationX, locationY);
		}
		if (verbose) {
			System.out.println("agents Initial Position");
		}

		// Seeding the flys
		for (int i = 0; i < numFlys; i++) {
			locationX = Math.random() * xMax / 2 + xMin;
			locationY = Math.random() * yMax / 2 + yMin;
			directionX = Math.random() * 2 - 1;
			directionY = Math.sqrt(1 - directionX * directionX);
			directionY *= Math.random() > 0.5 ? 1 : -1;// */
			FlyBody fly = new FlyBody(locationX, locationY, directionX, directionY, i, 2, preyRadius);
			FlyController flyC = new FlyController(fly);
			fly.setController((AgentController) flyC);
			this.setObjectLocation(fly, locationX, locationY);
		}
		if (verbose) {
			System.out.println("agents Initial Position");
		}

	}

	public static void main(String[] args) {
		doLoop(SimpleAntsConfig.class, args);
		System.exit(0);
	}

}// end of the class BioSim
