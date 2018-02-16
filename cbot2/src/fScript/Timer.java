package fScript;

//counts the seconds since it's initialization

public class Timer {
	public long startingTime;
	public long elapsed;
	
	public Timer(){
		startingTime = System.currentTimeMillis() ;
		update();
	}
	
	public void update(){
		elapsed = System.currentTimeMillis() - startingTime;
	}
	
	public void reset(){
		startingTime = System.currentTimeMillis();
		update();
	}
	
}
