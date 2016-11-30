package algorithms.ascendingauction;

import structures.Goods;

/**
 * This class represents a tuple (Good, Price).
 * 
 * @author Enrique Areyan Viqueira
 */
public class GoodPrice {
  /**
   * Good.
   */
  protected final Goods good;
  
  /**
   * Price.
   */
  protected double price;
  
  /**
   * Constructor.
   * 
   * @param good
   * @param price
   */
  public GoodPrice(Goods good, double price) {
    this.good = good;
    this.price = price;
  }
  
  /**
   * Adds epsilon to this good.
   * @param epsilon
   */
  public void incrementePrice(double epsilon) {
    this.price += epsilon;
  }
  
  @Override
  public String toString() {
    return this.good + "," + this.price;
  }
}
