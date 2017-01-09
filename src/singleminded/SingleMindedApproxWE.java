package singleminded;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.MarketAllocation;
import structures.MarketOutcome;
import structures.comparators.BiddersComparatorByReward;
import structures.exceptions.MarketAllocationException;
import allocations.objectivefunction.SingleStepObjectiveFunction;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * This class implements the approximation WE algorithm for single-minded
 * bidders as presented in Huang, L.S., Li, M., Zhang, B.: Approximation of
 * Walrasian equilibrium in single- minded auctions. Theoretical computer
 * science 337(1), 390–398 (2005)
 * 
 * @author Enrique Areyan Viqueira
 */
public class SingleMindedApproxWE {

  /**
   * Market is the input market. This needs to be a singleton market.
   */
  private final SingleMindedMarket<Goods, Bidder<Goods>> M;

  /**
   * A is the boolean matrix encoding the single-minded preferences over items.
   */
  private final boolean[][] A;

  /**
   * The list of Rewards keeps tracks of all bidders rewards.
   */
  private final List<Bidder<Goods>> listOfBidders;

  /**
   * The allocation matrix.
   */
  private final int[][] X;

  /**
   * The price vector.
   */
  private final double[] p;
  
  /**
   * Start time, for statistics purposes.
   */
  private final long startTime;

  /**
   * Constructor.
   * 
   * @param M - a market object.
   */
  public SingleMindedApproxWE(SingleMindedMarket<Goods, Bidder<Goods>> M) {
    this.startTime = System.nanoTime();
    this.M = M;
    // Make a copy of the connection matrix since we will change in the solve method.
    this.A = this.M.getCopyOfA();
    // Initialize the allocation matrix and price vector.
    this.X = new int[this.M.getNumberGoods()][this.M.getNumberBidders()];
    this.p = new double[this.M.getNumberGoods()];
    // Make an ArrayList of BidderReward so that we can order the rewards.
    this.listOfBidders = new ArrayList<Bidder<Goods>>(this.M.getBidders());
    Collections.sort(this.listOfBidders, new BiddersComparatorByReward());
  }

  /**
   * Solves for the approximation allocation and prices.
   * 
   * @return a MarketPrices object.
   * @throws MarketAllocationException
   */
  public PricesStatistics<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> Solve()
      throws MarketAllocationException {
    // Keep iterating while there still are bidders that can be allocated.
    while (!matrixAllFalse(this.A)) {
      // Find the commodity which attracts most bidders.
      int mostPopularItem = -1, popularityOfItem = -1;
      for (int i = 0; i < this.M.getNumberGoods(); i++) {
        int acum = 0;
        for (int j = 0; j < this.M.getNumberBidders(); j++) {
          acum += this.A[i][j] ? 1 : 0;
        }
        if (acum >= popularityOfItem) {
          popularityOfItem = acum;
          mostPopularItem = i;
        }
      }
      // Find the bidder with highest budget that wants the most popular item.
      int winner = -1;
      for (Bidder<Goods> bidder : this.listOfBidders) {
        if (this.A[mostPopularItem][this.M.biddersToIndex.get(bidder)]) {
          // Assign prices and bundle
          winner = this.M.biddersToIndex.get(bidder);
          this.p[mostPopularItem] = bidder.getReward();
          // This bidder has been allocated, remove it.
          this.listOfBidders.remove(bidder);
          break;
        }
      }
      // Remove any conflicts
      for (int i = 0; i < this.M.getNumberGoods(); i++) {
        // The winner claimed item i
        if (this.A[i][winner]) {
          // Loop through all bidders
          for (int j = 0; j < this.M.getNumberBidders(); j++) { 
            if (j != winner && this.A[i][j]) {
              // This bidder is not a winner and wanted item i.
              // He can't have it, so remove this bidder.
              for (int iPrime = 0; iPrime < this.M.getNumberGoods(); iPrime++) {
                this.A[iPrime][j] = false;
              }
            }
          }
        }
      }
      // Remove winner
      for (int i = 0; i < this.M.getNumberGoods(); i++) {
        this.X[i][winner] = (this.A[i][winner]) ? 1 : 0;
        this.A[i][winner] = false;
      }
    }

    /*
     * Create appropriate structures to return the outcome of the market.
     */
    // Allocation
    HashBasedTable<Goods, Bidder<Goods>, Integer> allocation = HashBasedTable.create();
    for (Goods good : this.M.getGoods()) {
      for (Bidder<Goods> bidder : this.M.getBidders()) {
        allocation.put(good, bidder,
            this.X[this.M.goodsToIndex.get(good)][this.M.biddersToIndex.get(bidder)]);
      }
    }
    // Prices
    Builder<Goods, Double> result = ImmutableMap.<Goods, Double> builder();
    for (Goods good : this.M.getGoods()) {
      result.put(good, this.p[this.M.goodsToIndex.get(good)]);
    }
    return new PricesStatistics<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(
        new MarketOutcome<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(
            new MarketAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(
                this.M, allocation, new SingleStepObjectiveFunction()), result.build()), System.nanoTime() - this.startTime);
  }

  /**
   * This function returns true if all the entries of a matrix are false.
   * Otherwise, returns false.
   * 
   * @param X - a boolean matrix
   * @return true if all the entries of X are false. Otherwise, false.
   */
  protected boolean matrixAllFalse(boolean[][] X) {
    for (int i = 0; i < X.length; i++) {
      for (int j = 0; j < X[0].length; j++) {
        if (X[i][j]) {
          return false;
        }
      }
    }
    return true;
  }

}
