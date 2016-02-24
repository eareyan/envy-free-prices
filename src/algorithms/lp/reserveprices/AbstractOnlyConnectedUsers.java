package algorithms.lp.reserveprices;

import java.util.ArrayList;

import structures.Market;

public abstract class AbstractOnlyConnectedUsers extends AbstractLPReservePrices{

	public AbstractOnlyConnectedUsers(Market market) {
		super(market);
	}

	@Override
	protected ArrayList<Integer> selectUsers(int j) {
		ArrayList<Integer> users = new ArrayList<Integer>();
		for(int i=0;i<this.market.getNumberUsers();i++){
			if(this.market.isConnected(i, j)){
				users.add(i);
			}
		}
		return users;
	}
}
