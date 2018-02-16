package fScript;


public class Coordinate {
	private double x;
	private double y;
	
	public Coordinate(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public int x(){
		return (int)(x*FScript.SCREEN_RES_X);
	}
	
	public int y(){
		return (int)(y*FScript.SCREEN_RES_Y);
	}
	
}
