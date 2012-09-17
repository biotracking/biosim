package core.basic_api;

import java.util.*;

public class ObstacleCollisionChecker {
	public static ArrayList<Object> obstacles = new ArrayList<Object>();
	
	public ObstacleCollisionChecker(){
		//obstacles = new Bag();
	}
	
	public void recordObstacle(Object obj){
		obstacles.add(obj);
	}
	
	public void unrecordObstacle(Object obj){
		obstacles.remove(obj);
	}

	public void clearObstacle(){
		this.obstacles.clear();
	}
	
	//x,y are target position for check collision. radius is the target radius
	//for example, an agent with radius 2 at (3,4) and want to go to (5,6)
	//then the input parameters should be (5, 6, 2)
	//if there is collision, return false, else return true
	public boolean checkCollision(double x, double y, double radius){
		AgentPortrayal tmp;
		double distance = 0;
		for(int i = 0; i < obstacles.size(); i++){
			tmp = (AgentPortrayal)obstacles.get(i);
			if(tmp != null){
				//get distance from the center of the target to the center of the obstacle
				distance = tmp.getLoc().distance(x, y); 
				//check distance is long enough for no collision happen
				if(distance < tmp.getAgentSize()/2 + radius) return false;
			}
		}
		return true;
	}
}
