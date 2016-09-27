package singleminded;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import allocations.objectivefunction.SingleStepFunction;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketPrices;

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
  protected Market M;

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

  /**
   * The list of Rewards keeps tracks of all bidders rewards.
   */
  protected List<BidderReward> Rewards;
  
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
  public ApproxWE(Market M) {
    this.M = M;
    this.numberOfBidders = this.M.getNumberBidders();
    this.numberOfItems = this.M.getNumberGoods();
    // Make an ArrayList of BidderReward so that we can ordered.
    this.Rewards = new ArrayList<BidderReward>();
    for (int j = 0; j < this.M.getNumberBidders(); j++) {
      this.Rewards.add(new BidderReward(j, this.M.getBidder(j).getReward()));
    }
    Collections.sort(Rewards, this.UserRewardComparator);
    // Make a copy of the connections matrix so that we don't change the original matrix.
    this.A = new boolean[this.numberOfItems][this.numberOfBidders];
    for (int i = 0; i < numberOfItems; i++) {
      this.A[i] = Arrays.copyOf(this.M.getConnections()[i],
          this.M.getConnections()[i].length);
    }
    this.X = new int[numberOfItems][numberOfBidders];
    this.p = new double[numberOfItems];
    this.allocVector = new boolean[numberOfBidders];
  }

  /**
   * Solves for the approximation allocation and prices.
   * @return a MarketPrices object.
   */
  public MarketPrices Solve() {
    //Keep iterating while there still are bidders that can be allocated.
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
      for (BidderReward bidder : this.Rewards) {
        if (this.A[mostPopularItem][bidder.getIndex()]) {
          /*
           * Assign prices and bundle
           */
          winner = bidder.getIndex();
          this.allocVector[winner] = true;
          this.p[mostPopularItem] = bidder.getReward();
          this.Rewards.remove(bidder); // This bidder is satisfy, remove it
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
    return new MarketPrices(new MarketAllocation(this.M, this.X, new SingleStepFunction()), this.p);
  }
  
  /**
   * This function returns true if all the entries of a 2x2 matrix are false.
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

  /**
   * This class is an auxiliary class to be able to
   * order bidders by their reward.
   * @author Enrique Areyan Viqueira
   */
  private class BidderReward {
    
    /**
     * Bidder index.
     */
    protected int j;
    
    /**
     * Bidder reward.
     */
    protected double reward;
    
    /**
     * Constructor.
     * @param j - bidder index.
     * @param reward - bidder reward.
     */
    public BidderReward(int j, double reward) {
      this.j = j;
      this.reward = reward;
    }

    /**
     * Getter.
     * @return the bidder index.
     */
    public int getIndex() {
      return this.j;
    }

    /**
     * Getter.
     * @return the bidder reward.
     */
    public double getReward() {
      return this.reward;
    }

    @Override
    public String toString() {
      return "(" + this.j + "," + this.reward + ")";
    }
    
  }

  /**
   * User Reward comparator.
   */
  public final Comparator<BidderReward> UserRewardComparator = new Comparator<BidderReward>() {
    public int compare(BidderReward u1, BidderReward u2) {
      return Double.compare(u2.getReward(), u1.getReward());
    }
  };
  
}
