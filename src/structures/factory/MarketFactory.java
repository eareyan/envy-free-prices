package structures.factory;

import java.util.Random;

import structures.Campaign;
import structures.Market;
import structures.User;
import structures.exceptions.CampaignCreationException;

/**
 * Markets can be created in different ways.
 * This class implements methods to create Market Objects.
 * 
 * @author Enrique Areyan Viqueira
 */
public class MarketFactory {
  
  /**
   * Copies the given market without user i, deleting the corresponding 
   * row from the connection matrix
   * 
   * @param market - a Market object.
   * @param userToDelete - the index of the user to delete.
   * @return a copy of market but without the specified user.
   * @throws CampaignCreationException in case a campaign could not be created.
   */
  public static Market copyMarketWithoutUser(Market market, int userToDelete) throws CampaignCreationException {
    // Create Users
    User[] users = new User[market.getNumberUsers() - 1];
    int k = 0;
    for (int i = 0; i < market.getNumberUsers(); i++) {
      if (i != userToDelete) {
        users[k] = new User(market.getUser(i).getSupply());
        k++;
      }
    }
    // Create Campaigns
    Campaign[] campaigns = new Campaign[market.getNumberCampaigns()];
    for (int j = 0; j < market.getNumberCampaigns(); j++) {
      campaigns[j] = new Campaign(market.getCampaign(j).getDemand(), market.getCampaign(j).getReward());
    }
    // Create Connections
    boolean[][] connections = new boolean[market.getNumberUsers() - 1][market
        .getNumberCampaigns()];
    int l = 0;
    for (int i = 0; i < market.getNumberUsers(); i++) {
      if (i != userToDelete) {
        for (int j = 0; j < market.getNumberCampaigns(); j++) {
          connections[l][j] = market.isConnected(i, j);
        }
        l++;
      }
    }
    return new Market(users, campaigns, connections);
  }
  
  /**
   * Copies the given market without campaign j, deleting the corresponding 
   * row from connection matrix.
   * 
   * @param market - a Market object.
   * @param campaignToDelete - the index of the campaign to delete.
   * @return a copy of market without the specified campaign.
   * @throws CampaignCreationException in case a campaign could not be created.
   */
  public static Market copyMarketWithoutCampaign(Market market,
      int campaignToDelete) throws CampaignCreationException {
    // Create Users
    User[] users = new User[market.getNumberUsers()];
    for (int i = 0; i < market.getNumberUsers(); i++) {
      users[i] = new User(market.getUser(i).getSupply());
    }
    // Create Campaigns
    Campaign[] campaigns = new Campaign[market.getNumberCampaigns() - 1];
    int k = 0;
    for (int j = 0; j < market.getNumberCampaigns(); j++) {
      if (j != campaignToDelete) {
        campaigns[k] = new Campaign(market.getCampaign(j).getDemand(), market.getCampaign(j).getReward());
        k++;
      }
    }
    // Create Connections
    boolean[][] connections = new boolean[market.getNumberUsers()][market.getNumberCampaigns() - 1];
    int l = 0;
    for (int j = 0; j < market.getNumberCampaigns(); j++) {
      if (j != campaignToDelete) {
        for (int i = 0; i < market.getNumberUsers(); i++) {
          connections[i][l] = market.isConnected(i, j);
        }
        l++;
      }
    }
    return new Market(users, campaigns, connections);
  }

  /**
   * Adds a dummy campaign per item that demands impressions from that item at
   * the given value and is not connected to any other item.
   * 
   * @param M - a Market object
   * @param reserveValue - a reserve value.
   * @param reserveDemand - a reserve demand.
   * @return a Market with dummy campaigns.
   * @throws CampaignCreationException
   */
  public static Market augmentMarketWithReserve(Market M, double reserveValue, int reserveDemand) throws CampaignCreationException {
    int totalNewCampaigns = M.getNumberCampaigns() + M.getNumberUsers();
    Campaign[] campaigns = new Campaign[totalNewCampaigns];
    for (int j = 0; j < M.getNumberCampaigns(); j++) {
      campaigns[j] = M.getCampaign(j);
    }
    for (int j = M.getNumberCampaigns(); j < totalNewCampaigns; j++) {
      campaigns[j] = new Campaign(reserveDemand, reserveValue);
    }
    boolean[][] connections = new boolean[M.getNumberUsers()][totalNewCampaigns];
    for (int i = 0; i < M.getNumberUsers(); i++) {
      for (int j = 0; j < M.getNumberCampaigns(); j++) {
        connections[i][j] = M.isConnected(i, j);
      }
    }
    int i = 0;
    for (int j = M.getNumberCampaigns(); j < totalNewCampaigns; j++) {
      connections[i][j] = (j - M.getNumberCampaigns() == i);
      i++;
    }
    return new Market(M.getUsers(), campaigns, connections);
  }
  
