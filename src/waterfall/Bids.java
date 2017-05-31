package waterfall;

import java.util.Collections;
import java.util.List;

import structures.Goods;

/**
 * This class contains all the bids made for a specific good.
 * 
 * @author Enrique Areyan Viqueira
 * @param <G>
 */
public class Bids<G extends Goods> {

  /**
   * Good.
   */
  private final G good;

  /**
   * List of bids.
   */
  private final List<Double> bids;

  /**
   * Constructor.
   * 
   * @param good
   * @param bids
   */
  public Bids(G good, List<Double> bids) {
    this.good = good;
    Collections.sort(bids);
    Collections.reverse(bids);
    this.bids = bids;
  }

  /**
   * Gets the good associated with the bids.
   * 
   * @return
   */
  public G getGood() {
    return this.good;
  }

  /**
   * Gets the second highest bid. Returns 0.0 if there are one or fewer bids.
   * 
   * @return the second highest bid or 0.0.
   */
  public double get2ndHighest() {
    if (this.bids.size() > 1) {
      return this.bids.get(1);
    } else {
      return 0.0;
    }
  }

  @Override
  public String toString() {
    return this.good + "-" + this.bids;
  }

}
