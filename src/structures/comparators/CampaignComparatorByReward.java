package structures.comparators;

import java.util.Comparator;

import structures.Campaign;

/**
 * This class implements a comparator by reward for campaigns
 * 
 * @author Enrique Areyan Viqueira
 */
public class CampaignComparatorByReward implements Comparator<Campaign> {
  @Override
  public int compare(Campaign c1, Campaign c2) {
    if (c1.getReward() < c2.getReward()) {
      return 1;
    } else if (c1.getReward() > c2.getReward()) {
      return -1;
    } else {
      return 0;
    }
  }
}
