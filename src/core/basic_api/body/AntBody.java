package core.basic_api.body;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.util.Bag;
import sim.util.Double2D;
import sim.util.MutableDouble2D;
import core.basic_api.AgentController;
import core.basic_api.AgentPortrayal;
import core.basic_api.BoundChecker;
import core.basic_api.DrawAgentBody;
import core.basic_api.ObstacleCollisionChecker;
import core.basic_api.Sim;
import core.util.Vec2;

/**
 * @author Hai Shang
 * @author hshang9@gatech.edu
 * @see Define the specific function of ants body
 */
public class AntBody extends AgentPortrayal {
	
	private AgentController controller;
	private double distanceOfPerception = 0; // the distance of ant detect.
	private double probabilityOfTagging = 0;
	private double[] homeLoc = {0.15, 0.008};
	private double dorpProbability = 0.0; // the probability of drop,
	private MutableDouble2D loc;
	private MutableDouble2D direction;
	private Bag perception;
	private ArrayList<Double2D> objects;
	// private ArrayList<Double2D> agents;
	private double speed;
	private double turnRate;
	private double dsrSpeed;
	private double dsrTurnRate;
	private double vx;
	private double vy;
	private double vt;
	private boolean usedSetVelocities = false;
	public boolean toroidal = false;
	private double timeStepSize;
	private boolean isAnyThingInGripper = false;
	private int typeInGripper = 0;
	private boolean isItDrop = false;
	private DrawAgentBody DrawBody;
	private BoundChecker bc;
	private ObstacleCollisionChecker occ;
	
	private double maxSpeed = 0.005;
	private double maxTurnRate = 1;
	private double maxAc = Math.PI/1.5;
	private double maxTAc = Math.PI/3;
	
	private double maxVX=0.1, maxVY=0.1, maxVT=Math.PI/3;

	public AntBody() {
		this.loc = new MutableDouble2D(Math.random() + 0.04,
				Math.random() + 0.03);
		double ran = Math.random();
		this.direction = new MutableDouble2D(Math.sin(22 / 7 * (ran - 0.5)),
				Math.cos(22 / 7 * (ran - 0.5)));
		perception = new Bag();
		speed = 0;
		turnRate = 0;
		objects = new ArrayList<Double2D>();
	}

	public AntBody(double x, double y, double d_x, double d_y, int i, int t,
			double size) {
		this.loc = new MutableDouble2D(x, y);
		double temp = Math.sqrt(d_x * d_x + d_y * d_y);
		this.direction = new MutableDouble2D(d_x / temp, d_y / temp);
		perception = new Bag();
		speed = 0;
		turnRate = 0;
		this.setAgentId(i);
		this.setAgentType(t);
		objects = new ArrayList<Double2D>();
		DrawBody = new DrawAgentBody();
		bc = new BoundChecker();
		occ = new ObstacleCollisionChecker();
		this.setAgentSize(size);
		distanceOfPerception = this.getAgentSize()*3;
	}

	private boolean drawLine;
	private Color defLineColor;
	private double[] defPoint;
	public void setDefLine(boolean drawLine){ this.drawLine = drawLine; }
	public boolean getDefLine(){ return drawLine; }
	public void setDefLineColor(Color color){ defLineColor = color; }
	public Color getDefLineColor(){ return defLineColor; }
	public void setDefPoint(double[] defPoint){ this.defPoint = defPoint; }
	public double[] getDefPoint(){return defPoint;}

	public void setController(AgentController c) {
		this.controller = c;
	}

	public void setVisible(boolean torf){ DrawBody.isVis = torf; }

	public double[] getHomeLoc() {
		return homeLoc;
	}

	public void setDorpPeryProbility(double DPP) {
		dorpProbability = DPP;
	}

	public double getDorpPeryProbility() {
		return this.dorpProbability;
	}

	public Double2D getLoc() {
		Double2D loca = new Double2D(this.loc.x, loc.y);
		return loca;
	}// */

	public Double2D getDir() { // getHead
		Double2D dire = new Double2D(direction.x, direction.y);
		return dire;
	}// */

	public double getDirAngle() { // getHeading
		return direction.angle();
	}// */

	public double getDistanceOfPerception() {
		return distanceOfPerception;
	}

	public void setDesiredSpeed(double dsrS) {
		this.dsrSpeed = dsrS;
		this.usedSetVelocities = false;
	}

	public void setDesiredTurnRate(double dsrT) {
		this.dsrTurnRate = dsrT;
		this.usedSetVelocities = false;
	}

