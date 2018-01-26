package structures.comparators;

import java.util.Comparator;

import structures.Bidder;
import structures.Goods;

/**
 * This class implements a comparator to compare bidders by their reward to demand ratio.
 * 
 * @author Enrique Areyan Viqueira
 */
public class BiddersComparatorByRewardToImpressionsRatio implements Comparator<Bidder<Goods>> {

  @Override
  public int compare(Bidder<Goods> b1, Bidder<Goods> b2) {
    if (b1.getReward() / b1.getDemand() < b2.getReward() / b2.getDemand()) {
      return 1;
    } else if (b1.getReward() / b1.getDemand() > b2.getReward() / b2.getDemand()) {
      return -1;
    }
    return 0;
  }

}
