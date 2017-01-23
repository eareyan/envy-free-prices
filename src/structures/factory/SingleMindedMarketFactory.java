package structures.factory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;

import singleminded.SingleMindedMarket;
import structures.Bidder;
import structures.Goods;
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
   * Creates and returns Random-k-Single-Minded-Market(n,m,k) with rewards drawn from uniform distribution.
   * 
   * @param n
   * @param m
   * @return a Random-k-Single-Minded-Market(n,m,k)
   * @throws Exception
   */
  public static SingleMindedMarket<Goods, Bidder<Goods>> createUniformRewardRandomSingleMindedMarket(int n, int m, int k) throws Exception {
    return SingleMindedMarketFactory.createRandomSingleMindedMarket(n, m, k, RewardsGenerator.getRandomUniformRewardFunction());
  }

  /**
   * Creates and returns Random-k-Single-Minded-Market(n,m,k) with rewards drawn from elitist distribution.
   * 
   * @param n
   * @param m
   * @return a Random-k-Single-Minded-Market(n,m,k)
   * @throws Exception
   */
  public static SingleMindedMarket<Goods, Bidder<Goods>> createElitistRewardRandomSingleMindedMarket(int n, int m, int k) throws Exception {
    return SingleMindedMarketFactory.createRandomSingleMindedMarket(n, m, k, RewardsGenerator.getElitistRewardFunction());
  }

  /**
   * Creates a random single-minded market.
   * 
   * @param n - number of items.
   * @param m - number of bidders.
   * @param k - bound on size of demand set.
   * @return a single minded market.
   * @throws Exception
   */
  public static SingleMindedMarket<Goods, Bidder<Goods>> createRandomSingleMindedMarket(int n, int m, int k, Callable<Double> rewardFunction) throws Exception {
    if (n <= 0) {
      throw new GoodsCreationException("There must be at least one good to create a market.");
    }
    if (m <= 0) {
      throw new BidderCreationException("There must be at least one bidder to create a market.");
    }
    if (k > n) {
      throw new MarketCreationException("The size of the demand set of a Single-Minded Bidder cannot be bigger than the number of available goods.");
    }
    // Create goods, each with unit supply.
    ArrayList<Goods> goods = new ArrayList<Goods>();
    for (int i = 0; i < n; i++) {
      goods.add(new Goods(1));
    }
    // Create bidders
    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    for (int j = 0; j < m; j++) {
      // Each bidder connects exactly with k distinct random goods.
      Set<Integer> connectTo = SingleMindedMarketFactory.randomNumbers(k, n);
      // System.out.println("Bidder " + j + " connect to " + connectTo);
      HashSet<Goods> bDemandSet = new HashSet<Goods>();
      for (Integer i : connectTo) {
        bDemandSet.add(goods.get(i));
      }
      bidders.add(new Bidder<Goods>(k, rewardFunction.call(), bDemandSet));
    }
    return new SingleMindedMarket<Goods, Bidder<Goods>>(goods, bidders);
  }

  /**
   * Computes a set of n distinct, random integers, between 0 and max. If n>=max, returns the set of integers 0...max
   *
   * @param n - the number of integers to produce.
   * @param max - the maximum value of any integer to be produced.
   * @return a list of integers.
   */
  public static Set<Integer> randomNumbers(int n, int max) {
    HashSet<Integer> generated = new HashSet<Integer>();
    if (n >= max) {
      // If we want more numbers than the max, it means we want all numbers from 1...max.
      for (int i = 0; i < max; i++) {
        generated.add(i);
      }
      return generated;
    } else {
      // Ideally just create one instance globally.
      Random rng = new Random();
      while (generated.size() < n) {
        Integer next = rng.nextInt(max);
        // As we're adding to a set, this will automatically do a containment check
        generated.add(next);
      }
    }
    return generated;
  }

}
