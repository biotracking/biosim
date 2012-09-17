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

public class FlyBody extends AgentPortrayal {
	private double distanceOfPerception = 0.006; // the distance of ant detect.
	public final double MAX_TRANSLATION = 1; // maximum translation speed
	public double probabilityOfSubdued = 0.01;
	private AgentController controller;
	private MutableDouble2D loc;
	private MutableDouble2D direction;

	private Bag perception;
	private ArrayList<Double2D> objects;
	private ArrayList<Double2D> agents;

	private double speed;
	private double turnRate;
	private double dsrSpeed;
	private double dsrTurnRate;
	private Color agentColor = Color.red;
	private double timeStepSize;
	private DrawAgentBody DAB;
	private BoundChecker bc;
	private ObstacleCollisionChecker occ;
	
	public FlyBody() {
		this.loc = new MutableDouble2D(Math.random() + 0.04,
				Math.random() + 0.03);
		double ran = Math.random();
		this.direction = new MutableDouble2D(Math.sin(22 / 7 * (ran - 0.5)),
				Math.cos(22 / 7 * (ran - 0.5)));
		perception = new Bag();
		speed = 0;
		turnRate = 0;
		objects = new ArrayList<Double2D>();
		timeStepSize = 1;
	}

	public FlyBody(double x, double y, double d_x, double d_y, int i, int t,
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
		timeStepSize = 1;
		DAB = new DrawAgentBody();
		bc = new BoundChecker();
		occ = new ObstacleCollisionChecker();
		this.setAgentSize(size);
	}

	public Double2D getLoc() {
		Double2D loca = new Double2D(loc.x, loc.y);
		return loca;
	}

	public Double2D getDir() {
		Double2D dire = new Double2D(direction.x, direction.y);
		return dire;
	}

	public void setController(AgentController c) {
		this.controller = c;
	}

	public int tag(int type, double x, double y) {
		return 0;
	}

	public void releaseToHome(int type) {

	}

	public Double2D getHomeDirection() {
		return new Double2D(-1, -1);
	}

	public Double2D getNewDirection(Vec2 val) {
		// double angleBetweenDirection = Math.atan2((val.y-loc.y),
		// (val.x-loc.x));
		// angleBetweenDirection -= this.direction.angle();
		// if(angleBetweenDirection > Math.PI) angleBetweenDirection -=
		// 2*Math.PI;
		// if(angleBetweenDirection < -Math.PI) angleBetweenDirection +=
		// 2*Math.PI;
		// double x = Math.sin(angleBetweenDirection);
		// double y = Math.cos(angleBetweenDirection);

		Vec2 tmp_val = new Vec2();
		tmp_val.x = val.x - loc.x;
		tmp_val.y = val.y - loc.y;
		return new Double2D(tmp_val.x, tmp_val.y);
	}

	public boolean atHome() {
		return false;
	}

	public void updatePerception(SimState state) {
		timeStepSize = ((Sim) state).getTimeStepSize();
		perception = ((Sim) state).forest.getObjectsExactlyWithinDistance(
				(new Double2D(loc.x * this.getScaler(), loc.y
						* this.getScaler())), this.distanceOfPerception * this.getScaler());
		// perception = ((Sim)state).forest.getObjectsExactlyWithinDistance((new
		// Double2D(loc.x, loc.y)),0.019);
		objects.clear();
		double distanceToAgent = 0.0;
		double angleBetweenDirection = 0.0;
		double x = 0.0;
		double y = 0.0;
		for (int i = 0; i < perception.numObjs; i++) {
			// choose other predators
			if (((AgentPortrayal) perception.objs[i]).getAgentId() != this
					.getAgentId()
					&& ((AgentPortrayal) perception.objs[i]).getAgentType() == 3) {
				// the distance between center of this predator and the nearest
				// point other predator.
				distanceToAgent = ((AgentPortrayal) perception.objs[i]).getLoc().distance(loc.x, loc.y);
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
				x = Math.sin(angleBetweenDirection) * distanceToAgent;
				y = Math.cos(angleBetweenDirection) * distanceToAgent;
				Double2D newVector = new Double2D(x, y);
				objects.add(newVector);
			}// end of the botteam if..
		}// end of the loop
	}

	public void setDesiredSpeed(double dsrS) {
		this.dsrSpeed = dsrS;
	}

	public void setDesiredTurnRate(double dsrT) {
		this.dsrTurnRate = dsrT;
	}

