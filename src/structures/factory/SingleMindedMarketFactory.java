package structures.factory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import singleminded.structures.SingleMindedMarket;
import structures.Bidder;
import structures.Goods;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;
import structures.exceptions.MarketCreationException;
import structures.rewardfunctions.ElitistRewardFunction;
import structures.rewardfunctions.RewardsGeneratorInterface;
import structures.rewardfunctions.UniformIntegerRewardFunction;
import structures.rewardfunctions.UniformRewardFunction;
import util.MyRandom;

/**
 * This class implements methods to create single-minded markets.
 * 
 * @author Enrique Areyan Viqueira
 */
public class SingleMindedMarketFactory {

  /**
   * A single instance of this class. Used to instantiate a DemandSetSizeBuilder object.
   */
  public static SingleMindedMarketFactory singleMindedMarketFactoryInstance = new SingleMindedMarketFactory();

  /**
   * An interface to define different ways to construct the size of a demand set.
   */
  interface DemandSetSizeBuilder {
    int getSizeOfDemandSet();
  }

  /**
   * All bidders demand the same number of items.
   */
  class FixedDemandSetSize implements DemandSetSizeBuilder {
    int k;

    public FixedDemandSetSize(int k) {
      this.k = k;
    }

    @Override
    public int getSizeOfDemandSet() {
      return this.k;
    }
  }

  /**
   * Bidders demand a random number of items.
   */
  class RandomDemandSetSize implements DemandSetSizeBuilder {

    int n;

    public RandomDemandSetSize(int n) {
      this.n = n;
    }

    @Override
    public int getSizeOfDemandSet() {
      return MyRandom.generator.nextInt(n) + 1;
    }

  }

  /**
   * Checks the validity of the parameters needed to create a single minded market.
   * 
   * @param n - number of items.
   * @param m - number of bidders.
   * @param k - size of demand set.
   * @param p - probability of bidder demanding a good.
   * @throws GoodsCreationException
   * @throws BidderCreationException
   */
  public static void checkSingleMindedParameters(int n, int m, int k, double p) throws GoodsCreationException, BidderCreationException {
    if (n <= 0) {
      throw new GoodsCreationException("There must be at least one good to create a market.");
    }
    if (m <= 0) {
      throw new BidderCreationException("There must be at least one bidder to create a market.");
    }
    if (k > n) {
      throw new BidderCreationException("The size of the demand set of a Single-Minded Bidder cannot be bigger than the number of available goods.");
    }
    if (p <= 0) {
      throw new BidderCreationException("When using probability p of connection of a bidder to a good, p must be non-negative and greater than zero.");
    }
  }

  /**
   * Creates and returns Random-k-Single-Minded-Market(n,m,k) with rewards drawn from uniform distribution.
   * 
   * @param n - number of items.
   * @param m - number of bidders.
   * @return a Random-k-Single-Minded-Market(n,m,k)
   * @throws MarketCreationException
   * @throws BidderCreationException
   * @throws GoodsCreationException
   */
  public static SingleMindedMarket<Goods, Bidder<Goods>> uniformIntegerRewardRandomSingleMindedMarket(int n, int m, int k) throws GoodsCreationException, BidderCreationException, MarketCreationException {
    return SingleMindedMarketFactory.createRandomSingleMindedMarket(n, m,
        SingleMindedMarketFactory.singleMindedMarketFactoryInstance.new FixedDemandSetSize(k), UniformIntegerRewardFunction.singletonInstance);
  }

  /**
   * Creates and returns Random-k-Single-Minded-Market(n,m,k) with rewards drawn from uniform distribution.
   * 
   * @param n - number of items.
   * @param m - number of bidders.
   * @return a Random-k-Single-Minded-Market(n,m,k)
   * @throws MarketCreationException
   * @throws BidderCreationException
   * @throws GoodsCreationException
   */
  public static SingleMindedMarket<Goods, Bidder<Goods>> uniformRewardRandomSingleMindedMarket(int n, int m, int k) throws GoodsCreationException, BidderCreationException, MarketCreationException {
    return SingleMindedMarketFactory.createRandomSingleMindedMarket(n, m,
        SingleMindedMarketFactory.singleMindedMarketFactoryInstance.new FixedDemandSetSize(k), UniformRewardFunction.singletonInstance);
  }

