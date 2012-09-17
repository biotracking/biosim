package app.test;

import java.awt.Color;

import core.basic_api.AbstractRobot;
import core.basic_api.AgentController;
import core.basic_api.ClayController;
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
import core.clay.b_bumped_r;
import core.clay.d_Select_i;
import core.clay.i_FSA_ba;
import core.clay.i_InGripper_r;
import core.clay.v_Avoid_va;
import core.clay.v_Closest_i;
import core.clay.v_FixedPoint_;
import core.clay.v_GlobalToEgo_rv;
import core.clay.v_LinearAttraction2_v;
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

public class TestController extends AgentController {

	public double TimeStamp = 0;
	private double[] avoidPara = { 0.7, 0.3 }; // (%) [0] > [1]
	private double noiseTimeOut = 2;
	private int exploreTimeOut = 8;
	private int followTimeOut = 7;
	private int subdueTimeOut = 8;
	private int searchAntTimeOut = 8;
	private int searchPreyTimeOut = 8;
	private int deliver1TimeOut = 9;
	private int deliver2TimeOut = 9;

	private NodeVec2 turret_configuration;
	private NodeVec2 steering_configuration;
	private NodeDouble gripper_fingers_configuration;
	private NodeVec2Array PS_OBS;
	private NodeVec2Array PS_OBS_OF_ANT;
	// private NodeVec2 PS_GLOBAL_POS;
	private NodeVec2 PS_POS;
	private NodeVec2 PS_HOMEBASE0_GLOBAL;// the place to deliver
	private NodeVec2 PS_HOMEBASE0; // make it egocentric
	// private NodeVec2Array PS_TARGETS0_EGO;
	// private NodeVec2Array PS_TARGETS0_GLOBAL;
	// private NodeVec2Array PS_TARGETS0_GLOBAL_FILT;
	// private NodeVec2Array PS_TARGETS0_EGO_FILT;
	private NodeVec2 PS_CLOSEST_PREY;
	// private NodeVec2 PS_SUBDUED_PREY;
	private NodeInt PS_IN_GRIPPER;
	// private NodeVec2Array PS_TARGETS1_EGO;
	// private NodeVec2Array PS_TARGETS1_GLOBAL;
	// private NodeVec2Array PS_TARGETS1_EGO_FILT;
	private NodeVec2 PS_CLOSEST_ANT;
	// private NodeVec2 PS_SPIRAL_POINT;

