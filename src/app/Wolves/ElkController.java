package app.Wolves;

import java.awt.Color;

import core.basic_api.AbstractRobot;
import core.basic_api.AgentController;
import core.basic_api.ClayController;
import core.basic_api.body.AntBody;
import core.clay.NodeBoolean;
import core.clay.NodeDouble;
import core.clay.NodeInt;
import core.clay.NodeScalar;
import core.clay.NodeVec2;
import core.clay.NodeVec2Array;
import core.clay.b_Close_vr;
import core.clay.b_Equal_br;
import core.clay.b_Equal_i;
import core.clay.b_NonZero_v;
import core.clay.b_Not_s;
import core.clay.b_Probability_s;
import core.clay.b_TimeOutCounter_i;
import core.clay.b_between_d;
import core.clay.b_bumped_r;
import core.clay.d_Select_i;
import core.clay.i_FSA_ba;
import core.clay.i_InGripper_r;
import core.clay.v_Avoid_va;
import core.clay.v_Closest_;
import core.clay.v_Closest_i;
import core.clay.v_FixedPoint_;
import core.clay.v_GlobalToEgo_rv;
import core.clay.v_LinearAttraction2_v;
import core.clay.v_LinearAttraction_v;
import core.clay.v_Noise2_;
import core.clay.v_Noise_;
import core.clay.v_Select_vai;
import core.clay.v_Spiral_r;
import core.clay.v_StaticWeightedSum_va;
import core.clay.v_Stop_r;
import core.clay.v_Swirl_vav;
import core.clay.va_Obstacles_r;

/*
 * Ant controller with Subdue
 * This example program is simulating the ant's subdue behavior.
 * 
 * date: 2011/8/9
 * Tucker Balch group
 * static machine design by Andy
 * program by YuTing
 * 
 */

public class ElkController extends AgentController {

	public double TimeStamp = 0;
	private double[] avoidPara = { 0.7, 0.3 }; // (%) [0] > [1]
	private double noiseTimeOut = 2;
	private double interactingTimeOut = 0.5;
	private double departTimeOut = 0.5;
	private double avoidTimeOut = 3;

	private NodeVec2 turret_configuration;
	private NodeVec2 steering_configuration;
	private NodeDouble gripper_fingers_configuration;
	private NodeVec2Array PS_OBS;
	private NodeVec2Array PS_OBS_OF_ANT;
	private NodeVec2 PS_CLOSEST_ANT;
	private NodeBoolean SEE_ANT;
	private NodeBoolean NOT_SEE_ANT;
	private NodeBoolean GET_HEAD;
	private NodeBoolean INTERACTING_TIME_OUT;
	private NodeBoolean DEPART_TIME_OUT;
	private NodeBoolean AVOID_TIME_OUT;
	private NodeBoolean IS_BUMPED;
	private NodeBoolean IS_NO_BUMPED;
	private NodeDouble WEIGHT_FROM_STATUS;
	// private NodeScalar TARGET0_DROP;
	private NodeBoolean NOT_ANT_VISIBLE;
	private NodeBoolean CLOSE_TO_ANT;
	private NodeVec2 MS_AVOID_OBSTACLES;
	private NodeVec2 MS_AVOID_ANTS;
	private NodeVec2 MS_FOLLOW_ANT;
	private NodeVec2 MS_NOISE_VECTOR;
	private NodeVec2 MS_NOT_MOVE;
	private v_StaticWeightedSum_va AS_EXPLORE;
	private v_StaticWeightedSum_va AS_GOHEAD;
	private v_StaticWeightedSum_va AS_INTERACTING;
	private v_StaticWeightedSum_va AS_DEPART;
	private v_StaticWeightedSum_va AS_AVOID;
	private i_FSA_ba STATE_MACHINE;
	private String[] stateName={"EXPLORE","GOHEAD","INTERACTING","DEPART","AVOID"};// set up states' name

	
	public ElkController() {
	}

	public ElkController(AbstractRobot ab) {
		this.setBody(ab);
		configure(ab);
		clayControl = new ClayController(ab, steering_configuration, STATE_MACHINE);
	}

