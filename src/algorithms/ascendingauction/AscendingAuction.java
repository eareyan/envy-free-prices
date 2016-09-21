package algorithms.ascendingauction;

import java.util.ArrayList;
import java.util.Collections;

import structures.Market;
import structures.MarketAllocation;
import structures.MarketPrices;

/**
 * Implements an ascending auction.
 * This class returns an allocation and prices so that every campaign is envy-free 
 * (up to epsilon) and a user class that is completely unallocated has price zero.
 * 
 * @author Enrique Areyan Viqueira
 */
public class AscendingAuction {

  protected Market market;

  protected double[] prices;

  protected int[][] allocation;

  protected static double epsilon = 0.005;

  /**
   * Constructor.
   * @param market - the market object in which to run the auction.
   */
  public AscendingAuction(Market market) {
    this.market = market;
    this.prices = new double[this.market.getNumberUsers()];
    this.allocation = new int[this.market.getNumberUsers()][this.market
        .getNumberCampaigns()];
  }

  /**
   * This method runs the auction.
   * @return a MarketPrices object with the result of the auction.
   */
  public MarketPrices Solve() {
    ArrayList<UserPrice> currentPrices = new ArrayList<UserPrice>(this.market.getNumberUsers());
    // Initial structures.
    for (int i = 0; i < this.market.getNumberUsers(); i++) {
      currentPrices.add(new UserPrice(i, 0.0));
    }
    ArrayList<Integer> unallocatedCampaigns = new ArrayList<Integer>();
    ArrayList<ArrayList<BundleEntry>> listOfUsers = new ArrayList<ArrayList<BundleEntry>>(this.market.getNumberCampaigns());
    for (int j = 0; j < this.market.getNumberCampaigns(); j++) {
      //Initially all campaigns are unallocated.
      unallocatedCampaigns.add(j);
      // Populate the list of users that campaigns have access to.
      ArrayList<BundleEntry> listOfUsersForCampaign = new ArrayList<BundleEntry>();
      for (int i = 0; i < this.market.getNumberUsers(); i++) {
        if (this.market.isConnected(i, j)) {
          listOfUsersForCampaign.add(new BundleEntry(i, 0));
        }
      }
      listOfUsers.add(listOfUsersForCampaign);
    }
    while (true) {
      // System.out.println("unallocatedCampaigns = " + unallocatedCampaigns);
      ArrayList<Bundle> B = new ArrayList<Bundle>();
      for (Integer j : unallocatedCampaigns) {
        // System.out.println("Campaign "+j+" is unallocated, try to find a match");
        ArrayList<BundleEntry> bundle = this.findUtilityMaximizerBundle(
            currentPrices, j);
        // System.out.println("greedyBundle = " + bundle);
        if (!bundle.isEmpty()) {
          B.add(new Bundle(j, bundle));
        }
      }
      // System.out.println("SET B = " + B);
      if (B.size() > 0) { // If some unallocated campaign placed a bid.
        Bundle b = B.get(0);
        int campaignIndex = b.getJ();
        ArrayList<BundleEntry> bundle = b.getBundle();
        // System.out.println("Alloc campaign " + campaignIndex +
        // " with bundle " + bundle);
        unallocatedCampaigns.remove(new Integer(campaignIndex));
        // For each entry in the bundle of the bid
        for (BundleEntry entry : bundle) { 
          int userId = entry.getI(), campaignId = b.getJ(), allocToUser = 0;
          // System.out.println("\t\tx[i][j] = x["+userId + "][" +campaignId + "] = " + entry.getX());
          // Allocate this many.
          this.allocation[userId][campaignId] = entry.getX();
          for (int l = 0; l < this.market.getNumberCampaigns(); l++) {
            allocToUser += this.allocation[userId][l];
          }
          // Check if we haven't exceed the total supply of this user
          if (allocToUser > this.market.getUser(userId).getSupply()) {
            // We need to unallocate campaigns now since we have exceeded supply!
            // System.out.println("We need to unallocate campaigns from user " +
            // userId+", current alloc = " + allocToUser + ", max = " +
            // this.market.getUser(userId).getSupply());
            this.updatePrices(currentPrices, userId);
            for (int l = 0; l < this.market.getNumberCampaigns(); l++) {
              if (l != campaignId && this.allocation[userId][l] > 0) {
                // Completely unallocate another campaign also allocated to this user.
                allocToUser -= this.allocation[userId][l];
                for (int i = 0; i < this.market.getNumberUsers(); i++) {
                  this.allocation[i][l] = 0;
                }
                unallocatedCampaigns.add(new Integer(l));
                /*
                 * If the following 'if' is not commented, then we will delete
                 * enough campaigns to make the user not over exhausted. Note
                 * that doing so produces not envy-free prices.
                 */

                /*
                 * if(allocToUser <= this.market.getUser(userId).getSupply()){
                 * break; }
                 */
              }
            }
          }
        }
      } else {
        break;
      }
    }
    /*
     * For debugging only System.out.println("Final Allocation:");
     * Printer.printMatrix(this.allocation);
     */

    // System.out.println("Final Prices:");
    this.storeFinalPrices(currentPrices);
    /*
     * Printer.printVector(this.prices); for(int
     * i=0;i<this.market.getNumberUsers();i++){
     * System.out.println("P["+i+"] = "+this.prices[i]); }
     */
    return new MarketPrices(new MarketAllocation(this.market, this.allocation), this.prices);
  }
  
