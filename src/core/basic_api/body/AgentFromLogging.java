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
import core.basic_api.DrawAgentBody;
import core.basic_api.Sim;
import core.tools.Interaction;
import core.tools.SimInfo;

/**
 * @author Hai Shang
 * @author hshang9@gatech.edu
 * @see Define the specific function of ants body
 */
public class AgentFromLogging extends OvalPortrayal2D implements Steppable {
	private MutableDouble2D loc;
	private MutableDouble2D direction;
	private ArrayList<SimInfo> history;
	private double pointer;
	private ArrayList<Interaction> interactionList;
	//public double scale = 4000;
	private DrawAgentBody DrawBody;

	public AgentFromLogging(double x, double y, double d_x, double d_y, int i,
			int t) {
		// super();
		// logger.
		this.loc = new MutableDouble2D(x, y);
		double temp = Math.sqrt(d_x * d_x + d_y * d_y);
		this.direction = new MutableDouble2D(d_x / temp, d_y / temp);
		this.interactionList = new ArrayList<Interaction>();
		DrawBody = new DrawAgentBody();
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
			loc.x = si.getPX() * DrawBody.scaler;
			loc.y = si.getPY() * DrawBody.scaler;
			direction.x = si.getDX();
			direction.y = si.getDY();
			this.interactionList = si.getInteractionList();
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
		double agentSize = 0.007;
		DrawBody.draw(this.getAgentType(), object, g, info, 
				this.getDisplayColor(), this.getDisplayTextColor(), this.getDisplayTextLoc(), 
				this.getDisplayText(), new Double2D(direction), direction.angle(), agentSize);
		
		// public void draw(final Graphics2D g){
		// double scaler = getScaler()* info.draw.width;
//		double scaler = scale;// * info.draw.width;
//		if(info.draw.width/info.draw.height > 125/68) scaler = info.draw.width;
//		else scaler = info.draw.height;
//		double tempX = info.draw.x;
//		double tempY = info.draw.y;
//		g.setColor(Color.black);
//		g.fillOval((int) (tempX + (0.002 * direction.x - 0.002) * scaler),
//				(int) (tempY + (0.002 * direction.y - 0.002) * scaler),
//				(int) (0.004 * scaler), (int) (0.004 * scaler));
//		g.fillOval((int) (tempX + (0.0065 * direction.x - 0.0025) * scaler),
//				(int) (tempY + (0.0065 * direction.y - 0.0025) * scaler),
//				(int) (0.005 * scaler), (int) (0.005 * scaler));
//		g.fillOval((int) (tempX - (0.002 * direction.x + 0.002) * scaler),
//				(int) (tempY - (0.002 * direction.y + 0.002) * scaler),
//				(int) (0.004 * scaler), (int) (0.004 * scaler));
//		g.fillOval((int) (tempX - (0.0075 * direction.x + 0.0035) * scaler),
//				(int) (tempY - (0.0075 * direction.y + 0.0035) * scaler),
//				(int) (0.007 * scaler), (int) (0.007 * scaler));
//		g.drawLine((int) (tempX - (0.0075 * direction.y) * scaler),
//				(int) (tempY + (0.0075 * direction.x) * scaler),
//				(int) (tempX + (0.0075 * direction.y) * scaler),
//				(int) (tempY - (0.0075 * direction.x) * scaler));
//		double angle = direction.angle() - Math.PI / 5;
//		g.drawLine((int) (tempX - (0.01 * Math.sin(angle)) * scaler),
//				(int) (tempY + (0.01 * Math.cos(angle)) * scaler),
//				(int) (tempX + (0.01 * Math.sin(angle)) * scaler),
//				(int) (tempY - (0.01 * Math.cos(angle)) * scaler));
//		angle = direction.angle() + Math.PI / 5;
//		g.drawLine((int) (tempX - (0.01 * Math.sin(angle)) * scaler),
//				(int) (tempY + (0.01 * Math.cos(angle)) * scaler),
//				(int) (tempX + (0.01 * Math.sin(angle)) * scaler),
//				(int) (tempY - (0.01 * Math.cos(angle)) * scaler));
//		angle = direction.angle() + Math.PI / 18;
//		g.drawLine((int) tempX, (int) tempY, (int) (tempX + (0.0105 * Math
//				.cos(angle))
//				* scaler), (int) (tempY + (0.0105 * Math.sin(angle)) * scaler));
//
//		angle = direction.angle() - Math.PI / 18;
//		g.drawLine((int) tempX, (int) tempY, (int) (tempX + (0.0105 * Math
//				.cos(angle))
//				* scaler), (int) (tempY + (0.0105 * Math.sin(angle)) * scaler));
//
//		// begin to draw the interaction lines
//		if (this.interactionList.size() > 0) {
//			for (int i = 0; i < interactionList.size(); i++) {
//				Interaction inter = interactionList.get(i);
//				Color c = getColorFromInteractionType(inter.getType());
//				g.setColor(c);
//				double toX = inter.getToX();
//				double toY = inter.getToY();
//				double fromX = tempX + 0.0065 * direction.x * scaler;
//				double fromY = tempY + 0.0065 * direction.y * scaler;
//				g.drawLine((int) (fromX), (int) (fromY), (int) (toX * scaler),
//						(int) (toY * scaler - 22));
//			}
//		}

	}

	private double[] getDisplayTextLoc() {
		// TODO Auto-generated method stub
		double textLoc[];
		textLoc = new double[2];
		textLoc[0] = -1;
		textLoc[1] = -1;
		return textLoc;
	}

	private String getDisplayText() {
		// TODO Auto-generated method stub
		return new String("");
	}

	private Color getDisplayTextColor() {
		// TODO Auto-generated method stub
		return Color.black;
	}

	private Color getDisplayColor() {
		// TODO Auto-generated method stub
		return Color.black;
	}

	private int getAgentType() {
		// TODO Auto-generated method stub
		return 1;
	}

	private Color getColorFromInteractionType(int type) {
		/*
		 * 1 for head to tail, red 2 for head to body, green 3 for head to head,
		 * yellow others unknown, black
		 */
		if (type == 1)
			return Color.red;
		else if (type == 2)
			return Color.green;
		else if (type == 3)
			return Color.blue;
		return Color.white;
	}

	private Double2D getFromPosition() {
		double x = loc.x*scale + 0.0065 * direction.x * scale;
		double y = loc.y*scale + 0.0065 * direction.y * scale;
		return new Double2D(x, y);
	}

}// end of the class AgentFromLogging
