package app.SimpleAnts;

import core.basic_api.*;
import core.basic_api.body.*;
import core.clay.*;

public class AntController extends AgentController {

	public double TimeStamp = 0;
	private double noiseTimeOut = 2;

	//private NodeVec2Array PS_OBS_WALLS;
	//private NodeVec2Array PS_OBS_ANTS;

	private NodeVec2 MS_NOISE_VECTOR;
	//private NodeVec2 MS_AVOID_WALLS;
	//private NodeVec2 MS_AVOID_ANTS;

	private NodeBoolean TRIGGER;
	private v_StaticWeightedSum_va AS_RANDOM;

	private i_FSA_ba STATE_MACHINE;

	private NodeVec2 steering_configuration;

	
	public AntController() {
	}

	public AntController(AbstractRobot ab) {
		this.setBody(ab);
		configure(ab);
		clayControl = new ClayController(ab, steering_configuration, STATE_MACHINE);
	}

	public void takeStep() {
		TimeStamp = body.getTimeStamp();
		double[] allTurnRate = clayControl.Value(TimeStamp);
		this.getFinalTurnRate(allTurnRate, TimeStamp);
	}

	public void configure(AbstractRobot ab) {
		// ======
		// perceptual schemas
		// ======
		//PS_OBS_WALLS = new va_Obstacles_r(ab, 3);
		//PS_OBS_ANTS = new va_Obstacles_r(ab, 1);

		TRIGGER = new b_Probability_s(1);

		// ======
		// motor schemas
		// ======
		MS_NOISE_VECTOR = new v_Noise2_(noiseTimeOut, ab);
		//MS_AVOID_WALLS = new v_Avoid_va(0.7, 0.3, PS_OBS_WALLS, ab);
		//MS_AVOID_ANTS = new v_Avoid_va(2.0, 0.3, PS_OBS_ANTS, ab);

		// =====
		// AS_RANDOM
		// =====
		AS_RANDOM = new v_StaticWeightedSum_va();
		AS_RANDOM.weights[0] = 0.5;
		AS_RANDOM.embedded[0] = MS_NOISE_VECTOR;
		//AS_RANDOM.weights[1] = 1.0;
		//AS_RANDOM.embedded[1] = MS_AVOID_WALLS;
		//AS_RANDOM.weights[2] = 2.0;
		//AS_RANDOM.embedded[2] = MS_AVOID_ANTS;

		// ======
		// STATE_MACHINE
		// ======
		int stateNum = 0;
		STATE_MACHINE = new i_FSA_ba();
		STATE_MACHINE.state = 0;
		STATE_MACHINE.triggers[stateNum][0] = TRIGGER;
		STATE_MACHINE.follow_on[stateNum][0] = 0;

		// ======
		// CONFIGURATION
		// ======
		v_Select_vai CONFIGURATION = new v_Select_vai((NodeInt) STATE_MACHINE);
		CONFIGURATION.embedded[stateNum] = AS_RANDOM;

		steering_configuration = CONFIGURATION;
	}
}
