package algorithms.pricing.lp.reserveprices;

import algorithms.Waterfall;
import algorithms.WaterfallPrices;
import structures.Market;
import structures.MarketAllocation;

public class WFAlloc implements AllocationAlgorithm{
	@Override
	public MarketAllocation getAllocWithReservePrice(Market market, double reserve) {
		Waterfall wf = new Waterfall(market, reserve);
		WaterfallPrices wfSol = wf.Solve();
		return wfSol.getMarketAllocation();
	}
}
