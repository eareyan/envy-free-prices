package algorithms.waterfall;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import structures.Market;
import structures.MarketAllocation;

/**
 * Implements waterfall algorithm.
 * 
 *  @author Enrique Areyan Viqueira
 */
public class Waterfall {
  
  /**
   * Input Market
   */
  protected Market market;
  
  /**
   * If min = true, then select the cheapest market, i.e., the one with the
   * lowest second-highest bid.
   */
  protected boolean min = true;
  
  /**
   * reserve price
   */
  protected double reserve = 0.0;
  
  /**
   * Constructor.
   * @param market - a Market object.
   */
  public Waterfall(Market market) {
    this.market = market;
  }

  /**
   * Constructor.
   * @param market - a Market object.
   * @param reserve - a reserve price.
   */
  public Waterfall(Market market, double reserve) {
    this.market = market;
    this.reserve = reserve;
  }

  /**
   * Constructor.
   * @param market - a Market object.
   * @param min - a boolean indicating which user to select.
   */
  public Waterfall(Market market, boolean min) {
    this.market = market;
    this.min = min;
  }

  /**
   * Runs the waterfall algorithm.
   * @return a WaterfallPrices object.
   */
  public WaterfallPrices Solve() {
    // Initialize structures to return results
    double[][] bids = new double[this.market.getNumberGoods()][this.market.getNumberBidders()];
    double[][] prices = new double[this.market.getNumberGoods()][this.market.getNumberBidders()];
    int[][] allocation = new int[this.market.getNumberGoods()][this.market.getNumberBidders()];
    //Initialize the supply
    int[] supply = new int[this.market.getNumberGoods()];
    for (int i = 0; i < this.market.getNumberGoods(); i++) {
      supply[i] = this.market.getGood(i).getSupply();
    }
    // Initialize demand and budget
    int[] demand = new int[this.market.getNumberBidders()];
    double[] budget = new double[this.market.getNumberBidders()];
    for (int j = 0; j < this.market.getNumberBidders(); j++) {
      demand[j] = this.market.getBidder(j).getDemand();
      budget[j] = this.market.getBidder(j).getReward();
    }
    // Find out which campaigns actually have connections and have enough to pay for reserve prices.
    ArrayList<Integer> Campaigns = new ArrayList<Integer>();
    for (int j = 0; j < this.market.getNumberBidders(); j++) {
      if (this.market.hasConnectionsBidder(j)
          && (this.market.getBidder(j).getReward() - this.market.getBidder(j).getDemand()* this.reserve) > 0) {
        Campaigns.add(j);
      }
    }
    // System.out.println("Campaigns: " + Campaigns);

    // Main loop of the algorithm.
    while (true) {
      //Compute Feasible Campaigns and their corresponding users.
      ArrayList<Integer> Users = new ArrayList<Integer>();
      ArrayList<Integer> feasibleCampaigns = new ArrayList<Integer>();
      for (int l = 0; l < Campaigns.size(); l++) {
        // Add campaign to set of feasible campaigns only if it is actually feasible
        if (isSatisfiable(Campaigns.get(l), supply, demand)) {
          feasibleCampaigns.add(Campaigns.get(l));
          for (int i = 0; i < this.market.getNumberGoods(); i++) {
            if (!(supply[i] == 0) && !Users.contains(i)
                && this.market.isConnected(i, Campaigns.get(l))) {
              Users.add(i);
            }
          }
        }
      }
      // System.out.println("Users: " + Users);
      // System.out.println("feasibleCampaigns = " + feasibleCampaigns);

      if (!(feasibleCampaigns.size() > 0)) {
        break;
      }
      // Compute bid vector.
      ArrayList<Bid> HighestBids = new ArrayList<Bid>();
      ArrayList<Bid> SecondHighestBids = new ArrayList<Bid>();
      for (int k = 0; k < Users.size(); k++) {
        ArrayList<Bid> bidVector = new ArrayList<Bid>();
        bidVector.add(new Bid(this.market.getGood(Users.get(k)).getReservePrice(), Users.get(k), -1));
        for (int l = 0; l < feasibleCampaigns.size(); l++) {
          if (this.market.isConnected(Users.get(k), feasibleCampaigns.get(l))) {
            // System.out.println("(" + Users.get(k) + "," +
            // feasibleCampaigns.get(l) + ")");
            bidVector.add(new Bid(budget[feasibleCampaigns.get(l)] / demand[feasibleCampaigns.get(l)], Users.get(k), feasibleCampaigns.get(l)));
          }
        }
        Collections.sort(bidVector, new BidComparator());
        HighestBids.add(bidVector.get(0));
        SecondHighestBids.add(bidVector.get(1));
        // System.out.println("bidvector ordered for user  "+ Users.get(k) +
        // " = " + bidVector);
      }
      Collections.sort(SecondHighestBids, new BidComparator());
      // System.out.println("HighestBids \t  = " + HighestBids);
      // System.out.println("SecondHighestBids = " + SecondHighestBids);
      int cheapestUser;
      if (this.min) {
        cheapestUser = SecondHighestBids.get(SecondHighestBids.size() - 1).getI();
      } else {
        cheapestUser = SecondHighestBids.get(0).getI();
      }
      double secondHighestCheapestMarket = SecondHighestBids.get(SecondHighestBids.size() - 1).getValue();
      Bid winningBid = this.getBid(cheapestUser, HighestBids);
      double valueWinningBid = winningBid.getValue();
      int indexCampaignWinningBid = winningBid.getJ();
      int alloc = Math.min(demand[indexCampaignWinningBid], supply[cheapestUser]);
      // System.out.println("k* = " + cheapestUser + ", p* = "+
      // secondHighestCheapestMarket + ", b* = " + valueWinningBid + ", l* = " +
      // indexCampaignWinningBid + ", q* = " + alloc);
      supply[cheapestUser] = Math.max(0, supply[cheapestUser] - alloc);
      // for(int i=0;i<this.market.getNumberUsers();i++){
      // System.out.println("supply["+i+"] = " + supply[i]);
      // }
      demand[indexCampaignWinningBid] = Math.max(0, demand[indexCampaignWinningBid] - alloc);
      budget[indexCampaignWinningBid] = Math.max(0.0, budget[indexCampaignWinningBid] - secondHighestCheapestMarket * alloc);
      // for(int j=0;j<this.market.getNumberCampaigns();j++){
      // System.out.println("demand["+j+"] = " + demand[j]);
      // System.out.println("budget["+j+"] = " + budget[j]);
      // }

      bids[cheapestUser][indexCampaignWinningBid] = valueWinningBid;
      prices[cheapestUser][indexCampaignWinningBid] = secondHighestCheapestMarket;
      allocation[cheapestUser][indexCampaignWinningBid] = alloc;

      if (demand[indexCampaignWinningBid] == 0) {
        Campaigns.remove(new Integer(indexCampaignWinningBid));
      }
      // System.out.println("Current Allocation:");
      // printMatrix(allocation);
    }
    // printMatrix(bids);
    // System.out.println("\n-");
    // printMatrix(allocation);
    // System.out.println("\n-");
    // printMatrix(prices);
    return new WaterfallPrices(new MarketAllocation(this.market, allocation),
        prices);
  }
  
