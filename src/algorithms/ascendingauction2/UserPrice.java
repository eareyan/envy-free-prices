package algorithms.ascendingauction2;

public class UserPrice {
	
	protected int i;
	
	protected double price;
	
	public UserPrice(int i, double price){
		this.i = i;
		this.price = price;
	}
	
	public int getI(){
		return this.i;
	}
	
	public double getPrice(){
		return this.price;
	}
	
	public void updatePrice(double price){
		this.price = price;
	}
	
	public String toString(){
		return "("+this.i+","+this.price+")";
	}
}
