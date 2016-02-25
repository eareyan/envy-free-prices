package algorithms.lp.reserveprices;

import algorithms.EnvyFreePricesVectorLP;
import structures.Market;

public class SimpleReservePrices extends AbstractOnlyConnectedUsers{

	public SimpleReservePrices(Market market) {
		super(market);
	}

	@Override
	protected void setReservePrices(int i, int j, EnvyFreePricesVectorLP LP, double[] initialPrices) {
		//System.out.println("Set reserve for user "+i+", of: " + (this.market.getCampaign(j).getReward() / this.market.getCampaign(j).getDemand()));
		LP.setReservePriceForUser(i, this.market.getCampaign(j).getReward() / this.market.getCampaign(j).getDemand());
	}
}
