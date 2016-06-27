package algorithms.pricing.lp.reserveprices;

import structures.Market;
import structures.MarketAllocation;
import structures.exceptions.CampaignCreationException;
import allocations.greedy.CampaignComparatorByRewardToImpressionsRatio;
import allocations.greedy.GreedyAllocation;
import allocations.greedy.UsersSupplyComparatorByRemainingSupply;

public class GreedyAlloc implements AllocationAlgorithm{
	protected int order;
	public GreedyAlloc(int order){
		this.order = order;
	}
	@Override
	public MarketAllocation getAllocWithReservePrice(Market market, double reserve) throws CampaignCreationException {
		//System.out.println("GreedyAlloc with reserve = " + reserve);
		market.setReserveAllCampaigns(reserve);
		GreedyAllocation G = new GreedyAllocation(new CampaignComparatorByRewardToImpressionsRatio(), new UsersSupplyComparatorByRemainingSupply(this.order));
		return G.Solve(market);
	}
}
