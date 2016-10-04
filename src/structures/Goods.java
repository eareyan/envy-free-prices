package structures;

import structures.exceptions.GoodsCreationException;
import structures.exceptions.GoodsException;

/**
 * Represents a single kind of goods.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Goods {
  
  /**
   * Goods supply. Immutable.
   */
  protected final int supply;
  
  /**
   * Reserve price.
   */
  protected final double reservePrice;
  
  /**
   * A mutable field to denote current supply. This is usually used by
   * allocation algorithms.
   */
  protected int remainingSupply;

  /**
   * Constructor
   * 
   * @param supply - integer corresponding to the goods supply.
   * @throws GoodsCreationException in case the good could not be created.
   */
  public Goods(int supply) throws GoodsCreationException {
    this(supply, 0);
  }

  /**
   * Constructor.
   * 
   * @param supply - integer corresponding to the goods supply.
   * @param reservePrice - the goods reserve price.
   * @throws GoodsCreationException in case the good could not be created
   */
  public Goods(int supply, double reservePrice) throws GoodsCreationException {
    if (supply <= 0) {
      throw new GoodsCreationException("The supply of a good must be a positive integer.");
    }
    this.supply = supply;
    if(reservePrice < 0){
      throw new GoodsCreationException("The reserve price must be a positive integer or zero.");
    }
    this.reservePrice = reservePrice;
  }
  
  /**
   * Getter.
   * 
   * @return the goods supply.
   */
  public int getSupply() {
    return this.supply;
  }

  /**
   * Getter.
   * 
   * @return the goods reserve price.
   */
  public double getReservePrice() {
    return this.reservePrice;
  }
  
  /**
   * Getter.
   * 
   * @return the remaining supply of the user.
   */
  public int getRemainingSupply(){
    return this.remainingSupply;
  }
  
  /**
   * Setter.
   * 
   * @param remainingSupply - the remaining supply.
   * @throws GoodsException in case the remaining supply is less than zero.
   */
  public void setRemainingSupply(int remainingSupply) throws GoodsException {
    if(remainingSupply < 0){
      throw new GoodsException("Trying to set the remaining supply of a good to a number less than zero.");
    }
    this.remainingSupply = remainingSupply;
  }
  
  @Override
  public String toString(){
    return "(" + this.supply + "," + this.remainingSupply + ")";
  }
}