	public void setDesiredVelocities(double vx, double vy, double vt){
		this.usedSetVelocities = true;
		this.vx = vx;
		this.vy = vy;
		this.vt = vt;
	}

	public double getDistanceToLoc(double x, double y) {
		double distance = 0;
		distance = this.getLoc().distance(x, y);
		// distance = Math.abs(Math.atan2(y-loc.y, x-loc.x));
		return distance;
	}

	public Double2D getHomeDirection() {
		double angleBetweenDirection = Math.atan2(
				(this.getHomeLoc()[0] - loc.y), (this.getHomeLoc()[1] - loc.x));
		angleBetweenDirection -= this.direction.angle();
		if (angleBetweenDirection > Math.PI)
			angleBetweenDirection -= 2 * Math.PI;
		if (angleBetweenDirection < -Math.PI)
			angleBetweenDirection += 2 * Math.PI;
		double x = Math.sin(angleBetweenDirection);
		double y = Math.cos(angleBetweenDirection);
		return new Double2D(x, y);
	}

	public void getNewDirection(double val_t, double val_x, double val_y) {
		double angleBetweenDirection = Math.atan2(val_y, val_x);
		angleBetweenDirection = angleBetweenDirection + this.direction.angle();
		if (angleBetweenDirection > Math.PI)
			angleBetweenDirection = angleBetweenDirection - 2 * Math.PI;
		if (angleBetweenDirection < -Math.PI)
			angleBetweenDirection = angleBetweenDirection + 2 * Math.PI;
	}// */

	public double lastAngle;

	public double getNewVector(double val_x, double val_y) {
		double angleBetweenDirection = Math.atan2(val_y, val_x);

		angleBetweenDirection = angleBetweenDirection - this.direction.angle();
		if (angleBetweenDirection > Math.PI)
			angleBetweenDirection = angleBetweenDirection - 2 * Math.PI;
		if (angleBetweenDirection < -Math.PI)
			angleBetweenDirection = angleBetweenDirection + 2 * Math.PI;
		if (Math.abs(angleBetweenDirection - lastAngle) > 6)
			angleBetweenDirection = lastAngle;
		else
			lastAngle = angleBetweenDirection;
		return angleBetweenDirection;
	}// */

	public boolean atHome(double locx, double locy, double range) {
		return (locx - range < loc.x && loc.x < locx + range
				&& locy - range < loc.y && loc.y < locy + range);
	}// */

	public void releaseToHome(int type) {
		if (!getTagging())
			return;
		// System.out.print("antBody-> releaseToHome taggeeID= "+getTaggeeId()+"\n");
		for (int i = 0; i < perception.numObjs; i++) {
			if (((AgentPortrayal) perception.objs[i]).getAgentType() == type
					&& (((AgentPortrayal) perception.objs[i]).getAgentId() == getTaggeeId())
					&& ((AgentPortrayal) perception.objs[i]).getTagged()) {
				// System.out.print("releaseToHome-> \n");
				((AgentPortrayal) perception.objs[i]).setTagged(false);
				((AgentPortrayal) perception.objs[i]).setTaggerId(-1);
				setTagging(false);
				setTaggeeId(-1);
				((AgentPortrayal) perception.objs[i]).setAlive(false);
				this.setAnyThingInGripper(false, 0);
				this.setBumped(false);
				return;
			}// end of the botteam if..
		}// end of the loop
	}

	public void releaseInLoc(int type) {
		if (!getTagging())
			return;
		// System.out.print("antBody-> releaseInLoc taggeeID= "+getTaggeeId()+"\n");
		for (int i = 0; i < perception.numObjs; i++) {

			if (((AgentPortrayal) perception.objs[i]).getAgentType() == type
					&& (((AgentPortrayal) perception.objs[i]).getAgentId() == getTaggeeId())
					&& ((AgentPortrayal) perception.objs[i]).getTagged()) {
				// System.out.print("releaseInLoc-> \n");
				((AgentPortrayal) perception.objs[i]).setTagged(false);
				((AgentPortrayal) perception.objs[i]).setTaggerId(-1);
				setTagging(false);
				setTaggeeId(-1);
				((AgentPortrayal) perception.objs[i]).setGetButDrop(true);
				((AgentPortrayal) perception.objs[i]).setSubdued(false);
				((AgentPortrayal) perception.objs[i]).setAlive(true);
				this.setAnyThingInGripper(false, 0);
				this.setBumped(false);
				return;
			}// end of the botteam if..
		}// end of the loop
	}// */

