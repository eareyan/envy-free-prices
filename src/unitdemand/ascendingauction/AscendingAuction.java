package unitdemand.ascendingauction;

import java.util.ArrayList;
import java.util.Collections;

import unitdemand.Matching;

/**
 * Implements ascending auction for the unit-demand case. 
 * 
 * @author Enrique Areyan Viqueira
 */
public class AscendingAuction {

  /**
   * Valuation matrix.
   */
  protected double[][] valuationMatrix;

  /**
   * Allocation matrix.
   */
  protected int[][] allocation;

  /**
   * Vector of prices.
   */
  protected double[] prices;

  /**
   * Epsilon parameter.
   */
  protected static double epsilon = 0.01;

  /**
   * Constructor.
   * @param valuationMatrix - a matrix of valuations.
   */
  public AscendingAuction(double[][] valuationMatrix) {
    this.valuationMatrix = valuationMatrix;
    this.prices = new double[this.valuationMatrix.length];
    this.allocation = new int[this.valuationMatrix.length][this.valuationMatrix[0].length];
  }

  /**
   * Solve method. Runs the auction.
   * @return a Matching object.
   */
  public Matching Solve() {
    ArrayList<Integer> unallocatedCampaigns = new ArrayList<Integer>();
    for (int j = 0; j < this.valuationMatrix[0].length; j++) { 
      // Initially all campaigns are unallocated
      unallocatedCampaigns.add(j);
    }
    while (true) {
      // System.out.println("unallocatedCampaigns = " + unallocatedCampaigns);
      ArrayList<Bid> setOfBids = new ArrayList<Bid>();
      for (Integer j : unallocatedCampaigns) {
        ArrayList<Bid> setOfBidsOfCampaign = new ArrayList<Bid>();
        for (int i = 0; i < this.valuationMatrix.length; i++) {
          double bid = this.valuationMatrix[i][j] - (this.prices[i] + AscendingAuction.epsilon);
          if (bid >= 0) {
            setOfBidsOfCampaign.add(new Bid(i, j, bid));
          }
        }
        // System.out.println(setOfBidsOfCampaign);
        if (setOfBidsOfCampaign.size() > 0) {
          Collections.sort(setOfBidsOfCampaign, new BidsComparatorByBid());
          setOfBids.add(setOfBidsOfCampaign.get(0));
        }
      }
      if (setOfBids.size() > 0) {
        // System.out.println("Set of Bids = " + setOfBids);
        Bid b = setOfBids.get(0);
        // System.out.println("Set x["+b.getUser()+"]["+b.getCampaign()+"] = 1, and p["+b.getUser()+"] = p + e, and try to reallocate");
        this.allocation[b.getUser()][b.getCampaign()] = 1;
        unallocatedCampaigns.remove(new Integer(b.getCampaign()));
        for (int j = 0; j < this.valuationMatrix[0].length; j++) {
          if (j != b.getCampaign() && this.allocation[b.getUser()][j] == 1) {
            this.allocation[b.getUser()][j] = 0;
            this.prices[b.getUser()] += AscendingAuction.epsilon;
            unallocatedCampaigns.add(new Integer(j));
            // System.out.println("We have to unallocate");
          }
        }
      } else { // No more bids, halt with current allocation and prices
        break;
      }
    }
    return new Matching(this.valuationMatrix, this.allocation, this.prices);
  }
  
}
