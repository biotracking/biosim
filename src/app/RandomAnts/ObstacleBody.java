package app.RandomAnts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.util.Double2D;
import sim.util.MutableDouble2D;
import core.basic_api.AgentController;

public class ObstacleBody extends Adapter {
	private MutableDouble2D loc;
	private double radius;

	public ObstacleBody() {
		this.loc = new MutableDouble2D(100 * Math.random() - 50, 100 * Math
				.random() - 50);
		radius = 0.002;
		this.setId(-1);
		this.setAgentType(-1);
	}

	public ObstacleBody(double x, double y, double r, int i, int t) {
		this.loc = new MutableDouble2D(x, y);
		radius = r;
		this.setId(i);
		this.setAgentType(t);
	}

	public boolean tag(int type, double x, double y) {
		return false;
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

	public void step(final SimState state) {
		((BioSim) state).forest.setObjectLocation(this, new Double2D(loc.x,
				loc.y));
	}

	public void draw(Object object, final Graphics2D g, final DrawInfo2D info) {
		// public void draw(final Graphics2D g){
		g.setColor(Color.blue);
		double radius2 = radius * getScaler() * info.draw.width;
		g.fillRect((int) (info.draw.x - radius2),
				(int) (info.draw.y - radius2), (int) (2 * radius2),
				(int) (2 * radius2));
	}

	public ArrayList<Double2D> getAgents(int agentType) {
		return null;
	}

	public ArrayList<Double2D> getObjects() {
		return null;
	}

}// end of the class ObstacleBody
