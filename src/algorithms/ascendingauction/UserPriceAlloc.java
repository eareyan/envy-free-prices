package algorithms.ascendingauction;

public class UserPriceAlloc {
	
	protected int i;
	protected double price;
	protected int alloc;
	
	public UserPriceAlloc(int i,double price,int alloc){
		this.i = i;
		this.price = price;
		this.alloc = alloc;
	}
	
	public int getI(){
		return this.i;
	}
	
	public double getPrice(){
		return this.price;
	}
	
	public int getAlloc(){
		return this.alloc;
	}
	
	public void updateAlloc(int alloc){
		this.alloc = alloc;
	}
	
	public void updatePrice(double price){
		this.price = price;
	}
	
	public String toString(){
		return "("+i+","+price+","+alloc+")";
	}

}
