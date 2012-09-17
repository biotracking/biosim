package core.basic_api;

import java.util.ArrayList;

import core.tools.Logger;

import sim.util.Double2D;
import sim.util.MutableDouble2D;

public abstract class AgentController implements Analysis {
	public AbstractRobot body;
	private static Logger logger = new Logger();

	// public double Pixel2Meter;
	// public double Meter2Pixel;

	public AgentController() {
	}

	public AgentController(AbstractRobot ab) {
		this.body = ab;
		// Pixel2Meter = 2.54/(100*196.6439); // unit pixel to m
		// Meter2Pixel = (100*196.6439)/2.54;
	}

	public void setBody(AbstractRobot ab) {
		this.body = ab;
	}

	public core.basic_api.ClayController clayControl;

	public MutableDouble2D getTotal(ArrayList<Double2D> vectors) {
		double x = 0;
		double y = 0;
		for (int i = 0; i < vectors.size(); i++) {
			x += vectors.get(i).getX();
			y += vectors.get(i).getY();
		}
		MutableDouble2D totalVector = new MutableDouble2D(x, y);
		return totalVector;
	}// */

	public MutableDouble2D getShortest(ArrayList<Double2D> vectors) {
		double x = 100;
		double y = 100;
		double tempX = 0, tempY = 0;
		for (int i = 0; i < vectors.size(); i++) {
			tempX = vectors.get(i).x;
			tempY = vectors.get(i).y;
			if (x * x + y * y > tempX * tempX + tempY * tempY) {
				x = tempX;
				y = tempY;
			}
		}
		if (x == 100 && y == 100) {
			return new MutableDouble2D(0, 0);
		}
		return new MutableDouble2D(x, y);
	}

	private ArrayList<Double2D> teammates;
	private ArrayList<Double2D> preys;
	private ArrayList<Double2D> obstacles;
	private MutableDouble2D totalDistance;
	public MutableDouble2D preysNear;

	public MutableDouble2D getPreyNear() {
		preys = body.getAgents(2); // type 2: preys
		preysNear = getTotal(preys);
		return preysNear;
	}

	public ArrayList<Double2D> GetObjectInfo() {
		teammates = body.getAgents(1); // type 1: team mates
		preys = body.getAgents(2); // type 2: preys
		obstacles = body.getAgents(3); // type 3: obstacles

		MutableDouble2D total1 = getTotal(teammates);
		MutableDouble2D total2 = getTotal(obstacles);
		totalDistance = new MutableDouble2D(total1.x + total2.x, total1.y
				+ total2.y);
		preysNear = getTotal(preys);
		// return preysNear;
		return preys;
	}

	private double lastRate = 0;

	/*
	 * allResult[0] = result.x; allResult[1] = result.y; allResult[2] =
	 * result.t; allResult[3] = result.r;
	 */
	public void getFinalTurnRate(double[] TurnRate, double timeStep) {
		double finalTurnRate;// = TurnRate[2];
		finalTurnRate = TurnRate[2];

		if (Math.abs(finalTurnRate - lastRate) > 6)
			finalTurnRate = lastRate;
		else
			lastRate = finalTurnRate;

		if (TurnRate[5] == 1)// body.atHome(homeLoc[0],homeLoc[1]))
		{
			body.releaseToHome(2);
			//log in .txt
			String line = "  <row> 10:" + timeStep*body.getTimeStampSize() +" </row>"; //
			logger.log(line);
		}
		// System.out.print("AgentController: "+TurnRate[3]+"\t"+finalTurnRate+"\n");
		body.setDesiredSpeed(TurnRate[3]);
		// body.getNewDirection(TurnRate[2],TurnRate[0],TurnRate[1]);
		body.setDesiredTurnRate(finalTurnRate);
	}

}// end of the abstract class AgentController

