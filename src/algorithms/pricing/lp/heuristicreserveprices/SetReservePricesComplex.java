package algorithms.pricing.lp.heuristicreserveprices;

import algorithms.pricing.RestrictedEnvyFreePricesLP;
import algorithms.pricing.lp.heuristicreserveprices.interfaces.SetReservePrices;
import structures.Market;
import structures.MarketAllocation;

/**
 * This class implements interface SetReservePrices and defines functionality
 * for method setReservePrices where a reserve price is defined as: p_i = (R_j - sum_{iprime\ne i}(x_{iprime,j)P_{iprime} / x_{ij},
 * in words: reserve prices are defined as the amount spent on user i by campaign j. 
 * 
 * @author Enrique Areyan Viqueira
 */
public class SetReservePricesComplex implements SetReservePrices {
  
  /**
   * Implements the reserve price.
   * 
   * @see
   * algorithms.lp.reserveprices.AbstractLPReservePrices#setReservePrices(int,
   * int, algorithms.RestrictedEnvyFreePricesLP, double[])
   */
  @Override
  public void setReservePrices(int i, int j, RestrictedEnvyFreePricesLP LP, double[] initialPrices, Market market, MarketAllocation initialMarketAllocation) {
    System.out.println("Set reserve for user "
        + i
        + ", of: "
        + (market.getBidder(j).getReward() / market.getBidder(j)
            .getDemand()));
    double spentOnOtherUsers = 0.0;
    for (int iPrime = 0; iPrime < market.getNumberGoods(); iPrime++) {
      if (iPrime != i) {
        spentOnOtherUsers += initialPrices[iPrime] * initialMarketAllocation.getAllocation()[iPrime][j];
      }
    }
    LP.setReservePriceForGood(i, (market.getBidder(j).getReward() - spentOnOtherUsers) / initialMarketAllocation.getAllocation()[i][j]);
    System.out.println("\t\t spentOnOtherUsers = " + spentOnOtherUsers);
  }
  
}
