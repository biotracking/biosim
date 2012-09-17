package core.basic_api;

import java.awt.Color;
import java.awt.Graphics2D;

import sim.portrayal.DrawInfo2D;
import sim.util.Double2D;

public class DrawAgentBody {

	public static double scaler = 1;
	public static double xMin = 1;
	public static double xMax = 1;
	public static double yMin = 0;
	public static double yMax = 0;
	public boolean isVis = true;

	// public AgentBody body;
	public DrawAgentBody() {
		// body = ab;
	}

	public void setSize(double xmin, double ymin, double xmax, double ymax,
			double screen) {
		this.xMin = xmin;
		this.xMax = xmax;
		this.yMin = ymin;
		this.yMax = ymax;
		this.scaler = screen;
	}

	public void draw(int agentType, Object object, final Graphics2D g,
			final DrawInfo2D info, Color agentColor, Color agentTextColor,
			double[] agentTextLoc, String agentText, Double2D agentDir,
			double agentDirAngle, double agentSize) {
		if(!isVis) return;
		if (agentType == 1)// ant
		{
			// double tempX = info.draw.x;
			// double tempY = info.draw.y;
			double tempX = info.draw.x + xMin;
			double tempY = info.draw.y + yMin;

			double wideOfAnt = info.draw.width * agentSize * scaler;// 0.004*scaler;
			double longOfAnt = info.draw.height * agentSize * scaler;// agentSize*scaler;

			g.setColor(agentColor);
			double tempSize = 0.4 * wideOfAnt;
			double templong = longOfAnt / 3;
			g.fillOval(
					(int) (tempX - (tempSize / 2) + (templong) * agentDir.x),
					(int) (tempY - (tempSize / 2) + (templong) * agentDir.y),
					(int) (tempSize), (int) (tempSize));
			tempSize = 0.25 * wideOfAnt;
			templong = templong / 2;
			g.fillOval(
					(int) (tempX - (tempSize / 2) + (templong) * agentDir.x),
					(int) (tempY - (tempSize / 2) + (templong) * agentDir.y),
					(int) (tempSize), (int) (tempSize));
			g.fillOval(
					(int) (tempX - (tempSize / 2) - (templong) * agentDir.x),
					(int) (tempY - (tempSize / 2) - (templong) * agentDir.y),
					(int) (tempSize), (int) (tempSize));
			tempSize = 0.4 * wideOfAnt;
			templong = templong * 2;
			g.fillOval(
					(int) (tempX - (tempSize / 2) - (templong) * agentDir.x),
					(int) (tempY - (tempSize / 2) - (templong) * agentDir.y),
					(int) (tempSize), (int) (tempSize));

			g.drawLine((int) (tempX - (0.3 * wideOfAnt * agentDir.y)),
					(int) (tempY + (0.3 * wideOfAnt * agentDir.x)),
					(int) (tempX + (0.3 * wideOfAnt * agentDir.y)),
					(int) (tempY - (0.3 * wideOfAnt * agentDir.x)));
			// */
			double antFeet = longOfAnt * 0.015;
			double angle = agentDirAngle - Math.PI / 5;
			g.drawLine((int) (tempX - (antFeet * wideOfAnt * Math.sin(angle))),
					(int) (tempY + (antFeet * wideOfAnt * Math.cos(angle))),
					(int) (tempX + (antFeet * wideOfAnt * Math.sin(angle))),
					(int) (tempY - (antFeet * wideOfAnt * Math.cos(angle))));
			angle = agentDirAngle + Math.PI / 5;
			g.drawLine((int) (tempX - (antFeet * wideOfAnt * Math.sin(angle))),
					(int) (tempY + (antFeet * wideOfAnt * Math.cos(angle))),
					(int) (tempX + (antFeet * wideOfAnt * Math.sin(angle))),
					(int) (tempY - (antFeet * wideOfAnt * Math.cos(angle))));
			
			angle = agentDirAngle + Math.PI / 18;
			antFeet = antFeet + 0.35;
			g.drawLine((int) (tempX - ((antFeet - longOfAnt / 100) * wideOfAnt * Math.cos(angle))),
							(int) (tempY - ((antFeet - longOfAnt / 100) * wideOfAnt * Math.sin(angle))),
							(int) (tempX + (antFeet * wideOfAnt * Math.cos(angle))), (int) (tempY + (antFeet
									* wideOfAnt * Math.sin(angle))));
			angle = agentDirAngle - Math.PI / 18;
			g.drawLine((int) (tempX - ((antFeet - longOfAnt / 100) * wideOfAnt * Math.cos(angle))),
							(int) (tempY - ((antFeet - longOfAnt / 100) * wideOfAnt * Math.sin(angle))),
							(int) (tempX + (antFeet * wideOfAnt * Math.cos(angle))), 
							(int) (tempY + (antFeet * wideOfAnt * Math.sin(angle))));
			
			g.setColor(agentTextColor);
			if(agentText!=null)
				g.drawString(agentText,(int)((agentTextLoc[0]*this.scaler)),(int)((agentTextLoc[1]*this.scaler)));
			//
			 
		} else if (agentType == 2)// prey
		{
			// double tempX = info.draw.x;
			// double tempY = info.draw.y;
			double tempX = info.draw.x + xMin;
			double tempY = info.draw.y + yMin;

			double wideOfPrey = info.draw.width * agentSize * this.scaler; // the
			double longOfPrey = info.draw.height * agentSize * this.scaler; // the

			g.setColor(agentColor);
			double tempSize = wideOfPrey * 3 / 5;
			g.fillOval((int) (tempX - (tempSize / 2)),
					(int) (tempY - (tempSize / 2)), (int) (tempSize),
					(int) (tempSize));
			tempSize = wideOfPrey * 2 / 5;
			double templong = tempSize;
			g.fillOval(
					(int) (tempX - (tempSize / 2) + (templong) * agentDir.x),
					(int) (tempY - (tempSize / 2) + (templong) * agentDir.y),
					(int) (tempSize), (int) (tempSize));

			g.setColor(agentTextColor);
			if(agentText!=null)
				g.drawString(agentText,(int)((agentTextLoc[0]*this.scaler)),(int)((agentTextLoc[1]*this.scaler)));

		} else if(agentType == 3)// obstacles
		{
			double tempW = info.draw.width * agentSize * this.scaler;
			double tempH = info.draw.height * agentSize * this.scaler;
			double tempX = info.draw.x + xMin;
			double tempY = info.draw.y + yMin;

			g.setColor(agentColor);
			g.fillOval((int)(tempX - (tempW / 2)),(int)(tempY - (tempW / 2)),(int)tempW,(int)tempW);
//			g.fillRect((int) (tempX - tempW / 2), (int) (tempY - tempH / 2),
//					(int) (tempW), (int) (tempH));
			
			g.setColor(agentTextColor);
			if(agentText!=null)
				g.drawString(agentText,(int)((agentTextLoc[0]*this.scaler)),(int)((agentTextLoc[1]*this.scaler)));

		}else if(agentType == 4)//deer
		{
			double tempX = info.draw.x + xMin;
			double tempY = info.draw.y + yMin;

			double wideOfPrey = info.draw.width * agentSize * this.scaler; // the
			double longOfPrey = info.draw.height * agentSize * this.scaler; // the

			g.setColor(agentColor);
			double tempSize = wideOfPrey * 3 / 5;
			g.fillOval((int) (tempX - (tempSize / 2)),
					(int) (tempY - (tempSize / 2)), (int) (tempSize),
					(int) (tempSize));
			tempSize = wideOfPrey * 2 / 5;
			double templong = tempSize;
			g.fillOval(
					(int) (tempX - (tempSize / 2) + (templong) * agentDir.x),
					(int) (tempY - (tempSize / 2) + (templong) * agentDir.y),
					(int) (tempSize), (int) (tempSize));

			g.setColor(agentTextColor);
			if(agentText!=null)
				g.drawString(agentText,(int)((agentTextLoc[0]*this.scaler)),(int)((agentTextLoc[1]*this.scaler)));
		}
	}

