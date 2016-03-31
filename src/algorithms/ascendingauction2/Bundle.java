package algorithms.ascendingauction2;

import java.util.ArrayList;

public class Bundle {
	
	protected int j;
	
	protected ArrayList<BundleEntry> bundle;
	
	public Bundle(int j, ArrayList<BundleEntry> bundle){
		this.j = j;
		this.bundle = bundle;
	}
	
	public int getJ(){
		return this.j;
	}
	
	public ArrayList<BundleEntry> getBundle(){
		return this.bundle;
	}

	public String toString(){
		return "(j = " + this.j + "," + this.bundle +")";
	}
}
