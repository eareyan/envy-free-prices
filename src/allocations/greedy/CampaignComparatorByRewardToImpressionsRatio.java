package allocations.greedy;

import java.util.Comparator;

import structures.Campaign;

/**
 * This class implements a comparator to compare campaigns by their
 * reward to demand ratio.
 * @author Enrique Areyan Viqueira
 */
public class CampaignComparatorByRewardToImpressionsRatio implements Comparator<Campaign> {

  @Override
  public int compare(Campaign c1, Campaign c2) {
    if (c1.getReward() / (c1.getDemand() - c1.getAllocationSoFar()) < c2.getReward() / (c2.getDemand() - c2.getAllocationSoFar())) {
      return 1;
    } else if (c1.getReward() / (c1.getDemand() - c1.getAllocationSoFar()) > c2.getReward() / (c2.getDemand() - c2.getAllocationSoFar())) {
      return -1;
    }
    return 0;
  }

}
