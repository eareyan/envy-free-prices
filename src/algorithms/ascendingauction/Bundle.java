package algorithms.ascendingauction;

import java.util.ArrayList;

public class Bundle {
	protected int j;
	ArrayList<UserPriceAlloc> bundle;
	
	public Bundle(int j, ArrayList<UserPriceAlloc> bundle){
		this.j = j;
		this.bundle = bundle;
	}
	
	public int getCampaignIndex(){
		return this.j;
	}
	
	public ArrayList<UserPriceAlloc> getBundle(){
		return this.bundle;
	}
	
	public String toString(){
		return "(j = " + this.j + ", " + this.bundle + ")";
	}
}
