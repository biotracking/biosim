package app.RandomAnts;

import java.awt.Color;

import javax.swing.JFrame;

import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.continuous.ContinuousPortrayal2D;

public class BioSimWithUI extends GUIState {
	public Display2D display;
	public JFrame displayFrame;
	ContinuousPortrayal2D forestPortrayal = new ContinuousPortrayal2D();

	// public BioSimWithUI() { super(new BioSim( System.currentTimeMillis())); }
	public BioSimWithUI() {
		super(new BioSim(System.currentTimeMillis()));
	}

	public BioSimWithUI(SimState state) {
		super(state);
	}

	public static String getName() {
		return "BioSim";
	}

	public void start() {
		super.start();
		setupPortrayals();
	}

	public void load(SimState state) {
		super.load(state);

		setupPortrayals();
	}

	public void setupPortrayals() {
		// BioSim hunt = (BioSim) state;
		BioSim hunt = (BioSim) state;
		// tell the portrayals what to portray and how to portray them

		forestPortrayal.setField(hunt.forest);
		forestPortrayal.setPortrayalForClass(Adapter.class,
				new sim.portrayal.simple.OvalPortrayal2D(Color.red));
		// forestPortrayal.setPortrayalForClass(DolphinWallRobot.class, new
		// sim.portrayal.simple.RectanglePortrayal2D(Color.red));
		// forestPortrayal.setPortrayalForClass(PreyRobot.class, new
		// sim.portrayal.simple.OvalPortrayal2D(Color.white));
		// forestPortrayal.setPortrayalForClass(Obstacles.class, new
		// sim.portrayal.simple.RectanglePortrayal2D(Color.red));

		// reschedule the displayer
		display.reset();

		// redraw the display
		display.repaint();
	}

	public static void main(String[] args) {
		Console c = new Console(new BioSimWithUI());
		c.setVisible(true);
	}

	public class simulator extends Thread {
		public void run() {

		}
	}

	public class logReader extends Thread {
		public void run() {

		}
	}

	public void init(Controller c) {
		super.init(c);

		// make the displayer
		display = new Display2D(1250, 680, this, 1);
		display.setScale(1);
		display.setBackdrop(Color.white);
		// turn off clipping
		// display.setClipping(false);
		displayFrame = display.createFrame();
		displayFrame.setTitle("BioSim Display");

		// register the frame so it appears in the "Display" list
		c.registerFrame(displayFrame);
		displayFrame.setVisible(true);
		display.attach(forestPortrayal, "Ants");

		// specify the backdrop color -- what gets painted behind the displays

	}

	public void quit() {
		super.quit();
		if (displayFrame != null)
			displayFrame.dispose();
		displayFrame = null;
		display = null;
	}

}// end of the class
