package app.AntsLearned;

import java.util.ArrayList;
import java.util.HashMap;

import core.basic_api.AgentController;
import core.basic_api.Sim;
import core.basic_api.body.ObstacleBody;
import core.basic_api.body.AntBody;
import core.basic_api.BoundChecker;
import core.util.Vec2;
import core.util.TrackFile;
import core.util.FastKNN;

/**
 * Creates a BioSim simulation with the given constant number predator and prey.
 */

public class AntsLearned extends Sim

{
	public int numObstacles = 1;
	public int numAnts = 10;
	//public static final double TEMNO_SIZE = 0.00293;
	public static final double APHENO_SIZE = 0.0086;
	public static final double ARENA_SIZE_X = 0.360;
	public static final double ARENA_SIZE_Y = 0.204;
	public boolean didFinish = false;
	public TrackFile tf;


	public AntsLearned() {
		this(System.currentTimeMillis());
	}

	public AntsLearned(long seed) {
		//this(seed, 1.0, 1.0);
		this(seed, ARENA_SIZE_X, ARENA_SIZE_Y);
	}

	public AntsLearned(long seed, double width, double height) {
		super(seed, width, height);
		xMax = width;
		yMax = height;
	}

	public void start() {
		//why the hell do I have to do the following?
		BoundChecker.xMin = 0.0;
		//BoundChecker.xMin = 0;
		BoundChecker.xMax = xMax; //0.05;
		BoundChecker.yMin = 0.0;
		//BoundChecker.yMin = 0;
		BoundChecker.yMax = yMax; //0.1;
		//ArrayList<ArrayList<HashMap<String,String>>> antCntrlTracks = loadAtf("foo.atf");
		FastKNN knn = loadAtf("flipped-theta-sensors.atf");
		forest.clear(); // before start new sim. clear the field
		schedule.reset();
		//ObstacleBody obs = new ObstacleBody(xMax/2,yMax/2,0.1,0,3);
		//setObjectLocation(obs,xMax/2,yMax/2);
		for(int i=0;i<numAnts;i++){
			double y = (Math.random()*(yMax-APHENO_SIZE)+APHENO_SIZE/2.0)/2.0;
			double x = (Math.random()*(xMax-APHENO_SIZE)+APHENO_SIZE/2.0)/2.0;
			double ty = Math.random()-0.5;
			double tx = Math.random()-0.5;
			//posx, posy, dirx, diry, object-id, object-type, sense-radius
			AntBody bod = new AntBody(x,y,tx,ty,i,1,APHENO_SIZE);
			bod.toroidal = true;
			bod.setAgentSize(APHENO_SIZE);
			AntsLearnedController cntrl = new AntsLearnedController(bod,knn,i);
			bod.setController(cntrl);
			setObjectLocation(bod,x,y);
		}

	}
	public FastKNN loadAtf(String fname){
		FastKNN rv = new FastKNN(9,3);
		try{
			TrackFile tfile = new TrackFile(fname);
			ArrayList<HashMap<String,String>> tracks, tracks2 = tfile.getTracksHashMap();
			//double[] fvec = new double[4]; //antX,antY,wallX,wallY
			//double[] fvec = new double[2]; //wallX, wallY
			//double[] fvec = new double[7]; //antX,antY,homeX,homeY,oldDX,oldDY,oldDT
			double[] fvec = new double[9]; //antX,antY,homeX,homeY,obsX,obsY,oldDX,oldDY,oldDT
			//double[] fvec = new double[6]; //antX,antY,homeX,homeY,obsX,obsY
			double[] cvec = new double[3]; //antVelX, antVelY, antVelT
			//double[] cvec_old = new double[3];
			//double[] cvec_deriv = new double[3];
			tracks = tracks2;
			double tmp;
			//int notMoving=0, moving=0;
			for(int i=0;i<tracks.size();i++){
				fvec[0] = Double.parseDouble(tracks.get(i).get("nearAntX"));
				fvec[1] = Double.parseDouble(tracks.get(i).get("nearAntY"));
				fvec[2] = Double.parseDouble(tracks.get(i).get("homeX"));
				fvec[3] = Double.parseDouble(tracks.get(i).get("homeY"));
				//normalize the home vector
				tmp = Math.sqrt(Math.pow(fvec[2],2)+Math.pow(fvec[3],2));
				fvec[2] = fvec[2]/tmp;
				fvec[3] = fvec[3]/tmp;
				fvec[4] = Double.parseDouble(tracks.get(i).get("nearObsX"));
				fvec[5] = Double.parseDouble(tracks.get(i).get("nearObsY"));

				fvec[6] = Double.parseDouble(tracks.get(i).get("oldAntVelX"));
				fvec[7] = Double.parseDouble(tracks.get(i).get("oldAntVelY"));
				fvec[8] = Double.parseDouble(tracks.get(i).get("oldAntVelT"));
				//and the observed control
				cvec[0] = Double.parseDouble(tracks.get(i).get("antVelX"));
				cvec[1] = Double.parseDouble(tracks.get(i).get("antVelY"));
				cvec[2] = Double.parseDouble(tracks.get(i).get("antVelT"));
				
				/*
				if(cvec[0] != 0.0 || cvec[1] != 0.0 || cvec[2] != 0.0){
					moving++;
				} else {
					notMoving++;
				}
				*/
				rv.add(fvec,cvec);
			}
			//System.out.println("Not Moving / moving: "+notMoving+"/"+moving);
			//for computing prev speed etc. if it's not in the
			//trackfile
			/*
			for(int agentIDs=0; agentIDs<numAnts; agentIDs++){
				tracks = tfile.getTracksHashMap("agent_ID",""+agentIDs);
				//cvec_old[0] = cvec_old[1] = cvec_old[2] = 0.0;
				for(int i=0;i<tracks.size()-1;i++){
					fvec[0] = Double.parseDouble(tracks.get(i).get("nearAntX"));
					fvec[1] = Double.parseDouble(tracks.get(i).get("nearAntY"));
					fvec[2] = Double.parseDouble(tracks.get(i).get("homeX"));
					fvec[3] = Double.parseDouble(tracks.get(i).get("homeY"));
					//normalize the home vector?
					fvec[2] = fvec[2] / (Math.sqrt(Math.pow(fvec[2],2)+Math.pow(fvec[3],2)));
					fvec[3] = fvec[3] / (Math.sqrt(Math.pow(fvec[2],2)+Math.pow(fvec[3],2)));
					//use prev speed
					fvec[4] = cvec_old[0];
					fvec[5] = cvec_old[1];
					fvec[6] = cvec_old[2];
					
					double timestep = Double.parseDouble(tracks.get(i+1).get("timestamp")) - Double.parseDouble(tracks.get(i).get("timestamp"));
					
					cvec[0] = Double.parseDouble(tracks.get(i+1).get("x"))
								- Double.parseDouble(tracks.get(i).get("x"));
					cvec[1] = Double.parseDouble(tracks.get(i+1).get("y"))
								- Double.parseDouble(tracks.get(i).get("y"));
					cvec[2] = Double.parseDouble(tracks.get(i+1).get("theta"))
								- Double.parseDouble(tracks.get(i).get("theta"));
					for(int eye=0;eye<cvec.length;eye++){
						cvec[eye]=cvec[eye]/timestep;
						cvec_deriv[eye] = (cvec[eye] - cvec_old[eye])/timestep;
						cvec_old[eye] = cvec[eye];
					}
					cvec[0] = Double.parseDouble(tracks.get(i).get("antXVel"));
					cvec[1] = Double.parseDouble(tracks.get(i).get("antYVel"));
					cvec[2] = Double.parseDouble(tracks.get(i).get("antTVel"));
					rv.add(new MVCSample(fvec,cvec));
				}
			}
			*/
			//rv.scaleNormalize();
		} catch(Exception e){
			System.out.println("Failed to parse track file:");
			e.printStackTrace();
		}
		return rv;
	}

	public static void main(String[] args) {
		
		doLoop(AntsLearned.class, args);
		//System.exit(0);
	}

}
