package algorithms.pricing.lp.reserveprices;

import structures.Market;
import structures.MarketAllocation;
import allocations.greedy.CampaignComparatorByRewardToImpressionsRatio;
import allocations.greedy.GreedyAllocation;
import allocations.greedy.UsersSupplyComparatorByRemainingSupply;

public class GreedyAlloc implements AllocationAlgorithm{
	protected int order;
	public GreedyAlloc(int order){
		this.order = order;
	}
	@Override
	public MarketAllocation getAllocWithReservePrice(Market market, double reserve) {
		//System.out.println("GreedyAlloc with reserve = " + reserve);
		GreedyAllocation G = new GreedyAllocation(market,new CampaignComparatorByRewardToImpressionsRatio(), new UsersSupplyComparatorByRemainingSupply(this.order),reserve);
		return G.Solve();
	}
}
