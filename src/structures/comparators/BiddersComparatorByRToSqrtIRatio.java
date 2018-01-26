package structures.comparators;

import java.util.Comparator;

import structures.Bidder;
import structures.Goods;

/**
 * This class implements a comparator to compare bidders by their reward to demand ratio.
 * 
 * @author Enrique Areyan Viqueira
 */
public class BiddersComparatorByRToSqrtIRatio<G extends Goods, B extends Bidder<G>> implements Comparator<B> {

  @Override
  public int compare(B b1, B b2) {
    if (b1.getReward() / Math.sqrt(b1.getDemand()) < b2.getReward() / Math.sqrt(b2.getDemand())) {
      return 1;
    } else if (b1.getReward() / Math.sqrt(b1.getDemand()) > b2.getReward() / Math.sqrt(b2.getDemand())) {
      return -1;
    }
    return 0;
  }

}
