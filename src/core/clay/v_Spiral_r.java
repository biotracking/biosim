package core.clay;

import core.util.Vec2;

public class v_Spiral_r extends NodeVec2 {

	private NodeBoolean res;// = false;

	public v_Spiral_r(NodeBoolean reset) {
		res = reset;
	}

	double time_inch = Math.PI / 2;
	double tempf = 1;
	Vec2 last_val = new Vec2();
	double lasttime = 0;

	public Vec2 Value(double timestamp) {
		if ((timestamp > lasttime) || (timestamp == -1)) {

			/*--- reset output ---*/
			last_val.setr(1);

			// double tempf = ((double)(timestamp-lasttime)/500);
			tempf = tempf + 0.3;
			double tempd = 20;
			time_inch = time_inch - (1 / (tempd * tempf));

			if (time_inch < 0.03)
				time_inch = 0.03;

			/*--- reset output ---*/
			// if (res.Value(timestamp) == true)
			if (timestamp - lasttime > 1) {
				time_inch = Math.PI / 2;
				tempf = 1;
				// System.out.print("v_Spiral_r: I start Sprial!!\n");
			}

			if (timestamp > 0)
				lasttime = timestamp;
			else
				timestamp = lasttime + 1;

			last_val.sett(time_inch);
			// last_val.setr(1);
			// System.out.print("v_Spiral_r: I am Sprialling!!\n");
		}
		return (new Vec2(last_val.x, last_val.y));
	}

}
