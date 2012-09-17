package app.Predator_Prey;

import core.basic_api.*;
import core.basic_api.body.*;
import core.clay.*;

/**
 * Prey controller using clay
*/

public class PreyController extends AgentController {
	public double TimeStamp = 0;

	private i_FSA_ba STATE_MACHINE;
	private NodeVec2 steering_configuration;


	public PreyController() {
	}

	public PreyController(AbstractRobot ab) {
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
		// perceptual schemas
		NodeVec2Array PS_OBS_PREDATORS = new va_Obstacles_r(ab, 1);
		NodeVec2Array PS_OBS_PREYS = new va_Obstacles_r(ab, 2);
		NodeVec2Array PS_OBS_WALLS = new va_Obstacles_r(ab, 3);
		NodeVec2 PS_CLOSEST_PREDATOR = new v_Closest_i(1, ab);

		// triggers
		NodeBoolean PF_SEE_PREDATOR = new b_NonZero_v(PS_CLOSEST_PREDATOR);
		NodeBoolean PF_NOT_SEE_PREDATOR = new b_Not_s(PF_SEE_PREDATOR);

		// motor schemas
		NodeVec2 MS_RANDOM = new v_Noise_(2, ab);
		NodeVec2 MS_AVOID_PREDATORS = new v_Avoid_va(1, 0.6, PS_OBS_PREDATORS, ab);
		NodeVec2 MS_AVOID_PREYS = new v_Avoid_va(0.7, 0.3, PS_OBS_PREYS, ab);
		NodeVec2 MS_AVOID_WALLS = new v_Avoid_va(0.7, 0.3, PS_OBS_WALLS, ab);

		// behavioral assemblages
		v_StaticWeightedSum_va AS_RANDOM = new v_StaticWeightedSum_va();
		AS_RANDOM.weights[0] = 0.6;
		AS_RANDOM.embedded[0] = MS_RANDOM;
		AS_RANDOM.weights[1] = 0.6;
		AS_RANDOM.embedded[1] = MS_AVOID_PREYS;
		AS_RANDOM.weights[2] = 1.0;
		AS_RANDOM.embedded[2] = MS_AVOID_WALLS;

		v_StaticWeightedSum_va AS_AVOID = new v_StaticWeightedSum_va();
		AS_AVOID.weights[0] = 1.0;
		AS_AVOID.embedded[0] = MS_AVOID_PREDATORS;
		AS_AVOID.weights[1] = 0.6;
		AS_AVOID.embedded[1] = MS_AVOID_PREYS;
		AS_AVOID.weights[2] = 0.8;
		AS_AVOID.embedded[2] = MS_AVOID_WALLS;

		// FSA
		STATE_MACHINE = new i_FSA_ba();
		STATE_MACHINE.triggers[0][0] = PF_SEE_PREDATOR;
		STATE_MACHINE.follow_on[0][0] = 1;
		STATE_MACHINE.triggers[1][0] = PF_NOT_SEE_PREDATOR;
		STATE_MACHINE.follow_on[1][0] = 0;
		
		// configuration
		v_Select_vai CONFIGURATION = new v_Select_vai((NodeInt)STATE_MACHINE);
		CONFIGURATION.embedded[0] = AS_RANDOM;
		CONFIGURATION.embedded[1] = AS_AVOID;
		steering_configuration = CONFIGURATION;
	}
}
