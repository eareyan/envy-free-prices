package algorithms.pricing.lp.reserveprices;

import structures.Market;
import structures.MarketAllocation;
import allocations.error.AllocationException;
import allocations.optimal.SingleStepEfficientAllocationILP;

public class EfficientAlloc implements AllocationAlgorithm{

	@Override
	public MarketAllocation getAllocWithReservePrice(Market market, double reserve) throws AllocationException{
		//System.out.println("EfficientAlloc with reserve = " + reserve);
		market.setReserveAllCampaigns(reserve);
		return new MarketAllocation(market,new SingleStepEfficientAllocationILP().Solve(market).getAllocation());
	}
}