	public Double2D nearestPetry() {
		ArrayList<Double2D> preysNear = this.getAgentsVector(2);
		Double2D temp = new Double2D();
		Double2D nearest = new Double2D();
		double closest = 99999999;
		for (int i = 0; i < preysNear.size(); i++) {
			if (preysNear.get(i) != null) {
				if (preysNear.get(i).distance(loc.x, loc.y) < closest) {
					closest = preysNear.get(i).distance(loc.x, loc.y);
					temp = preysNear.get(i);
					nearest = new Double2D(loc.x + temp.x, loc.y + temp.y);
				}
			}
		}
		return nearest;
	}// */

	public Double2D nearestAnt() {
		ArrayList<Double2D> antNear = this.getAgentsVector(1);
		Double2D temp = new Double2D();
		Double2D nearest = new Double2D();
		double closest = 99999999;
		for (int i = 0; i < antNear.size(); i++) {
			if (antNear.get(i) != null) {
				if (antNear.get(i).distance(loc.x, loc.y) < closest) {
					closest = antNear.get(i).distance(loc.x, loc.y);
					temp = antNear.get(i);
					nearest = new Double2D(loc.x + temp.x, loc.y + temp.y);
				}
			}
		}
		return nearest;
	}// */

	public ArrayList<Double2D> getAgents(int agentType) {
		ArrayList<Double2D> vectors = new ArrayList<Double2D>();
		double distanceToAgent = 0.0;
		double angleBetweenDirection = 0.0;
		double x = 0.0;
		double y = 0.0;
		for (int i = 0; i < perception.numObjs; i++) {
			// choose other predators
			if (((AgentPortrayal) perception.objs[i]).getAgentType() == agentType
					&& (((AgentPortrayal) perception.objs[i]).getAgentId() != this
							.getAgentId() || agentType != getAgentType())
					// && !(((AgentPortrayal)perception.objs[i]).getTagged())
					//&& ((AgentPortrayal) perception.objs[i]).getAlive()
							) {
				// the distance between center of this predator and the nearest
				// point other predator.
				distanceToAgent = ((AgentPortrayal) perception.objs[i]).getLoc().distance(loc.x, loc.y);

				angleBetweenDirection = Math.atan2(
								(((AgentPortrayal) perception.objs[i]).getLoc().y - loc.y),
								(((AgentPortrayal) perception.objs[i]).getLoc().x - loc.x));
				angleBetweenDirection -= direction.angle();
				if (angleBetweenDirection > Math.PI)
					angleBetweenDirection -= 2 * Math.PI;
				if (angleBetweenDirection < -Math.PI)
					angleBetweenDirection += 2 * Math.PI;
				if (angleBetweenDirection > Math.PI / 2
						|| angleBetweenDirection < -Math.PI / 2)
					continue;
				x = Math.sin(angleBetweenDirection) * distanceToAgent;
				y = Math.cos(angleBetweenDirection) * distanceToAgent;
				Double2D newVector = new Double2D(x, y);
				vectors.add(newVector);
			}// end of the botteam if..
		}// end of the loop
		return vectors;
	}

	public ArrayList<Double2D> getAgentsVector(int agentType) {
		ArrayList<Double2D> vectors = new ArrayList<Double2D>();
		double distanceToAgent = 0.0;
		double angleBetweenDirection = 0.0;
		double x = 0.0;
		double y = 0.0;
		for (int i = 0; i < perception.numObjs; i++) {
			// choose other predators
			if (((AgentPortrayal) perception.objs[i]).getAgentType() == agentType
					&& (((AgentPortrayal) perception.objs[i]).getAgentId() != this
							.getAgentId() || agentType != getAgentType())
					// && !(((AgentPortrayal)perception.objs[i]).getTagged())
					&& ((AgentPortrayal) perception.objs[i]).getAlive()) {
				// the distance between center of this predator and the nearest
				// point other predator.
				distanceToAgent = ((AgentPortrayal) perception.objs[i])
						.getLoc().distance(loc.x, loc.y);
				angleBetweenDirection = Math.atan2(
								(((AgentPortrayal) perception.objs[i]).getLoc().y - loc.y),
								(((AgentPortrayal) perception.objs[i]).getLoc().x - loc.x));
				angleBetweenDirection -= this.direction.angle();
				if (angleBetweenDirection > Math.PI)
					angleBetweenDirection -= 2 * Math.PI;
				if (angleBetweenDirection < -Math.PI)
					angleBetweenDirection += 2 * Math.PI;
				if (angleBetweenDirection > Math.PI / 2
						|| angleBetweenDirection < -Math.PI / 2)
					continue;
				x = ((AgentPortrayal) perception.objs[i]).getLoc().x - loc.x;
				y = ((AgentPortrayal) perception.objs[i]).getLoc().y - loc.y;
				Double2D newVector = new Double2D(x, y);
				vectors.add(newVector);
			}// end of the botteam if..
		}// end of the loop
		return vectors;
	}// */