	private NodeBoolean START_MOVE;
	private NodeBoolean SEE_PREY;
	private NodeBoolean SEE_ANT;
	// private NodeBoolean PF_TOUCH_ALIVE;
	// private NodeBoolean PF_IS_FOLLOW_ANT;
	// private NodeBoolean PF_IS_ENEMY_ANT;
	// private NodeBoolean PF_IS_PREY;
	// private NodeBoolean PS_PROBABILITY_SUBDUED_PREY;
	// private NodeBoolean PF_SUBDUED_PREY;
	// private NodeBoolean PF_NOT_SUBDUED_PREY;
	private NodeBoolean EXPLORE_TIME_OUT;
	private NodeBoolean IS_SUBDUED_GIVE_UP;
	private NodeBoolean SUBDUED_TIME_OUT;
	private NodeBoolean IS_BUMPED;
	private NodeBoolean IS_NO_BUMPED;
	private NodeBoolean FOLLOW_TIME_OUT;
	private NodeScalar PREY_IN_GRIPPER;
	private NodeBoolean PREY_NOT_IN_GRIPPER;
	private NodeScalar TARGET1_IN_GRIPPER;
	// private NodeScalar TARGET0_DROP;
	private NodeBoolean NOT_ANT_VISIBLE;
	private NodeBoolean NOT_PREY_VISIBLE;
	private NodeBoolean CLOSE_TO_HOMEBASE0;
	private NodeBoolean CLOSE_TO_PREY;
	private NodeBoolean NOT_CLOSE_TO_PREY;
	private NodeBoolean CLOSE_TO_ANT;
	private NodeBoolean ACQUIRE_TIME_OUT;
	private NodeBoolean SEARCH_ANT_TIME_OUT;
	private NodeBoolean SEARCH_PREY_TIME_OUT;
	private NodeBoolean DELIVER_1_TIME_OUT;
	private NodeBoolean DELIVER_2_TIME_OUT;
	private NodeVec2 MS_AVOID_OBSTACLES;
	private NodeVec2 MS_AVOID_ANTS;
	private NodeVec2 MS_FOLLOW_ANT;
	private NodeVec2 MS_NOISE_VECTOR;
	private NodeVec2 MS_SWIRL_OBSTACLES_PREY;
	private NodeVec2 MS_SWIRL_OBSTACLES_HOMEBASE0;
	private NodeVec2 MS_MOVE_TO_HOMEBASE0;
	private NodeVec2 MS_MOVE_TO_PREY;
	private NodeVec2 MS_MOVE_TO_SUBDUE_TARGET;
	private NodeVec2 MS_NOT_MOVE;
	private NodeVec2 MS_SWIRL_OBSTACLES_NOISE;
	private NodeVec2 MS_SWIRL_OBSTACLES_SPIRAL;
	private NodeVec2 MS_SPIRAL;
	private NodeVec2 MS_RELEASE_IN_HOME;
	private v_StaticWeightedSum_va AS_LOITER;
	private v_StaticWeightedSum_va AS_EXPLORE;
	private v_StaticWeightedSum_va AS_FOLLOW;
	private v_StaticWeightedSum_va AS_ACQUIRE;
	private v_StaticWeightedSum_va AS_SUBDUE;
	private v_StaticWeightedSum_va AS_SEARCH_ANT;
	private v_StaticWeightedSum_va AS_SEARCH_PREY;
	private v_StaticWeightedSum_va AS_DELIVER_TO_HOME;
	private v_StaticWeightedSum_va AS_DELIVER_TO_RANDOM_PLACE;
	private v_StaticWeightedSum_va AS_RELEASE;
	private i_FSA_ba STATE_MACHINE;

	public TestController() {
	}

	public TestController(AbstractRobot ab) {
		this.setBody(ab);
		configure(ab);
		clayControl = new ClayController(ab, turret_configuration,
				steering_configuration, gripper_fingers_configuration,
				STATE_MACHINE);

	}

	public void takeStep() {
		TimeStamp = body.getTimeStamp();
		body.setDisplayText(body.getAgentId() + "\n", body.getLoc().x, body
				.getLoc().y, Color.magenta);
		//double[] allTurnRate = clayControl.Value(TimeStamp);
		//this.getFinalTurnRate(allTurnRate, TimeStamp);
		this.body.setDesiredSpeed(5);
		this.body.setDesiredTurnRate(0);
	}

