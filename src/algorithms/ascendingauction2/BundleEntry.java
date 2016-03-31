package algorithms.ascendingauction2;

public class BundleEntry {
	
	protected int i;
	
	protected int x;
	
	public BundleEntry(int i, int x){
		this.i = i;
		this.x = x;
	}

	public int getI(){
		return this.i;
	}
	
	public int getX(){
		return this.x;
	}
	
	public String toString(){
		return "("+this.i+","+this.x+")";
	}
}