	public ArrayList<Double2D> getObjects() {
		ArrayList<Double2D> objs = new ArrayList<Double2D>();
		double distanceToAgent = 0.0;
		double angleBetweenDirection = 0.0;
		double x = 0.0;
		double y = 0.0;
		for (int i = 0; i < perception.numObjs; i++) {
			// choose other predators
			if (((AgentPortrayal) perception.objs[i]).getAgentId() != this
					.getAgentId()) {
				// the distance between center of this predator and the nearest
				// point other predator.
				distanceToAgent = ((AgentPortrayal) perception.objs[i])
						.getLoc().distance(loc.x, loc.y);
				angleBetweenDirection = Math
						.atan2(
								(((AgentPortrayal) perception.objs[i]).getLoc().y - loc.y),
								(((AgentPortrayal) perception.objs[i]).getLoc().x - loc.x));
				angleBetweenDirection -= this.direction.angle();
				if (angleBetweenDirection > Math.PI)
					angleBetweenDirection -= 2 * Math.PI;
				if (angleBetweenDirection < -Math.PI)
					angleBetweenDirection += 2 * Math.PI;
				if (angleBetweenDirection > Math.PI / 2
						|| angleBetweenDirection < -Math.PI / 2)
					continue;
				x = Math.sin(angleBetweenDirection) * distanceToAgent;
				y = Math.cos(angleBetweenDirection) * distanceToAgent;
				Double2D newVector = new Double2D(x, y);
				objs.add(newVector);
			}// end of the botteam if..
		}// end of the loop
		return objs;
	}

/*
x, y is the vector from the new location to current location. For example, if 
the body is at (3,3) and want to go to (4,5), then the input parameters for the
checkCollision function should be (1,2). If there is collision, return false. Otherwise, return true
*/
	private boolean checkCollision(double x, double y) {
		
		double tempX = loc.x, tempY = loc.y;
		tempX += x;
		tempY += y;
		
		AgentPortrayal tmp;
		double distanceToAgent = 0;
		for (int i = 0; i < perception.numObjs; i++) {// choose other predators
			tmp = (AgentPortrayal) perception.objs[i];
			if (tmp.getAgentType() != this.getAgentType() || tmp.getAgentId() != this.getAgentId()) {
				distanceToAgent = tmp.getLoc().distance(tempX, tempY);
				if(distanceToAgent < this.getAgentSize()/2 + tmp.getAgentSize()/2) return false;
			}
		}
		if(! occ.checkCollision(tempX, tempY, this.getAgentSize()/2)) return false;
		return true;
		
	}

	public boolean touchObject() {
		ArrayList<Double2D> targetAnt = this.getAgents(1);// ANT
		ArrayList<Double2D> targetPrey = this.getAgents(2);// Prey
		boolean succ = false;
		int tagType = 0, tempType = 0;
		double targetX = 0;
		double targetY = 0;

		if (!this.getTagging() && !this.isAnyThingInGripper) {
			if (targetPrey.size() > 0) {
				targetX = targetPrey.get(0).getX();
				targetY = targetPrey.get(0).getY();
			} else if (targetAnt.size() > 0) {
				targetX = targetAnt.get(0).getX();
				targetY = targetAnt.get(0).getY();
			} else {
				targetX = 999;
				targetY = 999;
			}
			if (Math.sqrt(targetX * targetX + targetY * targetY) > 0.01) {
				succ = false;
			} else {
				if (targetPrey.size() > 0.01) {// some prey inside
					if (targetPrey.size() < 13) {
						tempType = 2;
					}
				} else if (targetAnt.size() > 0.01) {// some ant inside
					// if(targetAnt.size() < 13){(100*144)/2.54
					tempType = 1;
				} else {// no ant inside, release itself
					this.setTagged(false);
					this.setTaggeeId(-1);
					this.setBumped(false);
					succ = false;
					return succ;
				}
				tagType = this.tag(tempType, targetX, targetY);
				if (tagType == tempType) {
					// this.setAnyThingInGripper(true, 2);
					succ = true;
				}
			}
		}
		// System.out.print("touchObject-> " +this.getAgentId()+": "+
		// this.getIsBumped() + "\n");
		return succ;
	}

