package algorithms.lp.reserveprices;

import algorithms.EnvyFreePricesVectorLP;
import structures.Market;

public class ComplexReservePrices extends AbstractOnlyConnectedUsers{

	public ComplexReservePrices(Market market) {
		super(market);
	}

	@Override
	protected void setReservePrices(int i, int j, EnvyFreePricesVectorLP LP, double[] initialPrices) {
		System.out.println("Set reserve for user "+i+", of: " + (this.market.getCampaign(j).getReward() / this.market.getCampaign(j).getDemand()));
		double spentOnOtherUsers = 0.0;
		for(int iPrime=0; iPrime<this.market.getNumberUsers();iPrime++){
			if(iPrime != i){
				spentOnOtherUsers += initialPrices[iPrime] * this.initialMarketAllocation.getAllocation()[iPrime][j];
			}
		}
		LP.setReservePriceForUser(i, (this.market.getCampaign(j).getReward() - spentOnOtherUsers)/ this.initialMarketAllocation.getAllocation()[i][j]);
		System.out.println("\t\t spentOnOtherUsers = "+ spentOnOtherUsers);
	}
}