  /**
   * Creates and returns Random-k-Single-Minded-Market(n,m,k) with rewards drawn from elitist distribution.
   * 
   * @param n - number of items.
   * @param m - number of bidders.
   * @return a Random-k-Single-Minded-Market(n,m,k)
   * @throws MarketCreationException
   * @throws BidderCreationException
   * @throws GoodsCreationException
   */
  public static SingleMindedMarket<Goods, Bidder<Goods>> elitistRewardRandomSingleMindedMarket(int n, int m, int k) throws GoodsCreationException, BidderCreationException, MarketCreationException {
    return SingleMindedMarketFactory.createRandomSingleMindedMarket(n, m,
        SingleMindedMarketFactory.singleMindedMarketFactoryInstance.new FixedDemandSetSize(k), ElitistRewardFunction.singletonInstance);
  }

  /**
   * 
   * @param n
   * @param m
   * @param rewardFunction
   * @return
   * @throws GoodsCreationException
   * @throws BidderCreationException
   * @throws MarketCreationException
   */
  public static SingleMindedMarket<Goods, Bidder<Goods>> randomDemandSetSizeSingleMindedMarket(int n, int m) throws GoodsCreationException, BidderCreationException, MarketCreationException {
    return SingleMindedMarketFactory.createRandomSingleMindedMarket(n, m,
        SingleMindedMarketFactory.singleMindedMarketFactoryInstance.new RandomDemandSetSize(n), UniformIntegerRewardFunction.singletonInstance);
  }

  /**
   * Creates a random single-minded market.
   * 
   * @param n - number of items.
   * @param m - number of bidders.
   * @param k - size of demand set.
   * @return a single minded market.
   * @throws MarketCreationException
   * @throws BidderCreationException
   * @throws GoodsCreationException
   * @throws Exception
   */
  public static SingleMindedMarket<Goods, Bidder<Goods>> createRandomSingleMindedMarket(int n, int m, DemandSetSizeBuilder demandSetSizeBuilder, RewardsGeneratorInterface rewardFunction) throws GoodsCreationException, BidderCreationException, MarketCreationException {
    // Check validity of parameters. In this case we don't use p, so we use a default value of 1.0.
    SingleMindedMarketFactory.checkSingleMindedParameters(n, m, 1, 1.0);
    // Create goods, each with unit supply.
    ArrayList<Goods> goods = new ArrayList<Goods>();
    for (int i = 0; i < n; i++) {
      goods.add(new Goods(1));
    }
    // Create bidders
    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    for (int j = 0; j < m; j++) {
      int k = demandSetSizeBuilder.getSizeOfDemandSet();
      // Each bidder connects exactly with k distinct random goods.
      Set<Integer> connectTo = MyRandom.randomNumbers(k, n);
      HashSet<Goods> bDemandSet = new HashSet<Goods>();
      for (Integer i : connectTo) {
        bDemandSet.add(goods.get(i));
      }
      bidders.add(new Bidder<Goods>(k, rewardFunction.getReward(), bDemandSet));
    }
    return new SingleMindedMarket<Goods, Bidder<Goods>>(goods, bidders);
  }

  /**
   * Create random single- minded markets.
   * 
   * @param n
   * @param m
   * @param p
   * @param rewardFunction
   * @return
   * @throws MarketCreationException
   * @throws BidderCreationException
   * @throws GoodsCreationException
   */
  public static SingleMindedMarket<Goods, Bidder<Goods>> createRandomParametrizedSingleMindedMarket(int n, int m, double p, RewardsGeneratorInterface rewardFunction) throws GoodsCreationException, BidderCreationException, MarketCreationException {
    // Check validity of parameters. In this case we don't use k so we use a default value of 0.
    SingleMindedMarketFactory.checkSingleMindedParameters(n, m, 0, p);
    // Create goods, each with unit supply.
    ArrayList<Goods> goods = new ArrayList<Goods>();
    for (int i = 0; i < n; i++) {
      goods.add(new Goods(1));
    }
    // Create bidders
    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    for (int j = 0; j < m; j++) {
      HashSet<Goods> bDemandSet = new HashSet<Goods>();
      while (bDemandSet.isEmpty()) {
        for (int i = 0; i < n; i++) {
          if (Math.random() <= p) {
            bDemandSet.add(goods.get(i));
          }
        }
      }
      bidders.add(new Bidder<Goods>(bDemandSet.size(), rewardFunction.getReward(), bDemandSet));
    }
    return new SingleMindedMarket<Goods, Bidder<Goods>>(goods, bidders);
  }

}
