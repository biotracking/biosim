package core.clay;

import core.basic_api.AbstractRobot;

public class b_TimeOutCounter_i extends NodeBoolean {

	/**
	 * Turn debug printing on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	private double TIMEOUT = 5.0;
	private double accum = 0;//Math.random() * TIMEOUT;
	private AbstractRobot abstract_robot;

	/**
	 * Instantiate a b_TimeOutCounter_i schema.
	 * 
	 * @param t
	 *            int, the target value.
	 */
	public b_TimeOutCounter_i(double t, AbstractRobot ar) {
		TIMEOUT = t;
		abstract_robot = ar;
	}

	boolean last_val = false;
	double lasttime = 0;
	double time_incd;

	public boolean Value(double timestamp)
	{
		if (DEBUG) System.out.println("b_TimeOutCounter_i: Value()");
		this.time_incd = abstract_robot.getTimeStampSize();
    	
        if ((timestamp > lasttime)||(timestamp == -1))
        {
        	double tempTime = (timestamp - lasttime);
			if(tempTime>this.time_incd+0.00001) // 0.00001 is for rounding.
	    	{
	    		accum = 0;
//				System.out.println("b_TimeOutCounter_i: Time reset!"+timestamp+" "+accum+"\n");
	    	}
			
				/*--- reset the timestamp ---*/
				if (timestamp > 0) 
					lasttime = timestamp;
				else
					timestamp = lasttime + time_incd;

				accum += time_incd;
//				System.out.println("b_TimeOutCounter_i: accum "+accum+"\n");
				/*--- reset output ---*/
				if (accum > TIMEOUT)
				{
//					System.out.println("b_TimeOutCounter_i: Time Out! "+accum+" "+timestamp+" "+time_incd+"\n");
					accum = 0;
					last_val = true;
					abstract_robot.setBumped(false);
				}else
					last_val = false;
        	}else
        		last_val = false;
//        System.out.print("b_TimeOutCounter_i->time: "+ last_val +"__"+ accum+"__"+ tempFlag+ "\n");
		return last_val;
	}
}
