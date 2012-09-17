package app.AntsHunt;

import java.util.ArrayList;

import sim.util.Double2D;
import sim.util.MutableDouble2D;
import core.basic_api.AgentBody;
import core.basic_api.AgentController;

public class oldFlyController extends AgentController {
	private int stuckCounter;
	private int stuckCounter2;

	public oldFlyController() {
		stuckCounter = 0;
		stuckCounter2 = 0;
	}

	public oldFlyController(AgentBody ab) {
		this.setBody(ab);
		stuckCounter = 0;
		stuckCounter2 = 0;
	}

	public void takeStep() {
		ArrayList<Double2D> ants = body.getAgents(1);
		ArrayList<Double2D> obstacles = body.getAgents(3);
		// System.out.println("tm & ob: "+ ants.size()+ "\t"+ obstacles.size());
		MutableDouble2D total1 = getTotal(ants);
		MutableDouble2D total2 = getTotal(obstacles);
		MutableDouble2D total = new MutableDouble2D(total1.x + total2.x,
				total1.y + total2.y);
		MutableDouble2D temp = new MutableDouble2D(-1 * total.x, -1 * total.y);

		// if(body.g)

		if (total.length() > 0.01) { // found obstacles or stuck
			stuckCounter++;
			if (stuckCounter > 100) {
				// System.out.println("case 1");
				body.setDesiredSpeed(1);
				body.setDesiredTurnRate(2);
			} else {
				// System.out.println("case 2");
				body.setDesiredSpeed(3);
				body.setDesiredTurnRate(temp.angle());
			}
		} else { // no obstacles or just turn back from stuck
			if (stuckCounter > 0) {
				stuckCounter2++;
				if (stuckCounter2 < 6) {
					// System.out.println("case 3");
					body.setDesiredSpeed(0);
					body.setDesiredTurnRate(2);
				} else {
					// System.out.println("case 4");
					stuckCounter2 = 0;
					stuckCounter = 0;
					body.setDesiredSpeed(3);
					body.setDesiredTurnRate(0);
				}
			} else {
				// System.out.println("case 5");
				body.setDesiredSpeed(2);
				body.setDesiredTurnRate(0);
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
}// end of the class FlyController