	public void takeAct() {
		updateTimeStamp();
		double maxAc = 0.03;
		// if(Math.abs(dsrSpeed - speed) > maxAc)
		// dsrSpeed = speed + (dsrSpeed > speed? maxAc : -maxAc);
		// if(Math.abs(dsrSpeed) > 0.005)
		// dsrSpeed = 0.005 * (dsrSpeed > 0? 1: -1);
		if (Math.abs(dsrSpeed - speed) > maxAc)
			dsrSpeed = speed + (dsrSpeed > speed ? maxAc : -maxAc);
		if (Math.abs(dsrSpeed) > maxAc)
			if (dsrSpeed > 0)
				dsrSpeed = dsrSpeed * maxAc;
			else
				dsrSpeed = dsrSpeed * (-maxAc);

		if (Math.abs(dsrTurnRate - turnRate) > Math.PI / 6)
			dsrTurnRate = turnRate
					+ (dsrTurnRate > turnRate ? Math.PI / 8 : -Math.PI / 8);
		if (Math.abs(dsrTurnRate) > Math.PI / 6)
			dsrTurnRate = Math.PI / 6 * (dsrTurnRate > 0 ? 1 : -1);
		double angleOff = (dsrTurnRate + turnRate) / 2;
		double oldAngle = direction.angle();
		double newAngle = oldAngle + angleOff;
		double xOff = 0, yOff = 0;
		xOff = (speed * Math.cos(oldAngle) + dsrSpeed * Math.cos(newAngle))
				* timeStepSize;// 0.5;
		yOff = (speed * Math.sin(oldAngle) + dsrSpeed * Math.sin(newAngle))
				* timeStepSize;// 0.5;
		// xOff = (speed* Math.cos(oldAngle)+ dsrSpeed*
		// Math.cos(newAngle))*timeStepSize;//*this.Meter2Pixel;//getScaler();
		// yOff = (speed* Math.sin(oldAngle)+ dsrSpeed*
		// Math.sin(newAngle))*timeStepSize;//*this.Meter2Pixel;//getScaler();
		MutableDouble2D tempNewLoc = new MutableDouble2D(loc.x + xOff, loc.y+ yOff);
		if (this.checkBound(loc.x+xOff, loc.y+yOff) && this.checkCollision(xOff, yOff)) {
			loc.setX(loc.x + xOff);
			loc.setY(loc.y + yOff);
		} else
			dsrSpeed = 0;
		speed = dsrSpeed;
		direction.setX(Math.cos(newAngle));
		direction.setY(Math.sin(newAngle));
		// System.out.println(direction.angle());
		// */

	}

	public void takeStep(final SimState state) {
		if (!getAlive()) {// || this.getButDrop) {
			return;
		}
		if (this.getTagged()) {// && this.getSubdued()){
			MutableDouble2D newPos = getPosFromTagger(getTaggerId(), (Sim) state);
			loc.x = newPos.x;
			loc.y = newPos.y;
			this.DisplayNearAgent("", Color.black);
			// System.out.println(loc.x+ " : "+ loc.y);

		} else {
			updatePerception((Sim) state);
			controller.takeStep();
			takeAct();
		}
		((Sim) state).forest.setObjectLocation(this, new Double2D(loc.x
				* this.getScaler(), loc.y * this.getScaler()));
		// ((Sim)state).forest.setObjectLocation(this, new Double2D(loc.x,
		// loc.y));
	}

	public MutableDouble2D getPosFromTagger(int id, Sim state) {
		double x = 0, y = 0;
		Bag surrounding = state.forest.getObjectsExactlyWithinDistance(
				(new Double2D(loc.x * this.getScaler(), loc.y* this.getScaler())), 0.01 * this.getScaler());
		// Bag surrounding = state.forest.getObjectsExactlyWithinDistance((new
		// Double2D(loc.x, loc.y)),0.0095);
		for (int i = 0; i < surrounding.numObjs; i++) {
			// choose other predators
			if (((AgentPortrayal) surrounding.objs[i]).getAgentType() == 1
					&& ((AgentPortrayal) surrounding.objs[i]).getAgentId() == id) {
				double taggerSize = (((AgentPortrayal) surrounding.objs[i]).getAgentSize());
				double taggerX = ((AgentPortrayal) surrounding.objs[i]).getLoc().x;
				double taggerY = ((AgentPortrayal) surrounding.objs[i]).getLoc().y;
				double xOff = ((AgentPortrayal) surrounding.objs[i]).getDir().x * (taggerSize / 2);
				double yOff = ((AgentPortrayal) surrounding.objs[i]).getDir().y * (taggerSize / 2);
				x = taggerX + xOff;
				y = taggerY + yOff;
				MutableDouble2D result = new MutableDouble2D(x, y);
				return result;
			}// end of the botteam if..
		}// end of the loop
		return new MutableDouble2D(-1, -1);
	}

