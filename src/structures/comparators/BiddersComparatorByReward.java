package structures.comparators;

import java.util.Comparator;

import structures.Bidder;
import structures.Goods;

/**
 * This class implements a comparator to compare bidders by their reward.
 * 
 * @author Enrique Areyan Viqueira
 */
public class BiddersComparatorByReward<G extends Goods, B extends Bidder<G>> implements Comparator<B> {

  @Override
  public int compare(B b1, B b2) {
    if (b1.getReward() < b2.getReward()) {
      return 1;
    } else if (b1.getReward() > b2.getReward()) {
      return -1;
    }
    return 0;
  }

}
