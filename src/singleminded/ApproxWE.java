package singleminded;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.Market;
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
public class ApproxWE {
  
  /**
   * Market is the input market. This needs to be a singleton market.
   */
  protected Market<Goods, Bidder<Goods>> M;

  /**
   * A is the boolean matrix encoding the single-minded preferences over items.
   */
  protected boolean[][] A;

  /**
   * Integer number of bidders.
   */
  protected int numberOfBidders;

  /**
   * Integer number of items.
   */
  protected int numberOfItems;
  
  protected HashMap<Goods, Integer> goodsToIndex;
  
  protected HashMap<Bidder<Goods>, Integer> biddersToIndex;

  /**
   * The list of Rewards keeps tracks of all bidders rewards.
   */
  protected List<Bidder<Goods>> listOfBidders;
  
  /**
   * The allocation matrix.
   */
  protected int[][] X;
  
  /**
   * The price vector.
   */
  protected double[] p;
  
  /**
   * The allocation vector.
   */
  protected boolean[] allocVector;

  /**
   * Constructor.
   * @param M - a market object.
   */
  public ApproxWE(Market<Goods, Bidder<Goods>> M) {
    this.M = M;
    this.numberOfBidders = this.M.getNumberBidders();
    this.numberOfItems = this.M.getNumberGoods();
    // Create maps that point from bidders to indices, and from goods to indices.
    this.goodsToIndex = new HashMap<Goods, Integer>();
    for(int i = 0; i < M.getNumberGoods(); i++){
      this.goodsToIndex.put(M.getGoods().get(i), i);
    }
    this.biddersToIndex = new HashMap<Bidder<Goods>, Integer>();
    for(int j = 0; j < M.getNumberBidders(); j++){
      this.biddersToIndex.put(M.getBidders().get(j), j);
    }
    
    // Produce a matrix to encode the single-minded preferences.
    this.A = new boolean[this.numberOfItems][this.numberOfBidders];
    for (Goods good : M.getGoods()) {
      for(Bidder<Goods> bidder : M.getBidders()){
        this.A[this.goodsToIndex.get(good)][this.biddersToIndex.get(bidder)] = bidder.demandsGood(good);
      }
    }
    this.X = new int[numberOfItems][numberOfBidders];
    this.p = new double[numberOfItems];
    this.allocVector = new boolean[numberOfBidders];
    // Make an ArrayList of BidderReward so that we can ordered.
    this.listOfBidders = new ArrayList<Bidder<Goods>>(this.M.getBidders());
    Collections.sort(this.listOfBidders, new BiddersComparatorByReward());
  }

  /**
   * Solves for the approximation allocation and prices.
   * @return a MarketPrices object.
   * @throws MarketAllocationException 
   */
  public PricesStatistics<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> Solve() throws MarketAllocationException {
    // Keep iterating while there still are bidders that can be allocated.
    while (!matrixAllFalse(this.A)) {
      // Find the commodity which attracts most bidders.
      int mostPopularItem = -1, popularityOfItem = -1;
      for (int i = 0; i < numberOfItems; i++) {
        int acum = 0;
        for (int j = 0; j < numberOfBidders; j++) {
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
        if (this.A[mostPopularItem][this.biddersToIndex.get(bidder)]) {
          /*
           * Assign prices and bundle
           */
          winner = this.biddersToIndex.get(bidder);
          this.allocVector[winner] = true;
          this.p[mostPopularItem] = bidder.getReward();
          this.listOfBidders.remove(bidder); // This bidder is satisfy, remove it
          break;
        }
      }
      // Printer.printMatrix(A);
      // System.out.println("The most popular item is " + mostPopularItem +
      // ", assigned it to " + winner + " at price " + p[mostPopularItem]);
      // Remove any conflicts
      for (int i = 0; i < numberOfItems; i++) {
        if (this.A[i][winner]) { // The winner claimed item i
          for (int j = 0; j < numberOfBidders; j++) { // Loop through all bidders
            if (j != winner && this.A[i][j]) { 
              // This bidder is not a winner and wanted item i. 
              // He can't have it, so remove this bidder.
              for (int iPrime = 0; iPrime < numberOfItems; iPrime++) {
                this.A[iPrime][j] = false;
              }
            }
          }
        }
      }
      //Remove winner
      for (int i = 0; i < numberOfItems; i++) {
        this.X[i][winner] = (this.A[i][winner]) ? 1 : 0;
        this.A[i][winner] = false;
      }
      /*
       * System.out.println("-----"); Printer.printMatrix(A);
       * Printer.printVector(p); Printer.printVector(allocVector);
       * Printer.printMatrix(X); System.out.println("-----");
       */
    }
    
    /*
     * Create appropriate structures to return the outcome of the market. 
     */
    // Allocation
    HashBasedTable<Goods,Bidder<Goods>,Integer> allocation = HashBasedTable.create();
    for(Goods good : this.M.getGoods()){
      for(Bidder<Goods> bidder : this.M.getBidders()){
        allocation.put(good, bidder, this.X[this.goodsToIndex.get(good)][this.biddersToIndex.get(bidder)]);
      }
    }    
    // Prices
    Builder<Goods, Double> result = ImmutableMap.<Goods, Double>builder();
    for(Goods good : this.M.getGoods()){
      result.put(good, this.p[this.goodsToIndex.get(good)]);
    }
    return new PricesStatistics<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(
        new MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(
            new MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(
                this.M, allocation, new SingleStepObjectiveFunction()), result
                .build()));
  }
  
  /**
   * This function returns true if all the entries of a matrix are false.
   * Otherwise, returns false.
   * @param X - a boolean matrix
   * @return true if all the entries of X are false. Otherwise, false.
   */
  protected boolean matrixAllFalse(boolean[][] X) {
    boolean result = true;

    for (int i = 0; i < X.length; i++) {
      for (int j = 0; j < X[0].length; j++) {
        if (X[i][j])
          return false;
      }
    }
    return result;
  }
  
}
