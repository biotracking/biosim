package core.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import sim.util.MutableDouble2D;

public class AddSensors{
	//locations of the boundaries of the cage, in meters
	//note, top and bot don't necissarily mean top and bot
	//they're just based on the coordinate system, which in
	//videos is flipped (y=0 is at the top, and y > 0 is down).
	public static final double cageBot = 0.0;
	//public static final double cageTop = 0.028;
	public static final double cageTop = 0.204;
	public static final double cageLeft = 0.0;
	//public static final double cageRight = 0.042;
	public static final double cageRight = 0.360;
	public static final double homeLocX = 0.18;
	public static final double homeLocY = 0.0;
	public static void main(String[] args){
		try{
			if(args.length != 3){
				System.out.println("Usage: java core.util.AddSensors <inputATF> <outputATF> sensorRange");
				return;
			}
			double sensorRange = Double.parseDouble(args[2]);
			TrackFile input = new TrackFile(args[0]);
			TrackFile output = new TrackFile();
			//set up the headers
			output.addFieldDescription(1,"Double","timestamp","Time at which the sample was taken","seconds");
			output.addFieldDescription(2,"Int","agentType","Type of the agent","none");
			output.addFieldDescription(3,"Int","agent_ID","ID number of the agent","none");
			output.addFieldDescription(4,"Double","x","x position","meters");
			output.addFieldDescription(5,"Double","y","y position","meters");
			output.addFieldDescription(6,"Double","theta","global orientation in the frame","radians");
			output.addFieldDescription(7,"Double","nearAntX","Nearest ant sensor, x component","meters");
			output.addFieldDescription(8,"Double","nearAntY","Nearest ant sensor, y component","meters");
			output.addFieldDescription(9,"Double","homeX","Direction to the nest, un-normalized x component","meters");
			output.addFieldDescription(10,"Double","homeY","Direction to the nest, un-normalized y component","meters");
			output.addFieldDescription(11,"Double","nearObsX","Nearest obstacle sensor, x component", "meters");
			output.addFieldDescription(12,"Double","nearObsY","Nearest obstacle sensor, y component", "meters");
			output.addFieldDescription(13,"Double","oldAntVelX","Previous velocity of the ant, x component", "meters/second");	
			output.addFieldDescription(14,"Double","oldAntVelY","Previous velocity of the ant, Y component", "meters/second");	
			output.addFieldDescription(15,"Double","oldAntVelT","Previous angular velocity of the ant", "radians/second");	
			output.addFieldDescription(16,"Boolean","justSeenAnt","Whether the ant has seen another ant in the past 5 seconds", "none");
			output.addFieldDescription(17,"Double","antVelX","Velocity of the ant, x component", "meters/second");	
			output.addFieldDescription(18,"Double","antVelY","Velocity of the ant, Y component", "meters/second");	
			output.addFieldDescription(19,"Double","antVelT","Angular velocity of the ant", "radians/second");	
			//parse the tracks
			ArrayList<HashMap<String,String>> tracks = input.getTracksHashMap(), sameTime, sameAgent;
			
			HashMap<String,ArrayList<HashMap<String,String>>> tracksByAgent = new HashMap<String,ArrayList<HashMap<String,String>>>();
			HashMap<String,ArrayList<HashMap<String,String>>> tracksByTimestamp = new HashMap<String,ArrayList<HashMap<String,String>>>();
			HashMap<String,Double> timeLastAntSeen = new HashMap<String,Double>();
			System.out.println("Getting tracks by agent ID and timestamp");
			int tenPercent = tracks.size()/10;
			long prevTime = System.currentTimeMillis(), tmpTime;
			double timePerUnit;
			for(int i=0;i<tracks.size();i++){
				String aID = tracks.get(i).get("agent_ID");
				String ts = tracks.get(i).get("timestamp");
				if(tracksByAgent.get(aID) == null) tracksByAgent.put(aID,input.getTracksHashMap("agent_ID",aID));
				if(tracksByTimestamp.get(ts) == null) tracksByTimestamp.put(ts,input.getTracksHashMap("timestamp",ts));
				if((i+1)%tenPercent==0){ 
					tmpTime = System.currentTimeMillis();
					timePerUnit = ((tmpTime-prevTime)/(tenPercent))/1000.0;
					double secondsLeft = (tracks.size()-i)*timePerUnit;
					int minutesLeft = (int)(secondsLeft/60.0);
					secondsLeft = secondsLeft - (minutesLeft*60);
					System.out.println("\t"+minutesLeft+" minutes, "+secondsLeft+" seconds left");
					prevTime = tmpTime;
				}
			}
			System.out.println("done!");
			prevTime = System.currentTimeMillis();
			System.out.println("Processing tracks");
			for(int i=0;i<tracks.size();i++){
				//System.out.println("Track #"+i);
				if((i+1)%tenPercent==0){ 
					tmpTime = System.currentTimeMillis();
					timePerUnit = ((tmpTime-prevTime)/(tenPercent))/1000.0;
					double secondsLeft = (tracks.size()-i)*timePerUnit;
					int minutesLeft = (int)(secondsLeft/60.0);
					secondsLeft = secondsLeft - (minutesLeft*60);
					System.out.println("\t"+minutesLeft+" minutes, "+secondsLeft+" seconds left");
					prevTime = tmpTime;
				}
				double trackX = Double.parseDouble(tracks.get(i).get("x"));
				double trackY = Double.parseDouble(tracks.get(i).get("y"));
				String trackID = tracks.get(i).get("agent_ID");
				sameTime = tracksByTimestamp.get(tracks.get(i).get("timestamp")); //input.getTracksHashMap("timestamp",tracks.get(i).get("timestamp"));
				sameAgent = tracksByAgent.get(trackID);//input.getTracksHashMap("agent_ID",trackID);
				
				//compute nearest other ant;
				double nearX = 0;
				double nearY = 0;
				double nearD = -1;
				for(int j=0;j<sameTime.size();j++){
					if(trackID.equals(sameTime.get(j).get("agent_ID")))
						continue;
					double tmpX = Double.parseDouble(sameTime.get(j).get("x"));
					double tmpY = Double.parseDouble(sameTime.get(j).get("y"));
					double tmpD = Math.pow(trackX-tmpX,2)+Math.pow(trackY-tmpY,2);
					if((Math.sqrt(tmpD)<sensorRange) && (nearD == -1 || tmpD < nearD)){
						nearX = tmpX;
						nearY = tmpY;
						nearD = tmpD;
						timeLastAntSeen.put(trackID,Double.parseDouble(tracks.get(i).get("timestamp")));
					}
				}
				if(nearD != -1){
					//make it ego centric
					nearX = nearX-trackX;
					nearY = nearY-trackY;
					MutableDouble2D tmpRot = new MutableDouble2D(nearX,nearY).rotate(-Double.parseDouble(tracks.get(i).get("theta")));
					nearX = tmpRot.x;
					nearY = tmpRot.y;
				}

				//add in obstacles
				double obsX, obsY;
				boolean left = (trackX < (cageRight/2));
				boolean top = (trackY < (cageTop/2));
				if( (left?trackX:(cageRight-trackX)) < (top?trackY:(cageTop-trackY)) ){
					obsX = (left?cageLeft:cageRight) - trackX;
					obsY = 0.0;
				} else {
					obsX = 0.0;
					obsY = (top?cageTop:cageBot) - trackY;
				}
				//rotate it to make it ego centric
				MutableDouble2D tmpRot = new MutableDouble2D(obsX,obsY).rotate(-Double.parseDouble(tracks.get(i).get("theta")));
				obsX = tmpRot.x;
				obsY = tmpRot.y;
				if(Math.sqrt(Math.pow(obsX,2)+Math.pow(obsY,2)) > sensorRange){
					obsX = obsY = 0.0;
				}

				//direction to nest
				double toNestX=homeLocX-trackX, toNestY=homeLocY-trackY, nrmlz = Math.sqrt(Math.pow(toNestX,2)+Math.pow(toNestY,2));
				//toNestX /= nrmlz;
				//toNestY /= nrmlz;
				//rotate to make it ego centric
				tmpRot = new MutableDouble2D(toNestX,toNestY).rotate(-Double.parseDouble(tracks.get(i).get("theta")));
				toNestX = tmpRot.x;
				toNestY = tmpRot.y;

				//Previous velocity
				double oldVelX=0.0,oldVelY=0.0,oldVelT=0.0;
				for(int j=0;j<sameAgent.size()-1;j++){
					if(sameAgent.get(j).get("timestamp").equals(tracks.get(i).get("timestamp"))){
						oldVelX = oldVelY = oldVelT = 0.0;
						break;
					} else if(sameAgent.get(j+1).get("timestamp").equals(tracks.get(i).get("timestamp"))){
						double tmp = Double.parseDouble(tracks.get(i).get("timestamp")) - Double.parseDouble(sameAgent.get(j).get("timestamp"));
						oldVelX = Double.parseDouble(tracks.get(i).get("x")) - Double.parseDouble(sameAgent.get(j).get("x"));
						oldVelY = Double.parseDouble(tracks.get(i).get("y")) - Double.parseDouble(sameAgent.get(j).get("y"));
						oldVelT = Double.parseDouble(tracks.get(i).get("theta")) - Double.parseDouble(sameAgent.get(j).get("theta"));
						//rotate to make ego-centric
						tmpRot = new MutableDouble2D(oldVelX,oldVelY).rotate(-Double.parseDouble(sameAgent.get(j).get("theta")));
						oldVelX = tmpRot.x;
						oldVelY = tmpRot.y;
						//scale by time elapsed
						oldVelX = oldVelX/tmp;
						oldVelY = oldVelY/tmp;
						oldVelT = oldVelT/tmp;
						break;
					}
				}
				
				//Have I seen an ant in the past 5 seconds?
				boolean seenAnt = (nearD != -1 || (timeLastAntSeen.get(trackID) != null && (Double.parseDouble(tracks.get(i).get("timestamp")) - timeLastAntSeen.get(trackID)) < 5));
				
				//Output velocity (control)
				double velX=0.0,velY=0.0,velT=0.0;
				for(int j=1;j<sameAgent.size();j++){
					if(sameAgent.get(j-1).get("timestamp").equals(tracks.get(i).get("timestamp"))){
						double tmp = Double.parseDouble(sameAgent.get(j).get("timestamp")) - Double.parseDouble(tracks.get(i).get("timestamp"));
						velX = Double.parseDouble(sameAgent.get(j).get("x"))- Double.parseDouble(tracks.get(i).get("x"));
						velY = Double.parseDouble(sameAgent.get(j).get("y"))- Double.parseDouble(tracks.get(i).get("y"));
						velT = Double.parseDouble(sameAgent.get(j).get("theta"))- Double.parseDouble(tracks.get(i).get("theta"));
						//rotate to make ego-centric
						tmpRot = new MutableDouble2D(velX,velY).rotate(-Double.parseDouble(tracks.get(i).get("theta")));
						velX = tmpRot.x;
						velY = tmpRot.y;
						//scale by time elapsed
						velX = velX/tmp;
						velY = velY/tmp;
						velT = velT/tmp;
					}
				}
				
				//build the row string
				String rowString = "";
				for(String key : tracks.get(i).keySet()){
					rowString += output.getFieldIdx(key)+":"+tracks.get(i).get(key)+" ";
				}
				rowString += "7:"+nearX+" 8:"+nearY;
				rowString += " 9:"+toNestX+" 10:"+toNestY;
				rowString += " 11:"+obsX+" 12:"+obsY;
				rowString += " 13:"+oldVelX+" 14:"+oldVelY+" 15:"+oldVelT;
				rowString += " 16:"+seenAnt;
				rowString += " 17:"+velX+" 18:"+velY+" 19:"+velT;
				output.addRow(rowString);
			}
		output.write(args[1]);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
