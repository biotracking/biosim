package app.AntsLearned;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Random;

import core.util.FastKNN;
import core.util.TrackFile;

import be.ac.ulg.montefiore.run.jahmm.*;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;


public class LearnedMultiController {
	public FastKNN[] learners;
	public int fsize, csize;
	public int[] trackLabels;
	public Hmm<ObservationInteger> hmm;
	public BaumWelchLearner bwlearner;
	public static final int MAX_SEQ_LENGTH=100;
	public static final double SAME_STATE_BIAS=0.6;
	public static final double SELF_TRANSITION_BIAS=0.5;
	public Random random;
	public LearnedMultiController(int numControllers,int fsize,int csize){
		learners = new FastKNN[numControllers];
		this.fsize = fsize;
		this.csize = csize;
		for(int i=0;i<numControllers;i++)
			learners[i] = new FastKNN(fsize,csize);
		trackLabels = null;
		hmm = null;
		bwlearner = new BaumWelchLearner();
		random = new Random();
	}
	private void convertFeatures(HashMap<String,String> track, double[] fvec, double[] cvec){
			double tmp;
			fvec[0] = Double.parseDouble(track.get("nearAntX"));
			fvec[1] = Double.parseDouble(track.get("nearAntY"));
			fvec[2] = Double.parseDouble(track.get("homeX"));
			fvec[3] = Double.parseDouble(track.get("homeY"));
			//normalize the home vector
			tmp = Math.sqrt(Math.pow(fvec[2],2)+Math.pow(fvec[3],2));
			fvec[2] = fvec[2]/tmp;
			fvec[3] = fvec[3]/tmp;
			fvec[4] = Double.parseDouble(track.get("nearObsX"));
			fvec[5] = Double.parseDouble(track.get("nearObsY"));
			fvec[6] = Double.parseDouble(track.get("oldAntVelX"));
			fvec[7] = Double.parseDouble(track.get("oldAntVelY"));
			fvec[8] = Double.parseDouble(track.get("oldAntVelT"));
			//and the observed control
			cvec[0] = Double.parseDouble(track.get("antVelX"));
			cvec[1] = Double.parseDouble(track.get("antVelY"));
			cvec[2] = Double.parseDouble(track.get("antVelT"));
	}
	public void buildControllers(TrackFile trackFile){
		//first, devide the tracks into equal chunks
		ArrayList<HashMap<String,String>> tracks = trackFile.getTracksHashMap();
		double[] fvec = new double[fsize]; //antX,antY,homeX,homeY,obsX,obsY,oldDX,oldDY,oldDT
		double[] cvec = new double[csize]; //antVelX, antVelY, antVelT
		int tracksPerBlock = tracks.size()/learners.length;
		int remainder = tracks.size() % learners.length;
		int tmpTrackIDX=0;
		trackLabels = new int[tracks.size()];
		for(int i = 0;i<learners.length;i++){
			for(int j=0;j<tracksPerBlock;j++){
				tmpTrackIDX = (i*tracksPerBlock)+j;
				trackLabels[tmpTrackIDX] = i;
				HashMap<String,String> track = tracks.get(tmpTrackIDX);
				convertFeatures(track,fvec,cvec);
				//add to the appropriate learner
				learners[i].add(fvec,cvec);
			}
		}
	}

	public void relabelTracks(TrackFile trackFile, int numNeighbors){
		double[] fvec = new double[fsize]; //antX,antY,homeX,homeY,obsX,obsY,oldDX,oldDY,oldDT
		double[] cvec = new double[csize]; //antVelX, antVelY, antVelT
		double[][] neighborClasses = new double[numNeighbors][csize];
		double tmpAvgDist2=0.0, minAvgDist2=-1.0;
		int minAvgDistIDX = -1;
		ArrayList<HashMap<String,String>> tracks = trackFile.getTracksHashMap();
		HashMap<String,String> track;
		for(int i=0;i<tracks.size();i++){
			track = tracks.get(i);
			convertFeatures(track,fvec,cvec);
			minAvgDistIDX = -1;
			minAvgDist2 = -1.0;
			//compute avg dist for each learner
			//note, since all learners are scaled
			//by the same number of neighbors,
			//and are squared, we can leave the sqrt
			//and normalization out and still get
			//the same ranking
			for(int j=0;j<learners.length;j++){
				learners[j].query(fvec,neighborClasses);
				tmpAvgDist2 = 0.0;
				for(int k=0;k<numNeighbors;k++){
					for(int l=0;l<csize;l++){
						tmpAvgDist2 += Math.pow(neighborClasses[k][l] - cvec[l],2.0);
					}
				}
				//System.out.println(tmpAvgDist2+" >? "+minAvgDist2);
				if(minAvgDistIDX == -1 || minAvgDist2 > tmpAvgDist2){
					minAvgDistIDX = j;
					minAvgDist2 = tmpAvgDist2;
				}
			}
			trackLabels[i] = minAvgDistIDX;
		}
	}

