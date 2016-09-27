package structures;

/**
 * Represents a single kind of goods.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Goods {
  
  /**
   * Goods supply.
   */
  protected int supply;
  
  /**
   * Reserve price.
   */
  protected double reservePrice = 0.0;

  /**
   * Constructor
   */
  public Goods(int supply) {
    this.supply = supply;
  }

  /**
   * Constructor.
   * @param supply - integer corresponding to the goods supply.
   * @param reservePrice - the goods reserve price.
   */
  public Goods(int supply, double reservePrice) {
    this(supply);
    this.reservePrice = reservePrice;
  }
  
  /**
   * Getter.
   * @return the goods supply.
   */
  public int getSupply() {
    return this.supply;
  }

  /**
   * Getter.
   * @return the goods reserve price.
   */
  public double getReservePrice() {
    return this.reservePrice;
  }
  
}
