package structures.aux;

import java.util.Comparator;

import structures.Bidder;

/**
 * This class implements a comparator to compare b by their
 * reward to demand ratio.
 * 
 * @author Enrique Areyan Viqueira
 */
public class BiddersComparatorByRewardToImpressionsRatio implements Comparator<Bidder> {

  @Override
  public int compare(Bidder c1, Bidder c2) {
    if (c1.getReward() / (c1.getDemand() - c1.getAllocationSoFar()) < c2.getReward() / (c2.getDemand() - c2.getAllocationSoFar())) {
      return 1;
    } else if (c1.getReward() / (c1.getDemand() - c1.getAllocationSoFar()) > c2.getReward() / (c2.getDemand() - c2.getAllocationSoFar())) {
      return -1;
    }
    return 0;
  }

}
