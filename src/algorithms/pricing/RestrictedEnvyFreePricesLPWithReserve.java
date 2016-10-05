package algorithms.pricing;

import allocations.objectivefunction.interfaces.SafeReserveFunction;
import ilog.concert.IloException;
import structures.Bidder;
import structures.Goods;
import structures.MarketAllocation;
import structures.exceptions.MarketAllocationException;

/**
 * This class extends RestrictedEnvyFreePricesLP and provides methods to add
 * reserve prices. Using reserve prices one can find a restricted Walrasian
 * Equilibrium with reserve.
 * 
 * @author Enrique Areyan Viqueira
 */
public class RestrictedEnvyFreePricesLPWithReserve<T extends SafeReserveFunction> extends RestrictedEnvyFreePricesLP<T>{

  public RestrictedEnvyFreePricesLPWithReserve(MarketAllocation<Goods, Bidder<Goods>, T> allocatedMarket) throws IloException {
    super(allocatedMarket);
  }
  
  /**
   * Set Reserve price for good i.
   * 
   * @param i - good index.
   * @param reservePrice - reserve price.
   */
  public void setReservePrice(double reservePrice) {
    try {
      if (this.verbose) {
        System.out.println("Setting Reserve Price of " + reservePrice + " for all goods ");
      }
      for(Goods good : this.allocatedMarket.getMarket().getGoods()){
        this.linearConstrains.add(this.cplex.addGe(this.prices[this.goodToPriceIndex.get(good)], reservePrice));
      }
    } catch (IloException e) {
      System.out.println("Exception: ==>");
      e.printStackTrace();
    }
  }
  
  /**
   * This method generates the market clearance condition with reserve prices.
   * This conditions state that unallocated items must be priced at the reserve.
   * 
   * @param reserve - reserve price
   * @throws IloException in case the LP failed
   * @throws MarketAllocationException 
   */
  public void generateMarketClearanceConditionsWithReserve(double reserve) throws IloException, MarketAllocationException {
    for (Goods good : this.allocatedMarket.getMarket().getGoods()) {
      if (this.allocatedMarket.allocationFromGood(good) == 0) {
        this.linearConstrains.add(this.cplex.addLe(this.prices[this.goodToPriceIndex.get(good)], reserve));
        this.linearConstrains.add(this.cplex.addGe(this.prices[this.goodToPriceIndex.get(good)], reserve));
      }
    }
  }

}
