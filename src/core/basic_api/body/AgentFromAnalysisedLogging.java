package core.basic_api.body;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.util.Double2D;
import sim.util.MutableDouble2D;
import core.basic_api.Sim;
import core.tools.SimInfo;

/**
 * @author Hai Shang
 * @author hshang9@gatech.edu
 * @see Define the specific function of ants body
 */
public class AgentFromAnalysisedLogging extends OvalPortrayal2D implements
		Steppable {
	private MutableDouble2D loc;
	private MutableDouble2D direction;
	private ArrayList<SimInfo> history;
	private double pointer;

	public AgentFromAnalysisedLogging(double x, double y, double d_x,
			double d_y, int i, int t) {
		// super();
		// logger.
		this.loc = new MutableDouble2D(x, y);
		double temp = Math.sqrt(d_x * d_x + d_y * d_y);
		this.direction = new MutableDouble2D(d_x / temp, d_y / temp);
		// this.setId(i);
		// this.setAgentType(t);
	}

	public void setHistory(ArrayList<SimInfo> si) {
		this.history = si;
		if (history.size() > 0) {
			pointer = history.get(0).getTimeStamp();

		} else
			pointer = -1;
	}

	public void step(final SimState state) {
		if (history.size() > 0) {
			SimInfo si = history.get(0);
			double tmpTS = si.getTimeStamp();
			// if(pointer == tmpTS){
			loc.x = si.getPX() * 2000;
			loc.y = si.getPY() * 2000;
			direction.x = si.getDX();
			direction.y = si.getDY();
			// pointer++;
			history.remove(0);
			// } else if (pointer > tmpTS){
			//				
			// } else {
			//				
			// }
		}
		((Sim) state).forest
				.setObjectLocation(this, new Double2D(loc.x, loc.y));

	}

	public void draw(Object object, final Graphics2D g, final DrawInfo2D info) {
		// public void draw(final Graphics2D g){
		// double scaler = getScaler()* info.draw.width;
		double scaler = 2000 * info.draw.width;
		double tempX = info.draw.x;
		double tempY = info.draw.y;
		g.setColor(Color.black);
		g.fillOval((int) (tempX + (0.002 * direction.x - 0.002) * scaler),
				(int) (tempY + (0.002 * direction.y - 0.002) * scaler),
				(int) (0.004 * scaler), (int) (0.004 * scaler));
		g.fillOval((int) (tempX + (0.0065 * direction.x - 0.0025) * scaler),
				(int) (tempY + (0.0065 * direction.y - 0.0025) * scaler),
				(int) (0.005 * scaler), (int) (0.005 * scaler));
		g.fillOval((int) (tempX - (0.002 * direction.x + 0.002) * scaler),
				(int) (tempY - (0.002 * direction.y + 0.002) * scaler),
				(int) (0.004 * scaler), (int) (0.004 * scaler));
		g.fillOval((int) (tempX - (0.0075 * direction.x + 0.0035) * scaler),
				(int) (tempY - (0.0075 * direction.y + 0.0035) * scaler),
				(int) (0.007 * scaler), (int) (0.007 * scaler));
		g.drawLine((int) (tempX - (0.0075 * direction.y) * scaler),
				(int) (tempY + (0.0075 * direction.x) * scaler),
				(int) (tempX + (0.0075 * direction.y) * scaler),
				(int) (tempY - (0.0075 * direction.x) * scaler));
		double angle = direction.angle() - Math.PI / 5;
		g.drawLine((int) (tempX - (0.01 * Math.sin(angle)) * scaler),
				(int) (tempY + (0.01 * Math.cos(angle)) * scaler),
				(int) (tempX + (0.01 * Math.sin(angle)) * scaler),
				(int) (tempY - (0.01 * Math.cos(angle)) * scaler));
		angle = direction.angle() + Math.PI / 5;
		g.drawLine((int) (tempX - (0.01 * Math.sin(angle)) * scaler),
				(int) (tempY + (0.01 * Math.cos(angle)) * scaler),
				(int) (tempX + (0.01 * Math.sin(angle)) * scaler),
				(int) (tempY - (0.01 * Math.cos(angle)) * scaler));
		angle = direction.angle() + Math.PI / 18;
		g.drawLine((int) tempX, (int) tempY, (int) (tempX + (0.0105 * Math
				.cos(angle))
				* scaler), (int) (tempY + (0.0105 * Math.sin(angle)) * scaler));

		angle = direction.angle() - Math.PI / 18;
		g.drawLine((int) tempX, (int) tempY, (int) (tempX + (0.0105 * Math
				.cos(angle))
				* scaler), (int) (tempY + (0.0105 * Math.sin(angle)) * scaler));
		// g.drawLine(x1, y1, x2, y2)
	}

}// end of the class AgentFromAnalysisedLogging
