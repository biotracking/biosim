package core.basic_api;

import core.basic_api.AbstractRobot;
import core.clay.Node;
import core.clay.NodeDouble;
import core.clay.NodeVec2;
import core.clay.i_FSA_ba;
import core.util.Vec2;

public class ClayController extends AgentController {
	/**
	 * Turn debug printing on or off.
	 */
	public static final boolean DEBUG = /* true; */Node.DEBUG;

	private boolean tagging = false;
	private AbstractRobot abstract_robot;
	private NodeVec2 turret_configuration;
	private NodeVec2 steering_configuration;
	private NodeDouble gripper_fingers_configuration;
	private i_FSA_ba STATE_MACHINE;

	public ClayController() {
		tagging = false;
		flag = false;
	}
	
	public ClayController(AbstractRobot ab, NodeVec2 sc, i_FSA_ba SM) {
		this.abstract_robot = ab;
		this.steering_configuration = sc;
		this.STATE_MACHINE = SM;
	}

	public ClayController(AbstractRobot ab, NodeVec2 tc, NodeVec2 sc,
			NodeDouble gfc, i_FSA_ba SM) {
		this.abstract_robot = ab;
		this.turret_configuration = tc;
		this.steering_configuration = sc;
		this.gripper_fingers_configuration = gfc;
		this.STATE_MACHINE = SM;
		flag = false;
	}

	Vec2 last_val = new Vec2();
	double lasttime = -1;
	Vec2 result;
	double dresult;
	double[] allResult = { 0, 0, 0, 0, 0, 0 };
	boolean flag = false;

	public double[] Value(double timestamp) {

		// get object axis in front of ant
		// this.GetObjectInfo(abstract_robot);

		if (DEBUG)
			System.out.println("ClayController: Value()");

		if ((timestamp > lasttime) || (timestamp == -1)) {
			/*--- reset the timestamp ---*/
			if (timestamp > 0)
				lasttime = timestamp;

			// STEER
			result = steering_configuration.Value(timestamp);
			// if(result.r>1)
			// result.setr(1);
			allResult[0] = result.x;
			allResult[1] = result.y;
			allResult[2] = result.t;
			allResult[3] = result.r;

			// System.out.print("ClayCountroller-> result: "+result.x
			// +"\t"+result.y +"\n");

			// TURRET
			allResult[4] = 0;
			if(turret_configuration != null){
				result = turret_configuration.Value(timestamp);
				allResult[4] = result.t;
			}
			// FINGERS
			allResult[5] = 0;
			if(gripper_fingers_configuration != null){
				dresult = gripper_fingers_configuration.Value(timestamp);
				allResult[5] = dresult;
			}
			// STATE DISPLAY
			/*
			 * int state = STATE_MACHINE.Value(timestamp); if (state == 0)
			 * System.out.print("wander \n"); else if (state == 1)
			 * System.out.print("acquire \n"); else if (state == 2)
			 * System.out.print("deliver \n"); else
			 * System.out.print("error \n"); //
			 */
		}
		return (allResult);
	}

	@Override
	public void takeStep() {
		// TODO Auto-generated method stub
		
	}
}
