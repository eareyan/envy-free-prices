package structures;

import java.util.ArrayList;

import structures.exceptions.MarketCreationException;

import com.google.common.collect.ImmutableList;

/**
 * A market is a collection of goods and a collection of bidders that demand
 * goods. This class implements a market object and related basic functionality.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Market<G extends Goods, B extends Bidder<G>> {

  /**
   * Array of Goods.
   */
  protected ImmutableList<G> goods;

  /**
   * Array of Bidders.
   */
  protected ImmutableList<B> bidders;

  /**
   * Highest reward among all bidders. Implemented as a singleton.
   */
  protected double highestReward = -1.0;
  
  /**
   * Total supply of a market. Implemented as a singleton.
   */
  protected int totalSupply = -1;
  
  /**
   * Total demand of a market. Implemented as a singleton.
   */
  protected int totalDemand = -1;
  
  /**
   * Total supply to demand ratio. Implemented as a singleton.
   */
  protected double supplyToDemandRatio = -1.0;

  /**
   * Constructor for a market. Receives bidders and goods as ArrayLists.
   * 
   * @param goods - an ArrayList of goods.
   * @param bidders - an ArrayList of bidders.
   * @throws MarketCreationException 
   */
  public Market(ArrayList<G> goods, ArrayList<B> bidders) throws MarketCreationException{
    // Create immutable goods list.
    if(goods == null || goods.size() == 0){
      throw new MarketCreationException("A market must contain at least one good.");
    }
    ImmutableList.Builder<G> goodsBuilder = ImmutableList.builder();
    goodsBuilder.addAll(goods);
    this.goods = goodsBuilder.build();
    // Create immutable bidders list.
    if(bidders == null || bidders.size() == 0){
      throw new MarketCreationException("A market must contain at least one bidder");
    }
    ImmutableList.Builder<B> biddersBuilder = ImmutableList.builder();
    biddersBuilder.addAll(bidders);
    this.bidders = biddersBuilder.build();
  }
  
  /**
   * Gets the array of bidders.
   * 
   * @return an array of Bidder objects.
   */
  public ImmutableList<B> getBidders() {
    return this.bidders;
  }

  /**
   * Get the array of goods.
   * 
   * @return an array of Goods objects.
   */
  public ImmutableList<G> getGoods() {
    return this.goods;
  }

  /**
   * Gets the number of goods.
   * 
   * @return the number of goods.
   */
  public int getNumberGoods() {
    return this.goods.size();
  }

  /**
   * Gets the number of bidders.
   * 
   * @return the number of bidders.
   */
  public int getNumberBidders() {
    return this.bidders.size();
  }

  /**
   * Computes the highest reward among all bidders in the market.
   * Implements singleton.
   * 
   * @return the max value of rewards among all bidders.
   */
  public double getHighestReward() {
    if (this.highestReward == -1.0) {
      double tempHighestReward = -1.0;
      for (B bidder : this.bidders) {
        if (bidder.getReward() > tempHighestReward) {
          tempHighestReward = bidder.getReward();
        }
      }
      this.highestReward = tempHighestReward;
    }
    return this.highestReward;
  }

  /**
   * This method computes the total number of goods supplied by the market.
   * 
   * @return the total number of goods supplied in the market, i.e., \sum_i N_i.
   */
  public int getTotalSupply() {
    if (this.totalSupply == -1) {
      int tempTotalSupply = 0;
      for (G good : this.goods) {
        tempTotalSupply += good.getSupply();
      }
      this.totalSupply = tempTotalSupply;
    }
    return this.totalSupply;
  }

  /**
   * This method computes the total number of goods demanded by the market.
   * 
   * @return the total number of goods demanded in the market, i.e., \sum_j I_j
   */
  public int getTotalDemand() {
    if (this.totalDemand == -1) {
      int tempTotalDemand = 0;
      for (B bidder : this.bidders) {
        tempTotalDemand += bidder.getDemand();
      }
      this.totalDemand = tempTotalDemand;
    }
    return this.totalDemand;
  }

  /**
   * This method computes the supply to demand ratio of the market.
   * 
   * @return the supply to demand ratio of the market.
   */
  public double getSupplyToDemandRatio() {
    return (this.supplyToDemandRatio == -1.0) ? (double) this.getTotalSupply() / this.getTotalDemand() : this.supplyToDemandRatio;
  }

  /**
   * Printer. Representation of objects as strings.
   * 
   * @return a string representation of the information of goods in the market
   */
  protected String stringGoodsInfo() {
    String ret = "";
    for (G good : this.goods) {
      ret += "\nN = " + good.supply;
    }
    return ret;
  }

  /**
   * Printer. Representation of objects as strings.
   * 
   * @return a string representation of the information of bidders in the market
   */
  protected String stringBiddersInfo() {
    String ret = String.format("%-20s %-20s", "Bidders Demand", "Bidders Reward");
    for (B bidder : this.bidders) {
      ret += "\n" +
          String.format("%-20s %-20s", 
              "I = " + bidder.demand , 
              "R = " + String.format("%.2f", bidder.reward) + ";" );
    }
    return ret;
  }

  /**
   * Printer. Representation of objects as strings.
   * 
   * @return a string representation of the connection matrix.
   */
  protected String stringConnectionsMatrix() {
    String ret = "";
    for (G good : this.goods) {
      ret += "\n";
      for (B bidder : this.bidders) {
        if (bidder.demandsGood(good)) {
          ret += "\t yes";
        } else {
          ret += "\t no";
        }
      }
    }
    return ret + "\n";
  }

  @Override
  public String toString() {
    return "NbrBidders:\t"
        + this.getNumberBidders()
        + "\n"
        + "NbrGoods:\t"
        + this.getNumberGoods()
        + "\n\n"
        + this.stringBiddersInfo() + "\n"
        + "\nGoods Supply" + this.stringGoodsInfo() + "\n"
        + "\nConnections Matrix:\t" + this.stringConnectionsMatrix();
  }
  
}
