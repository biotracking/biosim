package app.Predator_Prey;

import java.awt.Color;
import java.util.ArrayList;

import sim.util.Double2D;
import sim.util.MutableDouble2D;

import core.basic_api.AbstractRobot;
import core.basic_api.AgentController;

/*
 * Predator controller without using clay
 * This example program is showing how to get perception from body
 * and control the body.
 * 
 * date: 2012/3/8
 * Tucker Balch group
 * program by Hai
 * 
 */

public class PredatorController extends core.basic_api.AgentController {

	public double TimeStamp = 0;
	//private AbstractRobot body;

	public PredatorController() {
	}

	public PredatorController(AbstractRobot ab) {
		this.setBody(ab);
	}
	
	/*
	 * calculate the total vector of the input vectors in the ArrayList
	 */
	private MutableDouble2D getTotalVector(ArrayList<Double2D> vectors){
		double x = 0;
		double y = 0;
		for(int i = 0; i < vectors.size(); i++){
			x += vectors.get(i).getX();
			y += vectors.get(i).getY();
		}
		MutableDouble2D totalVector = new MutableDouble2D(x, y);
		return totalVector;
	}
	
	/*
	 * find the vector with the shortest length, from the input vectors in the ArrayList
	 */
	private MutableDouble2D getShortestVector(ArrayList<Double2D> vectors){
		double x = 0;
		double y = 0;
		double length = 100;
		for(int i = 0; i < vectors.size(); i++){
			if(vectors.get(i).distance(0,0) < length){
				x = vectors.get(i).getX();
				y = vectors.get(i).getY();
			}
		}
		MutableDouble2D shortestVector = new MutableDouble2D(x, y);
		return shortestVector;
	}
	
	/*
	 * get sum direction of the repels from the obstacles whose relative position is in vectors.
	 * the near obstacles will generate bigger repel than the far obstacles.
	 * the repel have a reciprocal relationship with the distance.
	 * add all the repel, and return the normalized value 
	 */
	private MutableDouble2D getRepel(ArrayList<Double2D> vectors){
		double x = 0;
		double y = 0;
		double distance = 0;
		for(int i = 0; i < vectors.size(); i++){
			distance = vectors.get(i).distance(0,0);
			if(Math.abs(distance) < 0.00001) 
				continue;
			x += -vectors.get(i).x/(distance*distance);
			y += -vectors.get(i).y/(distance*distance);
		}
		MutableDouble2D repel = new MutableDouble2D(x, y);
		if(repel.length() > 0.00001)
			repel.normalize();
		return repel;
	}

	public void takeStep() {
		TimeStamp = body.getTimeStamp();		
		ArrayList<Double2D> preyArray = new ArrayList();
		ArrayList<Double2D> predatorArray = new ArrayList();
		ArrayList<Double2D> obstacleArray = new ArrayList();
		predatorArray = (ArrayList<Double2D>)body.getAgents(1);
		preyArray = (ArrayList<Double2D>)body.getAgents(2);
		obstacleArray = (ArrayList<Double2D>)body.getAgents(3);
		MutableDouble2D preyVector = this.getShortestVector(preyArray);
		MutableDouble2D predatorVector = this.getTotalVector(predatorArray);
		MutableDouble2D obstacleVector = this.getRepel(obstacleArray);
		int status = preyVector.length()<0.000001?1:2; //1 for searching, 2 for catching
		double coeff1 = (status==1?0:30);    //high value for the prey when catching
		double coeff2 = 0;//(status==1?1.5:0);	//no noise(random factor) in catching
		double coeff3 = (status==1?1:0.3);//low value for obs when catching
		double coeff4 = (status==1?1:0.3); //low repelling from other predators 
											//for catching, high for searching
		MutableDouble2D newLocCandid = new MutableDouble2D();
		MutableDouble2D totalVector=new MutableDouble2D();
		//calculate the total vector from the preyVector, obstacleVector and predatorVector
		totalVector.setX(coeff1*preyVector.getX()-coeff4*predatorVector.getX()+coeff3*obstacleVector.getX());
		totalVector.setY(coeff1*preyVector.getY()-coeff4*predatorVector.getY()+coeff3*obstacleVector.getY());
		if(((int)TimeStamp) % 20 == 0){
			double rndm_double1 = Math.random() -0.5;
			double rndm_double2 = Math.random() -0.5;
			totalVector.setX(totalVector.x + coeff2*rndm_double1);
			totalVector.setY(totalVector.y + coeff2*rndm_double2);
		}
		double angle1 = totalVector.angle();
		double angle2 = body.getDirAngle();
		double desiredTurnAngle = 0;
		int clockWise = 1;
		if(totalVector.length() > 0.00001){
			angle1 -= Math.PI/2;
			if(angle1>Math.PI) angle1 = 2*Math.PI - angle1;
			else if(angle1 < -Math.PI) angle1 = 2*Math.PI + angle1;
			angle1 *= -1;
			desiredTurnAngle = angle1;
		}
		body.setDesiredSpeed(5);//totalVector.length());
		body.setDesiredTurnRate(2*desiredTurnAngle);
	}

}
