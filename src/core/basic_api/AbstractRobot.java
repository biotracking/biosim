package core.basic_api;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.util.Double2D;
import core.util.Vec2;

public interface AbstractRobot {
	
	boolean getAlive();

	public Double2D getLoc();

	public double[] getHomeLoc();

	public Double2D getDir();

	public double getDorpPeryProbility();

	public double getSubduedProbility();

	public double getDirAngle();

	public double getDistanceOfPerception();

	public ArrayList<Double2D> getObjects();

	public ArrayList<Double2D> getAgents(int agentType);

	public ArrayList<Double2D> getAgentsVector(int agentType);

	public double getTimeStamp();
	
	public double getTimeStampSize();

	public void updatePerception(SimState state);

	public void setDesiredSpeed(double dsrSpeed);

	public void setDesiredTurnRate(double dsrTurnRate);

	public void takeAct();

	public void takeStep(final SimState state);

	public int getAgentId();

	public void setController(AgentController controller);

	public int tag(int t, double x, double y);

	public void releaseToHome(int type);

	public double getNewVector(double val_x, double val_y);

	public void draw(Object object, final Graphics2D g, final DrawInfo2D info);

	public int getAgentType();

	public Double2D nearestPetry();

	public Double2D nearestAnt();

	public double getDistanceToLoc(double x, double y);

	public void setSubdueAgent(int type, boolean isSubdue);

	public boolean touchObject();

	public boolean getAntThingInGripper();

	public int getTypeInGripper();

	public boolean getIsBumped();

	public void setBumped(boolean b);

	public void setDisplayColor(Color ac);

	public void setDisplayText(String text, double x, double y, Color magenta);
	
	public void DisplayNearAgent(String text, Color tc);

	public Double2D getHomeDirection();

	public Double2D getNewDirection(Vec2 val);

	public boolean isNotMove();
	
	public boolean checkBound(double xOff, double yOff);

}
