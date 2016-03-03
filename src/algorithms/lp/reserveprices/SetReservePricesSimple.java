package algorithms.lp.reserveprices;

import algorithms.EnvyFreePricesVectorLP;
import algorithms.lp.reserveprices.interfaces.SetReservePrices;
import structures.Market;
import structures.MarketAllocation;

/*
 * This class implements interface SetReservePrices and defines functionality
 * for method setReservePrices where a reserve price is defined as: p_i = R_j / I_j
 * 
 * @author Enrique Areyan Viqueira
 */
public class SetReservePricesSimple implements SetReservePrices{
	/*
	 * Implements a simple reserve price of R_j / I_j
	 * @see algorithms.lp.reserveprices.interfaces.SetReservePrices#setReservePrices(int, int, algorithms.EnvyFreePricesVectorLP, double[], structures.Market, structures.MarketAllocation)
	 */
	@Override
	public void setReservePrices(int i, int j, EnvyFreePricesVectorLP LP, double[] initialPrices,Market market, MarketAllocation initialMarketAllocation) {
		//System.out.println("Set reserve for user "+i+", of: " + (this.market.getCampaign(j).getReward() / this.market.getCampaign(j).getDemand()));
		LP.setReservePriceForUser(i, market.getCampaign(j).getReward() / market.getCampaign(j).getDemand());
	}
}
