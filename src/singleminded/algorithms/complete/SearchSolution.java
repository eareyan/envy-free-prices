package singleminded.algorithms.complete;

import java.util.HashSet;

import structures.Bidder;
import structures.Goods;
import structures.Market;

import com.google.common.collect.ImmutableMap;

/**
 * A class that represents a solution to one node of the search.
 * 
 * @author Enrique Areyan Viqueira
 *
 */
public class SearchSolution {

  /**
   * The objective value of the solution.
   */
  private final double revenueOfSolution;

  /**
   * The set of winners. All other bidders are assumed to be losers.
   */
  private final HashSet<Bidder<Goods>> winners;

  /**
   * The pricing.
   */
  private final ImmutableMap<Goods, Double> prices;

  /**
   * Constructor.
   * 
   * @param objValue
   * @param winners
   * @param prices
   */
  public SearchSolution(double objValue, HashSet<Bidder<Goods>> winners, ImmutableMap<Goods, Double> prices) {
    this.revenueOfSolution = objValue;
    this.winners = (winners != null) ? new HashSet<Bidder<Goods>>(winners) : null;
    this.prices = prices;
  }

  /**
   * Getter.
   * 
   * @return the
   */
  public double getRevenueOfSolution() {
    return this.revenueOfSolution;
  }

  /**
   * Returns true if the given bidder is a winner under the solution.
   * 
   * @param bidder
   * @return
   */
  public boolean isBidderAWinner(Bidder<Goods> bidder) {
    return this.winners.contains(bidder);
  }

  /**
   * Returns the price of the given good.
   * 
   * @param good
   * @return
   */
  public double getGoodPrice(Goods good) {
    return this.prices.get(good);
  }

  /**
   * Helper function to print prices.
   * 
   * @param market
   */
  public void printPrices(Market<Goods, Bidder<Goods>> market) {
    for (Goods good : market.getGoods()) {
      System.out.print(this.getGoodPrice(good) + "\t");
    }
  }

  /**
   * Helper function to print winners information.
   * 
   * @param market
   */
  public void printWinners(Market<Goods, Bidder<Goods>> market) {
    for (Bidder<Goods> bidder : market.getBidders()) {
      System.out.print("\n " + bidder + "->" + this.isBidderAWinner(bidder));
    }

  }

  @Override
  public String toString() {
    return "Sol = " + this.revenueOfSolution + ", winners = " + this.winners + ", prices = " + this.prices;
  }

}
