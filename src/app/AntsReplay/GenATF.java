package app.AntsLearned;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

import core.util.TrackFile;

public class GenATF {
	public static void main(String[] args){
		if(args.length != 1){
			System.out.println("Usage: java app.AntsLearned.GenATF output.atf");
			return;
		}
		int agentType = 1;
		try{
			TrackFile atf = new TrackFile();
			//setup the header
			atf.addFieldDescription(1,"Double","timestamp","Time at which the sample was taken","seconds");
			atf.addFieldDescription(2,"Int","agentType","Type of the agent","none");
			atf.addFieldDescription(3,"Int","agent_ID","ID number of the agent","none");
			atf.addFieldDescription(4,"Double","x","x position","meters");
			atf.addFieldDescription(5,"Double","y","y position","meters");
			atf.addFieldDescription(6,"Double","theta","global orientation in the frame","radians");
			
			int id0=0;
			double x0=0.01,y0=0.05,theta0=0.0, flip0=1.0;
			int id1=1;
			double x1=0.05,y1=0.01,theta1=Math.PI/2, flip1=1.0;

			String tmp, newRow;
			double timestamp;
			for(timestamp = 0.0; timestamp < 60.0; timestamp += (1.0/30.0)){
				
				x0 += 0.0005*flip0;
				y0 = y0;
				theta0=theta0+Math.PI/8;
				if(x0 > 0.1 || x0 < 0.01) flip0 = -flip0;
				newRow = String.format("1:%f 2:%d 3:%d 4:%f 5:%f 6:%f",timestamp,agentType,id0,x0,y0,theta0);
				atf.addRow(newRow);
				
				x1 = x1;
				y1 += 0.0005*flip1;
				if(y1 > 0.1 || y1 < 0.01) flip1 = -flip1;
				newRow = String.format("1:%f 2:%d 3:%d 4:%f 5:%f 6:%f",timestamp,agentType,id1,x1,y1,theta1);
				atf.addRow(newRow);
				
			}
			atf.write(args[0]);
			
		} catch(Exception e){
			System.out.println("GAAAAH!");
			e.printStackTrace();
		}
	}
}
