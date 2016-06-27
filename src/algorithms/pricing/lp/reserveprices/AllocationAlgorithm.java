package algorithms.pricing.lp.reserveprices;

import allocations.error.AllocationException;
import structures.Market;
import structures.MarketAllocation;
import structures.exceptions.CampaignCreationException;

public interface AllocationAlgorithm {
	
	public MarketAllocation getAllocWithReservePrice(Market market, double reserve) throws AllocationException, CampaignCreationException; 
}
