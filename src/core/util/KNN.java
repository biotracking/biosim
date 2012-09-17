package core.util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.io.FileReader;
import java.io.BufferedReader;
/**
 * A simple KNN implementation.
 */
public class KNN{
	private double[] scaleFactor;
	private ArrayList<KNNSample> data;
	
	public static interface KNNSample{
		public double nearness(KNNSample other, double[] scaleFactor);
		public void addFeatures(double[] sum);
		public void subFeatures(double[] sub);
		public int getNumFeatures();
	}
	
	public static class SimpleSample implements KNNSample {
		double[] features;
		int clss;
		public SimpleSample(double[] fts, int c){
			features = new double[fts.length];
			System.arraycopy(fts,0,features,0,fts.length);
			clss = c;
		}
		public double nearness(KNNSample other, double[] scaleFactor){
			SimpleSample o = (SimpleSample)other;
			double tmpDist = 0.0;
			for(int i=0;i<features.length;i++)
				tmpDist += Math.pow((features[i]-o.features[i])/scaleFactor[i],2);
			
			return tmpDist;
		}
		public void addFeatures(double[] sum){
			for(int i=0;i<features.length;i++)
				sum[i] += features[i];
		}
		public void subFeatures(double[] sub){
			for(int i=0;i<features.length;i++)
				sub[i] -= features[i];
		}
		public int getNumFeatures(){
			return features.length;
		}
	}
	
	public KNN(){
		data = new ArrayList<KNNSample>();
		scaleFactor = null;
	}
	/**
	 * Add a single training point.
	 */
	public void add(KNNSample sample){
		data.add(sample);
	}
	/**
	 * Wrapper for using SimpleSample.
	 */ 
	public void add(double[] sample,int clss){
		add(new SimpleSample(sample,clss));
	}
	/**
	 * Computes a scale factor equal to the standard deviation of each feature.
	 * Usefull when feature scale's are disproportionate to their weight.
	 */
	public void scaleNormalize(){
		scaleFactor = new double[data.get(0).getNumFeatures()];
		double[] avg = new double[scaleFactor.length];
		double[] stddev = new double[scaleFactor.length];
		double[] tmp = new double[scaleFactor.length];
		for(int i=0;i<scaleFactor.length;i++){
			scaleFactor[i] = 1.0;
			tmp[i] = avg[i] = stddev[i] = 0.0;
		}
		//get the avg
		for(int i=0;i<data.size();i++) data.get(i).addFeatures(avg);
		for(int i=0;i<avg.length;i++) avg[i] = avg[i]/data.size();
		//get the std. dev.
		for(int i=0;i<data.size();i++){
			System.arraycopy(avg,0,tmp,0,avg.length);
			data.get(i).subFeatures(tmp);
			for(int j=0;j<tmp.length;j++)
				stddev[j] = stddev[j] + Math.pow(tmp[j],2);
		}
		//store the std. dev in scaleFactor	
		for(int i=0;i<stddev.length;i++)
			scaleFactor[i] = Math.sqrt(stddev[i]/data.size());
		for(int i=0;i<scaleFactor.length;i++)
			scaleFactor[i] = (scaleFactor[i] == 0.0)?1.0:scaleFactor[i];
	}
	
	/**
	 * Find the K nearest neighbors to qPoint. The parameter neighbors
	 * should be a k-length array, each element of which will be overwritten
	 * by this method.
	 */
	public void query(KNNSample qPoint, KNNSample[] neighbors){
		int k = neighbors.length;
		int[] neighborIDs = new int[k];
		if(scaleFactor == null){
			scaleFactor = new double[data.get(0).getNumFeatures()];
			for(int i=0;i<scaleFactor.length;i++) scaleFactor[i] = 1.0;
		}
		double[] distances2 = new double[k];
		int farthest = 0;
		for(int i=0;i<k;i++){
			neighborIDs[i] = -1;
			distances2[i] = Double.MAX_VALUE;
		}
		for(int eye=0;eye<data.size();eye++){
				KNNSample sample = data.get(eye);
				double tmpdist2 = sample.nearness(qPoint,scaleFactor);
				//find replacement
				if(neighborIDs[farthest] == -1 || distances2[farthest] > tmpdist2 || (distances2[farthest] == tmpdist2 && (Math.random() > 0.5))){
					neighborIDs[farthest] = eye;
					distances2[farthest] = tmpdist2;
					//find the new farthest
					for(int i=0;i<k;i++)
						if(distances2[farthest] < distances2[i])
							farthest = i;
				}
		}
		//put the classes corresponding to the neighbors in neighborClasses
		for(int i=0;i<k;i++)
			neighbors[i] = data.get(neighborIDs[i]);
	}
	

	public static void main(String[] args){
		if(args.length<2){
			System.out.println("Usage KNN trainfile1 [train2 train3...] testfile");
			return;
		}
		String[] training = new String[args.length-1];
		for(int i=0;i<args.length-1;i++)
			training[i] = args[i];
		String testing = args[args.length-1];
		System.out.println("Training set:");
		for(int i=0;i<training.length;i++)
			System.out.println("\t"+training[i]);
		System.out.println("\nTesting set:");
		System.out.println(testing);
		KNN knn = new KNN();
		int clss;
		double[] foo = new double[2];
		KNN.SimpleSample[] neighbors = new KNN.SimpleSample[3];
		try {
			System.out.println("Adding training set");
			for(int i=0;i<training.length;i++){
				BufferedReader bRead = new BufferedReader(new FileReader(training[i]));
				String line = bRead.readLine();
				while(line != null && bRead.ready()){
					line.trim();
					Scanner trainFile = new Scanner(line);
					clss = trainFile.nextInt();
					foo[0] = trainFile.nextDouble();
					foo[1] = trainFile.nextDouble();
					knn.add(foo,clss);
					line = bRead.readLine();
				}
			}
			System.out.println("Number of samples: "+knn.data.size());
			System.out.println("Normalizing...");
			knn.scaleNormalize();
			System.out.print("Scale factor:[ ");
			for(int i=0;i<knn.scaleFactor.length;i++)
				System.out.print(knn.scaleFactor[i]+" ");
			System.out.println("]");
			System.out.println("Running test set");
			BufferedReader bRead = new BufferedReader(new FileReader(testing));
			String line = bRead.readLine();
			while(line != null && bRead.ready()){
				line.trim();
				Scanner testFile = new Scanner(line);
				clss = testFile.nextInt();
				foo[0] = testFile.nextDouble();
				foo[1] = testFile.nextDouble();
				knn.query(new KNN.SimpleSample(foo,-1),neighbors);
				System.out.print("Neighbors [ ");
				for(int i=0;i<neighbors.length;i++)
					System.out.print(neighbors[i].clss+" ");
				System.out.println("] Real: "+clss);
				line = bRead.readLine();
			}
		} catch(Exception e){
			e.printStackTrace();
			System.out.println(e);
		}
	}
}
