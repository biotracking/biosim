package app.AntsReplay;

import java.util.ArrayList;
import java.util.HashMap;

import core.basic_api.AgentController;
import core.basic_api.Sim;
import core.basic_api.body.ObstacleBody;
import core.basic_api.body.AntBody;
import core.basic_api.BoundChecker;
import core.util.Vec2;
import core.util.TrackFile;

/**
 * Creates a BioSim simulation with the given constant number predator and prey.
 */

public class AntsReplay extends Sim

{
	public int numObstacles = 1;
	//public static final double TEMNO_SIZE = 0.0293;
	public static final double TEMNO_SIZE = 0.0086;

	public AntsReplay() {
		this(System.currentTimeMillis());
	}

	public AntsReplay(long seed) {
		//this(seed, 1.0, 1.0);
		this(seed, 0.27, 0.27);
	}

	public AntsReplay(long seed, double width, double height) {
		super(seed, width, height);
		xMax = width;
		yMax = height;
	}

	public void start() {
		//why the hell do I have to do the following?
		BoundChecker.xMin = -1;
		//BoundChecker.xMin = 0;
		BoundChecker.xMax = 2;
		BoundChecker.yMin = -1;
		//BoundChecker.yMin = 0;
		BoundChecker.yMax = 2;
		//ArrayList<ArrayList<HashMap<String,String>>> antCntrlTracks = loadAtf("foo.atf");
		ArrayList<ArrayList<HashMap<String,String>>> antCntrlTracks = loadAtf("tmp.atf");
		forest.clear(); // before start new sim. clear the field
		schedule.reset();
		//ObstacleBody obs = new ObstacleBody(xMax/2,yMax/2,0.1,0,3);
		//setObjectLocation(obs,xMax/2,yMax/2);
		for(int i=0;i<antCntrlTracks.size();i++){
			double y = Double.parseDouble(antCntrlTracks.get(i).get(0).get("y")); //Math.random()*yMax;
			double x = Double.parseDouble(antCntrlTracks.get(i).get(0).get("x")); //Math.random()*xMax;
			double ty = Math.sin(Double.parseDouble(antCntrlTracks.get(i).get(0).get("theta"))); //Math.random()-0.5;
			double tx = Math.cos(Double.parseDouble(antCntrlTracks.get(i).get(0).get("theta"))); //Math.random()-0.5;
			//posx, posy, dirx, diry, object-id, object-type, sense-radius
			AntBody bod = new AntBody(x,y,tx,ty,i,1,0.007);
			bod.setAgentSize(TEMNO_SIZE);
			AntsReplayController cntrl = new AntsReplayController(bod,antCntrlTracks.get(i));
			bod.setController(cntrl);
			setObjectLocation(bod,x,y);
		}

	}

	public ArrayList<ArrayList<HashMap<String,String>>> loadAtf(String fname){
		try{
			TrackFile tfile = new TrackFile(fname);
			ArrayList<HashMap<String,String>> tracks = tfile.getTracksHashMap();
			ArrayList<ArrayList<HashMap<String,String>>> antCntrlTracks = new ArrayList<ArrayList<HashMap<String,String>>>();
			int maxAgentID = -1;
			//ACK! Timesteps! bad bad bad bad!
			double minTime = Double.MAX_VALUE, timeStep;
			for(int i=0;i<tracks.size();i++){
				if(Integer.parseInt(tracks.get(i).get("agent_ID")) > maxAgentID){
					maxAgentID = Integer.parseInt(tracks.get(i).get("agent_ID"));
				}
				if(Double.parseDouble(tracks.get(i).get("timestamp")) < minTime){
					minTime = Double.parseDouble(tracks.get(i).get("timestamp"));
				}
			}
			for(int i=0;i<=maxAgentID;i++)
				antCntrlTracks.add(new ArrayList<HashMap<String,String>>());
			for(int i=0;i<tracks.size();i++){
				antCntrlTracks.get(Integer.parseInt(tracks.get(i).get("agent_ID"))).add(tracks.get(i));
			}
			//prefill with do-nothings if the agent doesn't appear right away
			timeStep = Double.parseDouble(antCntrlTracks.get(0).get(1).get("timestamp")) - Double.parseDouble(antCntrlTracks.get(0).get(0).get("timestamp"));
			System.out.println("Min time:"+minTime);
			System.out.println("Timestep:"+timeStep);
			for(int agent=0;agent<=maxAgentID;agent++){
				System.out.println("Filling agent "+agent);
				int firstStart = 0;
				while(Double.parseDouble(antCntrlTracks.get(agent).get(0).get("timestamp")) > minTime){
					HashMap<String,String> tmp = new HashMap<String,String>(antCntrlTracks.get(agent).get(0));
					tmp.put("timestamp",""+(Double.parseDouble(tmp.get("timestamp"))-timeStep));
					antCntrlTracks.get(agent).add(0,tmp);
					firstStart++;
				}
				antCntrlTracks.get(agent).get(0).put("first",""+firstStart);
			}
			
			return antCntrlTracks;
		} catch(Exception e){
			System.out.println("CRAP!");
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		
		doLoop(AntsReplay.class, args);
		//System.exit(0);
	}

}
