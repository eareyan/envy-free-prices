package algorithms.pricing.lp.heuristicreserveprices;

import algorithms.pricing.RestrictedEnvyFreePricesLP;
import algorithms.pricing.lp.heuristicreserveprices.interfaces.SetReservePrices;
import structures.Market;
import structures.MarketAllocation;

/**
 * This class implements interface SetReservePrices and defines functionality
 * for method setReservePrices where a reserve price is defined as: p_i = R_j / I_j
 * 
 * @author Enrique Areyan Viqueira
 */
public class SetReservePricesSimple implements SetReservePrices {
  /**
   * Implements a simple reserve price of R_j / I_j
   * 
   * @see
   * algorithms.lp.reserveprices.interfaces.SetReservePrices#setReservePrices
   * (int, int, algorithms.RestrictedEnvyFreePricesLP, double[], structures.Market,
   * structures.MarketAllocation)
   */
  @Override
  public void setReservePrices(int i, int j, RestrictedEnvyFreePricesLP LP, double[] initialPrices, Market market, MarketAllocation initialMarketAllocation) {
    // System.out.println("Set reserve for user "+i+", of: " +
    // (this.market.getCampaign(j).getReward() /
    // this.market.getCampaign(j).getDemand()));
    LP.setReservePriceForGood(i, market.getBidder(j).getReward() / market.getBidder(j).getDemand());
  }
  
}
