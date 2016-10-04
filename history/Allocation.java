package structures;

import structures.exceptions.AllocationException;

/**
 * This class represents the allocation from a good to a bidder (or
 * equivalently, the allocation to a bidder from a good). A composition of this
 * class represents an allocation.
 * 
 * @author Enrique Areyan Viqueira
 *
 * @param <G>
 * @param <B>
 */
public class Allocation<G extends Goods, B extends Bidder<G>> {

  /**
   * Reference to a good.
   */
  public final G good;

  /**
   * Reference to a bidder.
   */
  public final B bidder;

  /**
   * The number of copies of good allocated to bidder.
   */
  public final int allocation;

  /**
   * Constructor.
   * 
   * @param good
   * @param bidder
   * @param allocation - an integer.
   * @throws AllocationException in case the allocation is less than zero.
   */
  public Allocation(G good, B bidder, int allocation) throws AllocationException {
    this.good = good;
    this.bidder = bidder;
    if (allocation < 0) {
      throw new AllocationException("An allocation from a good to a bidder has to be a positive integer.");
    }
    this.allocation = allocation;
  }

  @Override
  public String toString() {
    return "(G = " + this.good + ", B = " + this.bidder + ", x = " + this.allocation + ")";
  }

}