	public void buildModelFromLabels(int iterations){
		OpdfIntegerFactory factory = new OpdfIntegerFactory(learners.length);
		if(hmm == null){
			hmm = new Hmm<ObservationInteger>(learners.length,factory);
			double tmp1 = 1.0;
			for(int i=0;i<learners.length;i++){
				double[] obsProb = new double[learners.length];
				for(int j=0;j<obsProb.length;j++){
					obsProb[j] = (i==j)?SAME_STATE_BIAS:(1-SAME_STATE_BIAS)/(obsProb.length-1);
				}
				if(i == learners.length-1){
					hmm.setPi(i,tmp1);
				} else {
					hmm.setPi(i,random.nextDouble()*tmp1);
					tmp1 = tmp1 - hmm.getPi(i);
				}
				hmm.setOpdf(i, new OpdfInteger(obsProb));
				for(int j=0;j<learners.length;j++){
					if(i==j){
						hmm.setAij(i,j,SELF_TRANSITION_BIAS);
					} else {
						hmm.setAij(i,j,(1-SELF_TRANSITION_BIAS)/(learners.length-1));
					}
				}
				/*
				double tmp2 = 1.0;
				for(int j=0;j<learners.length-1;j++){
					hmm.setAij(i,j,random.nextDouble()*tmp2);
					tmp2 = tmp2 - hmm.getAij(i,j);
				}
				hmm.setAij(i,learners.length-1,tmp2);
				*/
			}
		}
		ArrayList<ArrayList<ObservationInteger>> sequences = new ArrayList<ArrayList<ObservationInteger>>();
		for(int i=0;i<=trackLabels.length/MAX_SEQ_LENGTH;i++){
			ArrayList<ObservationInteger> labelSeq = new ArrayList<ObservationInteger>();
			for(int j=(i*MAX_SEQ_LENGTH);j<((i+1)*MAX_SEQ_LENGTH) && j<trackLabels.length;j++){
				labelSeq.add(new ObservationInteger(trackLabels[j]));
			}
			sequences.add(labelSeq);
		}
		for(int i=0;i<iterations;i++){
			System.out.println("Model:");
			System.out.println(hmm);
			hmm = bwlearner.iterate(hmm,sequences);
		}
		int subseqIDX = ((int)(random.nextDouble()*sequences.size()));
		ViterbiCalculator vc = new ViterbiCalculator(sequences.get(subseqIDX),hmm);
		int[] stateSeq = vc.stateSequence();
		System.out.println("Viterbi most likely state sequence for subseq "+subseqIDX+": [ ");
		for(int i=0;i<stateSeq.length;i++)
			System.out.print(stateSeq[i]+" ");
		System.out.println("]");
		System.out.print("Original labeling for subsequence: [ ");
		for(int i=0;i<sequences.get(subseqIDX).size();i++)
			System.out.print(sequences.get(subseqIDX).get(i)+" ");
		System.out.println("]");
		
	}
	
	public void printTrackLabels(){
		System.out.print("[ ");
		for(int i=0;i<trackLabels.length;i++)
			System.out.print(trackLabels[i]+" ");
		System.out.println("]");
	}

	public static void main(String[] args){
		try{
			LearnedMultiController lmc = new LearnedMultiController(4,9,3);
			if(args.length != 1){
				System.out.println("Usage: java "+lmc.getClass().getName()+" <atf file>");
				return;
			}
			System.out.println("Loading track file ["+args[0]+"]");
			TrackFile tfile = new TrackFile(args[0]);
			System.out.println("building initial learners");
			lmc.buildControllers(tfile);
			System.out.println("Initial track labels:");
			lmc.printTrackLabels();
			System.out.println("Reassigning labels");
			lmc.relabelTracks(tfile,10);
			System.out.println("Reassigned labels:");
			lmc.printTrackLabels();
			System.out.println("building HMM:");
			lmc.buildModelFromLabels(100);
		} catch(Exception e){
			System.out.println("LearnedMultiController main() failed");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
