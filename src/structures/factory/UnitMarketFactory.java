package structures.factory;

import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;

/**
 * This class implements methods to create unit-demand markets.
 * 
 * @author Enrique Areyan Viqueira
 */
public class UnitMarketFactory {

  /**
   * Create a singleton market from a valuation matrix.
   * 
   * @param valuationMatrix - a matrix of valuations.
   * @return a Market object.
   * @throws BidderCreationException in case a bidder could not be created.
   * @throws GoodsCreationException in case a good could not be created.
   
  public static Market<Goods, Bidder<Goods>> createMarketFromValuationMatrix(double[][] valuationMatrix) throws BidderCreationException, GoodsCreationException {
    ArrayList<Goods> goods = new ArrayList<Goods>();
    for (int i = 0; i < valuationMatrix.length; i++) {
      goods.add(new Goods(1));
    }
    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    for (int j = 0; j < valuationMatrix[0].length; j++) {
      double reward = Double.NEGATIVE_INFINITY;
      HashSet<Goods> bDemandSet = new HashSet<Goods>();
      for (int i = 0; i < valuationMatrix.length; i++) {
        if (valuationMatrix[i][j] > Double.NEGATIVE_INFINITY) {
          reward = valuationMatrix[i][j];
          break;
        }
      }
      bidders.add(new Bidder(1, reward));
    }
    boolean[][] connections = new boolean[valuationMatrix.length][valuationMatrix[0].length];
    for (int i = 0; i < valuationMatrix.length; i++) {
      for (int j = 0; j < valuationMatrix[0].length; j++) {
        connections[i][j] = valuationMatrix[i][j] > Double.NEGATIVE_INFINITY;
      }
    }
    return new Market<Goods, Bidder>(goods, bidders, connections);
  }
  
  /**
   * Shortcut method to create a unit demand random market by just providing the
   * number of goods, bidders, and probability of connection.
   * 
   * @param numberGoods - number of goods.
   * @param numberBidders - number of bidders.
   * @param probabilityConnections - probability of a connection being present.
   * @return a Market object.
   * @throws BidderCreationException in case a bidder could not be created.
   * @throws GoodsCreationException in case a good could not be created.
   
  public static Market<Goods, Bidder> randomUnitDemandMarket(int numberGoods, int numberBidders, double probabilityConnections) throws BidderCreationException, GoodsCreationException {
    return RandomMarketFactory.randomMarket(numberGoods, 1, 1, numberBidders,
        1, 1, RandomMarketFactory.defaultMinReward,
        RandomMarketFactory.defaultMaxReward, probabilityConnections);
  }
  
  /**
   * Create a singleton market given all other parameters (connections matrix and rewards)
   * 
   * @param numberGoods - number of goods.
   * @param numberBidders - number of bidders.
   * @param connections - matrix of connections.
   * @param rewards - vector of bidders rewards.
   * @return a Market object.
   * @throws BidderCreationException in case a bidder could not be created.
   * @throws GoodsCreationException in case a good could not be created.
   
  public static Market<Goods, Bidder<Goods>> singletonMarket(int numberGoods, int numberBidders, boolean[][] connections, double[] rewards) throws BidderCreationException, GoodsCreationException {
    // Create Goods
    Goods[] goods = new Goods[numberGoods];
    for (int i = 0; i < numberGoods; i++) {
      goods[i] = new Goods(i, 1);
    }
    // Create Bidder
    Bidder[] bidders = new Bidder[numberBidders];
    for (int j = 0; j < numberBidders; j++) {
      bidders[j] = new Bidder(1, rewards[j]);
    }
    return new Market<Goods, Bidder>(goods, bidders, connections);
  }
  
  /**
   * Produces a random valuation matrix V_ij, where valuations are within the default range.
   * 
   * @param n - number of goods.
   * @param m - number of bidders.
   * @param prob - probability of a connection.
   * @param minReward - lower bound on bidders reward.
   * @param maxReward - upper bound on bidders reward.
   * @return a matrix of valuations.
   */
  public static double[][] getValuationMatrix(int n, int m, double prob, double minReward, double maxReward) {
    // Random generator = new Random();
    double[][] valuationMatrix = new double[n][m];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        valuationMatrix[i][j] = (Math.random() <= prob) ? (Math.random() * (maxReward - minReward) + minReward) : Double.NEGATIVE_INFINITY;
      }
    }
    return valuationMatrix;
  }

  /**
   * Generates a random valuation matrix.
   * 
   * @param n - number of goods.
   * @param m - number of bidders.
   * @param prob - probability of a connection.
   * @return a valuation matrix.
   */
  public static double[][] getValuationMatrix(int n, int m, double prob) {
    return getValuationMatrix(n, m, prob, RandomMarketFactory.defaultMinReward, RandomMarketFactory.defaultMaxReward);
  }

  /**
   * Given a valuation matrix V_ij, returns a valuation V_ij' that respects
   * reserve price r. Respect in this context means that any valuation below r
   * becomes -infinity and all others are decreased by r. You can think of this
   * operation as "shifting" the matrix by r.
   * 
   * @param X - a valuation matrix.
   * @param r - a reserve price.
   * @return a valuation matrix.
   */
  public static double[][] getValuationReserve(double[][] X, double r) {
    double[][] XReserve = new double[X.length][X[0].length];
    for (int i = 0; i < X.length; i++) {
      for (int j = 0; j < X[0].length; j++) {
        XReserve[i][j] = (X[i][j] - r <= 0) ? Double.NEGATIVE_INFINITY : X[i][j] - r;
      }
    }
    return XReserve;
  }

}