	public int tag(int agentType, double xOff, double yOff) {
		if (agentType == 2) // tag prey
		{
			if (Math.sqrt(xOff * xOff + yOff * yOff) > 0.01)
				return 0;// too far
			double distanceToAgent = 0, angleBetweenDirection = 0;
			double tempPro = Math.random();
			double x = 0, y = 0;
			for (int i = 0; i < perception.numObjs; i++) {
				// agentType, ID or not same agent
				if (((AgentPortrayal) perception.objs[i]).getAgentType() == agentType
						&& ((AgentPortrayal) perception.objs[i]).getAlive()
						// && (((AgentPortrayal)perception.objs[i]).getAgentId() != this.getAgentId() || agentType != getAgentType())
						&& (tempPro < ((AgentPortrayal) perception.objs[i]).getSubduedProbility())
						&& !(((AgentPortrayal) perception.objs[i]).getTagged())) {
					// the distance between center of this predator and the nearest point other predator.
					distanceToAgent = ((AgentPortrayal) perception.objs[i]).getLoc().distance(loc.x, loc.y);
					angleBetweenDirection = Math.atan2((((AgentPortrayal) perception.objs[i]).getLoc().y - loc.y),(((AgentPortrayal) perception.objs[i]).getLoc().x - loc.x));
					angleBetweenDirection -= this.direction.angle();
					if (angleBetweenDirection > Math.PI)
						angleBetweenDirection -= 2 * Math.PI;
					if (angleBetweenDirection < -Math.PI)
						angleBetweenDirection += 2 * Math.PI;
					if (angleBetweenDirection > Math.PI / 2 || angleBetweenDirection < -Math.PI / 2)
						continue;
					x = Math.sin(angleBetweenDirection) * distanceToAgent;
					y = Math.cos(angleBetweenDirection) * distanceToAgent;
					if(Math.sqrt(((x-xOff)*(x-xOff))+((y-yOff)*(y-yOff)))<0.002){
//					if (Math.abs(x - xOff) < 0.001 && Math.abs(y - yOff) < 0.001) {
						((AgentPortrayal) perception.objs[i]).setTagged(true);
						((AgentPortrayal) perception.objs[i]).setTaggerId(this.getAgentId());
						((AgentPortrayal) perception.objs[i]).setSubdued(true);
						setTagging(true);
						setTaggeeId(((AgentPortrayal) perception.objs[i]).getAgentId());
						getSubduedProbility(agentType);
						this.setBumped(false);
						this.setAnyThingInGripper(true,((AgentPortrayal) perception.objs[i]).getAgentType());
						return agentType;
					}
				}
			}

			if (this.getTagging())
				return agentType;
			else
				return 0;
		} else // bump ants
		{
			if (Math.sqrt(xOff * xOff + yOff * yOff) > 0.01)
				return 0;
			double distanceToAgent = 0, angleBetweenDirection = 0;
			double x = 0, y = 0;
			for (int i = 0; i < perception.numObjs; i++) {
				// choose other predators
				if (((AgentPortrayal) perception.objs[i]).getAgentType() == agentType
						&& (((AgentPortrayal) perception.objs[i]).getAgentId() != this.getAgentId() || agentType != getAgentType())
						&& !(((AgentPortrayal) perception.objs[i]).getTagged())) {
					distanceToAgent = ((AgentPortrayal) perception.objs[i]).getLoc().distance(loc.x, loc.y);
					angleBetweenDirection = Math.atan2((((AgentPortrayal) perception.objs[i]).getLoc().y - loc.y),(((AgentPortrayal) perception.objs[i]).getLoc().x - loc.x));
					angleBetweenDirection -= this.direction.angle();
					if (angleBetweenDirection > Math.PI)
						angleBetweenDirection -= 2 * Math.PI;
					if (angleBetweenDirection < -Math.PI)
						angleBetweenDirection += 2 * Math.PI;
					if (angleBetweenDirection > Math.PI / 2 || angleBetweenDirection < -Math.PI / 2)
						continue;
					x = Math.sin(angleBetweenDirection) * distanceToAgent;
					y = Math.cos(angleBetweenDirection) * distanceToAgent;
					if (Math.abs(x - xOff) < 0.007 && Math.abs(y - yOff) < 0.007) {
						if (!this.getIsMoving()) // I am not moving
							this.setBumped(true);
						else if (!((AgentPortrayal) perception.objs[i]).getIsMoving()) // he is not moving
							((AgentPortrayal) perception.objs[i]).setBumped(true);
						else // we are all moving
						{ // chose one of us
							if (this.getAgentId() > ((AgentPortrayal) perception.objs[i]).getAgentId())
								((AgentPortrayal) perception.objs[i]).setBumped(true);
							else
								this.setBumped(true);
						}
						return agentType;
					}
				}
			}
			if (this.getIsBumped())
				return agentType;
			else
				return 0;
		}
	}// */

