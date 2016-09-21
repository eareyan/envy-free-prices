package algorithms.pricing.lp.heuristicreserveprices.interfaces;

import structures.Market;
import structures.MarketAllocation;
import algorithms.pricing.EnvyFreePricesVectorLP;

/**
 * This interface SetReservePrices defines one method setReservePrices.
 * Implementing classes should define the reserve price for a user.
 * 
 * @author Enrique Areyan Viqueira
 */
public interface SetReservePrices {
  
  /**
   * Given a user index i and a campaign index j, and a LP
   * @param i - a user index.
   * @param j - a campaign index.
   * @param LP - an EnvyFreePricesVectorLP object. 
   * @param initialPrices - an array of initial prices.
   * @param market - a market object.
   * @param initialMarketAllocation - a MarketAllocation object.
   */
  public void setReservePrices(int i, int j, EnvyFreePricesVectorLP LP, double[] initialPrices, Market market, MarketAllocation initialMarketAllocation);
}
