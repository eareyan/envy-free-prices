package unitdemand.ascendingauction;

import java.util.Comparator;

/**
 * Comparator to compare two matchings by seller revenue.
 * 
 * @author Enrique Areyan Viqueira
 */
public class BidsComparatorByBid implements Comparator<Bid> {
  @Override
  public int compare(Bid b1, Bid b2) {
    if (b1.getBid() < b2.getBid()) {
      return 1;
    } else if (b1.getBid() > b2.getBid()) {
      return -1;
    } else {
      return 0;
    }
  }
}