	public void setAnyThingInGripper(boolean getThing, int type) {
		typeInGripper = type;
		isAnyThingInGripper = getThing;
	}

	public boolean getAntThingInGripper() {
		return isAnyThingInGripper;
	}

	public int getTypeInGripper() {
		return typeInGripper;
	}

	// public void gripperSensor()
	// {
	// double distanceToAgent = 0;
	// for(int i=0; i<perception.numObjs; i++)
	// {
	// distanceToAgent =
	// ((AgentPortrayal)perception.objs[i]).getLoc().distance(loc.x, loc.y);
	// if(this.atHome(this.homeLoc[0], this.homeLoc[1],0.007*this.Meter2Pixel)
	// && isAnyThingInGripper == true)
	// {
	// isAnyThingInGripper = false;
	// typeInGripper = -1;
	// System.out.print("no in gripper!" + distanceToAgent +"\n");
	// }
	// else if(!isNotMove()
	// &&(distanceToAgent<0.004*this.Meter2Pixel)
	// && distanceToAgent > 0)
	// {
	// System.out.print("in gripper!" + distanceToAgent +"\n");
	// }
	// }
	// }

	public void getSubduedProbility(int agentType) // the probability of agent
	// who is subdued
	{
		for (int i = 0; i < perception.numObjs; i++) {
			// choose other
			if (((AgentPortrayal) perception.objs[i]).getAgentType() == agentType
					&& (((AgentPortrayal) perception.objs[i]).getAgentId() != this.getAgentId() || agentType != getAgentType())
					&& (((AgentPortrayal) perception.objs[i]).getTagged())) {
				probabilityOfTagging = ((AgentPortrayal) perception.objs[i]).getSubduedProbility();
			}
		}
		// System.out.print("getSubduedProbility, "+probabilityOfTagging+" \n");
		// return tempS;
	}

	public boolean setDropAgent(int type) {
		boolean tempDrop = false;
		double tempfdrop = Math.random();
		for (int i = 0; i < perception.numObjs; i++) {
			if (((AgentPortrayal) perception.objs[i]).getAgentType() == type
					&& (getAntThingInGripper())
					&& ((tempfdrop < this.getDorpPeryProbility()) 
					&& ((AgentPortrayal) perception.objs[i]).getSubdued() == true)) {
				releaseInLoc(2);
				tempDrop = true;
			} else {
				tempDrop = false;
			}
		}
		if (this.getAntThingInGripper() == false)
			tempDrop = true;
		else
			tempDrop = false;
		return tempDrop;
	}

	public void setSubdueAgent(int type, boolean isSubdue) {
		if (!getTagging())
			return;
		if (!isSubdue) {
			for (int i = 0; i < perception.numObjs; i++) {
				if (((AgentPortrayal) perception.objs[i]).getAgentType() == type
						&& (((AgentPortrayal) perception.objs[i]).getAgentId() == getTaggeeId())
						&& ((AgentPortrayal) perception.objs[i]).getTagged()) {
					((AgentPortrayal) perception.objs[i]).setSubdued(isSubdue);
					// System.out.print("setSubdueAgent-> \n");
					((AgentPortrayal) perception.objs[i]).setTagged(false);
					((AgentPortrayal) perception.objs[i]).setTaggerId(-1);
					((AgentPortrayal) perception.objs[i]).setGetButDrop(true);
					setTagging(isSubdue);
					setTaggeeId(-1);
					this.setAnyThingInGripper(false, type);
				}
			}
			// System.out.print("setSubdueAgent, "+isSubdue+"\n");
		} else {
			for (int i = 0; i < perception.numObjs; i++) {
				if (((AgentPortrayal) perception.objs[i]).getAgentType() == type
						&& (((AgentPortrayal) perception.objs[i]).getAgentId() == getTaggeeId())
						&& ((AgentPortrayal) perception.objs[i]).getTagged()) {
					((AgentPortrayal) perception.objs[i]).setSubdued(isSubdue);
					// System.out.print("setSubdueAgent, "+isSubdue+" \n");
				}// end of the botteam if..
			}// end of the loop
		}
	}// */

