package core.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import sim.util.Double2D;
import sim.util.MutableDouble2D;

/**
 * @author Hai Shang
 * @author hshang9@gatech.edu
 * 
 */
public class InteractionAnalysiser {
	private File logFile;
	private File resultFile;
	private BufferedReader reader;
	private BufferedWriter writer;
	private double timeStamp;
	private ArrayList<ArrayList<SimInfo>> history;
	public ArrayList<SimInfo> interacting;
	private double interacting_time_length;
	private double interacting_distance;
	public long interacting_count;
	private double startTime, endTime;

	public InteractionAnalysiser() {
		
		String title = "./log/log_123.txt";
		String resultTitle = "./log/log_123_analysised.txt"; 
		logFile = new File(title);
		resultFile = new File(resultTitle); 
		history = new ArrayList<ArrayList<SimInfo>>();
		interacting = new ArrayList<SimInfo>();
		interacting_time_length = 1;
		interacting_distance = 1;
		interacting_count = 0;
		startTime = 0;
		endTime = 60;
		try{
			reader = new BufferedReader(new FileReader(logFile)); 
			writer = new BufferedWriter(new FileWriter(resultFile)); 
			String line ="<header>\n"; 
			line+="  <field>Index=1 type=\"double\" name=\"timeStamp\" description=\"the time since the beginning of the simulation\" unit=\"seconds\"</field>\n";
			line+="  <field>Index=2 type=\"int\" name=\"agentType\" description=\"the type of this agent\"</field>\n";
			line+="  <field>Index=3 type=\"int\" name=\"agentId\" description=\"the id of this agent\" </field>\n";
			line+="  <field>Index=4 type=\"double\" name=\"px\" description=\"The coordinate for x-axis position\" unit=\"meters\"</field>\n";
			line+="  <field>Index=5 type=\"double\" name=\"py\" description=\"The coordinate for y-axis position\" unit=\"meters\"</field>\n";
			line+="  <field>Index=6 type=\"double\" name=\"pz\" description=\"The coordinate for z-axis position\" unit=\"meters\"</field>\n";
			line+="  <field>Index=7 type=\"double\" name=\"dx\" description=\"The coordinate for x-axis position\" unit=\"meters\"</field>\n";
			line+="  <field>Index=8 type=\"double\" name=\"dy\" description=\"The coordinate for y-axis position\" unit=\"meters\"</field>\n";
			line+="  <field>Index=9 type=\"double\" name=\"dz\" description=\"The coordinate for z-axis position\" unit=\"meters\"</field>\n"; 
			line+="  <field>Index=10 type=\"InteractionList\" name=\"interactions\" description=\"List of interactions the agent is engaging in\" unit=\"event\"</field>\n";
			line+= "</header>\n<data>\n"; writer.write(line); writer.flush(); 
		}
		catch(IOException e){ System.err.println(e); System.exit(1); }
	}

	public ArrayList<ArrayList<SimInfo>> getHistory() {
		return this.history;
	}

	public void analysis() {
		String line = "";
		String[] subLines = null;
		String[] sub2 = null;
		String[] infoStr = new String[9];
		ArrayList<SimInfo> sia = new ArrayList<SimInfo>();
		SimInfo si = new SimInfo();
		boolean continueFlag = true;
		try {
			do {
				line = reader.readLine();
				if (line.contains("<data>")) {
					continueFlag = false;
				}
			} while (continueFlag);
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}
		continueFlag = true;
		do {
			try {
				line = reader.readLine();
			} catch (IOException e) {
				System.err.println(e);
				System.exit(1);
			}
			if (line == null) {
				frameAnalysis(sia);
				sia.clear();
				return;
			}
			if (line != null) {
				if (line.contains("</data>")) {
					frameAnalysis(sia);
					sia.clear();
					return;
				}
				subLines = line.split(" ");
				for (int i = 3; i < 12; i++) {
					sub2 = subLines[i].split(":");
					if (sub2.length >= 2) {
						infoStr[i - 3] = sub2[1];
					}
				}
				si = new SimInfo(// d1,i1,i2,d2,d3,d4,d5,d6,d7);
						Double.parseDouble(infoStr[0]), Integer
								.parseInt(infoStr[1]), Integer
								.parseInt(infoStr[2]), Double
								.parseDouble(infoStr[3]), Double
								.parseDouble(infoStr[4]), Double
								.parseDouble(infoStr[5]), Double
								.parseDouble(infoStr[6]), Double
								.parseDouble(infoStr[7]), Double
								.parseDouble(infoStr[8]));
				if(!id_contained(interacting, si)){
					interacting.add(si);
				}
				if (timeStamp != si.getTimeStamp()) {// new timeStamp
					frameAnalysis(sia);
					sia.clear();
					timeStamp = si.getTimeStamp();

				}
				sia.add(si);
			}
		} while (continueFlag);
	}

