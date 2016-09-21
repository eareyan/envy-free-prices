package structures;

import java.util.ArrayList;

/**
 * A market is a bipartite graph with Users connected to Campaigns. 
 * This class implements a market object and related basic functionality.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Market {

  /**
   * Array of Users.
   */
  protected User[] users;

  /**
   * Array of Campaigns.
   */
  protected Campaign[] campaigns;

  /**
   * Boolean matrix indicating which campaign is connected to which user.
   */
  protected boolean[][] connections;

  /**
   * Highest reward among all campaigns. Implemented as a singleton.
   */
  protected double highestReward = -1.0;

  /**
   * Constructor for a Market. This constructor is the most fundamental one. 
   * It receives campaigns, users and connections.
   * 
   * @param users - array of User objects.
   * @param campaigns - array of Campaign objects.
   * @param connections - matrix of booleans.
   */
  public Market(User[] users, Campaign[] campaigns, boolean[][] connections) {
    this.users = users;
    this.campaigns = campaigns;
    this.connections = connections;
  }

  /**
   * Gets the array of campaigns.
   * 
   * @return an array of Campaign objects.
   */
  public Campaign[] getCampaigns() {
    return this.campaigns;
  }

  /**
   * Gets a specific campaign.
   * 
   * @param j - the index of the campaign.
   * @return the jth Campaign object.
   */
  public Campaign getCampaign(int j) {
    return this.campaigns[j];
  }

  /**
   * Get the array of users.
   * 
   * @return an array of User objects.
   */
  public User[] getUsers() {
    return this.users;
  }

  /**
   * Gets a specific user.
   * 
   * @param i - the index of the user.
   * @return the ith user.
   */
  public User getUser(int i) {
    return this.users[i];
  }

  /**
   * Gets the number of users.
   * 
   * @return the number of users.
   */
  public int getNumberUsers() {
    return this.users.length;
  }

  /**
   * Gets the number of campaigns.
   * 
   * @return the number of campaigns.
   */
  public int getNumberCampaigns() {
    return this.campaigns.length;
  }

  /**
   * Gets the matrix of connections.
   * 
   * @return a matrix of booleans
   */
  public boolean[][] getConnections() {
    return this.connections;
  }

  /**
   * Set reserve of all campaigns
   * 
   * @param reserve - reserve price
   */
  public void setReserveAllCampaigns(double reserve) {
    for (int j = 0; j < campaigns.length; j++) {
      this.campaigns[j].setReserve(reserve);
    }
  }

  /**
   * Adds a campaign to the Market.
   * 
   * @param newCampaign - a campaign object.
   * @param usersIndices - an ArrayList of integers containing the users indices.
   */
  public void addCampaign(Campaign newCampaign, ArrayList<Integer> usersIndices) {
    // Add the new campaign to the array of existing campaigns.
    Campaign[] newCampaigns = new Campaign[this.getNumberCampaigns() + 1];
    System.arraycopy(this.campaigns, 0, newCampaigns, 0,
        this.getNumberCampaigns());
    newCampaigns[this.getNumberCampaigns()] = newCampaign;
    this.campaigns = newCampaigns;
    // Add this new campaign's connections
    boolean[][] newConnections = new boolean[this.getNumberUsers()][this.getNumberCampaigns()];
    for (int i = 0; i < this.getNumberUsers(); i++) {
      for (int j = 0; j < this.getNumberCampaigns() - 1; j++) {
        newConnections[i][j] = this.connections[i][j];
      }
    }
    for (Integer userid : usersIndices) {
      newConnections[userid][this.getNumberCampaigns() - 1] = true;
    }
    this.connections = newConnections;
  }

  /**
   * Computes the highest reward among all campaigns in the market.
   * 
   * @return the max value of rewards among all campaigns.
   */
  public double getHighestReward() {
    if (this.highestReward == -1.0) {
      double temp = -1.0;
      for (int j = 0; j < this.getNumberCampaigns(); j++) {
        if (this.getCampaign(j).getReward() > temp) {
          temp = this.getCampaign(j).getReward();
        }
      }
      this.highestReward = temp;
    }
    return this.highestReward;
  }

  /**
   * This method answers the question: does user i has any connections at all?
   * 
   * @param i - the index of a user.
   * @return true if user i has at least once connection.
   */
  public boolean hasConnectionsUser(int i) {
    for (int j = 0; j < this.getNumberCampaigns(); j++) {
      if (this.isConnected(i, j)) {
        return true;
      }
    }
    return false;
  }

  /**
   * This method answers the question: does campaign j has any connections at all?
   * 
   * @param j - the index of a campaign
   * @return true if campaign j has at least one connection.
   */
  public boolean hasConnectionsCampaign(int j) {
    for (int i = 0; i < this.getNumberUsers(); i++) {
      if (this.isConnected(i, j)) {
        return true;
      }
    }
    return false;
  }

  /**
   * This method computes all users connected to campaign j.
   * 
   * @param j - the index of a campaign.
   * @return an ArrayList of indices of users to which campaign j is connected.
   */
  public ArrayList<Integer> getListConnectedUsers(int j) {
    ArrayList<Integer> listOfUsers = new ArrayList<Integer>();
    for (int i = 0; i < this.getNumberUsers(); i++) {
      if (this.isConnected(i, j)) {
        listOfUsers.add(i);
      }
    }
    return listOfUsers;
  }

  /**
   * This method answers the question: is user i connected to campaign j?
   * 
   * @param i - a user index
   * @param j - a campaign index
   * @return true if user i is connected to campaign j
   */
  public boolean isConnected(int i, int j) {
    return this.connections[i][j];
  }

  /**
   * This method computes the total number of users supplied by the market.
   * 
   * @return the total number of users supplied in the market, i.e., \sum_i N_i.
   */
  public int getTotalSupply() {
    int totalSupply = 0;
    for (int i = 0; i < this.getNumberUsers(); i++) {
      totalSupply += this.getUser(i).getSupply();
    }
    return totalSupply;
  }

  /**
   * This method computes the total number of users demanded by the market.
   * 
   * @return the total number of users demanded in the market, i.e., \sum_j I_j
   */
  public int getTotalDemand() {
    int totalDemand = 0;
    for (int j = 0; j < this.getNumberCampaigns(); j++) {
      totalDemand += this.getCampaign(j).getDemand();
    }
    return totalDemand;
  }

  /**
   * This method computes the supply to demand ratio of the market.
   * 
   * @return the supply to demand ratio of the market.
   */
  public double getSupplyToDemandRatio() {
    return (double) this.getTotalSupply() / this.getTotalDemand();
  }

  /**
   * Printer. Representation of objects as strings.
   * 
   * @return a string representation of the information of users in the market
   */
  protected String stringUsersInfo() {
    String ret = "";
    for (int i = 0; i < this.getNumberUsers(); i++) {
      ret += "\nN(" + i + ") = " + this.users[i].supply;
    }
    return ret;
  }

  /**
   * Printer. Representation of objects as strings.
   * 
   * @return a string representation of the information of campaigns in the market
   */
  protected String stringCampaignsInfo() {
    String ret = "";
    for (int j = 0; j < this.getNumberCampaigns(); j++) {
      ret += "\n"
          + String.format(
              "%-20s %-20s %-20s %-20s %-20s %-12s %-20s",
              "R(" + j + ") = "
                  + String.format("%.2f", this.campaigns[j].reward) + ";",
              "I(" + j + ") = " + this.campaigns[j].demand + ";",
              "L(" + j + ") = "
                  + String.format("%.2f", this.campaigns[j].level) + ";",
              "r(" + j + ") = "
                  + String.format("%.2f", this.campaigns[j].reserve),
              this.campaigns[j].backpointer, this.campaigns[j].priority,
              this.campaigns[j].allocationSoFar);
    }
    return ret;
  }

  /**
   * Printer. Representation of objects as strings.
   * 
   * @return a string representation of the connection matrix.
   */
  protected String stringConnectionsMatrix() {
    if (this.connections != null) {
      String ret = "";
      for (int i = 0; i < this.getNumberUsers(); i++) {
        ret += "\n";
        for (int j = 0; j < this.getNumberCampaigns(); j++) {
          if (this.isConnected(i, j)) {
            ret += "\t yes";
          } else {
            ret += "\t no";
          }
        }
      }
      return ret + "\n";
    } else {
      return "The connection matrix is not initialized";
    }
  }

  @Override
  public String toString() {
    return "NbrCampaigns:\t"
        + this.getNumberCampaigns()
        + "\n"
        + "NbrUsers:\t"
        + this.getNumberUsers()
        + "\n"
        + String.format("%-20s %-20s %-20s %-20s %-20s %-12s %s",
            "Camp. Rewards", "Camp. Demand", "Level", "Reserve", "Backpointer",
            "Priority", "Alloc So Far") + this.stringCampaignsInfo() + "\n"
        + "Users Supply" + this.stringUsersInfo() + "\n"
        + "Connections Matrix:\t" + this.stringConnectionsMatrix();
  }
  
}
