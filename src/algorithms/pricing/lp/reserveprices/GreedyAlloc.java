package algorithms.pricing.lp.reserveprices;

import structures.Market;
import structures.MarketAllocation;
import structures.exceptions.CampaignCreationException;
import allocations.greedy.CampaignComparatorByRewardToImpressionsRatio;
import allocations.greedy.GreedyAllocation;
import allocations.greedy.UsersSupplyComparatorByRemainingSupply;

/**
 * This class implements AllocationAlgorithm and implements greedy allocation.
 * 
 * @author Enrique Areyan Viqueira
 */
public class GreedyAlloc implements AllocationAlgorithm {
  /**
   * Ordering of users by remaining supply.
   */
  protected int order;

  /**
   * Constructor for greedy allocation algorithm
   * @param order - the order in which to order users by remaining supply: 
   *                1 means ASC and -1 means DESC, any other means no order
   */
  public GreedyAlloc(int order) {
    this.order = order;
  }

  @Override
  public MarketAllocation getAllocWithReservePrice(Market market, double reserve) throws CampaignCreationException {
    // System.out.println("GreedyAlloc with reserve = " + reserve);
    market.setReserveAllCampaigns(reserve);
    GreedyAllocation G = new GreedyAllocation(
        new CampaignComparatorByRewardToImpressionsRatio(),
        new UsersSupplyComparatorByRemainingSupply(this.order));
    return G.Solve(market);
  }
  
}
