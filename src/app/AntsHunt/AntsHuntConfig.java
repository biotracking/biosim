package app.AntsHunt;

import core.basic_api.AgentController;
import core.basic_api.Sim;
import core.basic_api.body.AntBody;
import core.basic_api.body.FlyBody;
import core.basic_api.body.ObstacleBody;
import core.util.Vec2;

/**
 * Creates a BioSim simulation with the given constant number predator and prey.
 */

public class AntsHuntConfig extends Sim

{
	public int numFlys = 10; // fly(prey) number constant
	public int numagents = 20; // ant(predator) number constant
	public int numObstacles = 0;
	public double xMax;// 1200.0; //0929
	public double yMax;// 680.0; //0929
	double predatorRadius = 0.006;
	double preyRadius = 0.004;
	double obstacleRadius = 0.02;
	double wallObstackeRadius = 0.0035;
	public Vec2[] obstacle_body;

	boolean verbose = false;

	public AntsHuntConfig() {
		this(System.currentTimeMillis());
	}
	
	public AntsHuntConfig(long seed) {
//		super(seed, 0.22, 0.13);
		this(seed, 0.3125, 0.17);
	}

	public AntsHuntConfig(long seed, double width, double height) {
		super(seed, width, height);
		xMax = width;
		yMax = height;
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
		forest.clear(); // before start new sim. clear the field
		schedule.reset();
		double locationX = 0, locationY = 0;
		double directionX = 0, directionY = 0;
		int obstacleCounter = 0;

		// Seeding the obstacles
		ObstacleBody obs,obs2;
		/*for (int i = 0; i < numObstacles; i++) {
			locationX = Math.random() * (xMax - xMax/10) + xMax/20;
			locationY = Math.random() * (yMax - yMax/10) + yMax/20;
			obs = new ObstacleBody(locationX, locationY, obstacleRadius, i, 3);
			obstacleCounter++;
			setObjectLocation(obs, locationX, locationY);
		}// */
		
		//homeLoc = {0.15, 0.008};
		/*double homeObstacleX[]={ 0.09, 0.11, 0.13, 0.15, 0.17, 0.19};
		double homeObstacleY[]={0.035,0.020,0.035,0.035,0.020,0.035};
		for (int i = 0; i < homeObstacleX.length; i++) {
			locationX = homeObstacleX[i];
			locationY = homeObstacleY[i];
			obs = new ObstacleBody(locationX, locationY, obstacleRadius, i, 3);
			obstacleCounter++;
			setObjectLocation(obs, locationX, locationY);
		}// */
		
		
		// obstacle for room
		double xoMin = xMax/40, yoMin = yMax/40, xoMax = xMax*39/40, yoMax = yMax*39/40;
		for (double i = xoMin-this.wallObstackeRadius; 
			i <= xoMax + this.wallObstackeRadius; i=i + 0.001) 
		{
			locationX = i;
			locationY = yoMin - this.wallObstackeRadius;
			obs = new ObstacleBody(locationX, locationY, wallObstackeRadius,obstacleCounter, 3);
			obstacleCounter++;
			setObjectLocation(obs, locationX, locationY);
			locationY = yoMax + this.wallObstackeRadius;
			obs2 = new ObstacleBody(locationX, locationY, wallObstackeRadius, obstacleCounter, 3);
			obstacleCounter++;
			setObjectLocation(obs2, locationX, locationY);
		}

		for (double i = yoMin - this.wallObstackeRadius; 
			i <= yoMax + this.wallObstackeRadius; i = i + 0.001) 
		{
			locationX = xoMin - this.wallObstackeRadius;
			locationY = i;
			// ObstacleBody
			obs = new ObstacleBody(locationX, locationY, wallObstackeRadius, obstacleCounter, 3);
			obstacleCounter++;
			setObjectLocation(obs, locationX, locationY);
			locationX = xoMax + this.wallObstackeRadius;
			obs2 = new ObstacleBody(locationX, locationY, wallObstackeRadius, obstacleCounter, 3);
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
			AntHuntController agentC = new AntHuntController(agentBody);
			agentBody.setLogging(true);
			agentBody.setController((AgentController) agentC);
			this.setObjectLocation(agentBody, locationX, locationY);
		}

		if (verbose) {
			System.out.println("agents Initial Position");
		}

		// Seeding the flys
		for (int i = 0; i < numFlys; i++) {
			locationX = Math.random() * (xMax - xMax/10) + xMax/20;
			locationY = Math.random() * (yMax - yMax/10) + yMax/20;
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
		doLoop(AntsHuntConfig.class, args);
		System.exit(0);
	}

}// end of the class BioSim
