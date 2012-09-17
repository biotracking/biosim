package core.test;

import core.clay.b_Close_vv;
import core.clay.b_Equal_i;
import core.clay.b_NonZero_v;
import core.clay.b_Not_s;
import core.clay.d_Select_i;
import core.clay.i_FSA_ba;
import core.clay.i_InGripper_r;
import core.clay.v_Avoid_va;
import core.clay.v_Closest_va;
import core.clay.v_FixedPoint_;
import core.clay.v_GlobalPosition_r;
import core.clay.v_GlobalToEgo_rv;
import core.clay.v_LinearAttraction_v;
import core.clay.v_Noise_;
import core.clay.v_Select_vai;
import core.clay.v_StaticWeightedSum_va;
import core.clay.v_Swirl_vav;
import core.clay.v_Swirl_vv;
import core.clay.va_Add_vav;
import core.clay.va_FilterOutClose_vva;
import core.clay.va_Obstacles_r;
import core.clay.va_Subtract_vav;

public class testclay {
	// private KickActuator ar="";
	public static void main(String[] args) {
		b_CanKick_r b_cankick_r = new b_CanKick_r();
		b_Close_vv b_close_vv = new b_Close_vv();
		b_Equal_i b_equal_i = new b_Equal_i();
		b_NonNegative_s b_nonNegative_s = new b_NonNegative_s();
		b_NonZero_s b_nonZero_s = new b_NonZero_s();
		b_NonZero_v b_nonZero_v = new b_NonZero_v();
		b_Not_s b_not_s = new b_Not_s();
		b_Persist_s b_persist_s = new b_Persist_s();
		b_SameXSign_vv b_samexsign_vv = new b_SameXSign_vv();
		b_WatchDog_s b_watchdog_s = new b_WatchDog_s();
		d_Add_dd d_add_dd = new d_Add_dd();
		d_FixedDouble_ d_fixeddouble = new d_FixedDouble_(0);
		// d_ReinforcementComm_r d_reinforcementcomm_r =new
		// d_ReinforcementComm_r();
		d_Select_i d_select_i = new d_Select_i();
		i_FixedInt_ i_fixedint_ = new i_FixedInt_(0);
		i_FSA_ba i_fsa_ba = new i_FSA_ba();
		i_InGripper_r i_ingripper_r = new i_InGripper_r();
		// i_Learner_id i_learner_id = new i_Learner_id();
		i_Merge_ba i_merge_ba = new i_Merge_ba();
		i_Merge_ia i_merge_ia = new i_Merge_ia();
		// i_StepLearner_id i_steplearner_id = new i_StepLearner_id();
		Node node = new Node();
		// NodeBoolean nodeboolean = new NodeBoolean();
		// NodeDouble nodedouble = new NodeDouble();
		va_Add_vav va_add_vav = new va_Add_vav();
		va_FilterClose1_va va_filterclose1_va = new va_FilterClose1_va();
		va_FilterClose_va va_filterclose_va = new va_FilterClose_va();
		va_Merge_vav va_merge_vav = new va_Merge_vav();
		va_Merge_vava va_merge_vava = new va_Merge_vava();
		va_FilterOutClose_vva va_filteroutclose_vva = new va_FilterOutClose_vva();
		va_Obstacles_r va_obstacles_r = new va_Obstacles_r();
		va_Opponents_r va_opponents_r = new va_Opponents_r();
		va_PersistBlend_va va_persistBlend_va = new va_PersistBlend_va();
		va_Persist_va va_persist_va = new va_Persist_va();
		va_Subtract_vav va_subtract_vav = new va_Subtract_vav();
		va_Teammates_r va_teammates_r = new va_Teammates_r();
		v_Attract_va v_attract_va = new v_Attract_va();
		v_Average_va v_average_va = new v_Average_va();
		v_Average_vv v_average_vv = new v_Average_vv();
		va_VisualObjects_r va_visualobjects_r = new va_VisualObjects_r();
		v_Avoid_v v_avoid_v = new v_Avoid_v();
		v_Avoid_va v_avoid_va = new v_Avoid_va();
		v_Ball_r v_ball_r = new v_Ball_r();
		v_Closest_va v_closest_va = new v_Closest_va();
		v_EgoToGlobal_rv v_egotoglobal_rv = new v_EgoToGlobal_rv();
		v_FixedPoint_ v_fixedpoint = new v_FixedPoint_(0, 0);
		v_GeoField_vav v_geofield_vav = new v_GeoField_vav();
		v_GlobalPosition_r v_globalposition_r = new v_GlobalPosition_r();
		v_GlobalToEgo_rv v_globaltoego_rv = new v_GlobalToEgo_rv();
		v_Intercept_v v_intercept_v = new v_Intercept_v();
		v_LinearAttraction_v v_linearattraction_v = new v_LinearAttraction_v();
		v_LinearAttraction_va v_linearattraction_va = new v_LinearAttraction_va();
		// v_Localizer_rv v_localizer_rv = new v_Localizer_rv();
		v_Noise_ v_noise_ = new v_Noise_(0);
		v_OurGoal_r v_ourgoal_r = new v_OurGoal_r();
		v_Select_vai v_select_vai = new v_Select_vai();
		v_StaticWeightedSum_va v_staticweightedsum_va = new v_StaticWeightedSum_va();
		v_SteerHeading_r v_steerheading_r = new v_SteerHeading_r();
		v_Subtract_vv v_subtract_vv = new v_Subtract_vv();
		v_SwirlLeft_va v_swirlleft_va = new v_SwirlLeft_va();
		v_Swirl_vav v_swirl_vav = new v_Swirl_vav();
		v_Swirl_vv v_swirl_vv = new v_Swirl_vv();
		v_TheirGoal_r v_theirgoal_r = new v_TheirGoal_r();
		v_WinnerTakeAll_va v_winnertakeall_va = new v_WinnerTakeAll_va();

		if (b_cankick_r.test() == 0
				&& b_close_vv.test() == 0
				&& b_equal_i.test() == 0
				&& b_nonNegative_s.test() == 0
				&& b_nonZero_s.test() == 0
				&& b_nonZero_v.test() == 0
				&& b_not_s.test() == 0
				&& b_persist_s.test() == 0
				&& b_samexsign_vv.test() == 0
				&& b_watchdog_s.test() == 0
				&& d_add_dd.test() == 0
				&& d_fixeddouble.test() == 0
				// && d_reinforcementcomm_r.test()==0
				&& d_select_i.test() == 0
				&& i_fixedint_.test() == 0
				&& i_fsa_ba.test() == 0
				&& i_ingripper_r.test() == 0
				// && i_learner_id.test()==0
				&& i_merge_ba.test() == 0
				&& i_merge_ia.test() == 0
				// && i_steplearner_id.test()==0
				&& node.test() == 0 && va_add_vav.test() == 0
				&& va_filterclose1_va.test() == 0
				&& va_filterclose_va.test() == 0 && va_merge_vav.test() == 0
				&& va_merge_vava.test() == 0
				&& va_filteroutclose_vva.test() == 0
				&& va_obstacles_r.test() == 0 && va_opponents_r.test() == 0
				&& va_persistBlend_va.test() == 0 && va_persist_va.test() == 0
				&& va_subtract_vav.test() == 0 && va_teammates_r.test() == 0
				&& v_attract_va.test() == 0 && v_average_va.test() == 0
				&& v_average_vv.test() == 0 && va_visualobjects_r.test() == 0
				&& v_avoid_v.test() == 0 && v_avoid_va.test() == 0
				&& v_ball_r.test() == 0 && v_closest_va.test() == 0
				&& v_egotoglobal_rv.test() == 0 && v_fixedpoint.test() == 0
				&& v_geofield_vav.test() == 0 && v_globalposition_r.test() == 0
				&& v_globaltoego_rv.test() == 0 && v_intercept_v.test() == 0
				&& v_linearattraction_v.test() == 0
				&& v_linearattraction_va.test() == 0 && v_noise_.test() == 0
				&& v_ourgoal_r.test() == 0 && v_select_vai.test() == 0
				&& v_staticweightedsum_va.test() == 0
				&& v_steerheading_r.test() == 0 && v_subtract_vv.test() == 0
				&& v_swirlleft_va.test() == 0 && v_swirl_vav.test() == 0
				&& v_swirl_vv.test() == 0 && v_theirgoal_r.test() == 0
				&& v_winnertakeall_va.test() == 0) {
			System.out.println(0);
		} else {
			System.out.println(1);
		}

	}
}
