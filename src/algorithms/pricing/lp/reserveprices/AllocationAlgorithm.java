package algorithms.pricing.lp.reserveprices;

import allocations.error.AllocationException;
import structures.Market;
import structures.MarketAllocation;
import structures.exceptions.CampaignCreationException;
import structures.exceptions.MarketCreationException;

/**
 * Interface. Should be implemented by a class
 * that defines an allocation algorithm to be used with LPReservePrices.
 * 
 * @author Enrique Areyan Viqueira
 */
public interface AllocationAlgorithm {

  /**
   * This method should be extended to implement an allocation algorithm.
   * @param market - market object.
   * @param reserve - reserve price.
   * @return a MarketAllocation object.
   * @throws AllocationException when an allocation algorithm failed.
   * @throws CampaignCreationException when an allocation algorithm failed.
   * @throws MarketCreationException when an error occured creating a market.
   */
  public MarketAllocation getAllocWithReservePrice(Market market, double reserve) throws AllocationException, CampaignCreationException;
  
}
