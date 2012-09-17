import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;

import core.util.FastKNN;
import core.util.TrackFile;

public class LearnedPerformance{
	public static final int USE_NEAR_ANT=0, USE_HOME_DIR=1, USE_NEAR_OBS=2, USE_OLD_CMD=3;
	public static final int[] FEAT_SIZES={2,2,2,3};
	public static final String[] FEAT_NAMES={"Nearest ant","Direction to nest", "Nearest obstacle", "Previous command"};
	public static String mask2str(boolean[] feats){
		String rv = "[";
		for(int i=0;i<feats.length;i++){
			if(feats[i]){
				rv += FEAT_NAMES[i]+", ";
			}
		}
		return rv+"]";
	}
	public static Random rand;
	public static double[] runKNN(boolean[] useFeats, TrackFile tfile, String filePrefix, double trainingFrac, int numNeighbors) throws IOException{
		int numFeats=0;
		double[] rv = null;
		System.out.println("Feature set: "+mask2str(useFeats));
		/*
		for(int i=0;i<useFeats.length;i++) {
			if(useFeats[i]){
				numFeats+=FEAT_SIZES[i];
				System.out.print(FEAT_NAMES[i]+", ");
			}
		}
		System.out.println("]");
		*/
		for(int i=0;i<useFeats.length;i++) {
			if(useFeats[i]){
				numFeats+=FEAT_SIZES[i];
			}
		}
		if(numFeats == 0){
			System.out.println("All features masked, exiting");
			return rv;
		}
		FastKNN knn = new FastKNN(numFeats,3);
		double[] fvec = new double[numFeats];
		double[] cvec = new double[3];
		double[] rvec = new double[3];
		ArrayList<HashMap<String,String>> tracks = tfile.getTracksHashMap();
		int numTraining = (int)(trainingFrac*tracks.size());
		System.out.println("Training fraction: "+numTraining+"/"+tracks.size());
		for(int i=0;i<numTraining;i++){
			HashMap<String,String> track = tracks.get(i);
			convertFeatures(track,fvec,cvec,useFeats);
			knn.add(fvec,cvec);
		}
		//now test on remaining data
		FileWriter cvec_zero, cvec_one, cvec_two;
		System.out.println("Output files:");
		System.out.println("\t"+filePrefix+"deltax.txt");
		cvec_zero = new FileWriter(filePrefix+"deltax.txt");
		System.out.println("\t"+filePrefix+"deltay.txt");
		cvec_one = new FileWriter(filePrefix+"deltay.txt");
		System.out.println("\t"+filePrefix+"deltat.txt");
		cvec_two = new FileWriter(filePrefix+"deltat.txt");
		FileWriter[] files = {cvec_zero, cvec_one, cvec_two};
		double[][] neighborClasses = new double[numNeighbors][3];
		ArrayList<ArrayList<Double>> predicted = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> observed = new ArrayList<ArrayList<Double>>();
		for(int i=0;i<3;i++){
			predicted.add(new ArrayList<Double>());
			observed.add(new ArrayList<Double>());
		}
		/*
		double[] avg={0.,0.,0.};
		for(int i=numTraining;i<tracks.size();i++){
			HashMap<String,String> track = tracks.get(i);
			convertFeatures(track,fvec,cvec,useFeats);
			for(int j=0;j<avg.length;j++){
				avg[j] += cvec[j];
			}
		}
		for(int i=0;i<avg.length;i++) 
			avg[i] = avg[i]/(tracks.size()-numTraining);
		double[] sse={0.,0.,0.};
		double[] sst={0.,0.,0.};
		*/
		for(int i=numTraining;i<tracks.size();i++){
			HashMap<String,String> track = tracks.get(i);
			convertFeatures(track,fvec,cvec,useFeats);
			knn.query(fvec,neighborClasses);
			selectFromNeighbors(neighborClasses,rvec);
			for(int j=0;j<rvec.length;j++){
				files[j].write(cvec[j]+"\t"+rvec[j]+"\n");
				predicted.get(j).add(cvec[j]);
				observed.get(j).add(rvec[j]);
			}
			//cvec_zero.write(rvec[0]+"\t"+cvec[0]+"\n");
			//sst[0] += Math.pow(cvec[0]-avg[0],2);
			//sse[0] += Math.pow(cvec[0]-rvec[0],2);
			//cvec_one.write(rvec[1]+"\t"+cvec[1]+"\n");
			//sst[1] += Math.pow(cvec[1]-avg[1],2);
			//sse[1] += Math.pow(cvec[1]-rvec[1],2);
			//cvec_two.write(rvec[2]+"\t"+cvec[2]+"\n");
			//sst[2] += Math.pow(cvec[2]-avg[2],2);
			//sse[2] += Math.pow(cvec[2]-rvec[2],2);
		}
		//Coeff. of Determination
		double[] sse = {0.,0.,0.};
		double[] sst = {0.,0.,0.};
		double[] observed_mean = {0.,0.,0.};
		for(int i=0;i<observed.get(0).size();i++){
			for(int j=0;j<observed_mean.length;j++){
				observed_mean[j] += observed.get(j).get(i);
			}
		}
		for(int i=0;i<observed_mean.length;i++){
			observed_mean[i] = observed_mean[i]/(observed.get(i).size());
		}
		for(int i=0;i<observed.get(0).size();i++){
			for(int j=0;j<observed_mean.length;j++){
				sst[j] += Math.pow(observed.get(j).get(i)-observed_mean[j],2);
				sse[j] += Math.pow(observed.get(j).get(i)-predicted.get(j).get(i),2);
			}
		}
		rv = new double[cvec.length];
		for(int i=0;i<sse.length;i++){
			rv[i] = (1-(sse[i]/sst[i]));
		}
		System.out.println("R^2:");
		System.out.println("\tx: "+rv[0]);
		System.out.println("\ty: "+rv[1]);
		System.out.println("\tt: "+rv[2]);
		cvec_zero.close(); cvec_one.close(); cvec_two.close();
		return rv;
	}
	private static boolean[][] int2boolarr(int len){
		boolean[][] rv = new boolean[(int)(Math.pow(2,len))][len];
		for(int i=0;i<rv.length;i++){
			for(int j=0;j<rv[i].length;j++){
				rv[i][j] = (((i>>j)&1)==0);
				//System.out.print(rv[i][j]+" ");
			}
			//System.out.println();
		}
		return rv;
	}
	private static void selectFromNeighbors(double[][] neighbors, double[] result){
		//do random selection
		/*
		int idx = rand.nextInt(neighbors.length);
		for(int i=0;i<result.length;i++) result[i] = neighbors[idx][i];
		*/
		//do average!
		for(int i=0;i<result.length;i++) result[i] = 0.0;
		for(int i=0;i<neighbors.length;i++){
			for(int j=0;j<neighbors[i].length;j++)
				result[j] += neighbors[i][j];
		}
		for(int i=0;i<result.length;i++) result[i] = result[i]/neighbors.length;
		
	}
	private static void convertFeatures(HashMap<String,String> track, double[] fvec, double[] cvec, boolean[] featMask){
		double tmp;
		int fvec_IDX=0;
		if(featMask[USE_NEAR_ANT]){
			fvec[fvec_IDX++] = Double.parseDouble(track.get("nearAntX"));
			fvec[fvec_IDX++] = Double.parseDouble(track.get("nearAntY"));
		}
		if(featMask[USE_HOME_DIR]){
			fvec[fvec_IDX++] = Double.parseDouble(track.get("homeX"));
			fvec[fvec_IDX++] = Double.parseDouble(track.get("homeY"));
			//normalize
			tmp = Math.sqrt(Math.pow(fvec[fvec_IDX-1],2)+Math.pow(fvec[fvec_IDX-2],2));
			fvec[fvec_IDX-1] /= tmp;
			fvec[fvec_IDX-2] /= tmp;
		}
		if(featMask[USE_NEAR_OBS]){
			fvec[fvec_IDX++] = Double.parseDouble(track.get("nearObsX"));
			fvec[fvec_IDX++] = Double.parseDouble(track.get("nearObsY"));
		}
		if(featMask[USE_OLD_CMD]){
			fvec[fvec_IDX++] = Double.parseDouble(track.get("oldAntVelX"));
			fvec[fvec_IDX++] = Double.parseDouble(track.get("oldAntVelY"));
			fvec[fvec_IDX++] = Double.parseDouble(track.get("oldAntVelT"));
		}
		cvec[0] = Double.parseDouble(track.get("antVelX"));
		cvec[1] = Double.parseDouble(track.get("antVelY"));
		cvec[2] = Double.parseDouble(track.get("antVelT"));
	}
	public static void main(String[] args){
		if(args.length < 1){
			System.out.println("Usage: java LearnedPerformance <atf-file> [filename prefix]");
		}
		try{
			String prefix = "";
			if(args.length>=2) prefix = args[1];
			System.out.println("Initializing random number generator");
			rand = new Random();
			System.out.println("Loading track file ["+args[0]+"]");
			TrackFile tfile = new TrackFile(args[0]);
			//boolean[] mask = {true,true,true,true};
			boolean[][] masks = int2boolarr(4);
			double[] bestFit = new double[3];
			int[] bestMask={-1,-1,-1};
			double[] tmpFit={-1.,-1.,-1.};
			for(int i=0;i<masks.length;i++){
				tmpFit = runKNN(masks[i],tfile,prefix+"mask"+i,0.8,10);
				if(tmpFit == null) continue;
				for(int j=0;j<tmpFit.length;j++){
					if(bestMask[j] == -1 || tmpFit[j] > bestFit[j]){
						bestFit[j] = tmpFit[j];
						bestMask[j] = i;
					}
				}
			}
			for(int i=0;i<bestFit.length;i++){
				System.out.println("Best fit for output "+i+": "+bestFit[i]);
				System.out.println("using mask "+bestMask[i]+" :"+mask2str(masks[bestMask[i]]));
			}
			//runKNN(mask,tfile,"",0.8,10);
		} catch(Exception e){
			System.out.println("Learned Performance main() failed");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
