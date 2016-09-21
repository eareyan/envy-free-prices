package allocations.greedy;

/**
 * This auxiliary data structure keeps track of the remaining supply of users
 * so that they can be ordered before being assigned to campaigns.
 * @author Enrique Areyan Viqueira
 */
public class UserSupply {
  
  /**
   * User index
   */
  protected int i;
  
  /**
   * Remaining supply of this user.
   */
  protected int remainingSupply;

  /**
   * Constructor.
   * @param i - user index.
   * @param remainingSupply - user remaining supply.
   */
  public UserSupply(int i, int remainingSupply) {
    this.i = i;
    this.remainingSupply = remainingSupply;
  }

  /**
   * Getter.
   * @return the user id.
   */
  public int getId() {
    return this.i;
  }

  /**
   * Getter.
   * @return the user remaining supply.
   */
  public int getRemainingSupply() {
    return this.remainingSupply;
  }

  @Override
  public String toString() {
    return "(" + i + "," + this.remainingSupply + ")";
  }
  
}
