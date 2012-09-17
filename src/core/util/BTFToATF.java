package core.util;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

public class BTFToATF {
	public static void main(String[] args){
		if(args.length < 3 || args.length > 4){
			System.out.println("Usage: java BTFToATF output.atf pixPerMeter agentType [path/to/btf/directory/]");
			System.out.println("defaults to current directory if no path is given");
			return;
		}
		String dirName=".";
		double pixPerMeter = Double.parseDouble(args[1]);
		int agentType = Integer.parseInt(args[2]);
		if(args.length == 4) dirName=args[3];
		try{
			TrackFile atf = new TrackFile();
			//setup the header
			BufferedReader btfTime = new BufferedReader(new FileReader(new File(dirName,"timestamp.btf")));
			atf.addFieldDescription(1,"Double","timestamp","Time at which the sample was taken","seconds");
			atf.addFieldDescription(2,"Int","agentType","Type of the agent","none");
			BufferedReader btfID = new BufferedReader(new FileReader(new File(dirName,"id.btf")));
			atf.addFieldDescription(3,"Int","agent_ID","ID number of the agent","none");
			BufferedReader btfX = new BufferedReader(new FileReader(new File(dirName,"ximage.btf")));
			atf.addFieldDescription(4,"Double","x","x position","meters");
			BufferedReader btfY = new BufferedReader(new FileReader(new File(dirName,"yimage.btf")));
			atf.addFieldDescription(5,"Double","y","y position","meters");
			BufferedReader btfTheta = new BufferedReader(new FileReader(new File(dirName,"timage.btf")));
			atf.addFieldDescription(6,"Double","theta","global orientation in the frame","radians");
			
			//parse each line
			int id;
			double x,y,theta,timestamp;
			String tmp, newRow;
			while(btfID.ready() && btfTime.ready() && btfX.ready() && btfY.ready() && btfTheta.ready()){			
				tmp = btfID.readLine();
				if(tmp == null) break;
				id = Integer.parseInt(tmp.trim());
				
				tmp = btfTime.readLine();
				if(tmp == null) break;
				timestamp = Double.parseDouble(tmp.trim())/1000;
				
				tmp = btfX.readLine();
				if(tmp == null) break;
				x = Double.parseDouble(tmp.trim())/pixPerMeter;
				
				tmp = btfY.readLine();
				if(tmp == null) break;
				y = Double.parseDouble(tmp.trim())/pixPerMeter;
				
				tmp = btfTheta.readLine();
				if(tmp == null) break;
				theta= Double.parseDouble(tmp.trim());
				
				newRow = String.format("1:%f 2:%d 3:%d 4:%f 5:%f 6:%f",timestamp,agentType,id,x,y,theta+Math.PI);
				
				atf.addRow(newRow);
			}
			atf.write(args[0]);
			
		} catch(Exception e){
			System.out.println("GAAAAH!");
			e.printStackTrace();
		}
	}
}
