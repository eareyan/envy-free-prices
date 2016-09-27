package algorithms.ascendingauction;

/**
 * This class represents a pair (i,p), where i is the id of a user
 * and p is its price. This class is needed mainly to be able to 
 * sort prices of user classes.
 * 
 * @author Enrique Areyan Viqueira
 */
public class UserPrice {
  
  /**
   * User id
   */
  protected int i;
  
  /**
   * User price
   */
  protected double price;

  /**
   * Constructor.
   * 
   * @param i - the user index.
   * @param price - the user price.
   */
  public UserPrice(int i, double price) {
    this.i = i;
    this.price = price;
  }
  
  /**
   * Getter.
   * 
   * @return user id.
   */
  public int getI() {
    return this.i;
  }
  
  /**
   * Getter.
   * 
   * @return user price.
   */
  public double getPrice() {
    return this.price;
  }
  
  /**
   * Updates the user price.
   * 
   * @param price - new user price
   */
  public void updatePrice(double price) {
    this.price = price;
  }
  
  @Override
  public String toString() {
    return "(" + this.i + "," + this.price + ")";
  }
  
}
