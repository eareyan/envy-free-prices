package algorithms.pricing.helper;

import structures.Goods;

/**
 * 
 * @author Enrique Areyan Viqueira
 */
public class SupplyHelper {
  /**
   * A good.
   */
  private final Goods g;

  /**
   * How many units of supply of this good in use.
   */
  private final int usedSupply;

  /**
   * Constructor.
   * 
   * @param g
   * @param usedSupply
   */
  public SupplyHelper(Goods g, int usedSupply) {
    this.g = g;
    this.usedSupply = usedSupply;
  }

  /**
   * Getter.
   * 
   * @return
   */
  public Goods getGood() {
    return this.g;
  }

  /**
   * Getter.
   * 
   * @return
   */
  public int getSupply() {
    return this.usedSupply;
  }

  @Override
  public boolean equals(Object o) {
    // If the object is compared with itself then return true
    if (o == this) {
      return true;
    }
    // Check if o is an instance of supplyHelper or not "null instanceof [type]" also returns false
    if (!(o instanceof SupplyHelper)) {
      return false;
    }
    // typecast o to supplyHelper so that we can compare data members.
    SupplyHelper c = (SupplyHelper) o;
    return ((c.getSupply() == this.getSupply()) && (c.getGood() == this.getGood()));
  }

  @Override
  public String toString() {
    return "(" + this.g + "," + this.usedSupply + ")";
  }
}