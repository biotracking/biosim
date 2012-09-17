package core.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Hai Shang
 * @author hshang9@gatech.edu
 * 
 */
public class Analysiser {
	private File logFile;
	private BufferedReader reader;

	public Analysiser() {
		String title = "./logs/log_123.txt";
		logFile = new File(title);
		try {
			reader = new BufferedReader(new FileReader(logFile));
			String line = reader.readLine();
			while (!line.contains("<data>"))
				line = reader.readLine();
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}
	}

	public ArrayList<ArrayList<SimInfo>> analysis() {
		boolean continueFlag = true;
		int tmpId;
		ArrayList<ArrayList<SimInfo>> history = new ArrayList<ArrayList<SimInfo>>();
		SimInfo simInfo = null;

		while (continueFlag) {
			simInfo = this.analysisOneLine();

			if (simInfo == null) {
				continueFlag = false;
			} else {
				tmpId = simInfo.getId();
				while (tmpId > history.size() - 1)
					history.add(new ArrayList<SimInfo>());
				history.get(tmpId).add(simInfo);
			}
		}

		return history;
	}

	public SimInfo analysisOneLine() {
		String line = "";
		String[] subLines = null;
		String[] sub2 = null;
		String[] infoStr = new String[9];
		SimInfo si = new SimInfo();
		try {
			line = reader.readLine();
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}
		if (line != null) {
			subLines = line.split(" ");
			for (int i = 3; i < 12; i++) {
				// System.out.println("s"+ subLines[i]+ "s");
				sub2 = subLines[i].split(":");
				if (sub2.length >= 2) {
					infoStr[i - 3] = sub2[1];
				}
			}
			// if(subLines.length >= 14){
			// ArrayList<Interaction> ia = new ArrayList<Interaction>();
			// Interaction interaction = null;
			// String sub3 = subLines[12];
			// String[] interactStr1 = null;
			// interactStr1 = sub3.split(")");
			// if(interactStr1.length>=2){
			// String[] interactStr2 = sub3.split("(");
			// for(int l = 0; l < interactStr1.length-1; l++){
			// interaction = new Interaction(
			// //parameters go here
			// );
			// }
			// }
			// }
			// System.out.println(line);
			// for(int i = 0; i < 9; i++){
			// System.out.println(infoStr[i]);
			// }
			si = new SimInfo(
					// d1,i1,i2,d2,d3,d4,d5,d6,d7);
					Double.parseDouble(infoStr[0]), Integer
							.parseInt(infoStr[1]),
					Integer.parseInt(infoStr[2]), Double
							.parseDouble(infoStr[3]), Double
							.parseDouble(infoStr[4]), Double
							.parseDouble(infoStr[5]), Double
							.parseDouble(infoStr[6]), Double
							.parseDouble(infoStr[7]), Double
							.parseDouble(infoStr[8]));
			return si;
		}
		return null;
	}

}// end of the class Analysiser
