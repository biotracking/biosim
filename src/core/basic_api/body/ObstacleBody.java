package core.basic_api.body;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.util.Double2D;
import sim.util.MutableDouble2D;
import core.basic_api.AgentController;
import core.basic_api.AgentPortrayal;
import core.basic_api.DrawAgentBody;
import core.basic_api.Sim;
import core.util.Vec2;

public class ObstacleBody extends AgentPortrayal {
	public final double MAX_TRANSLATION = 0; // maximum translation speed
	private MutableDouble2D loc;
	private DrawAgentBody DrawBody;
	public double radius;

	public ObstacleBody() {
		this.loc = new MutableDouble2D(Math.random() + 0.04,
				Math.random() + 0.03);
		radius = 0.002;
		this.setAgentId(-1);
		this.setAgentType(-1);
	}

	public ObstacleBody(double x, double y, double r, int i, int t) {
		this.loc = new MutableDouble2D(x, y);
		radius = r;
		this.setAgentId(i);
		this.setAgentType(t);
		DrawBody = new DrawAgentBody();
		this.setAgentSize(r);
	}

	public int tag(int type, double x, double y) {
		return 0;
	}

	public void releaseToHome(int type) {

	}

	public Double2D getLoc() {
		Double2D loca = new Double2D(loc.x, loc.y);
		return loca;
	}

	public Double2D getDir() {
		Double2D dire = new Double2D(1, 0);
		return dire;
	}

	public Double2D getHomeDirection() {
		return new Double2D(-1, -1);
	}

	public boolean atHome() {
		return false;
	}

	public void setController(AgentController c) {

	}

	public void updatePerception(SimState state) {

	}

	public void setDesiredSpeed(double dsrS) {
	}

	public void setDesiredTurnRate(double dsrT) {
	}

	public void takeAct() {
	}

	public void takeStep(final SimState state) {
		((Sim) state).forest.setObjectLocation(this, new Double2D(loc.x
				* this.getScaler(), loc.y * this.getScaler()));
		// ((Sim)state).forest.setObjectLocation(this, new Double2D(loc.x,
		// loc.y));
	}

	public void draw(Object object, final Graphics2D g, final DrawInfo2D info) {
		// public void draw(final Graphics2D g){

		// double radius2 = radius;//*(this.Meter2Pixel/2);//*
		// info.draw.width;// getScaler()* info.draw.width;
		// g.fillRect((int)(info.draw.x-radius2),(int)(info.draw.y-radius2),
		// (int)(2*radius2),(int)(2*radius2));

		this.setDisplayColor(Color.blue);
		// this.setAgentSize(radius);
		double agentSize = this.getAgentSize();
		DrawBody.draw(this.getAgentType(), object, g, info, this
				.getDisplayColor(), this.getDisplayTextColor(), this
				.getDisplayTextLoc(), this.getDisplayText(), this.getDir(),
				this.getDirAngle(), agentSize);
	}

	public ArrayList<Double2D> getAgents(int agentType) {
		return null;
	}

	public ArrayList<Double2D> getObjects() {
		return null;
	}

	public void setObstacleMaxRange(double range) {
		radius = range;
	}

	public boolean atLocation(Vec2 val, double destance) {
		double Lotmpx = Math.abs((val.x - loc.x));
		double Lotmpy = Math.abs((val.y - loc.y));
		return (Math.sqrt(Lotmpx * Lotmpx + Lotmpy * Lotmpy) < destance);
	}

	public Double2D getNewDirection(Vec2 val) {
		double angleBetweenDirection = Math.atan2((val.y - loc.y),
				(val.x - loc.x));
		// angleBetweenDirection -= this.direction.angle();
		if (angleBetweenDirection > Math.PI)
			angleBetweenDirection -= 2 * Math.PI;
		if (angleBetweenDirection < -Math.PI)
			angleBetweenDirection += 2 * Math.PI;
		double x = Math.sin(angleBetweenDirection);
		double y = Math.cos(angleBetweenDirection);
		return new Double2D(x, y);
	}

	private double base_speed = MAX_TRANSLATION;

	public void setBaseSpeed(double speed) {
		if (speed > MAX_TRANSLATION)
			speed = MAX_TRANSLATION;
		else if (speed < 0)
			speed = 0;
		base_speed = speed;
		setDesiredSpeed(base_speed);
	}

	@Override
	public double getDirAngle() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getNewVector(double val) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDistanceOfPerception() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ArrayList<Double2D> getAgentsVector(int agentType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getAntThingInGripper() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getDorpPeryProbility() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getNewVector(double valX, double valY) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getSubduedProbility() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTypeInGripper() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setSubdueAgent(int type, boolean isSubdue) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getDistanceToLoc(double x, double y) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double[] getHomeLoc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double2D nearestAnt() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double2D nearestPetry() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean touchObject() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNotMove() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getTimeStampSize() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public boolean checkBound(double x, double y){
		return true;
	}

}// end of the class ObstacleBody