	public double getSubduedProbility() {
		return probabilityOfTagging;
	}

	Double2D tempLoc = new Double2D();

	public boolean isNotMove() {

		if((Math.abs(tempLoc.x-loc.x)<0.005)&&(Math.abs(tempLoc.y-loc.y)<0.005))
		{	
			tempLoc = new Double2D(loc.x,loc.y);
			return true;
		}else{
			tempLoc = new Double2D(loc.x,loc.y);
			return false;
		}
	}

	public void updatePerception(SimState state) {
		objects.clear();
		double distanceToAgent = 0.0;
		double angleBetweenDirection = 0.0;
		double x = 0.0;
		double y = 0.0;

		/*---timer---*/
		timeStepSize = ((Sim) state).getTimeStepSize();

		/*---distance sensors---*/
		perception = ((Sim) state).forest.getObjectsExactlyWithinDistance((new Double2D(loc.x * this.getScaler(), loc.y * this.getScaler())), distanceOfPerception * this.getScaler());
		// perception = ((Sim)state).forest.getObjectsExactlyWithinDistance((new
		// Double2D(loc.x, loc.y)),distanceOfPerception);
		for (int i = 0; i < perception.numObjs; i++) {
			// choose other predators
			if (((AgentPortrayal) perception.objs[i]).getAgentId() != this.getAgentId()
					&& ((AgentPortrayal) perception.objs[i]).getAgentType() == 3
					&& !(((AgentPortrayal) perception.objs[i]).getTagged())) {
				// the distance between center of this predator and the nearest
				// point other predator.
				distanceToAgent = ((AgentPortrayal) perception.objs[i]).getLoc().distance(loc.x, loc.y);
				angleBetweenDirection = Math.atan2((((AgentPortrayal) perception.objs[i]).getLoc().y - loc.y),(((AgentPortrayal) perception.objs[i]).getLoc().x - loc.x));
				angleBetweenDirection -= this.direction.angle();
				if (angleBetweenDirection > Math.PI)
					angleBetweenDirection -= 2 * Math.PI;
				if (angleBetweenDirection < -Math.PI)
					angleBetweenDirection += 2 * Math.PI;
				if (angleBetweenDirection > Math.PI / 2 || angleBetweenDirection < -Math.PI / 2)
					continue;
				x = Math.sin(angleBetweenDirection) * distanceToAgent;
				y = Math.cos(angleBetweenDirection) * distanceToAgent;
				Double2D newVector = new Double2D(x, y);
				objects.add(newVector);
			}// end of the botteam if..
		}// end of the loop

		/*---gripper sensor---*/
		touchObject();
		
		/*---is it drop?---*/
		isItDrop = this.setDropAgent(2);

		/*---is not move?---*/
		isNotMove();

	}// */

	public void draw(Object object, final Graphics2D g, final DrawInfo2D info) {
		double agentSize = this.getAgentSize();
		DrawBody.draw(this.getAgentType(), object, g, info, 
				this.getDisplayColor(), this.getDisplayTextColor(), this.getDisplayTextLoc(), 
				this.getDisplayText(), this.getDir(),this.getDirAngle(), agentSize);
		if(this.getDefLine()==true)
			DrawBody.drawLine(object, g, info, this.getDefLineColor(), this.getLoc(), this.getDefPoint());
	}

	public void takeStep(final SimState state) {
		updatePerception((Sim) state);
		controller.takeStep();
		takeAct();
		((Sim) state).forest.setObjectLocation(this, new Double2D(loc.x * this.getScaler(), loc.y * this.getScaler()));
		// ((Sim)state).forest.setObjectLocation(this, new Double2D(loc.x,loc.y));
	}
	
	public double getMaxSpeed(){
		return this.maxSpeed;
	}
	
	public double getMaxTurnRate(){
		return this.maxTurnRate;
	}
	
	public double getMaxAc(){
		return this.maxAc;
	}
	
	public double getMaxTAc(){
		return this.maxTAc;
	}
	
