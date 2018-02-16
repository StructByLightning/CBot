package bag;

public class Item {
	private boolean exists;
	private int x;
	private int y;
	
	public Item(boolean exists, int x, int y){
		this.exists = exists;
		this.x = x;
		this.y = y;
	}
	
	public boolean exists(){
		return exists;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
}