	public void takeStep() {
		TimeStamp = body.getTimeStamp();
		body.setDisplayText(body.getAgentId() + "\n", body.getLoc().x, body
				.getLoc().y, Color.magenta);
		int state = STATE_MACHINE.state;
		double[] allTurnRate = clayControl.Value(TimeStamp);
		allTurnRate[3] = ((AntBody)body).getMaxSpeed();
		if(state == 1) allTurnRate[3] = 0.7*((AntBody)body).getMaxSpeed();
		if(state == 2) allTurnRate[3] = 0;
		if(state == 3) allTurnRate[3] = 0;
		this.getFinalTurnRate(allTurnRate, TimeStamp);
		Color tc = Color.black;
		if(state == 4) tc = Color.red;
		if(state == 3) tc = Color.gray;
		if(state == 2) tc = Color.yellow;
		if(state == 1) tc = Color.blue;
		body.DisplayNearAgent(stateName[STATE_MACHINE.state], tc);
	}

	public void configure(AbstractRobot ab) {
		// ======
		// perceptual schemas
		// ======

		// --- robot's global position
		// --- obstacles
		PS_OBS = new va_Obstacles_r(ab, 3);
		// --- obstacles of ants
		PS_OBS_OF_ANT = new va_Obstacles_r(ab, 1);
		// --- get the closest one
		PS_CLOSEST_ANT = new v_Closest_(1, ab);
		// other ants insight
		SEE_ANT = new b_NonZero_v(PS_CLOSEST_ANT);
		// no other ants insight
		NOT_SEE_ANT = new b_Not_s(SEE_ANT);
		// proper distance to interacting
		GET_HEAD = new b_between_d(0, 0.004, PS_CLOSEST_ANT, ab);
		// interacting time length
		INTERACTING_TIME_OUT = new b_TimeOutCounter_i(interactingTimeOut, ab);
		// depart time length
		DEPART_TIME_OUT = new b_TimeOutCounter_i(departTimeOut, ab);
		// avoid time length
		AVOID_TIME_OUT = new b_TimeOutCounter_i(avoidTimeOut, ab);
		// close to ANT?
		CLOSE_TO_ANT = new b_Close_vr(0.003, PS_CLOSEST_ANT, ab);

		// is bumped?
		IS_BUMPED = new b_bumped_r(ab);
		// is no bumped?
		IS_NO_BUMPED = new b_Not_s(IS_BUMPED);

		// ======
		// motor schemas
		// ======
		// avoid obstacles
		MS_AVOID_OBSTACLES = new v_Avoid_va(avoidPara[0], avoidPara[1], PS_OBS, ab);
		// avoid ants
		MS_AVOID_ANTS = new v_Avoid_va(avoidPara[0], avoidPara[1], PS_OBS_OF_ANT, ab);
		// follow ant
		MS_FOLLOW_ANT = new v_LinearAttraction2_v(ab, 1, 0.0, PS_CLOSEST_ANT);
		// random noise
		MS_NOISE_VECTOR = new v_Noise2_(noiseTimeOut, ab);
		// freeze
		MS_NOT_MOVE = new v_Stop_r();
		
		// ======
		// AS_EXPLORE
		// ======
		AS_EXPLORE = new v_StaticWeightedSum_va();
		AS_EXPLORE.weights[0] = 3;
		AS_EXPLORE.embedded[0] = MS_AVOID_OBSTACLES;

		AS_EXPLORE.weights[1] = 0.5;
		AS_EXPLORE.embedded[1] = MS_NOISE_VECTOR;

		// ======
		// AS_GOHEAD
		// ======
		AS_GOHEAD = new v_StaticWeightedSum_va();
		AS_GOHEAD.weights[0] = 3.0;
		AS_GOHEAD.embedded[0] = MS_AVOID_OBSTACLES;

		AS_GOHEAD.weights[1] = 0.1;
		AS_GOHEAD.embedded[1] = MS_NOISE_VECTOR;

		AS_GOHEAD.weights[2] = 0.6;
		AS_GOHEAD.embedded[2] = MS_FOLLOW_ANT;

		// ======
		// AS_INTERACTING
		// ======
		AS_INTERACTING = new v_StaticWeightedSum_va();
		AS_INTERACTING.weights[0] = 1.0;
		AS_INTERACTING.embedded[0] = MS_NOT_MOVE;

		AS_INTERACTING.weights[1] = 0.0;
		AS_INTERACTING.embedded[1] = MS_AVOID_OBSTACLES;

		AS_INTERACTING.weights[2] = 0.0;
		AS_INTERACTING.embedded[2] = MS_NOISE_VECTOR;

		AS_INTERACTING.weights[3] = 0.6;
		AS_INTERACTING.embedded[3] = MS_FOLLOW_ANT;

		// ======
		// AS_DEPART
		// ======
		AS_DEPART = new v_StaticWeightedSum_va();
		AS_DEPART.weights[0] = 3.0;
		AS_DEPART.embedded[0] = MS_AVOID_OBSTACLES;

		AS_DEPART.weights[1] = 0.1;
		AS_DEPART.embedded[1] = MS_NOISE_VECTOR;

		AS_DEPART.weights[2] = 0.60;
		AS_DEPART.embedded[2] = MS_AVOID_ANTS;

		// ======
		// AS_AVOID
		// ======
		AS_AVOID = new v_StaticWeightedSum_va();
		AS_AVOID.weights[0] = 3.0;
		AS_AVOID.embedded[0] = MS_AVOID_OBSTACLES;

		AS_AVOID.weights[1] = 0.1;
		AS_AVOID.embedded[1] = MS_NOISE_VECTOR;
//
		AS_AVOID.weights[2] = 0.60;
		AS_AVOID.embedded[2] = MS_AVOID_ANTS;

		// ======
		// STATE_MACHINE
		// ======
		STATE_MACHINE = new i_FSA_ba();
		STATE_MACHINE.state = 0;
		int stateNum = 0;
		// STATE 0 AS_EXPLORE
		STATE_MACHINE.triggers[stateNum][0] = SEE_ANT;
		STATE_MACHINE.follow_on[stateNum][0] = 1; // transition to GOHEAD
		stateNum++;
		// STATE 1 AS_GOHEAD
		STATE_MACHINE.triggers[stateNum][0] = NOT_SEE_ANT;
		STATE_MACHINE.follow_on[stateNum][0] = 0;
		STATE_MACHINE.triggers[stateNum][1] = GET_HEAD;
		STATE_MACHINE.follow_on[stateNum][1] = 2; // transition to INTERACTING
		stateNum++;
		// STATE 2 AS_INTERACTING
		STATE_MACHINE.triggers[stateNum][0] = NOT_SEE_ANT;
		STATE_MACHINE.follow_on[stateNum][0] = 0; // transition to EXPLORE
		STATE_MACHINE.triggers[stateNum][1] = INTERACTING_TIME_OUT;
		STATE_MACHINE.follow_on[stateNum][1] = 3; // transition to DEPART
		stateNum++;
		// STATE 3 AS_DEPART
		STATE_MACHINE.triggers[stateNum][0] = DEPART_TIME_OUT; // TARGET0_IN_GRIPPER;
		STATE_MACHINE.follow_on[stateNum][0] = 4; // transition to
		stateNum++;
		// STATE 4 AS_AVOID
		STATE_MACHINE.triggers[stateNum][0] = AVOID_TIME_OUT; // TARGET0_IN_GRIPPER;
		STATE_MACHINE.follow_on[stateNum][0] = 0; // transition to
		// ======
		// STEERING
		// ======
		v_Select_vai STEERING = new v_Select_vai((NodeInt) STATE_MACHINE);
		stateNum = 0;
		STEERING.embedded[stateNum++] = AS_EXPLORE;
		STEERING.embedded[stateNum++] = AS_GOHEAD;
		STEERING.embedded[stateNum++] = AS_INTERACTING;
		STEERING.embedded[stateNum++] = AS_DEPART;
		STEERING.embedded[stateNum++] = AS_AVOID;

		steering_configuration = STEERING;

	}
}