	private void frameAnalysis(ArrayList<SimInfo> sia) {
		if(!(timeStamp > startTime && timeStamp <= endTime)) return;
		int size = sia.size();
		if (size == 0)
			return;
		while (size > history.size()) {
			history.add(new ArrayList<SimInfo>());
		}
		SimInfo si = null;

		ArrayList<MutableDouble2D> pa = new ArrayList<MutableDouble2D>();
		MutableDouble2D loc = null;
		for (int i = 0; i < size; i++) {
			loc = new MutableDouble2D(sia.get(i).getPX(), sia.get(i).getPY());
			pa.add(loc);
		}
		double tmp;
		ArrayList<Integer> ia = new ArrayList<Integer>();// store the id of the
		// interacting agent
		ArrayList<Integer> ib = new ArrayList<Integer>();// store the agentType
		// of the
		// interacting agent
		Interaction interaction = null;
		ArrayList<Interaction> interactionList = new ArrayList<Interaction>();
		for (int i = 0; i < size; i++) {
			si = sia.get(i);
			ia.clear();
			ib.clear();
			interactionList.clear();
			boolean in_touch = false;
			for (int j = 0; j < size; j++) {
				if (j != i) {
					tmp = Math.pow(pa.get(i).getX() - pa.get(j).getX(), 2);
					tmp += Math.pow(pa.get(i).getY() - pa.get(j).getY(), 2);
					tmp = Math.sqrt(tmp);
					if (tmp <= interacting_distance) { // interacting
						in_touch = true;
						ia.add(sia.get(j).getId());
						ib.add(sia.get(j).getAgentType());
					}
				}

			}
			int interacting_index = find_id(interacting, si.getId());
			if(in_touch){
				if(interacting_index != -1){
                    if((Math.abs(interacting.get(interacting_index).getPX()-si.getPX())<0.0000002) 
                    		&& (Math.abs(interacting.get(interacting_index).getPY()-si.getPY())<0.0000002)){
                    	if(si.getTimeStamp() - interacting.get(interacting_index).getTimeStamp() >= interacting_time_length){
                    		interacting.get(interacting_index).setTimeStamp(si.getTimeStamp());
                        	interacting.get(interacting_index).setPX(si.getPX());
                        	interacting.get(interacting_index).setPY(si.getPY());
                    		interacting_count++;
                    	}
                    } else {
                    	interacting.get(interacting_index).setTimeStamp(si.getTimeStamp());
                    	interacting.get(interacting_index).setPX(si.getPX());
                    	interacting.get(interacting_index).setPY(si.getPY());
                    }
				} else {
					interacting.add(si);
				}
			} else {
				if(interacting_index != -1){
					interacting.get(interacting_index).setTimeStamp(si.getTimeStamp());
                	interacting.get(interacting_index).setPX(si.getPX());
                	interacting.get(interacting_index).setPY(si.getPY());
				} else {
					interacting.add(si);
				}
			}
			try {
				writer.write("  <row> 1:" + si.getTimeStamp() + " 2:"
						+ si.getAgentType() + " 3:" + si.getId() + " 4:"
						+ si.getPX() + " 5:" + si.getPY() + " 6:" + si.getPZ()
						+ " 7:" + si.getDX() + " 8:" + si.getDY() + " 9:"
						+ si.getDZ() + " 10:[");

				for (int k = 0; k < ia.size(); k++) {
					int type = getInteractionType(sia, ib.get(k), ia.get(k), si
							.getAgentType(), si.getId());
					Double2D toLoc = getToLocation(sia, ib.get(k), ia.get(k),
							si.getAgentType(), si.getId());
					if (type != -1) {
						interaction = new Interaction(type, toLoc.getX(), toLoc
								.getY());
						interactionList.add(interaction);
						writer.write("(" + interaction.getType() + ":"
								+ ia.get(k) + "{" + interaction.getToX() + "+"
								+ interaction.getToY() + "})");
					}
				}
				writer.write("] </row>\n");
				writer.flush();
				si.setInteractionList(interactionList);
				history.get(si.getId()).add(si);
			} catch (IOException e) {
				System.err.println(e);
				System.exit(1);
			}
		}
	}

