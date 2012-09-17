package core.clay;

import core.basic_api.AbstractRobot;

public class b_Moving_r  extends NodeBoolean
{

	private AbstractRobot	abstract_robot;
	public b_Moving_r(AbstractRobot ar)
	{
		abstract_robot = ar;		
	}
	
	private boolean last_val;
	private boolean tmp_val;
	double	lasttime = 0;
	private double accum;
	
	public boolean Value(double timestamp) 
	{
		if ((timestamp > lasttime)||(timestamp == -1))
        {
			double time_incd = (double)(timestamp-lasttime)/100;
			if (timestamp > 0) 
				lasttime = timestamp;
			else
				timestamp = lasttime + 1;
			
			tmp_val = abstract_robot.isNotMove();
//			System.out.print("b_Moving_r: "+tmp_val+"\n");
			
			if(tmp_val==true)
			{
				accum += time_incd;
			}
			else
				accum = 0;
			
			if (accum > 2) //second
			{
				last_val = false;
			}else
				last_val = true;
        }
//		System.out.print("b_Moving_r: "+last_val+"\n");
		return last_val;
	}

}
