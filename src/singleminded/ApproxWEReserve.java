package singleminded;

import structures.Market;
import structures.MarketAllocation;
import structures.MarketPrices;
import structures.exceptions.BidderCreationException;
import structures.factory.SingleMindedMarketFactory;
import algorithms.pricing.lp.reserveprices.AllocationAlgorithm;
import allocations.error.AllocationException;

/**
 * 
 * 
 * @author Enrique Areyan Viqueira
 */
public class ApproxWEReserve implements AllocationAlgorithm {

  @Override
  public MarketAllocation getAllocWithReservePrice(Market market, double reserve) throws AllocationException, BidderCreationException {
    Market discountedM = SingleMindedMarketFactory.discountSingleMindedMarket(market, reserve);
    //System.out.println(discountedM);
    MarketPrices discountedApproxWEResult = new ApproxWE(discountedM).Solve();
    return discountedApproxWEResult.getMarketAllocation();
  }

}
