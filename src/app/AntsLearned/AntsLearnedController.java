package app.AntsLearned;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import java.awt.Color;

import sim.util.Double2D;
import sim.util.MutableDouble2D;

import core.basic_api.AbstractRobot;
import core.basic_api.AgentController;
import core.basic_api.body.AntBody;

import core.util.FastKNN;


public class AntsLearnedController extends AgentController {
	public static final double cageBot = core.util.AddSensors.cageBot;
	public static final double cageTop = core.util.AddSensors.cageTop;
	public static final double cageLeft = core.util.AddSensors.cageLeft;
	public static final double cageRight = core.util.AddSensors.cageLeft;

	public FastKNN knn;
	public double timest = 0.0;
	public int aID;
	public static final double EPS=0.0001;
	
	private double velX, velY, velT;


	public AntsLearnedController() {
	}

	public AntsLearnedController(AbstractRobot ab,FastKNN knn, int agentID) {
		this.setBody(ab);
		configure(ab);
		this.knn = knn;
		this.aID = agentID;
		velX=velY=velT=0.0;
	}
	
	public void takeStep() {
		MutableDouble2D tmpRot;
		//neither of these are ego centric. This needs to be fixed
		Double2D prey = body.nearestPetry();
		Double2D absAnt = body.nearestAnt();
		//double[] percept = {body.getLoc().x+absAnt.x,body.getLoc().y+absAnt.y};
		
		Double2D ant;
		if(absAnt.x == 0.0 && absAnt.y == 0.0) ant = new Double2D(0.0,0.0);
		else ant = new Double2D(new MutableDouble2D(absAnt.x-body.getLoc().x,absAnt.y-body.getLoc().y).rotate(-(new MutableDouble2D(body.getDir()).angle())));
		//double[] featVec = new double[4]; //antX,antY,wallX,wallY
		//double[] featVec = new double[7]; //antX,antY,homeX,homeY,oldDX,oldDY,oldDT
		double[] featVec = new double[9]; //antX,antY,homeX,homeY,obsX,obsY,oldDX,oldDY,oldDT
		//double[] featVec = new double[2]; //antX,antY
		//double[] featVec = new double[6]; //antX,antY,homeX,homeY,obsX,obsY
		double[] cvec = new double[3]; //velX,velY,velT
		featVec[0] = ant.x; featVec[1] = ant.y; 
		tmpRot = new MutableDouble2D(featVec[0],featVec[1]);
		tmpRot = tmpRot.rotate((new MutableDouble2D(body.getDir())).angle());
		double[] percept = {tmpRot.x+body.getLoc().x,tmpRot.y+body.getLoc().y};
		((AntBody)body).setDefLineColor(Color.black);
		((AntBody)body).setDefPoint(percept);
		((AntBody)body).setDefLine(true);
		
		//use home vector
		featVec[2] = core.util.AddSensors.homeLocX - body.getLoc().x;
		featVec[3] = core.util.AddSensors.homeLocY - body.getLoc().y;
		//normalize the home vector
		featVec[2] = featVec[2]/(Math.sqrt(Math.pow(featVec[2],2)+Math.pow(featVec[3],2)));
		featVec[3] = featVec[3]/(Math.sqrt(Math.pow(featVec[2],2)+Math.pow(featVec[3],2)));
		//make ego centric
		tmpRot = new MutableDouble2D(featVec[2],featVec[3]).rotate(-(new MutableDouble2D(body.getDir()).angle()));
		featVec[2] = tmpRot.x;
		featVec[3] = tmpRot.y;

		//now find the closest wall
		MutableDouble2D shortest,tmp;
		shortest = new MutableDouble2D(cageLeft-body.getLoc().x,0);
		tmp = new MutableDouble2D(cageRight-body.getLoc().x,0);
		if(shortest.lengthSq() > tmp.lengthSq()) shortest = tmp;
		tmp = new MutableDouble2D(0,cageTop-body.getLoc().y);
		if(shortest.lengthSq() > tmp.lengthSq()) shortest = tmp;
		tmp = new MutableDouble2D(0,cageBot-body.getLoc().y);
		if(shortest.lengthSq() > tmp.lengthSq()) shortest = tmp;
		featVec[4] = shortest.x;
		featVec[5] = shortest.y;
		//rotate to make it ego centric
		tmpRot = new MutableDouble2D(featVec[4],featVec[5]).rotate(-(new MutableDouble2D(body.getDir()).angle()));
		featVec[4] = tmpRot.x;
		featVec[5] = tmpRot.y;
		if(tmpRot.length() > ((AntBody)body).getDistanceOfPerception()){ 
			featVec[4] = featVec[5] = 0.0;
		}

		//use prev speed
		featVec[6] = velX; featVec[7] = velY; featVec[8] = velT;
		//featVec[4] = 0.; featVec[5] = 0.; featVec[6] = 0.;
		
		//query the KNN for the nearest example to this sensor set
		//featVec[2] = tmpRot.x; featVec[3] = tmpRot.y;
		//KNN.KNNSample[] neighbors = new KNN.KNNSample[1];
		double[][] neighbors = new double[10][3];
		//knn.query(new MVCSample(featVec,cvec),neighbors);
		knn.query(featVec, neighbors);
		/*
		if(neighbors[0][0] != 0.0 || neighbors[0][1] != 0.0 || neighbors[0][2] != 0.0){
			System.out.print("[ ");
			for(int i=0;i<neighbors[0].length;i++) 
				System.out.print(neighbors[0][i]+" ");
			System.out.println("]");
		}
		*/
		/*
		for(int j=0;j<neighbors.length;j++) {
			for(int i=0;i<neighbors[0].length;i++) 
				System.out.print(neighbors[0][i]+" ");
			System.out.println();
		}
		*/
		//double[] medClass = ((MVCSample)neighbors[(int)(Math.random()*neighbors.length)]).classvec;
		double[] medClass = new double[3];
		for(int i=0;i<medClass.length;i++) medClass[i] = 0.0;
		for(int i=0;i<neighbors.length;i++){
			for(int j=0;j<neighbors[i].length;j++){
				medClass[j] += neighbors[i][j];
			}
		}
		for(int i=0;i<medClass.length;i++) medClass[i] = medClass[i]/((double)neighbors.length);
		//double[] medClass = neighbors[(int)(Math.random()*neighbors.length)];
		//System.out.println(medClass[0]+" "+medClass[1]);
		//the class is velocity
		velX = medClass[0];
		velY = medClass[1];
		velT = medClass[2];
		//the class is accel
		/*
		velX += medClass[0];
		velY += medClass[1];
		velT += medClass[2];\
		*/
		((AntBody)body).setDesiredVelocities(velX,velY, velT);
		
	}

	public void configure(AbstractRobot ab) {

	}
}