	public void setMaxSpeed(double ms){
		this.maxSpeed = ms;
	}
	
	public void setMaxTurnRate(double mt){
		this.maxTurnRate = mt;
	}
	
	public void setMaxAc(double ma){
		this.maxAc = ma;
	}
	
	public void setMaxTAc(double mta){
		this.maxTAc = mta;
	}
	
	public void setDistanceOfPerception(double d){
		this.distanceOfPerception = d;
	}

	public void takeAct() {
		double xOff = 0, yOff = 0, angleOff = 0;
		double oldAngle = direction.angle();
		double newAngle = 0;
		updateTimeStamp();
		if(!this.usedSetVelocities){
			//double maxAc = 1;
			//double maxSpeed = 0.01;
			if (Math.abs(dsrSpeed - speed) > maxAc)
				dsrSpeed = speed + (dsrSpeed > speed ? maxAc : -maxAc);
			if (Math.abs(dsrSpeed) > maxSpeed)
				if (dsrSpeed > 0)
					dsrSpeed = maxSpeed;
				else
					dsrSpeed = -maxSpeed;
			// System.out.print(dsrSpeed+"\n");
	
			//double maxTAc = Math.PI / 5;
			if (Math.abs(dsrTurnRate - turnRate) > maxTAc)
				dsrTurnRate = turnRate
						+ (dsrTurnRate > turnRate ? maxTAc : -maxTAc);
			if (Math.abs(dsrTurnRate) > maxTAc)
				dsrTurnRate = maxTAc * (dsrTurnRate > 0 ? 1 : -1);
	
			angleOff = (dsrTurnRate + turnRate) / 2 * timeStepSize;
			oldAngle = direction.angle();
			newAngle = oldAngle + angleOff;
			// if(getAgentId() == 0) System.out.println("oldAngle:" +
			// oldAngle*180/Math.PI + ":(" + direction.x + ":" + direction.y + ")");
			xOff = (speed * Math.cos(oldAngle) + dsrSpeed * Math.cos(newAngle)) * timeStepSize;// *this.Meter2Pixel;//getScaler();
			yOff = (speed * Math.sin(oldAngle) + dsrSpeed * Math.sin(newAngle)) * timeStepSize;// *this.Meter2Pixel;//getScaler();
		} else {
			if(vx > maxVX) vx = maxVX;
			if(vy > maxVY) vy = maxVY;
			//if(vt > maxVT) vt = maxVT;
			MutableDouble2D step = new MutableDouble2D(vx*timeStepSize,vy*timeStepSize);
			step.rotate(oldAngle);
			xOff = step.x;
			yOff = step.y;
			newAngle = oldAngle + vt*timeStepSize;
			dsrTurnRate = 0; //vt;
			dsrSpeed = 0; //Math.sqrt(Math.pow(xOff,2)+Math.pow(yOff,2));
		}
		if (this.checkBound(loc.x+xOff, loc.y+yOff) && this.checkCollision(xOff, yOff)) {
			loc.setX(loc.x + xOff);
			loc.setY(loc.y + yOff);
		} else if(toroidal && this.checkCollision(xOff,yOff)){
			if(loc.x+xOff >= bc.xMax){ 
				loc.setX(loc.x+xOff - bc.xMax);
			} else if(loc.x+xOff <= bc.xMin){ 
				loc.setX(loc.x+xOff + bc.xMax);
			}
			if(loc.y+yOff >= bc.yMax){ 
				loc.setY(loc.y+yOff - bc.yMax);
			} else if(loc.y+yOff <= bc.yMin){ 
				loc.setY(loc.y+yOff + bc.yMax);
			}
		} else {
			dsrSpeed = 0;
			loc.setX(loc.x);
			loc.setY(loc.y);
			//System.out.println("at Bound with angle offset: "+ angleOff);
		}
		speed = dsrSpeed;
		turnRate = dsrTurnRate;
		direction.setX(Math.cos(newAngle));
		direction.setY(Math.sin(newAngle));
	}
	
	public Double2D getNewDirection(Vec2 val) {
		Vec2 tmp_val = new Vec2();
		tmp_val.x = val.x - loc.x;
		tmp_val.y = val.y - loc.y;
		return new Double2D(tmp_val.x, tmp_val.y);
	}
	
	public double getTimeStampSize()
	{
		return timeStepSize;
	}
	
	public boolean checkBound(double x, double y){
		return bc.checkBound(x, y, this.getAgentSize());
	}
	

}// end of the class AntBody
