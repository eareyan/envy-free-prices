package structures.comparators;

import java.util.Comparator;

import structures.Bidder;
import structures.Goods;

/**
 * This class implements a comparator to compare bidders by their reward.
 * 
 * @author Enrique Areyan Viqueira
 */
public class BiddersComparatorByReward implements Comparator<Bidder<Goods>> {

  @Override
  public int compare(Bidder<Goods> c1, Bidder<Goods> c2) {
    if (c1.getReward() < c2.getReward()) {
      return 1;
    } else if (c1.getReward() > c2.getReward()) {
      return -1;
    }
    return 0;
  }

}
