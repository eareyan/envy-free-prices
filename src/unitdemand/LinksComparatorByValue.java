package unitdemand;

import java.util.Comparator;

/**
 * Comparator to compare two links by value.
 * 
 * @author Enrique Areyan Viqueira
 */
public class LinksComparatorByValue implements Comparator<Link> {
  @Override
  public int compare(Link l1, Link l2) {
    if (l1.getValue() < l2.getValue()) {
      return 1;
    } else if (l1.getValue() > l2.getValue()) {
      return -1;
    } else {
      return 0;
    }
  }
  
}

