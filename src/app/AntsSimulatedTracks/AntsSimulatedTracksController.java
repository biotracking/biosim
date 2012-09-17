package app.AntsSimulatedTracks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import sim.util.Double2D;
import sim.util.MutableDouble2D;

import core.basic_api.AbstractRobot;
import core.basic_api.AgentController;
import core.basic_api.body.AntBody;

import core.util.TrackFile;


public class AntsSimulatedTracksController extends AgentController {
	
	public TrackFile tf;
	public double timest = 0.0;
	public int aID;
	public static final double EPS=0.0001;


	public AntsSimulatedTracksController() {
	}

	public AntsSimulatedTracksController(AbstractRobot ab, TrackFile tf, int agentID) {
		this.setBody(ab);
		configure(ab);
		this.tf = tf;
		this.aID = agentID;
	}
	
	public void takeStep() {
		Double2D prey = body.nearestPetry();
		Double2D ant = body.nearestAnt();
		//double[] featVec = new double[4]; //antX,antY,wallX,wallY
		double[] featVec = new double[2]; //antX,antY
		double[] cvec = new double[3];
		featVec[0] = ant.x; featVec[1] = ant.y; 
		/*
		//now find the closest wall
		boolean left = (body.getLoc().x < (cageRight/2));
		boolean top = (body.getLoc().y<(cageTop/2));
		if( (left?body.getLoc().x:(cageRight-body.getLoc().x)) < (top?body.getLoc().y:(cageTop - body.getLoc().y))){
			featVec[2] = (left?cageLeft:cageRight) - body.getLoc().x;
			featVec[3] = 0.0;
		} else {
			featVec[2] = 0.0;
			featVec[3] = (top?cageTop:cageBot) - body.getLoc().y;
		}
		//rotate to make it ego centric
		MutableDouble2D tmpRot = new MutableDouble2D(featVec[2],featVec[3]).rotate(-(new MutableDouble2D(body.getDir()).angle()));
		*/
		
		//go forward, 
		double[] u = new double[3];
		u[0] = 0.003; u[1] = 0.0; u[2] = 0.0;
		//avoid nearest ant
		if(ant.x > 0){
			if(ant.y < 0) u[2] = 2.0/(2*Math.PI);
			else u[2] = -2.0/(2*Math.PI);
		}
		((AntBody)body).setDesiredVelocities(u[0],u[1],u[2]);
		//add the data to the track file
		String newdata = "1:"+timest+" 2:1 3:"+aID+" 4:"+body.getLoc().x+" 5:"+body.getLoc().y+" 6:"+(new MutableDouble2D(body.getDir())).angle();
		newdata += " 7:"+featVec[0]+" 8:"+featVec[1]+" 9:"+u[0]+" 10:"+u[1]+" 11:"+u[2];
		tf.addRow(newdata);
		timest+=0.033;
	}

	public void configure(AbstractRobot ab) {

	}
}
