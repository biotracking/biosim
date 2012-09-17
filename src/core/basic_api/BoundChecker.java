package core.basic_api;

public class BoundChecker {
	public static double scaler = 1;
	public static double xMin = 1;
	public static double xMax = 1;
	public static double yMin = 0;
	public static double yMax = 0;
	
	public BoundChecker(){}
	
	public void setBound(double xmin, double ymin, double xmax, double ymax){
		this.xMin = xmin+(xmax-xmin)/40;
		this.xMax = xmax-(xmax-xmin)/40;
		this.yMin = ymin+(ymax-ymin)/40;
		this.yMax = ymax-(ymax-ymin)/40;
	}
	
	public boolean checkBound(double x, double y){
		if(xMin < x && x < xMax && yMin < y && y < yMax)
			return true;
		return false;
	}
	
	public boolean checkBound(double x, double y, double size){
		if(xMin < (x-size/2) && (x+size/2) < xMax && yMin < (y-size/2) && (y+size/2) < yMax)
			return true;
		return false;
	}
	
}
