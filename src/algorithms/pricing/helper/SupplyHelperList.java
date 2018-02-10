package algorithms.pricing.helper;

import java.util.ArrayList;

/**
 * 
 * @author Enrique Areyan Viqueira
 */
public class SupplyHelperList {
  /**
   * List of SupplyHelper Objects.
   */
  private final ArrayList<SupplyHelper> list;

  /**
   * The total supply of all SupplyHelper objects.
   */
  private final int totalSupply;

  /**
   * Constructor.
   * 
   * @param list
   */
  public SupplyHelperList(ArrayList<SupplyHelper> list) {
    this.list = list;
    int counter = 0;
    for (SupplyHelper s : list) {
      counter += s.getSupply();
    }
    this.totalSupply = counter;
  }

  /**
   * Getter.
   * 
   * @return
   */
  public ArrayList<SupplyHelper> getList() {
    return this.list;
  }

  /**
   * Getter.
   * 
   * @return
   */
  public int getTotalSupply() {
    return this.totalSupply;
  }

  @Override
  public boolean equals(Object o) {
    // If the object is compared with itself then return true
    if (o == this) {
      return true;
    }
    // Check if o is an instance of supplyHelper or not "null instanceof [type]" also returns false
    if (!(o instanceof SupplyHelperList)) {
      return false;
    }
    // typecast o to supplyHelper so that we can compare data members.
    SupplyHelperList c = (SupplyHelperList) o;
    if (this.list.size() != c.getList().size()) {
      return false;
    }
    int i = 0;
    for (SupplyHelper s : c.getList()) {
      if (!this.list.get(i).equals(s)) {
        return false;
      }
      i++;
    }
    return true;
  }

  @Override
  public String toString() {
    return this.list + " --> " + this.totalSupply;
  }

}
