package algorithms.ascendingauction;

import java.util.Comparator;

/**
 * Comparator to compare two UserPrice objects by price.
 * 
 * @author Enrique Areyan Viqueira
 */
public class UserPriceComparatorByPrice implements Comparator<UserPrice> {

  @Override
  public int compare(UserPrice o1, UserPrice o2) {
    if (o1.getPrice() > o2.getPrice()) {
      return 1;
    } else if (o1.getPrice() < o2.getPrice()) {
      return -1;
    } else {
      return 0;
    }
  }

}
