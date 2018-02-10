package algorithms.pricing.helper;

import java.util.ArrayList;

/**
 * 
 * @author Enrique Areyan Viqueira
 */
public class Output {
  /**
   * The output consists of a list of SupplyHelperList.
   */
  private ArrayList<SupplyHelperList> listOfLists;

  /**
   * Constructor.
   */
  public Output() {
    this.listOfLists = new ArrayList<SupplyHelperList>();
  }

  /**
   * Adds a list to our lists of lists only in case the lits is not already contained in our list of lists.
   * 
   * @param l
   */
  public void addList(SupplyHelperList l) {
    if (!this.containsList(l)) {
      this.listOfLists.add(l);
    }
  }

  /**
   * Checks if a given input SupplyHelperList is already contained in the list of lists.
   * 
   * @param l
   * @return
   */
  public boolean containsList(SupplyHelperList l) {
    for (SupplyHelperList lprime : this.listOfLists) {
      if (lprime.equals(l)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return "output = " + this.listOfLists;
  }

}