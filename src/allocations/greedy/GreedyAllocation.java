package allocations.greedy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import structures.Campaign;
import structures.Market;
import structures.MarketAllocation;
import structures.exceptions.CampaignCreationException;
import allocations.interfaces.AllocationAlgoInterface;
import allocations.objectivefunction.ObjectiveFunction;
import allocations.objectivefunction.SingleStepFunction;

/**
 * This class implements greedy allocation.
 * 
 * @author Enrique Areyan Viqueira
 */
public class GreedyAllocation implements AllocationAlgoInterface {

  /**
   * Campaign comparator.
   */
  protected Comparator<Campaign> CampaignComparator;

  /**
   * User comparator
   */
  protected Comparator<UserSupply> UserSupplyComparator;

  /**
   * Constructor.
   */
  public GreedyAllocation() {
    this.CampaignComparator = new CampaignComparatorByRewardToImpressionsRatio();
    this.UserSupplyComparator = new UsersSupplyComparatorByRemainingSupply();
  }

  /**
   * Constructor.
   * @param CampaignComparator - a comparator to order campaigns
   * @param UserSupplyComparator - a comparator to order users
   */
  public GreedyAllocation(Comparator<Campaign> CampaignComparator, Comparator<UserSupply> UserSupplyComparator) {
    this.CampaignComparator = CampaignComparator;
    this.UserSupplyComparator = UserSupplyComparator;
  }
  /**
   * Solve for the greedy allocation.
   * @param market - the market object to allocate.
   */
  public MarketAllocation Solve(Market market) throws CampaignCreationException {
    // Make a copy of the campaigns array, filtering the campaigns already allocated. 
    // Sort the copied list by reward.
    ArrayList<Campaign> campaigns = new ArrayList<Campaign>();
    for (int j = 0; j < market.getNumberCampaigns(); j++) {
      if (market.getCampaign(j).getDemand() - market.getCampaign(j).getAllocationSoFar() > 0
          & market.getCampaign(j).getReward() - market.getCampaign(j).getReserve() * (market.getCampaign(j).getDemand() - market.getCampaign(j).getAllocationSoFar()) > 0) {
        campaigns.add(new Campaign(market.getCampaign(j).getDemand(), 
            market.getCampaign(j).getReward() - market.getCampaign(j).getReserve() * (market.getCampaign(j).getDemand() - market.getCampaign(j).getAllocationSoFar()), j));
      }
    }
    //Sort campaigns by the given comparator.
    Collections.sort(campaigns, this.CampaignComparator);
    int[][] greedyAllocation = new int[market.getNumberUsers()][market.getNumberCampaigns()];
    int[] totalAllocationFromUserSoFar = new int[market.getNumberUsers()];
    // Allocate each campaign, if possible, one at a time.
    for (Campaign currentCampaign : campaigns) {
      ArrayList<UserSupply> accessibleUsers = new ArrayList<UserSupply>();
      int totalAvailableSupply = 0;
      int totalAllocationToCampaignSoFar = 0;
      for (int i = 0; i < market.getNumberUsers(); i++) { 
        // Compute the accessible users to this campaign.
        // z_ij computes the number of impressions available from user i to campaign j.
        int z_ij = (int) Math.min(
            market.getUser(i).getSupply() - totalAllocationFromUserSoFar[i],
            Math.floor(market.getCampaign(currentCampaign.getBackpointer()).getLevel() * market.getUser(i).getSupply()));
        if (market.isConnected(i, currentCampaign.getBackpointer()) && z_ij > 0) {
          totalAvailableSupply += z_ij;
          accessibleUsers.add(new UserSupply(i, market.getUser(i).getSupply() - totalAllocationFromUserSoFar[i]));
        }
      }
      Collections.sort(accessibleUsers, this.UserSupplyComparator);
      if (totalAvailableSupply >= market.getCampaign(currentCampaign.getBackpointer()).getDemand() - market.getCampaign(currentCampaign.getBackpointer()).getAllocationSoFar()) {
        // Try to allocate, one user at the time.
        for (UserSupply user : accessibleUsers) {
          int i = user.getId(); // User Index
          int jIndex = currentCampaign.getBackpointer(); // Campaign Index
          int z_ij = (int) Math.min(
              market.getUser(i).getSupply() - totalAllocationFromUserSoFar[i],
              Math.floor(market.getCampaign(jIndex).getLevel() * market.getUser(i).getSupply()));
          greedyAllocation[i][jIndex] = Math.min(market.getCampaign(jIndex).getDemand() - market.getCampaign(jIndex).getAllocationSoFar() - totalAllocationToCampaignSoFar, z_ij);
          totalAllocationFromUserSoFar[i] += greedyAllocation[i][jIndex];
          totalAllocationToCampaignSoFar += greedyAllocation[i][jIndex];
          if (totalAllocationToCampaignSoFar == market.getCampaign(jIndex).getDemand()) {
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
