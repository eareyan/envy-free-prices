package structures;

import java.util.ArrayList;

import structures.exceptions.MarketPricesException;

/**
 * In this class we have an allocated market and prices.
 * Prices are a vector of prices.
 * 
 * This class support common operations on prices for an allocated market,
 * regardless of the algorithm that produced the allocation and/or prices.
 * Such operations might be printing the prices or computing values such
 * as seller revenue.
 * 
 * @author Enrique Areyan Viqueira
 */
public class MarketPrices {
  
  /**
   * Market Allocation Object. Contains a market object and an allocation
   * (matrix of integers).
   */
  protected MarketAllocation marketAllocation;
  
  /**
   * priceVector[i] is the price per item of good i.
   */
  protected double[] pricesVector;

  /**
   * Basic Constructor.
   */
  public MarketPrices() {

  }
  
  /**
   * Constructor that takes an allocation and a price vector.
   * @param marketAllocation - a MarketAllocation object.
   * @param pricesVector - a vector of prices.
   */
  public MarketPrices(MarketAllocation marketAllocation, double[] pricesVector) {
    this.marketAllocation = marketAllocation;
    this.pricesVector = pricesVector;
  }
  
  /**
   * Getter.
   * @return a MarketAllocation object.
   */
  public MarketAllocation getMarketAllocation() {
    return this.marketAllocation;
  }

  /**
   * Getter.
   * @return a price vector.
   */
  public double[] getPriceVector() {
    return this.pricesVector;
  }

  /**
   * Getter.
   * @param i - a good index.
   * @return the price of good i.
   */
  public double getPriceVectorComponent(int i) {
    return this.pricesVector[i];
  }
  
  /**
   * This function computes the seller revenue for the entire market.
   * Seller revenue is defined as: \sum_{i,j}x_{i,j}p_{i}
   * @return the seller revenue.
   * @throws MarketPricesException in case the price vector is null.
   */
  public double sellerRevenuePriceVector() throws MarketPricesException {
    double value = 0;
    for (int j = 0; j < this.marketAllocation.allocation[0].length; j++) {
      value += this.sellerRevenueFromBidder(j);
    }
    return value;
  }
  
  /**
   * This function computes the seller revenue only for a given list of bidders.
   * @param biddersIndices - an ArrayList of bidders indices.
   * @return the seller revenue from bidders in biddersIndices
   * @throws MarketPricesException in case the price vector is null.
   */
  public double sellerRevenuePriceVector(ArrayList<Integer> biddersIndices) throws MarketPricesException {
    double value = 0;
    for (Integer j : biddersIndices) {
      value += this.sellerRevenueFromBidder(j);
    }
    return value;
  }
  
  /**
   * This function computes the seller revenue for a given bidder.
   * @param j - a bidder index.
   * @return the seller revenue from bidder j.
   * @throws MarketPricesException in case the price vector is null.
   */
  public double sellerRevenueFromBidder(int j) throws MarketPricesException {
    if (this.pricesVector == null){
      throw new MarketPricesException("Ask for seller revenue using price vector but the price vector is null");
    }
    double value = 0;
    for (int i = 0; i < this.marketAllocation.allocation.length; i++) {
      value += this.marketAllocation.allocation[i][j] * this.pricesVector[i];
    }
    return value;
  }
  
  /**
   * Get current bundle cost for a bidder.
   * @param j - a bidder index.
   * @return j bundle cost.
   */
  public double getBundleCost(int j) {
    double cost = 0.0;
    for (int i = 0; i < this.marketAllocation.getMarket().getNumberGoods(); i++) {
      if (this.marketAllocation.allocation[i][j] > 0) {
        cost += this.marketAllocation.allocation[i][j] * this.pricesVector[i];
      }
    }
    return cost;
  }

  @Override
  public String toString() {
    try {
      return "" + this.sellerRevenuePriceVector();
    } catch (MarketPricesException e) {
      System.out.println("MarketPricesException = " + e.getMessage());
    }
    return null;
  }
}
