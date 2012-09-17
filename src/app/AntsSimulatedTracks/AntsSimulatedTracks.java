package app.AntsSimulatedTracks;

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

public class AntsSimulatedTracks extends Sim

{
	public int numObstacles = 1;
	public int numAnts = 10;
	//public static final double TEMNO_SIZE = 0.0293;
	public static final double TEMNO_SIZE = 0.00293;
	public boolean didFinish = false;
	public TrackFile tf;


	public AntsSimulatedTracks() {
		this(System.currentTimeMillis());
	}

	public AntsSimulatedTracks(long seed) {
		//this(seed, 1.0, 1.0);
		this(seed, 0.100, 0.100);
	}

	public AntsSimulatedTracks(long seed, double width, double height) {
		super(seed, width, height);
		xMax = width;
		yMax = height;
	}

	public void start() {
		BoundChecker.xMin = 0.0;
		BoundChecker.xMax = xMax; //0.05;
		BoundChecker.yMin = 0.0;
		BoundChecker.yMax = yMax; //0.1;
		try{
			tf = new TrackFile();
		} catch(Exception e){
			throw new RuntimeException(e);
		}
		tf.addFieldDescription(1,"Double","timestamp","Time at which the sample was taken","seconds");
		tf.addFieldDescription(2,"Int","agentType","Type of the agent","none");
		tf.addFieldDescription(3,"Int","agent_ID","ID of the agent","none");
		tf.addFieldDescription(4,"Double","x","x position of the agent","meters");
		tf.addFieldDescription(5,"Double","y","y position of the agent","meters");
		tf.addFieldDescription(6,"Double","theta","global orientation in the frame","radians");
		tf.addFieldDescription(7,"Double","nearAntX","nearest ant's x","meters");
		tf.addFieldDescription(8,"Double","nearAntY","nearest ant's y","meters");
		forest.clear(); // before start new sim. clear the field
		schedule.reset();
		for(int i=0;i<numAnts;i++){
			double y = Math.random()*(yMax-TEMNO_SIZE)+TEMNO_SIZE/2.0;
			double x = Math.random()*(xMax-TEMNO_SIZE)+TEMNO_SIZE/2.0;
			double ty = Math.random()-0.5;
			double tx = Math.random()-0.5;
			//posx, posy, dirx, diry, object-id, object-type, sense-radius
			AntBody bod = new AntBody(x,y,tx,ty,i,1,0.007);
			bod.toroidal = true;
			bod.setAgentSize(TEMNO_SIZE);
			AntsSimulatedTracksController cntrl = new AntsSimulatedTracksController(bod,tf,i);
			bod.setController(cntrl);
			setObjectLocation(bod,x,y);
		}

	}
	public void finish(){
		super.finish();
		if(!didFinish){
			try{
				tf.write("simulated_ants.atf");
			} catch(Exception e){
				throw new RuntimeException(e);
			}
			didFinish = true;
		}
	}

	public static void main(String[] args) {
		
		doLoop(AntsSimulatedTracks.class, args);
		//System.exit(0);
	}

}
