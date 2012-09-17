package app.SimpleAnts;

import java.awt.Color;

import core.basic_api.AbstractRobot;
import core.basic_api.AgentController;
import core.basic_api.ClayController;
import core.clay.NodeBoolean;
import core.clay.NodeDouble;
import core.clay.NodeInt;
import core.clay.NodeVec2;
import core.clay.NodeVec2Array;
import core.clay.b_Close_vr;
import core.clay.b_NonZero_v;
import core.clay.b_Not_s;
import core.clay.d_Select_i;
import core.clay.i_FSA_ba;
import core.clay.v_Avoid_va;
import core.clay.v_Closest_i;
import core.clay.v_FixedPoint_;
import core.clay.v_GlobalToEgo_rv;
import core.clay.v_LinearAttraction_v;
import core.clay.v_Noise_;
import core.clay.v_Select_vai;
import core.clay.v_StaticWeightedSum_va;
import core.clay.v_Swirl_vav;
import core.clay.va_Obstacles_r;

public class FlyController extends AgentController {
	public double TimeStamp = 0;
	private double[] avoidPara = {1, 0.6}; // (%) [0] > [1]
	private double noiseTimeOut = 3;

	public double[] Loc1 = { 0.03, 0.03 };
	public double[] Loc2 = { 0.28, 0.04 };
	public double[] Loc3 = { 0.25, 0.10 };
	public double[] Loc4 = { 0.04, 0.15 };
	private NodeVec2 turret_configuration;
	private NodeVec2 steering_configuration;
	private NodeDouble gripper_fingers_configuration;
	private NodeVec2Array PS_OBS;
	private NodeVec2Array PS_OBS_OF_ANT;
	private NodeVec2 PS_LOC1_GLOBAL;
	private NodeVec2 PS_LOC2_GLOBAL;
	private NodeVec2 PS_LOC3_GLOBAL;
	private NodeVec2 PS_LOC4_GLOBAL;
	// private NodeVec2 PS_GLOBAL_POS;
	private NodeVec2 PS_LOC1;
	private NodeVec2 PS_LOC2;
	private NodeVec2 PS_LOC3;
	private NodeVec2 PS_LOC4;
	// private NodeVec2Array PS_TARGETS1_EGO;
	// private NodeVec2Array PS_TARGETS1_GLOBAL;
	// private NodeVec2Array PS_TARGETS1_EGO_FILT;
	private NodeVec2 PS_CLOSEST1;

	private NodeBoolean PF_SEE_ANT;
	private NodeBoolean PF_NOT_TARGET1_VISIBLE;
	private NodeBoolean PF_CLOSE_TO_LOC1;
	private NodeBoolean PF_CLOSE_TO_LOC2;
	private NodeBoolean PF_CLOSE_TO_LOC3;
	private NodeBoolean PF_CLOSE_TO_LOC4;

	private NodeVec2 MS_AVOID_OBSTACLES;
	private NodeVec2 MS_AVOID_ANTS;
	private NodeVec2 MS_NOISE_VECTOR;
	private NodeVec2 MS_MOVE_TO_LOC1;
	private NodeVec2 MS_MOVE_TO_LOC2;
	private NodeVec2 MS_MOVE_TO_LOC3;
	private NodeVec2 MS_MOVE_TO_LOC4;
	private NodeVec2 MS_SWIRL_OBSTACLES_LOC1;
	private NodeVec2 MS_SWIRL_OBSTACLES_LOC2;
	private NodeVec2 MS_SWIRL_OBSTACLES_LOC3;
	private NodeVec2 MS_SWIRL_OBSTACLES_LOC4;
	private v_StaticWeightedSum_va AS_LOC1;
	private v_StaticWeightedSum_va AS_LOC2;
	private v_StaticWeightedSum_va AS_LOC3;
	private v_StaticWeightedSum_va AS_LOC4;
	private i_FSA_ba STATE_MACHINE;
	String[] stateName={"Go Loc 1","Go Loc 2","Go Loc 3","Go Loc 4"}; // set up states' name

