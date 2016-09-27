package structures.factory;

import java.util.Random;

import structures.Bidder;
import structures.Market;
import structures.Goods;
import structures.exceptions.BidderCreationException;

/**
 * Markets can be created in different ways.
 * This class implements methods to create Market Objects.
 * 
 * @author Enrique Areyan Viqueira
 */
public class MarketFactory {
  
  /**
   * Copies the given market without good i, deleting the corresponding 
   * row from the connection matrix
   * 
   * @param market - a Market object.
   * @param goodToDelete - the index of the good to delete.
   * @return a copy of market but without the specified good.
   * @throws BidderCreationException in case a bidder could not be created.
   */
  public static Market copyMarketWithoutGood(Market market, int goodToDelete) throws BidderCreationException {
    // Create Goods
    Goods[] goods = new Goods[market.getNumberGoods() - 1];
    int k = 0;
    for (int i = 0; i < market.getNumberGoods(); i++) {
      if (i != goodToDelete) {
        goods[k] = new Goods(market.getGood(i).getSupply());
        k++;
      }
    }
    // Create Bidders
    Bidder[] bidders = new Bidder[market.getNumberBidders()];
    for (int j = 0; j < market.getNumberBidders(); j++) {
      bidders[j] = new Bidder(market.getBidder(j).getDemand(), market.getBidder(j).getReward());
    }
    // Create Connections
    boolean[][] connections = new boolean[market.getNumberGoods() - 1][market
        .getNumberBidders()];
    int l = 0;
    for (int i = 0; i < market.getNumberGoods(); i++) {
      if (i != goodToDelete) {
        for (int j = 0; j < market.getNumberBidders(); j++) {
          connections[l][j] = market.isConnected(i, j);
        }
        l++;
      }
    }
    return new Market(goods, bidders, connections);
  }
  
  /**
   * Copies the given market without bidder j, deleting the corresponding 
   * row from connection matrix.
   * 
   * @param market - a Market object.
   * @param bidderToDelete - the index of the bidder to delete.
   * @return a copy of market without the specified bidder.
   * @throws BidderCreationException in case a bidder could not be created.
   */
  public static Market copyMarketWithoutBidder(Market market, int bidderToDelete) throws BidderCreationException {
    // Create Goods
    Goods[] goods = new Goods[market.getNumberGoods()];
    for (int i = 0; i < market.getNumberGoods(); i++) {
      goods[i] = new Goods(market.getGood(i).getSupply());
    }
    // Create Bidders
    Bidder[] bidders = new Bidder[market.getNumberBidders() - 1];
    int k = 0;
    for (int j = 0; j < market.getNumberBidders(); j++) {
      if (j != bidderToDelete) {
        bidders[k] = new Bidder(market.getBidder(j).getDemand(), market.getBidder(j).getReward());
        k++;
      }
    }
    // Create Connections
    boolean[][] connections = new boolean[market.getNumberGoods()][market.getNumberBidders() - 1];
    int l = 0;
    for (int j = 0; j < market.getNumberBidders(); j++) {
      if (j != bidderToDelete) {
        for (int i = 0; i < market.getNumberGoods(); i++) {
          connections[i][l] = market.isConnected(i, j);
        }
        l++;
      }
    }
    return new Market(goods, bidders, connections);
  }

  /**
   * Adds a dummy bidder per good that demands impressions from that good at
   * the given value and is not connected to any other good.
   * 
   * @param M - a Market object
   * @param reserveValue - a reserve value.
   * @param reserveDemand - a reserve demand.
   * @return a Market with dummy bidders.
   * @throws BidderCreationException
   */
  public static Market augmentMarketWithReserve(Market M, double reserveValue, int reserveDemand) throws BidderCreationException {
    int totalNewBidders = M.getNumberBidders() + M.getNumberGoods();
    Bidder[] bidders = new Bidder[totalNewBidders];
    for (int j = 0; j < M.getNumberBidders(); j++) {
      bidders[j] = M.getBidder(j);
    }
    for (int j = M.getNumberBidders(); j < totalNewBidders; j++) {
      bidders[j] = new Bidder(reserveDemand, reserveValue);
    }
    boolean[][] connections = new boolean[M.getNumberGoods()][totalNewBidders];
    for (int i = 0; i < M.getNumberGoods(); i++) {
      for (int j = 0; j < M.getNumberBidders(); j++) {
        connections[i][j] = M.isConnected(i, j);
      }
    }
    int i = 0;
    for (int j = M.getNumberBidders(); j < totalNewBidders; j++) {
      connections[i][j] = (j - M.getNumberBidders() == i);
      i++;
    }
    return new Market(M.getGoods(), bidders, connections);
  }
  
