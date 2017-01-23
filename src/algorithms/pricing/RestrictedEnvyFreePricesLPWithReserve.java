package algorithms.pricing;

import ilog.concert.IloException;
import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.exceptions.MarketAllocationException;

/**
 * This class extends RestrictedEnvyFreePricesLP and provides methods to add reserve prices. Using reserve prices one can find a restricted Walrasian
 * Equilibrium with reserve.
 * 
 * @author Enrique Areyan Viqueira
 */
public class RestrictedEnvyFreePricesLPWithReserve<M extends Market<G, B>, G extends Goods, B extends Bidder<G>> extends RestrictedEnvyFreePricesLP<M, G, B> {

  /**
   * The reserve price.
   */
  private final double reserve;

  /**
   * Constructor.
   * 
   * @param allocatedMarket
   * @param reserve
   * @throws IloException
   */
  public RestrictedEnvyFreePricesLPWithReserve(MarketAllocation<M, G, B> allocatedMarket, double reserve) throws IloException {
    super(allocatedMarket);
    this.reserve = reserve;
  }

  /**
   * Set Reserve price for good i.
   * 
   * @param i - good index.
   * @param reservePrice - reserve price.
   */
  private void setReservePrice() {
    try {
      if (this.verbose) {
        System.out.println("Setting Reserve Price of " + this.reserve + " for all goods ");
      }
      for (Goods good : this.allocatedMarket.getMarket().getGoods()) {
        this.linearConstrains.add(this.cplex.addGe(this.prices[this.goodToPriceIndex.get(good)], this.reserve));
      }
    } catch (IloException e) {
      System.out.println("Exception: ==>");
      e.printStackTrace();
    }
  }

  /**
   * This method generates the market clearance condition with reserve prices. This conditions state that unallocated items must be priced at the reserve.
   * 
   * @param reserve - reserve price
   * @throws IloException in case the LP failed
   * @throws MarketAllocationException
   */
  @Override
  protected void generateMarketClearanceConditions() throws IloException, MarketAllocationException {
    System.out.println("In RestrictedEnvyFreePricesLPWithReserve, generateMarketClearanceConditions = " + this.reserve);
    for (G good : this.allocatedMarket.getMarket().getGoods()) {
      if (this.allocatedMarket.allocationFromGood(good) == 0) {
        this.linearConstrains.add(this.cplex.addLe(this.prices[this.goodToPriceIndex.get(good)], this.reserve));
        this.linearConstrains.add(this.cplex.addGe(this.prices[this.goodToPriceIndex.get(good)], this.reserve));
      }
    }
  }

  /**
   * This method creates the LP.
   * 
   * @throws MarketAllocationException
   *
   */
  @Override
  public void createLP() throws MarketAllocationException {
    super.createLP();
    this.setReservePrice();
  }

}