	public void configure(AbstractRobot ab) {
		// ======
		// perceptual schemas
		// ======

		// --- robot's global position
		// PS_GLOBAL_POS = new v_GlobalPosition_r(ab);
		PS_POS = new v_FixedPoint_(0, 0);
		// --- obstacles
		PS_OBS = new va_Obstacles_r(ab, 3);
		// --- obstacles of ants
		PS_OBS_OF_ANT = new va_Obstacles_r(ab, 1);
		// --- homebase
		PS_HOMEBASE0_GLOBAL = new v_FixedPoint_(ab.getHomeLoc()[0], ab
				.getHomeLoc()[1]);
		PS_HOMEBASE0 = new v_GlobalToEgo_rv(ab, PS_HOMEBASE0_GLOBAL);

		// --- targets of visual type 2 (prey)
		// PS_TARGETS0_EGO = new va_VisualObjects_r(2,ab);
		// PS_TARGETS0_GLOBAL = new va_Add_vav(PS_TARGETS0_EGO, PS_GLOBAL_POS);
		// --- filter out targets close to homebase
		// PS_TARGETS0_GLOBAL_FILT = new
		// va_FilterOutClose_vva(0.02*this.Meter2Pixel, PS_HOMEBASE0_GLOBAL,
		// PS_TARGETS0_GLOBAL);
		// --- make them ego
		// PS_TARGETS0_EGO_FILT = new va_Subtract_vav(PS_TARGETS0_GLOBAL_FILT,
		// PS_GLOBAL_POS);
		// --- get the closest one
		PS_CLOSEST_PREY = new v_Closest_i(2, ab);

		// --- targets of visual type 1 (ant)
		// PS_TARGETS1_EGO = new va_VisualObjects_r(1,ab);
		// PS_TARGETS1_GLOBAL = new va_Add_vav(PS_TARGETS1_EGO, PS_GLOBAL_POS);
		// --- make them ego
		// PS_TARGETS1_EGO_FILT = new va_Subtract_vav(PS_TARGETS1_GLOBAL,
		// PS_GLOBAL_POS);
		// --- get the closest one
		PS_CLOSEST_ANT = new v_Closest_i(1, ab);

		// --- type of object in the gripper
		PS_IN_GRIPPER = new i_InGripper_r(ab);
		// get probability of subdued
		// PS_PROBABILITY_SUBDUED_PREY = new b_Probability_s(ab);

		// ======
		// Perceptual Features
		// ======
		// start move
		START_MOVE = new b_Probability_s(0.002);
		// see prey?
		SEE_PREY = new b_NonZero_v(PS_CLOSEST_PREY);
		// see ant?
		SEE_ANT = new b_NonZero_v(PS_CLOSEST_ANT);
		// not see prey?
		NOT_PREY_VISIBLE = new b_Not_s(SEE_PREY);
		// not see ant?
		NOT_ANT_VISIBLE = new b_Not_s(SEE_ANT);
		// --- set subdued time out and give up
		EXPLORE_TIME_OUT = new b_TimeOutCounter_i(exploreTimeOut, ab);

		// touch alive? (when ant touch a living ceature)
		// close to PREY?
		CLOSE_TO_PREY = new b_Close_vr(0.003, PS_CLOSEST_PREY, ab);
		// not close to PREY?
		NOT_CLOSE_TO_PREY = new b_Not_s(CLOSE_TO_PREY);
		// close to ANT?
		CLOSE_TO_ANT = new b_Close_vr(0.003, PS_CLOSEST_ANT, ab);
		// is prey in the gripper?
		PREY_IN_GRIPPER = new b_Equal_i(2, PS_IN_GRIPPER); // prey
		// is taget drop?
		PREY_NOT_IN_GRIPPER = new b_Not_s(PREY_IN_GRIPPER);
		// --- set subdued time out and give up
		SUBDUED_TIME_OUT = new b_TimeOutCounter_i(subdueTimeOut, ab);

		// is bumped?
		IS_BUMPED = new b_bumped_r(ab);
		// is no bumped?
		IS_NO_BUMPED = new b_Not_s(IS_BUMPED);
		// bumped time out
		FOLLOW_TIME_OUT = new b_TimeOutCounter_i(followTimeOut, ab);
		// is something in the gripper?
		// TARGET1_IN_GRIPPER = new b_Equal_i(1,PS_IN_GRIPPER); //ant
		// is follow ant?
		// IS_FOLLOW_ANT = new b_Equal_i(1,PS_IN_GRIPPER); // follow ant type 1
		// is enemy ant?
		// IS_ENEMY_ANT = new b_Equal_i(4,PS_IN_GRIPPER); //enemy ant type 4

		// is subdued prey?
		// SUBDUED_PREY = new b_probabilitySubduedPrey_r(ab);
		// not subdued prey?
		// NOT_SUBDUED_PREY = new b_Not_s(SUBDUED_PREY);
		// is subdued give up?
		IS_SUBDUED_GIVE_UP = new b_Equal_br(PREY_NOT_IN_GRIPPER, SUBDUED_TIME_OUT, ab);
		// is drop?
		// TARGET0_DROP = new b_Probability_s(1);
		// close to homebase
		CLOSE_TO_HOMEBASE0 = new b_Close_vr(0.005, PS_HOMEBASE0_GLOBAL, ab);

		SEARCH_ANT_TIME_OUT = new b_TimeOutCounter_i(searchAntTimeOut, ab);

		SEARCH_PREY_TIME_OUT = new b_TimeOutCounter_i(searchPreyTimeOut, ab);

		DELIVER_1_TIME_OUT = new b_TimeOutCounter_i(deliver1TimeOut, ab);

		DELIVER_2_TIME_OUT = new b_TimeOutCounter_i(deliver2TimeOut, ab);

		// ======
		// motor schemas
		// ======
		// avoid obstacles
		MS_AVOID_OBSTACLES = new v_Avoid_va(avoidPara[0], avoidPara[1], PS_OBS, ab);
		// avoid ants
		MS_AVOID_ANTS = new v_Avoid_va(avoidPara[0], avoidPara[1],
				PS_OBS_OF_ANT, ab);
		// follow ant
		MS_FOLLOW_ANT = new v_LinearAttraction2_v(ab, 1, 0.0, PS_CLOSEST_ANT);
		// swirl obstacles wrt target 0
		MS_SWIRL_OBSTACLES_PREY = new v_Swirl_vav(1.0, avoidPara[1], PS_OBS,
				PS_CLOSEST_PREY);
		// swirl obstacles wrt homebase0
		MS_SWIRL_OBSTACLES_HOMEBASE0 = new v_Swirl_vav(1.0, avoidPara[1],
				PS_OBS, PS_HOMEBASE0);
		// go home 0
		MS_MOVE_TO_HOMEBASE0 = new v_LinearAttraction2_v(ab, 1, 0.0,
				PS_HOMEBASE0);
		// go to target0
		MS_MOVE_TO_PREY = new v_LinearAttraction2_v(ab, 1, 0.0, PS_CLOSEST_PREY);
		// noise vector
		MS_NOISE_VECTOR = new v_Noise_(noiseTimeOut, ab);
		// swirl obstacles wrt noise
		MS_SWIRL_OBSTACLES_NOISE = new v_Swirl_vav(1.0, avoidPara[1], PS_OBS,
				MS_NOISE_VECTOR);
		// go to subdue target
		// MS_MOVE_TO_SUBDUE_TARGET = new
		// v_LinearAttraction2_v(ab,1,0.0,PS_SUBDUED_PREY);
		// not move
		MS_NOT_MOVE = new v_Stop_r();
		// spiral
		MS_SPIRAL = new v_Spiral_r(SEE_PREY);
		// swirl obstacles wrt noise
		MS_SWIRL_OBSTACLES_SPIRAL = new v_Swirl_vav(1.0, avoidPara[1], PS_OBS,
				MS_SPIRAL);

		// ======
		// AS_LOITER
		// ======
		AS_LOITER = new v_StaticWeightedSum_va();
		AS_LOITER.weights[0] = 1.0;
		AS_LOITER.embedded[0] = MS_NOT_MOVE;

		AS_LOITER.weights[1] = 0.01;
		AS_LOITER.embedded[1] = MS_NOISE_VECTOR;

		AS_LOITER.weights[2] = 0.01;
		AS_LOITER.embedded[2] = MS_AVOID_OBSTACLES;

		AS_LOITER.weights[3] = 0.01;
		AS_LOITER.embedded[3] = MS_AVOID_ANTS;

		// ======
		// AS_EXPLORE
		// ======
		AS_EXPLORE = new v_StaticWeightedSum_va();
		AS_EXPLORE.weights[0] = 0.6;
		AS_EXPLORE.embedded[0] = MS_AVOID_OBSTACLES;

		AS_EXPLORE.weights[1] = 0.8;
		AS_EXPLORE.embedded[1] = MS_NOISE_VECTOR;

		AS_EXPLORE.weights[2] = 0.1;
		AS_EXPLORE.embedded[2] = MS_SWIRL_OBSTACLES_NOISE;

		AS_EXPLORE.weights[3] = 0.6;
		AS_EXPLORE.embedded[3] = MS_AVOID_ANTS;

		// ======
		// AS_FOLLOW
		// ======
		AS_FOLLOW = new v_StaticWeightedSum_va();
		AS_FOLLOW.weights[0] = 1.0;
		AS_FOLLOW.embedded[0] = MS_AVOID_OBSTACLES;

		AS_FOLLOW.weights[1] = 0.2;
		AS_FOLLOW.embedded[1] = MS_NOISE_VECTOR;

		AS_FOLLOW.weights[2] = 0.5;
		AS_FOLLOW.embedded[2] = MS_FOLLOW_ANT;

		AS_FOLLOW.weights[3] = 1.0;
		AS_FOLLOW.embedded[3] = MS_AVOID_ANTS;

		// ======
		// AS_ACQUIRE
		// ======
		AS_ACQUIRE = new v_StaticWeightedSum_va();
		AS_ACQUIRE.weights[0] = 1.0;
		AS_ACQUIRE.embedded[0] = MS_AVOID_OBSTACLES;

		AS_ACQUIRE.weights[1] = 0.6;
		AS_ACQUIRE.embedded[1] = MS_MOVE_TO_PREY;

		AS_ACQUIRE.weights[2] = 0.4;
		AS_ACQUIRE.embedded[2] = MS_SWIRL_OBSTACLES_PREY;

		AS_ACQUIRE.weights[3] = 0.1;
		AS_ACQUIRE.embedded[3] = MS_NOISE_VECTOR;

		AS_ACQUIRE.weights[4] = 1.0;
		AS_ACQUIRE.embedded[4] = MS_AVOID_ANTS;

		// ======
		// AS_SUBDUE
		// ======
		AS_SUBDUE = new v_StaticWeightedSum_va();
		AS_SUBDUE.weights[0] = 1.0;
		AS_SUBDUE.embedded[0] = MS_NOT_MOVE;

		// ======
		// AS_SEARCH_ANT
		// ======
		AS_SEARCH_ANT = new v_StaticWeightedSum_va();
		AS_SEARCH_ANT.weights[0] = 0.6;
		AS_SEARCH_ANT.embedded[0] = MS_SPIRAL;

		AS_SEARCH_ANT.weights[1] = 1.0;
		AS_SEARCH_ANT.embedded[1] = MS_AVOID_OBSTACLES;

		AS_SEARCH_ANT.weights[2] = 0.2;
		AS_SEARCH_ANT.embedded[2] = MS_NOISE_VECTOR;

		AS_SEARCH_ANT.weights[3] = 0.2;
		AS_SEARCH_ANT.embedded[3] = MS_SWIRL_OBSTACLES_SPIRAL;

		AS_SEARCH_ANT.weights[4] = 1.0;
		AS_SEARCH_ANT.embedded[4] = MS_AVOID_ANTS;

		// ======
		// AS_SEARCH_PREY
		// ======
		AS_SEARCH_PREY = new v_StaticWeightedSum_va();
		AS_SEARCH_PREY.weights[0] = 0.6;
		AS_SEARCH_PREY.embedded[0] = MS_SPIRAL;

		AS_SEARCH_PREY.weights[1] = 1.0;
		AS_SEARCH_PREY.embedded[1] = MS_AVOID_OBSTACLES;

		AS_SEARCH_PREY.weights[2] = 0.2;
		AS_SEARCH_PREY.embedded[2] = MS_NOISE_VECTOR;

		AS_SEARCH_PREY.weights[3] = 0.2;
		AS_SEARCH_PREY.embedded[3] = MS_SWIRL_OBSTACLES_SPIRAL;

		AS_SEARCH_PREY.weights[4] = 1.0;
		AS_SEARCH_PREY.embedded[4] = MS_AVOID_ANTS;

		// ======
		// AS_DELIVER_TO_HOME
		// ======
		AS_DELIVER_TO_HOME = new v_StaticWeightedSum_va();
		AS_DELIVER_TO_HOME.weights[0] = 1.0;
		AS_DELIVER_TO_HOME.embedded[0] = MS_AVOID_OBSTACLES;

		AS_DELIVER_TO_HOME.weights[1] = 1.0;
		AS_DELIVER_TO_HOME.embedded[1] = MS_MOVE_TO_HOMEBASE0;

		AS_DELIVER_TO_HOME.weights[2] = 0.2;
		AS_DELIVER_TO_HOME.embedded[2] = MS_SWIRL_OBSTACLES_HOMEBASE0;

		AS_DELIVER_TO_HOME.weights[3] = 1.0;
		AS_DELIVER_TO_HOME.embedded[3] = MS_AVOID_ANTS;

		AS_DELIVER_TO_HOME.weights[4] = 0.2;
		AS_DELIVER_TO_HOME.embedded[4] = MS_NOISE_VECTOR;// */

		// ======
		// AS_DELIVER_TO_RANDOM_PLACE
		// ======
		AS_DELIVER_TO_RANDOM_PLACE = new v_StaticWeightedSum_va();
		AS_DELIVER_TO_RANDOM_PLACE.weights[0] = 1.0;
		AS_DELIVER_TO_RANDOM_PLACE.embedded[0] = MS_AVOID_OBSTACLES;

		AS_DELIVER_TO_RANDOM_PLACE.weights[1] = 0.2;
		AS_DELIVER_TO_RANDOM_PLACE.embedded[1] = MS_MOVE_TO_HOMEBASE0;

		AS_DELIVER_TO_RANDOM_PLACE.weights[2] = 0.2;
		AS_DELIVER_TO_RANDOM_PLACE.embedded[2] = MS_SWIRL_OBSTACLES_HOMEBASE0;

		AS_DELIVER_TO_RANDOM_PLACE.weights[3] = 1.0;
		AS_DELIVER_TO_RANDOM_PLACE.embedded[3] = MS_AVOID_ANTS;

		AS_DELIVER_TO_RANDOM_PLACE.weights[4] = 1.0;
		AS_DELIVER_TO_RANDOM_PLACE.embedded[4] = MS_NOISE_VECTOR;// */
		// ======
		// AS_RELEASE
		// ======
		AS_RELEASE = new v_StaticWeightedSum_va();
		AS_RELEASE.weights[0] = 1.0;
		AS_RELEASE.embedded[0] = MS_NOT_MOVE;

		// ======
		// STATE_MACHINE
		// ======
		STATE_MACHINE = new i_FSA_ba();
		STATE_MACHINE.state = 0;
		int stateNum = 0;
		// STATE 0 AS_LOITER
		STATE_MACHINE.triggers[stateNum][0] = START_MOVE;
		STATE_MACHINE.follow_on[stateNum][0] = stateNum + 1; // transition to
		// AS_EXPLORE
		STATE_MACHINE.triggers[stateNum][1] = IS_BUMPED;
		STATE_MACHINE.follow_on[stateNum][1] = stateNum + 2; // transition to
		// AS_EXPLORE
		STATE_MACHINE.triggers[stateNum][2] = SEE_PREY;
		STATE_MACHINE.follow_on[stateNum][2] = stateNum + 3; // transition to
		// ACQUIRE
		stateNum++;
		// STATE 1 AS_EXPLORE
		STATE_MACHINE.triggers[stateNum][0] = SEE_PREY;
		STATE_MACHINE.follow_on[stateNum][0] = stateNum + 2; // transition to
		// ACQUIRE
		STATE_MACHINE.triggers[stateNum][1] = EXPLORE_TIME_OUT;
		STATE_MACHINE.follow_on[stateNum][1] = stateNum;
		STATE_MACHINE.triggers[stateNum][2] = IS_BUMPED;
		STATE_MACHINE.follow_on[stateNum][2] = stateNum + 1; // transition to
		// AS_EXPLORE
		stateNum++;
		// STATE 2 AS_FOLLOW
		STATE_MACHINE.triggers[stateNum][0] = SEE_PREY;
		STATE_MACHINE.follow_on[stateNum][0] = stateNum + 1; // transition to
		// ACQUIRE
		STATE_MACHINE.triggers[stateNum][1] = FOLLOW_TIME_OUT;
		STATE_MACHINE.follow_on[stateNum][1] = stateNum - 2;
		STATE_MACHINE.triggers[stateNum][2] = NOT_ANT_VISIBLE;
		STATE_MACHINE.follow_on[stateNum][2] = stateNum + 3; // transition to
		// SEARCH ANT
		stateNum++;
		// STATE 3 ACQUIRE
		STATE_MACHINE.triggers[stateNum][0] = CLOSE_TO_PREY; // TARGET0_IN_GRIPPER;
		STATE_MACHINE.follow_on[stateNum][0] = stateNum + 1; // transition to
		// SUBDUE
		STATE_MACHINE.triggers[stateNum][1] = NOT_PREY_VISIBLE;
		STATE_MACHINE.follow_on[stateNum][1] = stateNum - 2; // transition to
		// WANDER
		STATE_MACHINE.triggers[stateNum][2] = ACQUIRE_TIME_OUT;
		STATE_MACHINE.follow_on[stateNum][2] = stateNum - 2; // transition to
		// WANDER
		stateNum++;
		// STATE 4 SUBDUE
		STATE_MACHINE.triggers[stateNum][0] = PREY_IN_GRIPPER; // SUBDUED_PREY;
		STATE_MACHINE.follow_on[stateNum][0] = stateNum + 3; // transition to
		// AS_DELIVER TO
		// HOME
		STATE_MACHINE.triggers[stateNum][1] = SUBDUED_TIME_OUT;
		STATE_MACHINE.follow_on[stateNum][1] = stateNum + 2; // transition to
		// SEARCH PREY
		STATE_MACHINE.triggers[stateNum][1] = NOT_CLOSE_TO_PREY;
		STATE_MACHINE.follow_on[stateNum][1] = stateNum + 2; // transition to
		// SEARCH PREY
		stateNum++;
		// STATE 5(5.1) SEARCH ANT
		STATE_MACHINE.triggers[stateNum][0] = SEE_PREY;
		STATE_MACHINE.follow_on[stateNum][0] = stateNum - 2; // transition to
		// ACQUIRE
		STATE_MACHINE.triggers[stateNum][1] = SEARCH_ANT_TIME_OUT;
		STATE_MACHINE.follow_on[stateNum][1] = stateNum - 4; // transition to
		// EXPLORE
		STATE_MACHINE.triggers[stateNum][2] = SEE_ANT;
		STATE_MACHINE.follow_on[stateNum][2] = stateNum - 3; // transition to
		// FOLLOW
		stateNum++;
		// STATE 6(5.2) SEARCH PREY
		STATE_MACHINE.triggers[stateNum][0] = SEE_PREY;
		STATE_MACHINE.follow_on[stateNum][0] = stateNum - 3; // transition to
		// ACQUIRE
		STATE_MACHINE.triggers[stateNum][1] = SEARCH_PREY_TIME_OUT;
		STATE_MACHINE.follow_on[stateNum][1] = stateNum - 5; // transition to
		// EXPLORE
		stateNum++;
		// STATE 7(6.1) DELIVER TO HOME
		STATE_MACHINE.triggers[stateNum][0] = CLOSE_TO_HOMEBASE0;
		STATE_MACHINE.follow_on[stateNum][0] = stateNum + 2; // transition to
		// DROP IN HOME
		STATE_MACHINE.triggers[stateNum][1] = PREY_NOT_IN_GRIPPER;
		STATE_MACHINE.follow_on[stateNum][1] = stateNum - 1; // transition to
		// SEARCH PREY
		STATE_MACHINE.triggers[stateNum][2] = DELIVER_1_TIME_OUT;
		STATE_MACHINE.follow_on[stateNum][2] = stateNum + 1; // transition to
		// DELIVER TO
		// RANDOM PLACE
		stateNum++;
		// STATE 8(6.2) DELIVER TO RANDOM PLACE
		STATE_MACHINE.triggers[stateNum][0] = CLOSE_TO_HOMEBASE0;
		STATE_MACHINE.follow_on[stateNum][0] = stateNum + 1; // transition to
		// DROP IN HOME
		STATE_MACHINE.triggers[stateNum][1] = PREY_NOT_IN_GRIPPER;
		STATE_MACHINE.follow_on[stateNum][1] = stateNum - 2; // transition to
		// SEARCH PREY
		STATE_MACHINE.triggers[stateNum][2] = DELIVER_2_TIME_OUT;
		STATE_MACHINE.follow_on[stateNum][2] = stateNum - 1; // transition to
		// DELIVER TO
		// HOME
		stateNum++;
		// STATE 9(7) DROP IN HOME
		STATE_MACHINE.triggers[stateNum][0] = PREY_NOT_IN_GRIPPER;
		STATE_MACHINE.follow_on[stateNum][0] = stateNum - 8; // transition to
		// EXPLORE
		stateNum++;
		// */
		// state_monitor = STATE_MACHINE;

		// ======
		// STEERING
		// ======
		v_Select_vai STEERING = new v_Select_vai((NodeInt) STATE_MACHINE);
		stateNum = 0;
		STEERING.embedded[stateNum++] = AS_LOITER;
		STEERING.embedded[stateNum++] = AS_EXPLORE;
		STEERING.embedded[stateNum++] = AS_FOLLOW;
		STEERING.embedded[stateNum++] = AS_ACQUIRE;
		STEERING.embedded[stateNum++] = AS_SUBDUE;
		STEERING.embedded[stateNum++] = AS_SEARCH_ANT;
		STEERING.embedded[stateNum++] = AS_SEARCH_PREY;
		STEERING.embedded[stateNum++] = AS_DELIVER_TO_HOME;
		STEERING.embedded[stateNum++] = AS_DELIVER_TO_RANDOM_PLACE;
		STEERING.embedded[stateNum++] = AS_RELEASE;

		// ======
		// TURRET //砲台
		// ======
		v_Select_vai TURRET = new v_Select_vai((NodeInt) STATE_MACHINE);
		stateNum = 0;
		TURRET.embedded[stateNum++] = AS_LOITER;
		TURRET.embedded[stateNum++] = AS_EXPLORE;
		TURRET.embedded[stateNum++] = AS_FOLLOW;
		TURRET.embedded[stateNum++] = AS_ACQUIRE;
		TURRET.embedded[stateNum++] = AS_SUBDUE;
		TURRET.embedded[stateNum++] = AS_SEARCH_ANT;
		TURRET.embedded[stateNum++] = AS_SEARCH_PREY;
		TURRET.embedded[stateNum++] = AS_DELIVER_TO_HOME;
		TURRET.embedded[stateNum++] = AS_DELIVER_TO_RANDOM_PLACE;
		TURRET.embedded[stateNum++] = AS_RELEASE;

		// ======
		// GRIPPER_FINGERS //爪子
		// ======
		d_Select_i GRIPPER_FINGERS = new d_Select_i(STATE_MACHINE);
		stateNum = 0;
		GRIPPER_FINGERS.embedded[stateNum++] = 0; // close in LOITER
		GRIPPER_FINGERS.embedded[stateNum++] = 0; // close in WANDER
		GRIPPER_FINGERS.embedded[stateNum++] = 0; // close in AS_FOLLOW
		GRIPPER_FINGERS.embedded[stateNum++] = -1; // trigger in ACQUIRE
		GRIPPER_FINGERS.embedded[stateNum++] = -1; // trigger in subdue
		GRIPPER_FINGERS.embedded[stateNum++] = 0; // close in AS_SEARCH_ANT
		GRIPPER_FINGERS.embedded[stateNum++] = 0; // close in AS_SEARCH_PREY
		GRIPPER_FINGERS.embedded[stateNum++] = 0; // close in AS_DELIVER_TO_HOME
		GRIPPER_FINGERS.embedded[stateNum++] = 0; // close in
		// AS_DELIVER_TO_RANDOM_PLACE
		GRIPPER_FINGERS.embedded[stateNum++] = 1; // open in AS_RELEASE;

		steering_configuration = STEERING;
		turret_configuration = TURRET;
		gripper_fingers_configuration = GRIPPER_FINGERS;

	}
}
