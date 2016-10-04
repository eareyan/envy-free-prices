package structures.factory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;
import structures.exceptions.MarketCreationException;

/**
 * This class implements methods to create single-minded markets.
 * 
 * @author Enrique Areyan Viqueira
 */
public class SingleMindedMarketFactory {

  /**
   * Creates a random single-minded market.
   * 
   * @param n - number of items.
   * @param m - number of bidders.
   * @return a single minded market.
   * @throws BidderCreationException in case a bidder could not be created.
   * @throws GoodsCreationException in case a good could not be created.
   * @throws MarketCreationException 
   */
  public static Market<Goods, Bidder<Goods>> createRandomSingleMindedMarket(int n, int m) throws BidderCreationException, GoodsCreationException, MarketCreationException {
    if (n <= 0) {
      throw new GoodsCreationException("There must be at least one good to create a market.");
    }
    if (m <= 0) {
      throw new BidderCreationException("There must be at least one bidder to create a market");
    }
    ArrayList<Goods> goods = new ArrayList<Goods>();
    for (int i = 0; i < n; i++) {
      goods.add(new Goods(1));
    }
    Random generator = new Random();
    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    //boolean[][] connections = new boolean[n][m];
    for (int j = 0; j < m; j++) {
      int demand = generator.nextInt(n) + 1;
      // Each bidder connects exactly with I_j goods.
      Set<Integer> connectTo = SingleMindedMarketFactory.randomNumbers(demand, n);
      // System.out.println("Bidder " + j + " connect to " + connectTo);
      HashSet<Goods> bDemandSet = new HashSet<Goods>();
      for (Integer i : connectTo) {
        //connections[i][j] = true;
        bDemandSet.add(goods.get(i));
      }
      bidders.add(new Bidder<Goods>( 
          demand,
          generator.nextDouble() * (RandomMarketFactory.defaultMaxReward - RandomMarketFactory.defaultMinReward) + RandomMarketFactory.defaultMinReward,
          bDemandSet));

    }
    return new Market<Goods, Bidder<Goods>>(goods, bidders);
  }
  
  /**
   * Creates, from the input single-minded market, 
   * another single-minded market where bidders that can't afford 
   * the reserve are dropped out.
   * 
   * @param market - a single minded market object.
   * @param reserve - a reserve price.
   * @return a single-minded market.
   * @throws GoodsCreationException 
   * @throws BidderCreationException 
   * @throws MarketCreationException in case the reserve is too high and no bidder survived.
   */
  public static Market<Goods, Bidder<Goods>> discountSingleMindedMarket(Market<Goods, Bidder<Goods>> market, double reserve) throws GoodsCreationException, BidderCreationException, MarketCreationException {
    
    // Copy the ArrayList of Goods.
    ArrayList<Goods> goods = new ArrayList<Goods>();
    for (Goods good : market.getGoods()) {
      goods.add(new Goods(good.getSupply()));
    }

    // Copy the ArrayList of Bidders
    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    for (Bidder<Goods> bidder : market.getBidders()) {
      if (bidder.getReward() - reserve * bidder.getDemand() >= 0) {
        HashSet<Goods> bDemandSet = new HashSet<Goods>();
        for (int i = 0; i < market.getNumberGoods(); i++) {
          if (bidder.demandsGood(market.getGoods().get(i))) {
            bDemandSet.add(goods.get(i));
          }
        }
        bidders.add(new Bidder<Goods>(bidder.getDemand(), bidder.getReward(), bDemandSet));
      }
    }

    return new Market<Goods, Bidder<Goods>>(goods, bidders);
  }
  
  /**
   * Computes a set of n distinct, random integers, between 0 and max. 
   * If n>=max, returns the set of integers 0...max
   *
   * @param n - the number of integers to produce.
   * @param max - the maximum value of any integer to be produced.
   * @return a list of integers.
   */
  public static Set<Integer> randomNumbers(int n, int max) {
    Set<Integer> generated = new LinkedHashSet<Integer>();
    if (n >= max) {
      // If we want more numbers than the max, it means we want all numbers from 1...max.
      for (int i = 0; i < max; i++) {
        generated.add(i);
      }
      return generated;
    } else {
      Random rng = new Random(); // Ideally just create one instance globally.
                                 // Note: use LinkedHashSet to maintain
                                 // insertion order
      while (generated.size() < n) {
        // System.out.println("11");
        Integer next = rng.nextInt(max);
        // As we're adding to a set, this will automatically do a containment check
        generated.add(next);
      }
    }
    return generated;
  }

}
