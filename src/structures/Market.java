package structures;

import java.util.ArrayList;

/**
 * A market is a bipartite graph with Bidders connected to Goods. 
 * This class implements a market object and related basic functionality.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Market {

  /**
   * Array of Goods.
   */
  protected Goods[] goods;

  /**
   * Array of Bidders.
   */
  protected Bidder[] bidders;

  /**
   * Boolean matrix indicating which bidder is connected to which goods.
   */
  protected boolean[][] connections;

  /**
   * Highest reward among all bidders. Implemented as a singleton.
   */
  protected double highestReward = -1.0;

  /**
   * Constructor for a Market. This constructor is the most fundamental one. 
   * It receives bidders, goods and connections.
   * 
   * @param goods - array of Goods objects.
   * @param bidders - array of Bidder objects.
   * @param connections - matrix of booleans.
   */
  public Market(Goods[] goods, Bidder[] bidders, boolean[][] connections) {
    this.goods = goods;
    this.bidders = bidders;
    this.connections = connections;
  }

  /**
   * Gets the array of bidders.
   * 
   * @return an array of Bidder objects.
   */
  public Bidder[] getBidders() {
    return this.bidders;
  }

  /**
   * Gets a specific bidder.
   * 
   * @param j - the index of the bidder.
   * @return the jth Bidder object.
   */
  public Bidder getBidder(int j) {
    return this.bidders[j];
  }

  /**
   * Get the array of goods.
   * 
   * @return an array of Goods objects.
   */
  public Goods[] getGoods() {
    return this.goods;
  }

  /**
   * Gets a specific good.
   * 
   * @param i - the index of the good.
   * @return the ith good.
   */
  public Goods getGood(int i) {
    return this.goods[i];
  }

  /**
   * Gets the number of goods.
   * 
   * @return the number of goods.
   */
  public int getNumberGoods() {
    return this.goods.length;
  }

  /**
   * Gets the number of bidders.
   * 
   * @return the number of bidders.
   */
  public int getNumberBidders() {
    return this.bidders.length;
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
    for (int j = 0; j < bidders.length; j++) {
      this.bidders[j].setReserve(reserve);
    }
  }

  /**
   * Adds a bidder to the Market.
   * 
   * @param newBidder - a bidder object.
   * @param goodsIndices - an ArrayList of integers containing the goods indices.
   */
  public void addBidder(Bidder newBidder, ArrayList<Integer> goodsIndices) {
    // Add the new bidder to the array of existing bidders.
    Bidder[] newBidders = new Bidder[this.getNumberBidders() + 1];
    System.arraycopy(this.bidders, 0, newBidders, 0, this.getNumberBidders());
    newBidders[this.getNumberBidders()] = newBidder;
    this.bidders = newBidders;
    // Add the new bidder's connections
    boolean[][] newConnections = new boolean[this.getNumberGoods()][this.getNumberBidders()];
    for (int i = 0; i < this.getNumberGoods(); i++) {
      for (int j = 0; j < this.getNumberBidders() - 1; j++) {
        newConnections[i][j] = this.connections[i][j];
      }
    }
    for (Integer goodid : goodsIndices) {
      newConnections[goodid][this.getNumberBidders() - 1] = true;
    }
    this.connections = newConnections;
  }

  /**
   * Computes the highest reward among all bidders in the market.
   * 
   * @return the max value of rewards among all bidders.
   */
  public double getHighestReward() {
    if (this.highestReward == -1.0) {
      double temp = -1.0;
      for (int j = 0; j < this.getNumberBidders(); j++) {
        if (this.getBidder(j).getReward() > temp) {
          temp = this.getBidder(j).getReward();
        }
      }
      this.highestReward = temp;
    }
    return this.highestReward;
  }

  /**
   * This method answers the question: does good i has any connections at all?
   * 
   * @param i - the index of a good.
   * @return true if good i has at least once connection.
   */
  public boolean hasConnectionsGood(int i) {
    for (int j = 0; j < this.getNumberBidders(); j++) {
      if (this.isConnected(i, j)) {
        return true;
      }
    }
    return false;
  }

  /**
   * This method answers the question: does bidder j has any connections at all?
   * 
   * @param j - the index of a bidder
   * @return true if bidder j has at least one connection.
   */
  public boolean hasConnectionsBidder(int j) {
    for (int i = 0; i < this.getNumberGoods(); i++) {
      if (this.isConnected(i, j)) {
        return true;
      }
    }
    return false;
  }

  /**
   * This method computes all goods connected to bidder j.
   * 
   * @param j - the index of a bidder.
   * @return an ArrayList of indices of goods to which bidder j is connected.
   */
  public ArrayList<Integer> getListConnectedGoods(int j) {
    ArrayList<Integer> listOfGoods = new ArrayList<Integer>();
    for (int i = 0; i < this.getNumberGoods(); i++) {
      if (this.isConnected(i, j)) {
        listOfGoods.add(i);
      }
    }
    return listOfGoods;
  }

  /**
   * This method answers the question: is good i connected to bidder j?
   * 
   * @param i - a good index
   * @param j - a bidder index
   * @return true if good i is connected to bidder j
   */
  public boolean isConnected(int i, int j) {
    return this.connections[i][j];
  }

  /**
   * This method computes the total number of goods supplied by the market.
   * 
   * @return the total number of goods supplied in the market, i.e., \sum_i N_i.
   */
  public int getTotalSupply() {
    int totalSupply = 0;
    for (int i = 0; i < this.getNumberGoods(); i++) {
      totalSupply += this.getGood(i).getSupply();
    }
    return totalSupply;
  }

  /**
   * This method computes the total number of goods demanded by the market.
   * 
   * @return the total number of goods demanded in the market, i.e., \sum_j I_j
   */
  public int getTotalDemand() {
    int totalDemand = 0;
    for (int j = 0; j < this.getNumberBidders(); j++) {
      totalDemand += this.getBidder(j).getDemand();
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
   * @return a string representation of the information of goods in the market
   */
  protected String stringGoodsInfo() {
    String ret = "";
    for (int i = 0; i < this.getNumberGoods(); i++) {
      ret += "\nN(" + i + ") = " + this.goods[i].supply;
    }
    return ret;
  }

  /**
   * Printer. Representation of objects as strings.
   * 
   * @return a string representation of the information of bidders in the market
   */
  protected String stringBiddersInfo() {
    String ret = "";
    for (int j = 0; j < this.getNumberBidders(); j++) {
      ret += "\n"
          + String.format(
              "%-20s %-20s %-20s %-20s %-20s %-12s %-20s",
              "R(" + j + ") = "
                  + String.format("%.2f", this.bidders[j].reward) + ";",
              "I(" + j + ") = " + this.bidders[j].demand + ";",
              "L(" + j + ") = "
                  + String.format("%.2f", this.bidders[j].level) + ";",
              "r(" + j + ") = "
                  + String.format("%.2f", this.bidders[j].reserve),
              this.bidders[j].backpointer, this.bidders[j].priority,
              this.bidders[j].allocationSoFar);
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
      for (int i = 0; i < this.getNumberGoods(); i++) {
        ret += "\n";
        for (int j = 0; j < this.getNumberBidders(); j++) {
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
    return "NbrBidders:\t"
        + this.getNumberBidders()
        + "\n"
        + "NbrGoods:\t"
        + this.getNumberGoods()
        + "\n"
        + String.format("%-20s %-20s %-20s %-20s %-20s %-12s %s",
            "Bidders Rewards", "Bidders Demand", "Level", "Reserve", "Backpointer",
            "Priority", "Alloc So Far") + this.stringBiddersInfo() + "\n"
        + "Goods Supply" + this.stringGoodsInfo() + "\n"
        + "Connections Matrix:\t" + this.stringConnectionsMatrix();
  }
  
}
