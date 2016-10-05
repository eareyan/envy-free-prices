package structures;

import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketOutcomeException;
import allocations.objectivefunction.interfaces.ObjectiveFunction;

import com.google.common.collect.ImmutableMap;

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
public class MarketOutcome <G extends Goods, B extends Bidder<G>, O extends ObjectiveFunction>{
  
  /**
   * Market Allocation Object. Contains a market object and an allocation
   * (matrix of integers).
   */
  protected final MarketAllocation<G, B, O> marketAllocation;
  
  /**
   * This map defines a price for each good.
   */
  protected final ImmutableMap<G, Double> prices;
  
  /**
   * Constructor that takes an allocation and a price map.
   * 
   * @param marketAllocation - a MarketAllocation object.
   * @param prices - a map from goods to doubles.
   */
  public MarketOutcome(MarketAllocation<G, B, O> marketAllocation, ImmutableMap<G, Double> prices) {
    this.marketAllocation = marketAllocation;
    this.prices = prices;
  }
  
  /**
   * Getter.
   * @return a MarketAllocation object.
   */
  public MarketAllocation<G, B, O> getMarketAllocation() {
    return this.marketAllocation;
  }

  /**
   * Getter.
   * 
   * @param good - the good to query price
   * @return the price of the good.
   * @throws MarketOutcomeException in case the price of the good is not found.
   */
  public double getPrice(G good) throws MarketOutcomeException {
    if(!this.prices.containsKey(good)){
      throw new MarketOutcomeException("Price of good not found.");
    }
    return this.prices.get(good);
  }
  
  /**
   * This function computes the seller revenue for the entire market.
   * Seller revenue is defined as: \sum_{i,j}x_{i,j}p_{i}.
   * 
   * @return the seller revenue.
   * @throws MarketOutcomeException in case the price vector is null.
   * @throws MarketAllocationException 
   */
  public double sellerRevenue() throws MarketOutcomeException, MarketAllocationException {
    double value = 0;
    for (B bidder : this.marketAllocation.market.bidders) {
      value += this.sellerRevenueFromBidder(bidder);
    }
    return value;
  }
  
  /**
   * This function computes the seller revenue for a given bidder.
   * 
   * @param bidder - a bidder object.
   * @return the seller revenue from the bidder.
   * @throws MarketOutcomeException in case the price vector is null.
   * @throws MarketAllocationException 
   */
  public double sellerRevenueFromBidder(B bidder) throws MarketOutcomeException, MarketAllocationException {
    double value = 0;
    for (G good : this.marketAllocation.market.goods) {
      value += this.marketAllocation.getAllocation(good, bidder) * this.prices.get(good);
    }
    return value;
  }
  
  /**
   * Get current bundle cost for a bidder.
   * @param bidder - a bidder object.
   * @return bidder bundle cost.
   * @throws MarketAllocationException 
   */
  public double getBundleCost(B bidder) throws MarketAllocationException {
    double cost = 0.0;
    for (G good : this.marketAllocation.market.goods) {
      if (this.marketAllocation.getAllocation(good, bidder) > 0) {
        cost += this.marketAllocation.getAllocation(good, bidder) * this.prices.get(good);
      }
    }
    return cost;
  }
  
  /**
   * This method prints a vector representation of the prices.
   * 
   * @throws MarketOutcomeException
   */
  public void printPrices() throws MarketOutcomeException{
    for(G good : this.marketAllocation.market.getGoods()){
      System.out.print(this.getPrice(good) + "\t");
    }
    System.out.println("");
  }

  @Override
  public String toString() {
    try {
      return "" + this.sellerRevenue();
    } catch (MarketOutcomeException e) {
      System.out.println("MarketPricesException = " + e.getMessage());
    } catch (MarketAllocationException e) {
      System.out.println("MarketAllocationException = " + e.getMessage());
    }
    return null;
  }
}