  /**
   * Clones a market
   * 
   * @param market - a market object.
   * @return a clone of market.
   * @throws BidderCreationException in case a bidder could not be created.
   */
  public static Market cloneMarket(Market market) throws BidderCreationException {
    Goods[] goods = new Goods[market.getNumberGoods()];
    for (int i = 0; i < market.getNumberGoods(); i++) {
      goods[i] = new Goods(market.getGood(i).getSupply());
    }
    Bidder[] bidders = new Bidder[market.getNumberBidders()];
    for (int j = 0; j < market.getNumberBidders(); j++) {
      bidders[j] = new Bidder(market.getBidder(j).getDemand(), market.getBidder(j).getReward());
    }
    boolean[][] connections = new boolean[market.getNumberGoods()][market.getNumberBidders()];
    for (int i = 0; i < market.getNumberGoods(); i++) {
      for (int j = 0; j < market.getNumberBidders(); j++) {
        connections[i][j] = market.isConnected(i, j);
      }
    }
    return new Market(goods, bidders, connections);
  }

  /**
   * Transpose market, i.e., interchange goods for bidders. This means: a good
   * i with supply N_i becomes a bidder j with demand N_i and a new random
   * reward. A bidder j with demand I_j becomes a good i with supply I_j. The
   * connections are preserved, i.e. if (i,j) is connected in the original
   * market, then it will be in the transposed market.
   * 
   * @param market - a Market object
   * @return a market object with bidders and goods interchanged.
   * @throws BidderCreationException in case a bidder could not be created.
   */
  public static Market transposeMarket(Market market) throws BidderCreationException {
    Goods[] goods = new Goods[market.getNumberBidders()];
    for (int j = 0; j < market.getNumberBidders(); j++) {
      goods[j] = new Goods(market.getBidder(j).getDemand());
    }
    Bidder[] bidders = new Bidder[market.getNumberGoods()];
    int maxReward = 100;
    int minReward = 1;
    Random generator = new Random();
    for (int i = 0; i < market.getNumberGoods(); i++) {
      bidders[i] = new Bidder(market.getGood(i).getSupply(),
          generator.nextDouble() * (maxReward - minReward) + minReward);
    }
    boolean[][] connections = new boolean[market.getNumberBidders()][market.getNumberGoods()];
    for (int i = 0; i < market.getNumberGoods(); i++) {
      for (int j = 0; j < market.getNumberBidders(); j++) {
        connections[j][i] = market.isConnected(i, j);
      }
    }
    return new Market(goods, bidders, connections);
  }

  /**
   * Creates a clone market but Subtract the rewards of all bidders by the
   * parameter r times number of impressions. Call this market with reserve
   * price r.
   * 
   * @param market - a market object.
   * @param r - a reserve price.
   * @return a market object.
   * @throws BidderCreationException in case a bidder could not be created. 
   */
  public static Market createReservePriceMarket(Market market, double r) throws BidderCreationException {
    Market clone = cloneMarket(market);
    for (int j = 0; j < clone.getNumberBidders(); j++) {
      clone.getBidder(j).setReward(market.getBidder(j).getReward() - r * market.getBidder(j).getDemand());
    }
    return clone;
  }

  /**
   * Create a market from a given market where, for all goods i, we create N_i
   * goods, each supplying only one impression and with the same connections as i.
   * 
   * @param market - a market object.
   * @return a market object with unit supply.
   */
  public static Market createUnitSupplyMarket(Market market) {

    /*
     * Given a market, construct a new market where each good i is divided
     * into N_i new goods. Run Ascending Auction in this new market and
     * then convert the price of the different goods to a single price
     * via a min operator.
     */
    Bidder[] bidders = market.getBidders(); // Use same bidders
    int totalGoodSupply = market.getTotalSupply(); // Compute total supply
    Goods[] newGoods = new Goods[totalGoodSupply]; // Have as many new goods as total supply
    boolean[][] newConnections = new boolean[totalGoodSupply][market.getNumberBidders()];
    int k = 0;
    // For each original good
    for (int i = 0; i < market.getNumberGoods(); i++) {
      for (int iPrime = 0; iPrime < market.getGood(i).getSupply(); iPrime++) {
        newGoods[k] = new Goods(1); // Create N_i copies for good i
        for (int j = 0; j < market.getNumberBidders(); j++) { 
          // Compute the new connections.
          newConnections[k][j] = market.isConnected(i, j);
        }
        k++;
      }
    }
    return new Market(newGoods, bidders, newConnections);
  }

}