  /**
   * This method stores the final prices vector.
   * @param prices - an arraylist of UserPrice objects.
   */
  public void storeFinalPrices(ArrayList<UserPrice> prices) {
    for (UserPrice u : prices) {
      this.prices[u.getI()] = u.getPrice();
    }
  }
  
  /**
   * Given the list of prices and a user index i, increment P_i by epsilon.
   * @param prices - ArrayList of UserPrice objects
   * @param i - user id
   */
  public void updatePrices(ArrayList<UserPrice> prices, int i) {
    for (int k = 0; k < this.market.getNumberUsers(); k++) {
      if (prices.get(k).getI() == i) {
        prices.get(k).updatePrice(
            prices.get(k).getPrice() + AscendingAuction.epsilon);
      }
    }
  }
  
  /**
   * Receives the current prices and a campaign index and returns 
   * the utility maximizer bundle for that campaign.
   * @param currentPrices - ArrayList of UserPrice objects.
   * @param j - campaign index.
   * @return the utility maximizer bundle (a list of BundleEntry objects).
   */
  public ArrayList<BundleEntry> findUtilityMaximizerBundle(ArrayList<UserPrice> currentPrices, int j) {
    Collections.sort(currentPrices, new UserPriceComparatorByPrice());
    double priceOfBundle = 0.0, priceOfUser = 0.0;
    int sizeOfBundle = 0, currentSizeOfBundle = 0, i = -1;
    ArrayList<BundleEntry> bundle = new ArrayList<BundleEntry>();
    for (UserPrice u : currentPrices) {
      i = u.getI();
      priceOfUser = u.getPrice();
      if (this.market.isConnected(i, j)) {
        currentSizeOfBundle = this.market.getCampaign(j).getDemand() - sizeOfBundle;
        if (currentSizeOfBundle < this.market.getUser(i).getSupply()) {
          // priceOfBundle += currentSizeOfBundle * (priceOfUser +
          // AscendingAuction.epsilon);
          priceOfBundle += currentSizeOfBundle * priceOfUser;
          sizeOfBundle += currentSizeOfBundle;
          bundle.add(new BundleEntry(i, currentSizeOfBundle));
        } else { // Bump price by epsilon only if we exhaust a market
          priceOfBundle += this.market.getUser(i).getSupply() * (priceOfUser + AscendingAuction.epsilon);
          sizeOfBundle += this.market.getUser(i).getSupply();
          bundle.add(new BundleEntry(i, this.market.getUser(i).getSupply()));
        }
        if (sizeOfBundle == this.market.getCampaign(j).getDemand() && priceOfBundle <= this.market.getCampaign(j).getReward()) {
          return bundle;
        }
      }
    }
    return new ArrayList<BundleEntry>();
  }

}
