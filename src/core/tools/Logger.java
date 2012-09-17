package core.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import sim.util.Double2D;
import core.basic_api.AgentPortrayal;
import core.basic_api.Sim;

/**
 * @author Hai Shang
 * @author hshang9@gatech.edu
 * 
 */
public class Logger implements Steppable {
	private File logFile;
	private BufferedWriter writer;
	private String fileName = "";

	public Logger() {
		String title = "./log/log_"+System.currentTimeMillis()+ "_" +System.nanoTime() + ".txt";
		logFile = new File(title);
		try {
			writer = new BufferedWriter(new FileWriter(logFile));
			String line = "<simulation>\n<header>\n";
			line += "  <field>Index=1 type=\"double\" name=\"timeStamp\" description=\"the time since the beginning of the simulation\" unit=\"seconds\"</field>\n";
			line += "  <field>Index=2 type=\"int\" name=\"agentType\" description=\"the type of this agent\"</field>\n";
			line += "  <field>Index=3 type=\"int\" name=\"agentId\" description=\"the id of this agent\" </field>\n";
			line += "  <field>Index=4 type=\"double\" name=\"px\" description=\"The coordinate for x-axis position\" unit=\"meters\"</field>\n";
			line += "  <field>Index=5 type=\"double\" name=\"py\" description=\"The coordinate for y-axis position\" unit=\"meters\"</field>\n";
			line += "  <field>Index=6 type=\"double\" name=\"pz\" description=\"The coordinate for z-axis position\" unit=\"meters\"</field>\n";
			line += "  <field>Index=7 type=\"double\" name=\"dx\" description=\"The coordinate for x-axis position\" unit=\"meters\"</field>\n";
			line += "  <field>Index=8 type=\"double\" name=\"dy\" description=\"The coordinate for y-axis position\" unit=\"meters\"</field>\n";
			line += "  <field>Index=9 type=\"double\" name=\"dz\" description=\"The coordinate for z-axis position\" unit=\"meters\"</field>\n";
			line += "  <field>Index=10 type=\"double\" name=\"second\" description=\"The second for drop in home\" unit=\"second\"</field>\n";
			line += "</header>\n<data>\n";
			writer.write(line);
			writer.flush();
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}
	}
	
	public Logger(String name){
		logFile = new File(name);
		try {
			writer = new BufferedWriter(new FileWriter(logFile));
			String line = "<simulation>\n<header>\n";
			line += "  <field>Index=1 type=\"double\" name=\"timeStamp\" description=\"the time since the beginning of the simulation\" unit=\"seconds\"</field>\n";
			line += "  <field>Index=2 type=\"int\" name=\"agentType\" description=\"the type of this agent\"</field>\n";
			line += "  <field>Index=3 type=\"int\" name=\"agentId\" description=\"the id of this agent\" </field>\n";
			line += "  <field>Index=4 type=\"double\" name=\"px\" description=\"The coordinate for x-axis position\" unit=\"meters\"</field>\n";
			line += "  <field>Index=5 type=\"double\" name=\"py\" description=\"The coordinate for y-axis position\" unit=\"meters\"</field>\n";
			line += "  <field>Index=6 type=\"double\" name=\"pz\" description=\"The coordinate for z-axis position\" unit=\"meters\"</field>\n";
			line += "  <field>Index=7 type=\"double\" name=\"dx\" description=\"The coordinate for x-axis position\" unit=\"meters\"</field>\n";
			line += "  <field>Index=8 type=\"double\" name=\"dy\" description=\"The coordinate for y-axis position\" unit=\"meters\"</field>\n";
			line += "  <field>Index=9 type=\"double\" name=\"dz\" description=\"The coordinate for z-axis position\" unit=\"meters\"</field>\n";
			line += "  <field>Index=10 type=\"double\" name=\"second\" description=\"The second for drop in home\" unit=\"second\"</field>\n";
			line += "</header>\n<data>\n";
			writer.write(line);
			writer.flush();
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}
	}

	public void log(String line) {
		try {
			writer.write(line);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}

	}

	public void step(final SimState state) {
		Bag objs = ((Sim) state).forest.allObjects;
		AgentPortrayal tmpObj;
		double tmpTimeStamp = 0;
		int tmpId = 0;
		Double2D tmpLoc;
		Double2D tmpDir;
		String line = "";
		for (int i = 0; i < objs.numObjs; i++) {
			tmpObj = (AgentPortrayal) objs.objs[i];
			if (tmpObj.getAgentType() == 1) {
				tmpTimeStamp = tmpObj.getTimeStamp();
				tmpId = tmpObj.getAgentId();
				tmpLoc = tmpObj.getLoc();
				tmpDir = tmpObj.getDir();
				line = "1:" + tmpTimeStamp + " 2:" + tmpId + " 3:" + tmpLoc.x
						+ " 4:" + tmpLoc.y + " 5:0" + " 6:" + tmpDir.x + " 7:"
						+ tmpDir.y + " 8:0";
				try {

					writer.write(line);
					writer.newLine();
					writer.flush();
				} catch (IOException e) {
					System.err.println(e);
					System.exit(1);
				}
			}
		}

	}
	
	public void setFileName(String name){
		this.fileName = name;
	}
}// end of the class Logger