  /**
   * Get a bid by userindex from a list of bids.
   * @param userIndex - a user index.
   * @param bids - an ArrayList of Bid objects.
   * @return
   */
  public Bid getBid(int userIndex, ArrayList<Bid> bids) {
    for (int i = 0; i < bids.size(); i++) {
      if (bids.get(i).getI() == userIndex) {
        return bids.get(i);
      }
    }
    return null;
  }
  
  /**
   * This method checks whether campaign j is satisfiable with the given supply,
   * for this market.
   * @param j - a campaign index.
   * @param supply - an array of supply.
   * @param demand - an array of demand.
   * @return a true if j is satisfiable with the given supply.
   */
  public boolean isSatisfiable(int j, int[] supply, int[] demand) {
    int currentDemand = demand[j];
    for (int i = 0; i < this.market.getNumberGoods(); i++) {
      if (this.market.isConnected(i, j)) {
        currentDemand -= supply[i];
      }
      if (currentDemand <= 0) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * This inner class represents a bid placed by a campaign to acquire a user class.
   * 
   * @author Enrique Areyan Viqueira
   */
  public class Bid {
    private final double value;
    private final int i;
    private final int j;

    /**
     * Constructor.
     * @param value - represents the bid of j for i.
     * @param i - user index.
     * @param j - campaign index.
     */
    public Bid(double value, int i, int j) {
      this.value = value;
      this.i = i;
      this.j = j;
    }

    /**
     * Getter.
     * @return value of the bid.
     */
    public double getValue() {
      return this.value;
    }

    /**
     * Getter.
     * @return index of user.
     */
    public int getI() {
      return this.i;
    }

    /**
     * Getter.
     * @return index of campaign.
     */
    public int getJ() {
      return this.j;
    }

    @Override
    public String toString() {
      return "(" + this.value + "," + this.i + "," + this.j + ")";
    }
  }
  
  /**
   * Comparator to compare bids by value.
   * 
   * @author Enrique Areyan Viqueira
   */
  public class BidComparator implements Comparator<Bid> {
    @Override
    public int compare(Bid b1, Bid b2) {
      if (b1.getValue() < b2.getValue()) {
        return 1;
      } else if (b1.getValue() > b2.getValue()) {
        return -1;
      } else {
        return 0;
      }
    }
  }
  
}
