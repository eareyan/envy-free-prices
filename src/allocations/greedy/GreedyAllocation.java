package allocations.greedy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import structures.Bidder;
import structures.Market;
import structures.MarketAllocation;
import structures.exceptions.BidderCreationException;
import allocations.interfaces.AllocationAlgoInterface;
import allocations.objectivefunction.ObjectiveFunction;
import allocations.objectivefunction.SingleStepFunction;
import structures.aux.BiddersComparatorByRewardToImpressionsRatio;
import structures.aux.GoodSupply;
import structures.aux.GoodsSupplyComparatorBySupply;

/**
 * This class implements greedy allocation.
 * 
 * @author Enrique Areyan Viqueira
 */
public class GreedyAllocation implements AllocationAlgoInterface {

  /**
   * Campaign comparator.
   */
  protected Comparator<Bidder> CampaignComparator;

  /**
   * User comparator
   */
  protected Comparator<GoodSupply> UserSupplyComparator;

  /**
   * Constructor.
   */
  public GreedyAllocation() {
    this.CampaignComparator = new BiddersComparatorByRewardToImpressionsRatio();
    this.UserSupplyComparator = new GoodsSupplyComparatorBySupply();
  }

  /**
   * Constructor.
   * @param CampaignComparator - a comparator to order campaigns
   * @param UserSupplyComparator - a comparator to order users
   */
  public GreedyAllocation(Comparator<Bidder> CampaignComparator, Comparator<GoodSupply> UserSupplyComparator) {
    this.CampaignComparator = CampaignComparator;
    this.UserSupplyComparator = UserSupplyComparator;
  }
  
  /**
   * Constructor.
   * 
   * @param userOrder - order of remaining user supply. is 1 means ASC and -1 means
   *          DESC, any other means no order
   */
  public GreedyAllocation(int userOrder) {
    this.CampaignComparator = new BiddersComparatorByRewardToImpressionsRatio();
    this.UserSupplyComparator = new GoodsSupplyComparatorBySupply(userOrder);
  }
  
  /**
   * Solve for the greedy allocation.
   * @param market - the market object to allocate.
   */
  public MarketAllocation Solve(Market market) throws BidderCreationException {
    // Make a copy of the campaigns array, filtering the campaigns already allocated. 
    // Sort the copied list by reward.
    ArrayList<Bidder> campaigns = new ArrayList<Bidder>();
    for (int j = 0; j < market.getNumberBidders(); j++) {
      if (market.getBidder(j).getDemand() - market.getBidder(j).getAllocationSoFar() > 0
          & market.getBidder(j).getReward() - market.getBidder(j).getReserve() * (market.getBidder(j).getDemand() - market.getBidder(j).getAllocationSoFar()) > 0) {
        campaigns.add(new Bidder(market.getBidder(j).getDemand(), 
            market.getBidder(j).getReward() - market.getBidder(j).getReserve() * (market.getBidder(j).getDemand() - market.getBidder(j).getAllocationSoFar()), j));
      }
    }
    //Sort campaigns by the given comparator.
    Collections.sort(campaigns, this.CampaignComparator);
    int[][] greedyAllocation = new int[market.getNumberGoods()][market.getNumberBidders()];
    int[] totalAllocationFromUserSoFar = new int[market.getNumberGoods()];
    // Allocate each campaign, if possible, one at a time.
    for (Bidder currentCampaign : campaigns) {
      ArrayList<GoodSupply> accessibleUsers = new ArrayList<GoodSupply>();
      int totalAvailableSupply = 0;
      int totalAllocationToCampaignSoFar = 0;
      for (int i = 0; i < market.getNumberGoods(); i++) { 
        // Compute the accessible users to this campaign.
        // z_ij computes the number of impressions available from user i to campaign j.
        int z_ij = (int) Math.min(
            market.getGood(i).getSupply() - totalAllocationFromUserSoFar[i],
            Math.floor(market.getBidder(currentCampaign.getBackpointer()).getLevel() * market.getGood(i).getSupply()));
        if (market.isConnected(i, currentCampaign.getBackpointer()) && z_ij > 0) {
          totalAvailableSupply += z_ij;
          accessibleUsers.add(new GoodSupply(i, market.getGood(i).getSupply() - totalAllocationFromUserSoFar[i]));
        }
      }
      Collections.sort(accessibleUsers, this.UserSupplyComparator);
      if (totalAvailableSupply >= market.getBidder(currentCampaign.getBackpointer()).getDemand() - market.getBidder(currentCampaign.getBackpointer()).getAllocationSoFar()) {
        // Try to allocate, one user at the time.
        for (GoodSupply user : accessibleUsers) {
          int i = user.getId(); // User Index
          int jIndex = currentCampaign.getBackpointer(); // Campaign Index
          int z_ij = (int) Math.min(
              market.getGood(i).getSupply() - totalAllocationFromUserSoFar[i],
              Math.floor(market.getBidder(jIndex).getLevel() * market.getGood(i).getSupply()));
          greedyAllocation[i][jIndex] = Math.min(market.getBidder(jIndex).getDemand() - market.getBidder(jIndex).getAllocationSoFar() - totalAllocationToCampaignSoFar, z_ij);
          totalAllocationFromUserSoFar[i] += greedyAllocation[i][jIndex];
          totalAllocationToCampaignSoFar += greedyAllocation[i][jIndex];
          if (totalAllocationToCampaignSoFar == market.getBidder(jIndex).getDemand()) {
            // Break if the current campaign has been completely satisfied.
            break; 
          }
        }
      }
    }
    return new MarketAllocation(market, greedyAllocation, this.getObjectiveFunction());
  }

  @Override
  public ObjectiveFunction getObjectiveFunction() {
    return new SingleStepFunction();
  }
  
}
