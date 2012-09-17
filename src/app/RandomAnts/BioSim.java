package app.RandomAnts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import sim.engine.SimState;
import sim.field.continuous.Continuous2D;
import sim.util.Double2D;

/**
 * Creates a BioSim simulation with the given constant number predator and prey.
 */

public class BioSim extends SimState

{
	public Continuous2D forest = new Continuous2D(1.0, 1200.0, 680.0);// width=15cm,
	// height
	// =10cm
	// field.
	public int numFlys = 3; // fly(prey) number constant
	public int numAnts = 9; // ant(predator) number constant
	public int numObstacles = 0;
	public double xMin = 0;
	public double xMax = 1200.0;
	public double yMin = 0;
	public double yMax = 680.0;
	public double[][] predatorPositionArray; // position arrays
	public double[][] preyPositionArray;
	public double[][] antsInfo;
	double predatorRadius = 10.4;
	double preyRadius = 5.2;
	double obstacleRadius = 15.5;
	public double preysMaxSensorRange = 80.0;
	public double predatorsMaxSensorRange = 250.0;
	public double obstaclesMaxSensorRange = 0.0;
	public double preySensorFieldAngle = Math.PI;
	public double predatorSensorFieldAngle = 2 * Math.PI;
	public double obstacleSensorFieldAngle = 0.0;
	public double communicationRangeOfAnts = 600.0;
	public double communicationRangeOfPredator = 600.0;
	public double communicationRangeOfObs = 0.0;
	public File logFile;
	public BufferedWriter writer;
	boolean verbose = false;

	public BioSim(long seed) {
		this(seed, 1200, 680);
	}

	public BioSim(long seed, int width, int height) {
		super(seed);
		xMax = width;
		yMax = height;
		createGrids();
		predatorPositionArray = new double[numFlys][2];
		preyPositionArray = new double[numAnts][2];
		if (verbose) {
			System.out.println("Array ini...");
		}
		logFile = new File("log_file.txt");
		try {
			writer = new BufferedWriter(new FileWriter(logFile));
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}
	}

	public void createGrids() {
		{
			forest = new Continuous2D(1, (xMax - xMin), (yMax - yMin));
		}

	}

	public void printArray(double aRRAY[][]) // /print func.
	{
		for (int i = 0; i < aRRAY.length; i++) {
			for (int j = 0; j < aRRAY[i].length; j++) {
				System.out.print(aRRAY[i][j] + "\t");
			}
			System.out.print("\n");
		}
	}

	// public boolean isValidStartingPosition(MutableDouble2D newLoc,
	// double radiusOfCreatedAgent) {
	// Bag objs = forest.getObjectsExactlyWithinDistance(new Double2D(
	// newLoc.x, newLoc.y), 60.0);
	// double dist = 0;
	//
	// // check objects
	// for (int x = 0; x < objs.numObjs; x++) {
	// if (objs.objs[x] != this) {
	// dist = ((Agents) objs.objs[x]).getLoc().distance(newLoc);
	// if ((((Agents) objs.objs[x]).height + radiusOfCreatedAgent) > dist) {
	// return false;
	// }
	//
	// }
	// }// end of for loop
	// return true;
	// }

	public void start() {
		super.start();

		// seed the agents separately

		forest.clear(); // before start new sim. clear the field
		double x_1 = 0.0;
		double y_1 = 0.0;
		double x_2 = 0.0;
		double y_2 = 0.0;
		double x_3 = 0;
		double y_3 = 0;

		// Seeding the obstacles
		for (int i = 0; i < numObstacles; i++) {
			x_2 = Math.random() * xMax / 2 + 0.2 * xMax;
			y_2 = Math.random() * yMax / 2;
			ObstacleBody obs = new ObstacleBody(x_2, y_2, 0.002, i, 3);
			forest.setObjectLocation(obs, new Double2D(x_2, y_2));
			schedule.scheduleRepeating(obs);
		}
		for (int i = (int) (xMax / 6) - 16; i <= 5 * xMax / 6 + 16; i += 8) {
			x_3 = i;
			y_3 = 24;
			ObstacleBody obs = new ObstacleBody(x_3, y_3, 0.002, i, 3);
			forest.setObjectLocation(obs, new Double2D(x_3, y_3));
			schedule.scheduleRepeating(obs);
			y_3 = yMax - 24;
			ObstacleBody obs2 = new ObstacleBody(x_3, y_3, 0.002, i, 3);
			forest.setObjectLocation(obs2, new Double2D(x_3, y_3));
			schedule.scheduleRepeating(obs2);
		}
		for (int i = 24; i <= yMax - 24; i += 8) {
			x_3 = xMax / 6 - 16;
			y_3 = i;
			ObstacleBody obs = new ObstacleBody(x_3, y_3, 0.002, i, 3);
			forest.setObjectLocation(obs, new Double2D(x_3, y_3));
			schedule.scheduleRepeating(obs);
			x_3 = 5 * xMax / 6 + 16;
			ObstacleBody obs2 = new ObstacleBody(x_3, y_3, 0.002, i, 3);
			forest.setObjectLocation(obs2, new Double2D(x_3, y_3));
			schedule.scheduleRepeating(obs2);
		}
		if (verbose) {
			System.out.println("Obstacles Initial Position");
			printArray(preyPositionArray);
		}

		// Seeding the ants
		double ranTemp1 = 0, ranTemp2 = 0;
		for (int i = 0; i < numAnts; i++) {
			x_2 = 280 + Math.random() * 640;// 500 + 50*i;//
			y_2 = 120 + Math.random() * 440;// 300 - 50*i;//
			ranTemp1 = Math.random() * 2 - 1;
			ranTemp2 = Math.sqrt(1 - ranTemp1 * ranTemp1);
			ranTemp2 *= Math.random() > 0.5 ? 1 : -1;
			RandomAntBody ant = new RandomAntBody(x_2, y_2, ranTemp1, ranTemp2,
					i, 1);
			RandomAntController antC = new RandomAntController(ant);
			ant.setController((RandomAntController) antC);
			forest.setObjectLocation(ant, new Double2D(x_2, y_2));
			schedule.scheduleRepeating(ant);
		}
		if (verbose) {
			System.out.println("Ants Initial Position");
			printArray(preyPositionArray);
		}

	}

	public static void main(String[] args) {
		// antsInfo = new double[][]
		doLoop(BioSim.class, args);
		System.exit(0);
	}

}// end of the class BioSim
