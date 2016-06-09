package algorithms.pricing.lp.reserveprices;

import allocations.error.AllocationException;
import structures.Market;
import structures.MarketAllocation;

public interface AllocationAlgorithm {
	
	public MarketAllocation getAllocWithReservePrice(Market market, double reserve) throws AllocationException; 
}
