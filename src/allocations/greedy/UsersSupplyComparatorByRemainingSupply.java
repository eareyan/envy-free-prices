package allocations.greedy;

import java.util.Comparator;

public class UsersSupplyComparatorByRemainingSupply implements Comparator<UserSupply>{
	protected int Order = 0; /*order 1 means ASC and -1 means DESC, any other means no order*/
	public UsersSupplyComparatorByRemainingSupply(int Order){
		this.Order = Order;
	}
	public UsersSupplyComparatorByRemainingSupply(){
		
	}
	@Override
	public int compare(UserSupply U1, UserSupply U2) {
		if(this.Order == -1){
			if(U1.getRemainingSupply() < U2.getRemainingSupply()) return 1;
			if(U1.getRemainingSupply() > U2.getRemainingSupply()) return -1;
		}else if(this.Order == 1){
			if(U1.getRemainingSupply() < U2.getRemainingSupply()) return -1;
			if(U1.getRemainingSupply() > U2.getRemainingSupply()) return 1;				
		}
		return 0;
	}		
}