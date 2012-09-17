package app.RandomAnts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.util.Bag;
import sim.util.Double2D;
import sim.util.MutableDouble2D;
import core.basic_api.AgentController;

/**
 * @author Hai Shang
 * @author hshang9@gatech.edu
 * @see Define the specific function of ants body
 */
public class RandomAntBody extends Adapter {
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

	// public int encounterNum;
	public RandomAntBody() {
		this.loc = new MutableDouble2D(100 * Math.random() - 50, 100 * Math
				.random() - 50);
		double ran = Math.random();
		this.direction = new MutableDouble2D(Math.sin(22 / 7 * (ran - 0.5)),
				Math.cos(22 / 7 * (ran - 0.5)));
		perception = new Bag();
		speed = 0;
		turnRate = 0;
		// encounterNum = 0;
		objects = new ArrayList<Double2D>();
	}

	public RandomAntBody(double x, double y, double d_x, double d_y, int i,
			int t) {
		this.loc = new MutableDouble2D(x, y);
		double temp = Math.sqrt(d_x * d_x + d_y * d_y);
		this.direction = new MutableDouble2D(d_x / temp, d_y / temp);
		perception = new Bag();
		speed = 0;
		turnRate = 0;
		this.setId(i);
		this.setAgentType(t);
		// encounterNum = 0;
		objects = new ArrayList<Double2D>();
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

	public void updatePerception(SimState state) {
		perception = ((BioSim) state).forest.getObjectsExactlyWithinDistance(
				(new Double2D(loc.x, loc.y)), 50);
		objects.clear();
		double distanceToAgent = 0.0;
		double angleBetweenDirection = 0.0;
		double x = 0.0;
		double y = 0.0;
		for (int i = 0; i < perception.numObjs; i++) {
			// choose other predators
			if (((Adapter) perception.objs[i]).getId() != this.getId()
					&& ((Adapter) perception.objs[i]).getAgentType() == 3
					&& !(((Adapter) perception.objs[i]).getTagged())) {
				// the distance between center of this predator and the nearest
				// point other predator.
				distanceToAgent = ((Adapter) perception.objs[i]).getLoc()
						.distance(loc.x, loc.y);
				angleBetweenDirection = Math.atan2(
						(((Adapter) perception.objs[i]).getLoc().y - loc.y),
						(((Adapter) perception.objs[i]).getLoc().x - loc.x));
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
		this.timeStamp++;
		double maxAc = 0.1;
		double maxTAc = Math.PI / 10;
		// if(this.getId() == 1){
		// maxAc = 1;
		// }
		// dsrSpeed = 10;
		if (getId() == 0)
			System.out.println("dsr speed:" + dsrSpeed + " speed:" + speed);
		if (getId() == 0)
			System.out.println("dsr turnRate:" + dsrTurnRate + " turnRate:"
					+ turnRate);

		if (Math.abs(dsrSpeed - speed) > maxAc)
			dsrSpeed = speed + (dsrSpeed > speed ? maxAc : -maxAc);
		if (Math.abs(dsrSpeed) > 6)
			dsrSpeed = 6 * (dsrSpeed > 0 ? 1 : -1);
		if (Math.abs(dsrTurnRate - turnRate) > maxTAc)
			dsrTurnRate = turnRate
					+ (dsrTurnRate > turnRate ? maxTAc : -maxTAc);
		if (Math.abs(dsrTurnRate) > Math.PI / 10)
			dsrTurnRate = Math.PI / 10 * (dsrTurnRate > 0 ? 1 : -1);
		double angleOff = (dsrTurnRate + turnRate) / 2;
		double oldAngle = direction.angle();
		double newAngle = oldAngle + angleOff;
		if (getId() == 0)
			System.out.println("oldAngle:" + oldAngle * 180 / Math.PI + ":("
					+ direction.x + ":" + direction.y + ")");
		double xOff = 0, yOff = 0;
		xOff = (speed * Math.cos(oldAngle) + dsrSpeed * Math.cos(newAngle)) * 0.5;
		yOff = (speed * Math.sin(oldAngle) + dsrSpeed * Math.sin(newAngle)) * 0.5;
		MutableDouble2D tempNewLoc = new MutableDouble2D(loc.x + xOff, loc.y
				+ yOff);
		if (checkCollision(xOff, yOff)) {
			loc.setX(loc.x + xOff);
			loc.setY(loc.y + yOff);
		} else
			dsrSpeed = 0;
		speed = dsrSpeed;
		turnRate = dsrTurnRate;
		direction.setX(Math.cos(newAngle));
		direction.setY(Math.sin(newAngle));
		if (getId() == 0)
			System.out.println("newAngle:" + newAngle * 180 / Math.PI + "="
					+ direction.angle() * 180 / Math.PI + ":(" + direction.x
					+ ":" + direction.y + ")\n");

	}

	public void step(final SimState state) {
		// MutableDouble2D loc = this.getLoc();
		// loc.setX(loc.x + Math.random() - 0.5);
		// loc.setY(loc.y + Math.random() - 0.5);
		// this.setLoc(loc.x, loc.y);
		// MutableDouble2D direction = this.getDirection();
		// double ran = (Math.random()-0.5)/10;
		// double new_x = direction.x + ran;
		// if(new_x > 1) new_x = 1;
		// if(new_x < -1) new_x = -1;
		//		
		// direction.setX(new_x);
		// direction.setY(Math.sqrt(1-new_x*new_x));
		// //body.update((BioSim)state);
		// //body.takeStep();
		// this.setDirection(direction.x, direction.y);
		updatePerception((BioSim) state);
		controller.think_then_act();
		takeAct();
		((BioSim) state).forest.setObjectLocation(this, new Double2D(loc.x,
				loc.y));
		logPosition((BioSim) state);
	}

	public logPosition(Biosim sim) {
		String position = "ID: " + ((Adapter) this).getId();

	}

	public Double2D getHomeDirection() {
		double angleBetweenDirection = Math.atan2((0 - loc.y), (600 - loc.x));
		angleBetweenDirection -= this.direction.angle();
		if (angleBetweenDirection > Math.PI)
			angleBetweenDirection -= 2 * Math.PI;
		if (angleBetweenDirection < -Math.PI)
			angleBetweenDirection += 2 * Math.PI;
		double x = Math.sin(angleBetweenDirection);
		double y = Math.cos(angleBetweenDirection);
		return new Double2D(x, y);
	}

	public boolean atHome() {
		return (550 < loc.x && loc.x < 650 && 0 < loc.y && loc.y < 100);
	}

	public void draw(Object object, final Graphics2D g, final DrawInfo2D info) {
		// public void draw(final Graphics2D g){
		double scaler = getScaler() * info.draw.width;
		double tempX = info.draw.x;
		double tempY = info.draw.y;
		g.setColor(Color.black);
		g.fillOval((int) (tempX + (0.002 * direction.x - 0.002) * scaler),
				(int) (tempY + (0.002 * direction.y - 0.002) * scaler),
				(int) (0.004 * scaler), (int) (0.004 * scaler));
		g.fillOval((int) (tempX + (0.0065 * direction.x - 0.0025) * scaler),
				(int) (tempY + (0.0065 * direction.y - 0.0025) * scaler),
				(int) (0.005 * scaler), (int) (0.005 * scaler));
		g.fillOval((int) (tempX - (0.002 * direction.x + 0.002) * scaler),
				(int) (tempY - (0.002 * direction.y + 0.002) * scaler),
				(int) (0.004 * scaler), (int) (0.004 * scaler));
		g.fillOval((int) (tempX - (0.0075 * direction.x + 0.0035) * scaler),
				(int) (tempY - (0.0075 * direction.y + 0.0035) * scaler),
				(int) (0.007 * scaler), (int) (0.007 * scaler));
		g.drawLine((int) (tempX - (0.0075 * direction.y) * scaler),
				(int) (tempY + (0.0075 * direction.x) * scaler),
				(int) (tempX + (0.0075 * direction.y) * scaler),
				(int) (tempY - (0.0075 * direction.x) * scaler));
		double angle = direction.angle() - Math.PI / 5;
		g.drawLine((int) (tempX - (0.01 * Math.sin(angle)) * scaler),
				(int) (tempY + (0.01 * Math.cos(angle)) * scaler),
				(int) (tempX + (0.01 * Math.sin(angle)) * scaler),
				(int) (tempY - (0.01 * Math.cos(angle)) * scaler));
		angle = direction.angle() + Math.PI / 5;
		g.drawLine((int) (tempX - (0.01 * Math.sin(angle)) * scaler),
				(int) (tempY + (0.01 * Math.cos(angle)) * scaler),
				(int) (tempX + (0.01 * Math.sin(angle)) * scaler),
				(int) (tempY - (0.01 * Math.cos(angle)) * scaler));
		angle = direction.angle() + Math.PI / 18;
		g.drawLine((int) tempX, (int) tempY, (int) (tempX + (0.0105 * Math
				.cos(angle))
				* scaler), (int) (tempY + (0.0105 * Math.sin(angle)) * scaler));

		angle = direction.angle() - Math.PI / 18;
		g.drawLine((int) tempX, (int) tempY, (int) (tempX + (0.0105 * Math
				.cos(angle))
				* scaler), (int) (tempY + (0.0105 * Math.sin(angle)) * scaler));
		// g.drawLine(x1, y1, x2, y2)
	}

	public boolean tag(int agentType, double xOff, double yOff) {
		if (xOff * xOff + yOff * yOff > 169) {
			System.out.println("Not enough long~");
			return false;
		}
		double distanceToAgent = 0, angleBetweenDirection = 0;
		double x = 0, y = 0;
		for (int i = 0; i < perception.numObjs; i++) {
			// choose other predators
			if (((Adapter) perception.objs[i]).getAgentType() == agentType
					&& (((Adapter) perception.objs[i]).getId() != this.getId() || agentType != getAgentType())
					&& !(((Adapter) perception.objs[i]).getTagged())) {
				// the distance between center of this predator and the nearest
				// point other predator.
				distanceToAgent = ((Adapter) perception.objs[i]).getLoc()
						.distance(loc.x, loc.y);
				angleBetweenDirection = Math.atan2(
						(((Adapter) perception.objs[i]).getLoc().y - loc.y),
						(((Adapter) perception.objs[i]).getLoc().x - loc.x));
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
				if (Math.abs(x - xOff) < 0.01 && Math.abs(y - yOff) < 0.01) {
					((Adapter) perception.objs[i]).setTagged(true);
					((Adapter) perception.objs[i]).setTaggerId(this.getId());
					setTagging(true);
					setTaggeeId(((Adapter) perception.objs[i]).getId());
					return true;
				}
			}// end of the botteam if..
		}// end of the loop
		return false;
	}

	public void releaseToHome(int type) {
		if (!getTagging())
			return;
		double distanceToAgent = 0, angleBetweenDirection = 0;
		double x = 0, y = 0;
		for (int i = 0; i < perception.numObjs; i++) {
			if (((Adapter) perception.objs[i]).getAgentType() == type
					&& (((Adapter) perception.objs[i]).getId() == getTaggeeId())
					&& ((Adapter) perception.objs[i]).getTagged()) {
				((Adapter) perception.objs[i]).setTagged(false);
				((Adapter) perception.objs[i]).setTaggerId(-1);
				setTagging(false);
				setTaggeeId(-1);
				((Adapter) perception.objs[i]).setAlive(false);
				return;
			}// end of the botteam if..
		}// end of the loop
	}

	public ArrayList<Double2D> getAgents(int agentType) {
		ArrayList<Double2D> vectors = new ArrayList<Double2D>();
		double distanceToAgent = 0.0;
		double angleBetweenDirection = 0.0;
		double x = 0.0;
		double y = 0.0;
		for (int i = 0; i < perception.numObjs; i++) {
			// choose other predators
			if (((Adapter) perception.objs[i]).getAgentType() == agentType
					&& (((Adapter) perception.objs[i]).getId() != this.getId() || agentType != getAgentType())
					&& !(((Adapter) perception.objs[i]).getTagged())
					&& ((Adapter) perception.objs[i]).getAlive()) {
				// the distance between center of this predator and the nearest
				// point other predator.
				distanceToAgent = ((Adapter) perception.objs[i]).getLoc()
						.distance(loc.x, loc.y);
				angleBetweenDirection = Math.atan2(
						(((Adapter) perception.objs[i]).getLoc().y - loc.y),
						(((Adapter) perception.objs[i]).getLoc().x - loc.x));
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
			if (((Adapter) perception.objs[i]).getId() != this.getId()) {
				// the distance between center of this predator and the nearest
				// point other predator.
				distanceToAgent = ((Adapter) perception.objs[i]).getLoc()
						.distance(loc.x, loc.y);
				angleBetweenDirection = Math.atan2(
						(((Adapter) perception.objs[i]).getLoc().y - loc.y),
						(((Adapter) perception.objs[i]).getLoc().x - loc.x));
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

	private boolean checkCollision(double xo, double yo) {
		double tempX = loc.x, tempY = loc.y;
		tempX += xo;
		tempY += yo;
		return (210 < tempX && tempX < 990 && 60 < tempY && tempY < 620);

		// //System.out.println(objects.size());
		// double tempX, tempY, temp = 0, temp2 = 60;
		// double xRecord = 0, yRecord = 0;
		// if(objects.size() == 0){
		// //System.out.println("cc case 1");
		// return true;
		// }
		// for(int i = 0; i < objects.size(); i++){
		// tempX = objects.get(i).x;
		// tempY = objects.get(i).y;
		// temp = Math.sqrt(tempX*tempX + tempY*tempY);
		// ////System.out.println(loca.x+" "+loca.y+" "+objects.get(i).x
		// // +" "+objects.get(i).y+ "DIS:"+temp);
		// if(temp < temp2){
		// temp2 = temp;
		// xRecord = tempX;
		// yRecord = tempY;
		// }
		// }
		// xRecord -= xo;
		// yRecord -= yo;
		// temp = Math.sqrt(xRecord*xRecord + yRecord*yRecord);
		// if(temp >= temp2){
		// //System.out.println("cc case 2");
		// return true;
		// } else {
		// //System.out.println("cc case 3");
		// return false;
		// }
	}
}// end of the class RandomAntBody
