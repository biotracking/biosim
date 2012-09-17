package app.Predator_Prey;

import java.awt.Color;

import core.basic_api.AgentController;
import core.basic_api.Sim;
import core.basic_api.ObstacleCollisionChecker;
import core.basic_api.body.AntBody;
import core.basic_api.body.FlyBody;
import core.basic_api.body.ObstacleBody;
import core.basic_api.body.PredatorBody;
import core.basic_api.body.PreyBody;
import core.util.Vec2;

/**
 * Creates a BioSim simulation for predators hunt preys.
 */

public class HuntConfig extends Sim

{
	public int numOfPreys = 3;
	public int numOfPredators = 10;
	public int numOfObstacles = 5;
	public double xMax = 0.5;
	public double yMax = 0.5;
	static double predatorRadius = 0.007;
	static double predatorSightLength = 0.021;
	static double preyRadius = 0.003;
	static double preySightLength = 0.021;
	double obstacleRadius = 0.002;
	double predatorMaxSpeed = 0.01;
	double preyMaxSpeed = 0.01;

	public double pixel2CM;

	boolean verbose = false;

	public HuntConfig() {
		this(System.currentTimeMillis());
	}

	public HuntConfig(long seed) {
		this(seed, 0.25, 0.25);
	}

	public HuntConfig(long seed, double width, double height) {
		super(seed, width, height, predatorSightLength - predatorRadius);
		xMax = width;
		yMax = height;
	}

	public void start() {

		forest.clear(); // before start new sim. clear the field
		schedule.reset();
		obstacleCollisionChecker.clearObstacle();
		double locationX = 0, locationY = 0;
		double directionX = 0, directionY = 0;
		int obstacleCounter = 0;
		this.setBigObstacleThreshold(0.005);
		// Seeding the obstacles
		ObstacleBody obs;
		for (int i = 0; i < numOfObstacles; i++) {
			locationX = Math.random()*xMax;
			locationY = Math.random()*yMax;
			obs = new ObstacleBody(locationX, locationY, obstacleRadius*3, i, 3);//the input parameters are: (x, y, radius, id, agentType)
			obstacleCounter++;
			setObjectLocation(obs, locationX, locationY);
		}

		//initialize the boundary with obstacles
		double xoMin = xMax/40, yoMin = yMax/40, xoMax = xMax*39/40, yoMax = yMax*39/40;		
		for (double i = xoMin - this.obstacleRadius; i <= xoMax
				+ this.obstacleRadius; i = i + 0.001) {
			locationX = i;
			locationY = yoMin - this.obstacleRadius;
			obs = new ObstacleBody(locationX, locationY, this.obstacleRadius,
					obstacleCounter, 3);
			obstacleCounter++;
			setObjectLocation(obs, locationX, locationY);
			locationY = yoMax + this.obstacleRadius;
			ObstacleBody obs2 = new ObstacleBody(locationX, locationY, 0.002,
					obstacleCounter, 3);
			obstacleCounter++;
			setObjectLocation(obs2, locationX, locationY);
		}

		for (double i = yoMin - this.obstacleRadius; i <= yoMax
				+ this.obstacleRadius; i = i + 0.001) {
			locationX = xoMin - this.obstacleRadius;
			locationY = i;
			// ObstacleBody
			obs = new ObstacleBody(locationX, locationY, this.obstacleRadius,
					obstacleCounter, 3);
			obstacleCounter++;
			setObjectLocation(obs, locationX, locationY);
			locationX = xoMax + this.obstacleRadius;
			ObstacleBody obs2 = new ObstacleBody(locationX, locationY,
					this.obstacleRadius, obstacleCounter, 3);
			obstacleCounter++;
			this.setObjectLocation(obs2, locationX, locationY);
		}
		//end of initialization the boundary

		//Seeding the predators
		for (int i = 0; i < numOfPredators; i++) {
			do{
				locationX = Math.random() * (xMax - xMax/10) + xMax/20;
				locationY = Math.random() * (yMax - yMax/10) + yMax/20;
			} while (!obstacleCollisionChecker.checkCollision(locationX, locationY, predatorRadius));
			directionX = Math.random() * 2 - 1;
			directionY = Math.sqrt(1 - directionX * directionX);
			directionY *= Math.random() > 0.5 ? 1 : -1;
			PredatorBody agentBody = new PredatorBody(locationX, locationY, directionX,
					directionY, i, 1, predatorRadius);
			PredatorController agentC = new PredatorController(agentBody);
			agentBody.setLogging(false);
			agentBody.setMaxSpeed(predatorMaxSpeed);
			agentBody.setDistanceOfPerception(predatorSightLength);
			agentBody.setController((AgentController) agentC);
			this.setObjectLocation(agentBody, locationX, locationY);
		}
		
		//Seeding the preys
		for (int i = 0; i < numOfPreys; i++) {
			do{
				locationX = Math.random() * (xMax - xMax/10) + xMax/20;
				locationY = Math.random() * (yMax - yMax/10) + yMax/20;
			} while (!obstacleCollisionChecker.checkCollision(locationX, locationY, preyRadius));
			directionX = Math.random() * 2 - 1;
			directionY = Math.sqrt(1 - directionX * directionX);
			directionY *= Math.random() > 0.5 ? 1 : -1;
			PreyBody agentBody = new PreyBody(locationX, locationY, directionX,
					directionY, i, 2, preyRadius);
			PreyController agentC = new PreyController(agentBody);
//			FlyController agentC = new FlyController(agentBody);
			agentBody.setLogging(false);
			agentBody.setDisplayColor(Color.red);
			agentBody.setMaxSpeed(preyMaxSpeed);
			agentBody.setDistanceOfPerception(preySightLength);
			agentBody.setController((AgentController) agentC);
			this.setObjectLocation(agentBody, locationX, locationY);
		}

		
	}

	public static void main(String[] args) {
		doLoop(HuntConfig.class, args);
		System.exit(0);
	}

}// end of the class BioSim
