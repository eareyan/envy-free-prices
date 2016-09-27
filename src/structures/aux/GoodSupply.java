package structures.aux;

/**
 * This auxiliary class represents a good and its supply. This class is used
 * mainly in allocation algorithms to be able to sort goods by some function of
 * their supply, for example, by their remaining supply.
 * 
 * @author Enrique Areyan Viqueira
 */
public class GoodSupply {

  /**
   * Good index.
   */
  protected int i;

  /**
   * Good supply.
   */
  protected int supply;

  /**
   * Constructor.
   * 
   * @param i - good index.
   * @param supply - good supply
   */
  public GoodSupply(int i, int supply) {
    this.i = i;
    this.supply = supply;
  }

  /**
   * Getter.
   * 
   * @return the good index.
   */
  public int getId() {
    return this.i;
  }

  /**
   * Getter.
   * 
   * @return the good supply.
   */
  public int getSupply() {
    return this.supply;
  }

  /**
   * Decrements this good supply.
   * 
   * @param supplyDecrement - by how much to decrement this good supply.
   */
  public void decrementSupply(int supplyDecrement) {
    this.supply -= supplyDecrement;
  }

  @Override
  public String toString() {
    return "(" + this.i + "," + this.supply + ")";
  }
  
}
