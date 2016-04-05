package algorithms.pricing.lp.reserveprices;

import structures.Market;
import structures.MarketAllocation;

public interface AllocationAlgorithm {
	
	public MarketAllocation getAllocWithReservePrice(Market market, double reserve); 
}