	public void draw(Object object, final Graphics2D g, final DrawInfo2D info) {
		if (getAlive()) {
			if (this.getSubdued())
				this.setDisplayColor(Color.gray);// g.setColor(Color.gray);
			// else if(this.getGetButDrop())
			// this.setDisplayColor(Color.red);//g.setColor(Color.red);
			else
				this.setDisplayColor(Color.red);// g.setColor(Color.red);
		} else
			this.setDisplayColor(Color.gray);// g.setColor(Color.gray);

		double agentSize = this.getAgentSize();
		DAB.draw(this.getAgentType(), object, g, info, this.getDisplayColor(),
				this.getDisplayTextColor(), this.getDisplayTextLoc(), this
						.getDisplayText(), this.getDir(), this.getDirAngle(),
				agentSize);
	}

	public ArrayList<Double2D> getAgents(int agentType) {
		ArrayList<Double2D> vectors = new ArrayList<Double2D>();
		double distanceToAgent = 0.0;
		double angleBetweenDirection = 0.0;
		double x = 0.0;
		double y = 0.0;
		for (int i = 0; i < perception.numObjs; i++) {
			// choose other predators
			if (((AgentPortrayal) perception.objs[i]).getAgentType() == agentType
					&& ((AgentPortrayal) perception.objs[i]).getAgentId() != this
							.getAgentId()) {
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
				x = Math.cos(angleBetweenDirection) * distanceToAgent;
				y = Math.sin(angleBetweenDirection) * distanceToAgent;
				// x = Math.cos(angleBetweenDirection) * distanceToAgent;
				// y = Math.sin(angleBetweenDirection) * distanceToAgent;
				Double2D newVector = new Double2D(x, y);
				vectors.add(newVector);
			}// end of the botteam if..
		}// end of the loop
		return vectors;
	}

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


	public boolean atLocation(Vec2 val, double destance) {
		double Lotmpx = Math.abs((val.x - loc.x));
		double Lotmpy = Math.abs((val.y - loc.y));
		return (Math.sqrt(Lotmpx * Lotmpx + Lotmpy * Lotmpy) < destance);
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

	public double getSubduedProbility() // the probability of agent who is
	// subdued
	{
		return probabilityOfSubdued;
	}

	public double getDirAngle() {
		return direction.angle();
	}

	public double getDistanceOfPerception() {
		return distanceOfPerception;
	}

	public ArrayList<Double2D> getAgentsVector(int agentType) {
		ArrayList<Double2D> vectors = new ArrayList<Double2D>();
		double angleBetweenDirection = 0.0;
		double x = 0.0;
		double y = 0.0;
		for (int i = 0; i < perception.numObjs; i++) {
			// choose other predators
			if (((AgentPortrayal) perception.objs[i]).getAgentType() == agentType
					&& (((AgentPortrayal) perception.objs[i]).getAgentId() != this
							.getAgentId() || agentType != getAgentType())
					&& !(((AgentPortrayal) perception.objs[i]).getTagged())
					&& ((AgentPortrayal) perception.objs[i]).getAlive()) {
				// the distance between center of this predator and the nearest
				// point other predator.
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
	}

	public double getDorpPeryProbility() {
		return 0;
	}

	public double lastAngle;

	public double getNewVector(double val_y, double val_x) {
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
	}

	public void releaseInLoc(int type) {
		return;
	}

	public boolean getAntThingInGripper() {
		return false;
	}

	public int getTypeInGripper() {
		return 0;
	}

	public void setSubdueAgent(int type, boolean isSubdue) {
		;
	}

	public boolean touchObject() {
		return false;
	}

	public double getDistanceToLoc(double x, double y) {
		double distance = 0;
		distance = this.getLoc().distance(x, y);
		// distance = Math.abs(Math.atan2(y-loc.y, x-loc.x));
		return distance;
	}

	public double[] getHomeLoc() {
		// TODO Auto-generated method stub
		return null;
	}

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
	}

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
		return bc.checkBound(x, y, this.getAgentSize());
	}

	public void setDistanceOfPerception(double preySightLength) {
		this.distanceOfPerception = preySightLength;
	}
}// end of the class FlyBody
