package core.basic_api;

import java.awt.Color;

import core.tools.Logger;
import core.basic_api.Sim;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.util.MutableDouble2D;

public abstract class AgentPortrayal extends OvalPortrayal2D implements
		Steppable, AgentBody, AbstractRobot {

	// class: type, id, size, speed_limit, turn_limit, direction, color, text
	// state: tagging, tagged, bump, move, direction_angle, alive, subdue,
	// gripper,
	// world: meter, second

	// public double Pixel2Meter= 2.54/(100*144);
	// public double Meter2Pixel= (100*144)/2.54;
	private double scaler = DrawAgentBody.scaler;

	private int timeStamp;
	// private double scaler = 2000;// pixels for one meter.
	private static Logger logger = new Logger();
	private boolean logging = false;

	// agent class
	private int agentId;
	private int agentType;
	private double agentSize;
	private double agentMaxSpeed;
	private double agentMaxTurn;
	private MutableDouble2D agentdirection;
	private Color agentColor = Color.black;
	private String agentText = "";
	private double[] agentTextLoc = { 0, 0 };
	private Color agentTextColor = Color.red;
	private AgentController controller;

	// agent state
	private boolean tagging = false;
	private int taggerId = -1;
	private boolean tagged = false;
	private int taggeeId = -1;
	private boolean isBumped = false;
	private boolean isMoving = false;
	private double direction_angle;
	private boolean alive = true;
	private boolean subdued = false;
	private int gripper = 0; // 0 close, 1 open, -1 trigger

	private boolean getButDrop = false; // ??

	public AgentPortrayal() {
		agentId = -1;
		agentType = -1;
		timeStamp = 0;
	}

	public AgentPortrayal(int i, int at) {// double lx, double ly, double dx,
		// double
		agentId = i;
		agentType = at;
		timeStamp = 0;
	}

	public void setAgentId(int i) {
		this.agentId = i;
	}

	public int getAgentId() {
		return this.agentId;
	}

	public void setAgentType(int at) {
		this.agentType = at;
	}

	public int getAgentType() {
		return this.agentType;
	}

	public void setAgentSize(double AS) {
		this.agentSize = AS;
	}

	public double getAgentSize() {
		return this.agentSize;
	}

	public void setAgentMaxSpeed(double AMS) {
		this.agentMaxSpeed = AMS;
	}

	public double getAgentMaxSpeed() {
		return this.agentMaxSpeed;
	}

	public void setAgentMaxTurn(double AMT) {
		this.agentMaxTurn = AMT;
	}

	public double getAgentMaxTurn() {
		return this.agentMaxTurn;
	}

	public void setAgentdirection(MutableDouble2D AD) {
		this.setDirectionAngle(AD.angle());
		this.agentdirection = AD;
	}

	public MutableDouble2D getAgentdirection() {
		return this.agentdirection;
	}

	public void setDisplayColor(Color ac) {
		this.agentColor = ac;
	}

	public Color getDisplayColor() {
		return this.agentColor;
	}
	
	public void DisplayNearAgent(String text, Color tc){
		this.agentText = text;
		this.agentTextLoc[0] = this.getLoc().x;
		this.agentTextLoc[1] = this.getLoc().y;
		this.agentTextColor = tc;		
	}

	public void setDisplayText(String text, double tx, double ty, Color tc) {
		this.agentText = text;
		this.agentTextLoc[0] = tx;
		this.agentTextLoc[1] = ty;
		this.agentTextColor = tc;
	}

	public String getDisplayText() {
		return this.agentText;
	}

	public double[] getDisplayTextLoc() {
		return this.agentTextLoc;
	}

	public Color getDisplayTextColor() {
		return this.agentTextColor;
	}

	public void setController(AgentController controller) {
		this.controller = controller;
	}

	public void setTagged(boolean t) {
		this.tagged = t;
	}

	public boolean getTagged() {
		return this.tagged;
	}

	public void setTagging(boolean t) {
		this.tagging = t;
	}

	public boolean getTagging() {
		return this.tagging;
	}

	public void setTaggerId(int d) {
		this.taggerId = d;
	}

	public int getTaggerId() {
		return this.taggerId;
	}

	public void setTaggeeId(int d) {
		this.taggeeId = d;
	}

	public int getTaggeeId() {
		return this.taggeeId;
	}

	public void setBumped(boolean bump) {
		this.isBumped = bump;
	}

	public boolean getIsBumped() {
		return this.isBumped;
	}

	public void setIsMoving(boolean moving) {
		this.isMoving = moving;
	}

	public boolean getIsMoving() {
		return this.isMoving;
	}

	public void setDirectionAngle(double DA) {
		this.direction_angle = DA;
	}

	public double getDirectionAngle() {
		return this.direction_angle;
	}

	public void setAlive(boolean t) {
		this.alive = t;
	}

	public boolean getAlive() {
		return this.alive;
	}

	public void setSubdued(boolean Subdued) {
		// be subdued is true
		subdued = Subdued;
	}

	public boolean getSubdued() {
		return this.subdued;
	}

	public void setGripper(int G) {
		this.gripper = G;
	}

	public int getGripper() {
		/*
		 * 0 close 1 open -1 trigger
		 */
		return this.gripper;
	}

	public void setGetButDrop(boolean getDrop) {
		getButDrop = getDrop;
	}

	public boolean getGetButDrop() {
		return this.getButDrop;
	}

	public void updateTimeStamp() {
		timeStamp++;
	}

	public double getTimeStamp() {
		return this.timeStamp * core.basic_api.Sim.timeStepSize;
	}

	public void setLogging(boolean flag) {
		this.logging = flag;
	}

	public double getScaler() {
		return this.scaler;
	}

	public void step(final SimState state) {// ggg Dont forget to change 2000 to
		// the scaler
		if(!this.getAlive()) return;
		if (this.logging) {
			String line = "  <row> 1:" + this.getTimeStamp()
					 + " 2:" + this.getAgentType()
					+ " 3:" + this.getAgentId() + " 4:" + this.getLoc().x
					 + " 5:" + this.getLoc().y + " 6:0" + " 7:"
					+ this.getDir().x + " 8:" + this.getDir().y + " 9:0 </row>";
			logger.log(line);
		}//
		this.takeStep(state);
	}

	/*
	 * public void setLoc(MutableDouble2D loc) { this.loc = loc; }//
	 */

	/*
	 * public MutableDouble2D getLoc() { //Double2D loca = new
	 * Double2D(this.loc.x, loc.y); return this.loc; }//
	 */

	/*
	 * public void setDirection(MutableDouble2D dir) { this.direction = dir; }//
	 */

	/*
	 * public MutableDouble2D getDirection() { return this.direction; }//
	 */

}// end of the abstract class Adapter
