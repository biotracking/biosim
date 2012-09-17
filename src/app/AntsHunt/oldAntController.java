package app.AntsHunt;

import java.util.ArrayList;

import sim.util.Double2D;
import sim.util.MutableDouble2D;
import core.basic_api.AgentBody;
import core.basic_api.AgentController;

public class oldAntController extends AgentController {
	private int stuckCounter;
	private int stuckCounter2;
	private boolean tagging;

	public oldAntController() {
		stuckCounter = 0;
		stuckCounter2 = 0;
		tagging = false;
	}

	public oldAntController(AgentBody ab) {
		this.setBody(ab);
		stuckCounter = 0;
		stuckCounter2 = 0;
		tagging = false;
	}

	public void think_then_act() {
		ArrayList<Double2D> teammates = body.getAgents(1);
		ArrayList<Double2D> preys = body.getAgents(2);
		ArrayList<Double2D> obstacles = body.getAgents(3);
		if (((Adapter) body).getId() == 0)
			System.out.println(" prey & team: " + preys.size() + "\t"
					+ teammates.size());
		MutableDouble2D total1 = getTotal(teammates);
		MutableDouble2D total2 = getTotal(obstacles);
		MutableDouble2D preysNear = getTotal(preys);
		double total1Angle = total1.angle();
		total1Angle = total1Angle < Math.PI / 2 ? -total1Angle : Math.PI
				- total1Angle;
		double total2Angle = total2.angle();
		total2Angle = total2Angle < Math.PI / 2 ? -total2Angle : Math.PI
				- total2Angle;
		double preysNearAngle = preysNear.angle();
		preysNearAngle = Math.PI / 2 - preysNearAngle;
		double totalAngle = total1Angle + total2Angle + preysNearAngle;
		MutableDouble2D total = new MutableDouble2D(total1.x + total2.x,
				total1.y + total2.y);
		if (((Adapter) body).getId() == 0) {
			System.out.println(preysNear.x + " : " + preysNear.y + "->"
					+ preysNear.angle());
			System.out.println("t1:" + total1Angle * 180 / Math.PI + " t2:"
					+ total2Angle * 180 / Math.PI + " pn:" + preysNearAngle
					* 180 / Math.PI);
		}
		double coef1 = 1;
		coef1 = preysNear.length() > 0.001 ? 0 : 1;
		MutableDouble2D temp = new MutableDouble2D(preysNear.x - total.x
				* coef1, preysNear.y + total.y * coef1);
		if (!tagging) {
			if (preysNear.length() > 0.01) {// some prey insight
				if (preysNear.length() < 13) {
					boolean succ = body.tag(2, preysNear.x, preysNear.y);
					if (succ) {
						System.out.println("tag successfully");
						tagging = true;
					} else {
						System.out.println("tag fail~");
					}
				}
				body.setDesiredSpeed(4);
				body.setDesiredTurnRate(preysNearAngle + total2Angle);
			} else {// no prey insight
				if (total.length() > 0.01) { // found obstacles or stuck
					stuckCounter++;
					if (stuckCounter > 20) {
						// System.out.println("case 1");
						body.setDesiredSpeed(0);
						body.setDesiredTurnRate(2);
					} else {
						System.out.println("case 2");
						body.setDesiredSpeed(4);
						body.setDesiredTurnRate(total1Angle + total2Angle);
					}
				} else { // no obstacles or just turn back from stuck
					if (stuckCounter > 0) {
						stuckCounter2++;
						if (stuckCounter2 < 3) {
							// System.out.println("case 3");
							body.setDesiredSpeed(0);
							body.setDesiredTurnRate(2);
						} else {
							// System.out.println("case 4");
							stuckCounter2 = 0;
							stuckCounter = 0;
							body.setDesiredSpeed(4);
							body.setDesiredTurnRate((Math.random() - 0.5) / 10);
						}
					} else {
						// System.out.println("case 5");
						body.setDesiredSpeed(4);
						body.setDesiredTurnRate((Math.random() - 0.5) / 10);
					}
				}
			}
		} else {
			System.out.println("backing");
			if (body.atHome()) {
				System.out.println("got home!!!");
				body.releaseToHome(2);
				tagging = false;
				body.setDesiredSpeed(0);
				body.setDesiredTurnRate(0);
			} else {
				Double2D home = body.getHomeDirection();
				double homeAngle = Math.atan2(home.y, home.x);
				homeAngle = Math.PI / 2 - homeAngle;
				body.setDesiredSpeed(6);
				body.setDesiredTurnRate(homeAngle + 1.2 * total2Angle);
			}
		}
		// stuckCounter = 0;
		// stuckCounter2++;
		// } else {
		//			
		// }
		// if(stuckCounter > 20 || stuckCounter2 < 6){
		// body.setDesiredSpeed(0);
		// body.setDesiredTurnRate(2);
		// stuckCounter2++;
		// } else {
		// double angleOff = 0.0;//temp.angle();
		// if(temp.length() < 0.00001){
		// if(total2.length() > 0.00001){
		// angleOff = total2.angle();
		// }
		// else angleOff = 0;
		// } else
		// angleOff = temp.angle();
		// body.setDesiredSpeed(2.5);
		// body.setDesiredTurnRate(angleOff);
		// }
	}

	@Override
	public void takeStep() {
		// TODO Auto-generated method stub

	}
}// end of the class AntController