	public void drawLine(Object object, final Graphics2D g, final DrawInfo2D info, 
			Color agentColor, Double2D agentLoc, double[] targetLoc) 
	{
		double tempX = info.draw.x + xMin;
		double tempY = info.draw.y + yMin;
		double wideOfAnt = info.draw.width * 0.005 * scaler;// 0.004*scaler;
		double diffX = (agentLoc.x*scaler)-info.draw.x, diffY = (agentLoc.y*scaler)-info.draw.y;

		g.setColor(agentColor);
		if(targetLoc[0]!=0 || targetLoc[1]!=0)
		{	
			g.drawLine((int)tempX,(int)tempY,
					(int)((targetLoc[0]*this.scaler)-diffX),
					(int)((targetLoc[1]*this.scaler)-diffY));//+diffY*scaler)));

//			g.setColor(Color.red);
//			g.fillOval((int)((targetLoc[0]*this.scaler)-diffX),
//						(int)((targetLoc[1]*this.scaler)-diffY),
//					 (int)(wideOfAnt/3), (int)(wideOfAnt/3));
		}
//		 g.setColor(Color.yellow);
//		 g.fillOval((int)(targetLoc[0]), (int)(targetLoc[1]), (int)(wideOfAnt/3), (int)(wideOfAnt/3));

	}
	public double getScaler() {
		return this.scaler;
	}
}