	public FlyController(AbstractRobot ab) {
		this.setBody(ab);
		// Pixel2Meter = 2.54/(100*196.6439); // unit pixel to meter
		// Meter2Pixel = (100*196.6439)/2.54;
		configure(ab);
		clayControl = new ClayController(ab, turret_configuration,
				steering_configuration, gripper_fingers_configuration,
				STATE_MACHINE);
	}

	public void takeStep() {
		TimeStamp = body.getTimeStamp();
		double[] allTurnRate = clayControl.Value(TimeStamp);
		this.getFinalTurnRate(allTurnRate,TimeStamp);
		//if(body.getAlive())
		//	body.DisplayNearAgent(stateName[STATE_MACHINE.state], Color.black);
			// show state name in display
	}

	public void configure(AbstractRobot ab) {
		// ======
		// perceptual schemas
		// ======

		// --- robot's global position
		// PS_GLOBAL_POS = new v_GlobalPosition_r(ab);
		// --- obstacles
		PS_OBS = new va_Obstacles_r(ab, 3);
		// --- obstacles of ants
		PS_OBS_OF_ANT = new va_Obstacles_r(ab, 1);
		// --- location
		PS_LOC1_GLOBAL = new v_FixedPoint_(Loc1[0], Loc1[1]);
		PS_LOC2_GLOBAL = new v_FixedPoint_(Loc2[0], Loc2[1]);
		PS_LOC3_GLOBAL = new v_FixedPoint_(Loc3[0], Loc3[1]);
		PS_LOC4_GLOBAL = new v_FixedPoint_(Loc4[0], Loc4[1]);
		PS_LOC1 = new v_GlobalToEgo_rv(ab, PS_LOC1_GLOBAL);
		PS_LOC2 = new v_GlobalToEgo_rv(ab, PS_LOC2_GLOBAL);
		PS_LOC3 = new v_GlobalToEgo_rv(ab, PS_LOC3_GLOBAL);
		PS_LOC4 = new v_GlobalToEgo_rv(ab, PS_LOC4_GLOBAL);

		// --- targets of visual type 1 (ant)
		// PS_TARGETS1_EGO = new va_VisualObjects_r(1,ab);
		// PS_TARGETS1_GLOBAL = new va_Add_vav(PS_TARGETS1_EGO, PS_GLOBAL_POS);
		// --- make them ego
		// PS_TARGETS1_EGO_FILT = new va_Subtract_vav(PS_TARGETS1_GLOBAL,
		// PS_GLOBAL_POS);
		// --- get the closest one
		PS_CLOSEST1 = new v_Closest_i(1, ab);

		// ======
		// Perceptual Features
		// ======
		// see ant?
		PF_SEE_ANT = new b_NonZero_v(PS_CLOSEST1);
		// not see ant?
		PF_NOT_TARGET1_VISIBLE = new b_Not_s(PF_SEE_ANT);
		// close to location
		PF_CLOSE_TO_LOC1 = new b_Close_vr(0.01, PS_LOC1_GLOBAL, ab);
		PF_CLOSE_TO_LOC2 = new b_Close_vr(0.01, PS_LOC2_GLOBAL, ab);
		PF_CLOSE_TO_LOC3 = new b_Close_vr(0.01, PS_LOC3_GLOBAL, ab);
		PF_CLOSE_TO_LOC4 = new b_Close_vr(0.01, PS_LOC4_GLOBAL, ab);

		// ======
		// motor schemas
		// ======
		// avoid obstacles
		MS_AVOID_OBSTACLES = new v_Avoid_va(avoidPara[0], avoidPara[1], PS_OBS, ab);
		// avoid ants
		MS_AVOID_ANTS = new v_Avoid_va(avoidPara[0], avoidPara[1], PS_OBS_OF_ANT, ab);
		// go location
		MS_MOVE_TO_LOC1 = new v_LinearAttraction_v(ab, 1, 0.0, PS_LOC1);
		MS_MOVE_TO_LOC2 = new v_LinearAttraction_v(ab, 1, 0.0, PS_LOC2);
		MS_MOVE_TO_LOC3 = new v_LinearAttraction_v(ab, 1, 0.0, PS_LOC3);
		MS_MOVE_TO_LOC4 = new v_LinearAttraction_v(ab, 1, 0.0, PS_LOC4);
		// swirl obstacles
		MS_SWIRL_OBSTACLES_LOC1 = new v_Swirl_vav(1.0, avoidPara[1], PS_OBS,
				MS_MOVE_TO_LOC1);
		MS_SWIRL_OBSTACLES_LOC2 = new v_Swirl_vav(1.0, avoidPara[1], PS_OBS,
				MS_MOVE_TO_LOC2);
		MS_SWIRL_OBSTACLES_LOC3 = new v_Swirl_vav(1.0, avoidPara[1], PS_OBS,
				MS_MOVE_TO_LOC3);
		MS_SWIRL_OBSTACLES_LOC4 = new v_Swirl_vav(1.0, avoidPara[1], PS_OBS,
				MS_MOVE_TO_LOC4);
		// noise vector
		MS_NOISE_VECTOR = new v_Noise_(noiseTimeOut, ab);

		// ======
		// AS_LOC1
		// ======
		AS_LOC1 = new v_StaticWeightedSum_va();
		AS_LOC1.weights[0] = 0.8;
		AS_LOC1.embedded[0] = MS_AVOID_OBSTACLES;

		AS_LOC1.weights[1] = 0.1;
		AS_LOC1.embedded[1] = MS_NOISE_VECTOR;

		AS_LOC1.weights[2] = 1.0;
		AS_LOC1.embedded[2] = MS_MOVE_TO_LOC1;

		AS_LOC1.weights[3] = 0.1;
		AS_LOC1.embedded[3] = MS_SWIRL_OBSTACLES_LOC1;

		AS_LOC1.weights[4] = 1.0;
		AS_LOC1.embedded[4] = MS_AVOID_ANTS;

		// ======
		// AS_LOC2
		// ======
		AS_LOC2 = new v_StaticWeightedSum_va();
		AS_LOC2.weights[0] = 0.8;
		AS_LOC2.embedded[0] = MS_AVOID_OBSTACLES;

		AS_LOC2.weights[1] = 0.1;
		AS_LOC2.embedded[1] = MS_NOISE_VECTOR;

		AS_LOC2.weights[2] = 1.0;
		AS_LOC2.embedded[2] = MS_MOVE_TO_LOC2;

		AS_LOC2.weights[3] = 0.1;
		AS_LOC2.embedded[3] = MS_SWIRL_OBSTACLES_LOC2;

		AS_LOC2.weights[4] = 1.0;
		AS_LOC2.embedded[4] = MS_AVOID_ANTS;

		// ======
		// AS_LOC3
		// ======
		AS_LOC3 = new v_StaticWeightedSum_va();
		AS_LOC3.weights[0] = 0.8;
		AS_LOC3.embedded[0] = MS_AVOID_OBSTACLES;

		AS_LOC3.weights[1] = 0.1;
		AS_LOC3.embedded[1] = MS_NOISE_VECTOR;

		AS_LOC3.weights[2] = 1.0;
		AS_LOC3.embedded[2] = MS_MOVE_TO_LOC3;

		AS_LOC3.weights[3] = 0.1;
		AS_LOC3.embedded[3] = MS_SWIRL_OBSTACLES_LOC3;

		AS_LOC3.weights[4] = 1.0;
		AS_LOC3.embedded[4] = MS_AVOID_ANTS;

		// ======
		// AS_LOC4
		// ======
		AS_LOC4 = new v_StaticWeightedSum_va();
		AS_LOC4.weights[0] = 0.8;
		AS_LOC4.embedded[0] = MS_AVOID_OBSTACLES;

		AS_LOC4.weights[1] = 0.1;
		AS_LOC4.embedded[1] = MS_NOISE_VECTOR;

		AS_LOC4.weights[2] = 1.0;
		AS_LOC4.embedded[2] = MS_MOVE_TO_LOC4;

		AS_LOC4.weights[3] = 0.1;
		AS_LOC4.embedded[3] = MS_SWIRL_OBSTACLES_LOC4;

		AS_LOC4.weights[4] = 1.0;
		AS_LOC4.embedded[4] = MS_AVOID_ANTS;

		// ======
		// STATE_MACHINE
		// ======
		STATE_MACHINE = new i_FSA_ba();
		STATE_MACHINE.state = (int) Math.round(Math.random() * 10 / 4 - 0.5);
		int stateNum = 0;
		// STATE 0 AS_LOC1
		STATE_MACHINE.triggers[stateNum][0] = PF_SEE_ANT;
		STATE_MACHINE.follow_on[stateNum][0] = stateNum + 2; // transition to
		// AS_LOC3
		STATE_MACHINE.triggers[stateNum][1] = PF_CLOSE_TO_LOC1;
		STATE_MACHINE.follow_on[stateNum][1] = stateNum + 1; // transition to
		// AS_LOC2
		stateNum++;
		// STATE 1 AS_LOC2
		STATE_MACHINE.triggers[stateNum][0] = PF_SEE_ANT;
		STATE_MACHINE.follow_on[stateNum][0] = stateNum + 2; // transition to
		// AS_LOC4
		STATE_MACHINE.triggers[stateNum][1] = PF_CLOSE_TO_LOC2;
		STATE_MACHINE.follow_on[stateNum][1] = stateNum + 1; // transition to
		// AS_LOC3
		stateNum++;
		// STATE 2 AS_LOC3
		STATE_MACHINE.triggers[stateNum][0] = PF_SEE_ANT;
		STATE_MACHINE.follow_on[stateNum][0] = stateNum - 2; // transition to
		// AS_LOC1
		STATE_MACHINE.triggers[stateNum][1] = PF_CLOSE_TO_LOC3;
		STATE_MACHINE.follow_on[stateNum][1] = stateNum + 1; // transition to
		// AS_LOC4
		stateNum++;
		// STATE 3 AS_LOC4
		STATE_MACHINE.triggers[stateNum][0] = PF_SEE_ANT;
		STATE_MACHINE.follow_on[stateNum][0] = stateNum - 2; // transition to
		// AS_LOC2
		STATE_MACHINE.triggers[stateNum][1] = PF_CLOSE_TO_LOC4;
		STATE_MACHINE.follow_on[stateNum][1] = stateNum - 3; // transition to
		// AS_LOC1

		// ======
		// STEERING
		// ======
		v_Select_vai STEERING = new v_Select_vai((NodeInt) STATE_MACHINE);
		stateNum = 0;
		STEERING.embedded[stateNum++] = AS_LOC1;
		STEERING.embedded[stateNum++] = AS_LOC2;
		STEERING.embedded[stateNum++] = AS_LOC3;
		STEERING.embedded[stateNum++] = AS_LOC4;

		// ======
		// TURRET //砲台
		// ======
		v_Select_vai TURRET = new v_Select_vai((NodeInt) STATE_MACHINE);
		stateNum = 0;
		TURRET.embedded[stateNum++] = AS_LOC1;
		TURRET.embedded[stateNum++] = AS_LOC2;
		TURRET.embedded[stateNum++] = AS_LOC3;
		TURRET.embedded[stateNum++] = AS_LOC4;

		// ======
		// GRIPPER_FINGERS //爪子
		// ======
		d_Select_i GRIPPER_FINGERS = new d_Select_i(STATE_MACHINE);
		stateNum = 0;
		GRIPPER_FINGERS.embedded[stateNum++] = 0; // no gripper
		GRIPPER_FINGERS.embedded[stateNum++] = 0; // no gripper
		GRIPPER_FINGERS.embedded[stateNum++] = 0; // no gripper
		GRIPPER_FINGERS.embedded[stateNum++] = 0; // no gripper

		steering_configuration = STEERING;
		turret_configuration = TURRET;
		gripper_fingers_configuration = GRIPPER_FINGERS;

	}
}
