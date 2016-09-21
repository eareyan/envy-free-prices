package structures;

/**
 * Represents a single user.
 * 
 * @author Enrique Areyan Viqueira
 */
public class User {
  
  /**
   * Supply of this user
   */
  protected int supply;
  
  /**
   * Reserve price.
   */
  protected double reservePrice = 0.0;

  /**
   * Constructor
   */
  public User(int supply) {
    this.supply = supply;
  }

  /**
   * Constructor.
   * @param supply - integer corresponding to the user supply.
   * @param reservePrice - the user reserve price.
   */
  public User(int supply, double reservePrice) {
    this(supply);
    this.reservePrice = reservePrice;
  }
  
  /**
   * Getter.
   * @return the user supply.
   */
  public int getSupply() {
    return this.supply;
  }

  /**
   * Getter.
   * @return the user reserve price.
   */
  public double getReservePrice() {
    return this.reservePrice;
  }
  
}
