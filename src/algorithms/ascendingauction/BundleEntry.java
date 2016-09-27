package algorithms.ascendingauction;

/**
 * Represents an entry of a bundle.
 * This class contains a pair (i,x), where i is a user id and x is an integer
 * corresponding to how many users from i are allocated to the campaign.
 * 
 * @author Enrique Areyan Viqueira
 */
public class BundleEntry {
  
  /**
   * User id
   */
  protected int i;
 
  /**
   * Allocation from user i.
   */
  protected int x;

  /**
   * Constructor.
   * 
   * @param i - user id.
   * @param x - allocation from user i.
   */
  public BundleEntry(int i, int x) {
    this.i = i;
    this.x = x;
  }
  
  /**
   * Getter.
   * 
   * @return - user id
   */
  public int getI() {
    return this.i;
  }
  
  /**
   * Getter.
   * 
   * @return - x, the allocation from user i. 
   */
  public int getX() {
    return this.x;
  }
  
  @Override
  public String toString() {
    return "(" + this.i + "," + this.x + ")";
  }
  
}
