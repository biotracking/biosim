package core.tools;

import java.util.ArrayList;

/**
 * @author hshang9@gatech.edu
 * 
 */
public class SimInfo {
	private double timeStamp;
	private int id, agentType;
	private double px, py, pz;
	private double dx, dy, dz;
	private ArrayList<Interaction> interactionList;

	public SimInfo() {
		timeStamp = -1;
		agentType = -1;
		id = -1;
		px = -1;
		py = -1;
		pz = -1;
		dx = -1;
		dy = -1;
		dz = -1;
		interactionList = new ArrayList<Interaction>();
	}

	public SimInfo(double ts, int at, int id, double x1, double y1, double z1,
			double x2, double y2, double z2) {
		timeStamp = ts;
		agentType = at;
		this.id = id;
		px = x1;
		py = y1;
		pz = z1;
		dx = x2;
		dy = y2;
		dz = z2;
		interactionList = new ArrayList<Interaction>();
	}
	
	public SimInfo(double ts, int at, int id, double x, double y, double theta){
		this.timeStamp = ts;
		this.agentType = at;
		this.id = id;
		this.px = x;
		this.py = y;
		this.pz = 0;
		this.dx = Math.cos(theta);
		this.dy = Math.sin(theta);
		this.dz = 0;
		interactionList = new ArrayList<Interaction>();
	}

	public void setInteractionList(ArrayList<Interaction> ia) {
		for (int i = 0; i < ia.size(); i++) {
			this.interactionList.add(ia.get(i));
		}
	}

	public ArrayList<Interaction> getInteractionList() {
		return this.interactionList;
	}

	public void setTimeStamp(double ts) {
		this.timeStamp = ts;
	}

	public void setAgentType(int at) {
		this.agentType = at;
	}

	public void setId(int i) {
		this.id = i;
	}

	public void setPX(double p_x) {
		this.px = p_x;
	}

	public void setPY(double p_y) {
		this.py = p_y;
	}

	public void setDX(double d_x) {
		this.dx = d_x;
	}

	public void setDY(double d_y) {
		this.dy = d_y;
	}

	public double getTimeStamp() {
		return timeStamp;
	}

	public int getAgentType() {
		return agentType;
	}

	public int getId() {
		return id;
	}

	public double getPX() {
		return px;
	}

	public double getPY() {
		return py;
	}

	public double getPZ() {
		return pz;
	}

	public double getDX() {
		return dx;
	}

	public double getDY() {
		return dy;
	}

	public double getDZ() {
		return dz;
	}

}// end of the class SimInfo
