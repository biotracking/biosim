package core.basic_api;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.util.Double2D;

public interface AgentBody {

	public Double2D getLoc();

	public Double2D getDir();

	public double getDorpPeryProbility();

	public double getSubduedProbility();

	public double getDirAngle();

	public double getDistanceOfPerception();

	public ArrayList<Double2D> getObjects();

	public ArrayList<Double2D> getAgents(int agentType);

	public ArrayList<Double2D> getAgentsVector(int agentType);

	public double getTimeStamp();

	public void updatePerception(SimState state);

	public void setDesiredSpeed(double dsrSpeed);

	public void setDesiredTurnRate(double dsrTurnRate);

	public void takeAct();

	public void takeStep(final SimState state);

	public int getAgentId();

	public void setController(AgentController controller);

	public int tag(int t, double x, double y);

	public void releaseToHome(int type);

	// public void releaseInLoc(int type);

	// public Double2D getHomeDirection();

	// public void getNewDirection(double val_t, double val_x, double val_y);

	public double getNewVector(double val_x, double val_y);

	// public boolean atLocation(Vec2 val, double destance);

	public void draw(Object object, final Graphics2D g, final DrawInfo2D info);

	public int getAgentType();

	// public ArrayList<Double2D> nearPetry();

	// public ArrayList<Double2D> nearAnt();

	public void setSubdueAgent(int type, boolean isSubdue);

	// public boolean touchObject();

	public boolean getAntThingInGripper();

	public int getTypeInGripper();

	public boolean getIsBumped();

	public void setBumped(boolean b);

	public void setDisplayColor(Color ac);

	public void setDisplayText(String text, double x, double y, Color magenta);

}// end of the interface AgentBody