	private int getInteractionType(ArrayList<SimInfo> sia, int toAgentType,
			int toId, int fromAgentType, int fromId) {
		if (sia == null)
			return -1;
		if (sia.size() == 0)
			return -1;
		SimInfo siTmp = null;
		SimInfo siFrom = null;
		SimInfo siTo = null;
		for (int i = 0; i < sia.size(); i++) {
			siTmp = sia.get(i);
			if (siTmp.getAgentType() == toAgentType && siTmp.getId() == toId)
				siTo = siTmp;
			if (siTmp.getAgentType() == fromAgentType
					&& siTmp.getId() == fromId)
				siFrom = siTmp;
		}
		if (siTo == null || siFrom == null)
			return -1;
		double xoff = siTo.getPX() - siFrom.getPX();
		double yoff = siTo.getPY() - siFrom.getPY();
		double angle1 = Math.atan2(yoff, xoff);
		double angle2 = Math.atan2(siFrom.getDY(), siFrom.getDX());
		double angleOff = angle1 - angle2;
		if (angleOff > Math.PI)
			angleOff -= Math.PI * 2;
		if (angleOff < -Math.PI)
			angleOff += Math.PI * 2;
		// within pi/8 range from the "from agent direction", counted as valid
		// interaction
		if (Math.abs(angleOff) < Math.PI / 8) {
			angle1 = Math.atan2(siTo.getDY(), siTo.getDX());
			angleOff = angle1 - angle2;
			if (angleOff > Math.PI)
				angleOff -= Math.PI * 2;
			if (angleOff < -Math.PI)
				angleOff += Math.PI * 2;
			if (Math.abs(angleOff) < Math.PI / 6)
				return 1;// head to tail
			else if (Math.abs(angleOff) < Math.PI / 6 * 5)
				return 2;// head to body
			else
				return 3; // head to head
		}
		return -1;
	}

	private Double2D getToLocation(ArrayList<SimInfo> sia, int toAgentType,
			int toId, int fromAgentType, int fromId) {
		if (sia == null)
			return null;
		if (sia.size() == 0)
			return null;
		SimInfo si = null;
		for (int i = 0; i < sia.size(); i++) {
			si = sia.get(i);
			if (si.getAgentType() == toAgentType && si.getId() == toId) {
				return new Double2D(si.getPX(), si.getPY());
			}
		}
		return null;
	}
	
	boolean id_contained(ArrayList<SimInfo> sia, SimInfo si){
		for(int i = 0; i < sia.size(); ++i){
			if(sia.get(i).getId() == si.getId()){//contained
				return true;
			}
			
		}
		return false;
	}
	
	private int find_id(ArrayList<SimInfo> sia, int id) {
		for(int i = 0; i < sia.size(); ++i){
			if(sia.get(i).getId() == id){//contained
				return i;
			}
			
		}
		return -1;
	}
	
	public void setInteractingTimeLength(double l){
		this.interacting_time_length = l;
	}
	
	public void setInteractingDistance(double d){
		this.interacting_distance = d;
	}
	
	public void setStartTime(double t){
		this.startTime = t;
	}
	
	public void setEndTime(double t){
		this.endTime = t;
	}
	
	public double getStartTime(){
		return this.startTime;
	}
	
	public double getEndTime(){
		return this.endTime;
	}
	
}// end of the class InteractionAnalysiser
