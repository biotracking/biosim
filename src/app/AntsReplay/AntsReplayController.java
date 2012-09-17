package app.AntsReplay;

import java.util.ArrayList;
import java.util.HashMap;

import sim.util.Double2D;
import sim.util.MutableDouble2D;

import core.basic_api.AbstractRobot;
import core.basic_api.AgentController;
import core.basic_api.body.AntBody;


public class AntsReplayController extends AgentController {

	public ArrayList<HashMap<String,String>> myTracks;
	public int curTrack;
	public static final double EPS=0.0001;
	public int firstTrack;
	public AntsReplayController() {
	}

	public AntsReplayController(AbstractRobot ab,ArrayList<HashMap<String,String>> tracks) {
		this.setBody(ab);
		configure(ab);
		myTracks = tracks;
		curTrack = 0;
		firstTrack = Integer.parseInt(myTracks.get(0).get("first"));
	}

	public void takeStep() {
		if(curTrack >= myTracks.size()-1){ 
			((AntBody)body).setDesiredVelocities(0,0,0);
			//((AntBody)body).setAgentSize(0.0);
			((AntBody)body).setVisible(false);
			return;
		}
		if(curTrack < firstTrack) ((AntBody)body).setVisible(false);
		else ((AntBody)body).setVisible(true);
		double curX,curY,newX,newY,curT,newT,curTime,newTime;

		curX = Double.parseDouble(myTracks.get(curTrack).get("x"));
		curY = Double.parseDouble(myTracks.get(curTrack).get("y"));
		curT = Double.parseDouble(myTracks.get(curTrack).get("theta"));
		curTime = Double.parseDouble(myTracks.get(curTrack).get("timestamp"));
		
		//System.out.println("["+curX+", "+curY+","+curT+"] ("+body.getLoc().x+", "+body.getLoc().y+","+(new MutableDouble2D(body.getDir())).angle()+")");

		newX = Double.parseDouble(myTracks.get(curTrack+1).get("x"));
		newY = Double.parseDouble(myTracks.get(curTrack+1).get("y"));
		newT = Double.parseDouble(myTracks.get(curTrack+1).get("theta"));
		newTime = Double.parseDouble(myTracks.get(curTrack+1).get("timestamp"));
		MutableDouble2D fromMeToGoal = new MutableDouble2D((newX-curX),(newY-curY));
		//MutableDouble2D fromMeToGoal = new MutableDouble2D((newX-curX)/(newTime-curTime),(newY-curY)/(newTime-curTime));
		//fromMeToGoal.rotate(-(new MutableDouble2D(body.getDir()).angle()));
		fromMeToGoal.rotate(-curT);
		fromMeToGoal.multiplyIn(1/(newTime-curTime));
		

		double vt = newT - curT;
		/*
		if(Math.abs(vt) > Math.PI){
			if(vt > 0) vt = -(vt-Math.PI);
			else vt = -(vt+Math.PI);
		}
		*/
		vt = vt/(newTime-curTime);
		//double vt = fromMeToGoal.angle()/(newTime-curTime);
		//vt = vt/30.0; //AHHHHHHHH (newTime-curTime);
		//vt = 0;
		//System.out.println(fromMeToGoal);
		((AntBody)body).setDesiredVelocities(fromMeToGoal.x,fromMeToGoal.y, vt);
		curTrack++;
	}

	public void configure(AbstractRobot ab) {

	}
}
