package algorithms.pricing.lp.reserveprices;

import algorithms.waterfall.Waterfall;
import algorithms.waterfall.WaterfallPrices;
import structures.Market;
import structures.MarketAllocation;

/**
 * This class implements AllocationAlgorithm and implements efficient
 * allocation.
 * 
 * @author Enrique Areyan Viqueira
 */
public class WFAlloc implements AllocationAlgorithm {
  @Override
  public MarketAllocation getAllocWithReservePrice(Market market, double reserve) {
    Waterfall wf = new Waterfall(market, reserve);
    WaterfallPrices wfSol = wf.Solve();
    return wfSol.getMarketAllocation();
  }
  
}
