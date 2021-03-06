package core.util;
import core.util.annwrapper.SimpleANN;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileReader;
import java.io.BufferedReader;

public class FastKNN{
	private ArrayList<double[]> samples,classes;
	private SimpleANN kdann;
	private int sample_dim, class_dim;
	private static boolean libLoaded = false;
	
	public FastKNN(int sample_dim,int class_dim){
		if(!libLoaded){
			System.loadLibrary("annwrapper");
			libLoaded = true;
		}
		this.sample_dim = sample_dim;
		this.class_dim = class_dim;
		kdann = new SimpleANN(sample_dim);
		samples = new ArrayList<double[]>();
		classes = new ArrayList<double[]>();
	}
	
	public void add(double[] sample, double[] class_vec){
		double[] tmp_s = new double[sample.length];
		double[] tmp_c = new double[class_vec.length];
		System.arraycopy(sample,0,tmp_s,0,tmp_s.length);
		System.arraycopy(class_vec,0,tmp_c,0,tmp_c.length);
		samples.add(tmp_s);
		classes.add(tmp_c);
		kdann.add(sample);
	}
	
	public void query(double[] sample, double[][] neighbor_classes){
		int k = neighbor_classes.length;
		int[] neighborIdx = new int[k];
		if(!kdann.query(sample,neighborIdx,k)) System.out.println("ANN query failed!");
		for(int i=0;i<k;i++){ 
			//System.out.println("IDX: "+neighborIdx[i]);
			for(int j=0;j<class_dim;j++){
					neighbor_classes[i][j] = classes.get(neighborIdx[i])[j];
			}
		}
		
	}

	public void printEverything(){
		for(int i=0;i<samples.size();i++){
			System.out.print("IDX:"+i+" [");
			for(int j=0;j<sample_dim;j++){
				System.out.print(" "+samples.get(i)[j]);
			}
			System.out.print("] -> [");
			for(int j=0;j<class_dim;j++){
				System.out.print(" "+classes.get(i)[j]);
			}
			System.out.println("]");
		}
	}
	
	public static void main(String[] args){
		if(args.length<2){
			System.out.println("Usage FastKNN trainfile1 [train2 train3...] testfile");
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
		FastKNN knn = new FastKNN(2,1);
		double[] clsses = new double[1];
		double[] foo = new double[2];
		double[][] neighbors = new double[10][1];
		try {
			System.out.println("Adding training set");
			for(int i=0;i<training.length;i++){
				BufferedReader bRead = new BufferedReader(new FileReader(training[i]));
				String line = bRead.readLine();
				while(line != null && bRead.ready()){
					line.trim();
					Scanner trainFile = new Scanner(line);
					clsses[0] = trainFile.nextInt();
					foo[0] = trainFile.nextDouble();
					foo[1] = trainFile.nextDouble();
					knn.add(foo,clsses);
					line = bRead.readLine();
				}
			}
			System.out.println("Number of samples: "+knn.samples.size());
			//knn.printEverything();
			System.out.println("Running test set");
			BufferedReader bRead = new BufferedReader(new FileReader(testing));
			String line = bRead.readLine();
			while(line != null && bRead.ready()){
				line.trim();
				Scanner testFile = new Scanner(line);
				clsses[0] = testFile.nextInt();
				foo[0] = testFile.nextDouble();
				foo[1] = testFile.nextDouble();
				knn.query(foo,neighbors);
				System.out.print("Neighbors [ ");
				for(int i=0;i<neighbors.length;i++)
					System.out.print(neighbors[i][0]+" ");
				System.out.println("] Real: "+clsses[0]);
				line = bRead.readLine();
			}
		} catch(Exception e){
			e.printStackTrace();
			System.out.println(e);
		}
		
	}
}