  /**
   * Clones a market
   * 
   * @param market - a market object.
   * @return a clone of market.
   * @throws CampaignCreationException in case a campaign could not be created.
   */
  public static Market cloneMarket(Market market) throws CampaignCreationException {
    User[] users = new User[market.getNumberUsers()];
    for (int i = 0; i < market.getNumberUsers(); i++) {
      users[i] = new User(market.getUser(i).getSupply());
    }
    Campaign[] campaigns = new Campaign[market.getNumberCampaigns()];
    for (int j = 0; j < market.getNumberCampaigns(); j++) {
      campaigns[j] = new Campaign(market.getCampaign(j).getDemand(), market.getCampaign(j).getReward());
    }
    boolean[][] connections = new boolean[market.getNumberUsers()][market.getNumberCampaigns()];
    for (int i = 0; i < market.getNumberUsers(); i++) {
      for (int j = 0; j < market.getNumberCampaigns(); j++) {
        connections[i][j] = market.isConnected(i, j);
      }
    }
    return new Market(users, campaigns, connections);
  }

  /**
   * Transpose market, i.e., interchange users for campaigns. This means: a user
   * i with supply N_i becomes a campaign j with demand N_i and a new random
   * reward. A campaign j with demand I_j becomes a user i with supply I_j. The
   * connections are preserved, i.e. if (i,j) is connected in the original
   * market, then it will be in the transposed market.
   * 
   * @param market - a Market object
   * @return a market object with campaigns and users interchanged.
   * @throws CampaignCreationException in case a campaign could not be created.
   */
  public static Market transposeMarket(Market market)
      throws CampaignCreationException {
    User[] users = new User[market.getNumberCampaigns()];
    for (int j = 0; j < market.getNumberCampaigns(); j++) {
      users[j] = new User(market.getCampaign(j).getDemand());
    }
    Campaign[] campaigns = new Campaign[market.getNumberUsers()];
    int maxReward = 100;
    int minReward = 1;
    Random generator = new Random();
    for (int i = 0; i < market.getNumberUsers(); i++) {
      campaigns[i] = new Campaign(market.getUser(i).getSupply(),
          generator.nextDouble() * (maxReward - minReward) + minReward);
    }
    boolean[][] connections = new boolean[market.getNumberCampaigns()][market.getNumberUsers()];
    for (int i = 0; i < market.getNumberUsers(); i++) {
      for (int j = 0; j < market.getNumberCampaigns(); j++) {
        connections[j][i] = market.isConnected(i, j);
      }
    }
    return new Market(users, campaigns, connections);
  }

  /**
   * Creates a clone market but Subtract the rewards of all campaign by the
   * parameter r times number of impressions. Call this market with reserve
   * price r.
   * 
   * @param market - a market object.
   * @param r - a reserve price.
   * @return a market object.
   * @throws CampaignCreationException in case a campaign could not be created. 
   */
  public static Market createReservePriceMarket(Market market, double r) throws CampaignCreationException {
    Market clone = cloneMarket(market);
    for (int j = 0; j < clone.getNumberCampaigns(); j++) {
      clone.getCampaign(j).setReward(market.getCampaign(j).getReward() - r * market.getCampaign(j).getDemand());
    }
    return clone;
  }

  /**
   * Create a market from a given market where, for all users i, we create N_i
   * users, each supplying only one impression and with the same connections as i.
   * 
   * @param market - a market object.
   * @return a market object with unit supply.
   */
  public static Market createUnitSupplyMarket(Market market) {

    /*
     * Given a market, construct a new market where each user class i is divided
     * into N_i new user classes. Run Ascending Auction in this new market and
     * then convert the price of the different user classes to a single price
     * via a min operator.
     */
    Campaign[] campaigns = market.getCampaigns(); // Use same campaigns
    int totalUserSupply = market.getTotalSupply(); // Compute total supply
    User[] newUsers = new User[totalUserSupply]; // Have as many new users as total supply
    boolean[][] newConnections = new boolean[totalUserSupply][market.getNumberCampaigns()];
    int k = 0;
    // For each original user
    for (int i = 0; i < market.getNumberUsers(); i++) {
      // System.out.println("Create "+ this.market.getUser(i).getSupply() + " many users from user " + i);
      for (int iPrime = 0; iPrime < market.getUser(i).getSupply(); iPrime++) {
        newUsers[k] = new User(1); // Create N_i copies for user i
        for (int j = 0; j < market.getNumberCampaigns(); j++) { 
          // Compute the new connections.
          newConnections[k][j] = market.isConnected(i, j);
        }
        k++;
      }
    }
    return new Market(newUsers, campaigns, newConnections);
  }

}
