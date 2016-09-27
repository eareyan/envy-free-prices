package structures.comparators;

import java.util.Comparator;

import structures.Bidder;

/**
 * This class implements a comparator by reward for campaigns
 * 
 * @author Enrique Areyan Viqueira
 */
public class CampaignComparatorByReward implements Comparator<Bidder> {
  @Override
  public int compare(Bidder c1, Bidder c2) {
    if (c1.getReward() < c2.getReward()) {
      return 1;
    } else if (c1.getReward() > c2.getReward()) {
      return -1;
    } else {
      return 0;
    }
  }
}
