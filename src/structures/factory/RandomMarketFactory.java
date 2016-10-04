package structures.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

import structures.Bidder;
import structures.Market;
import structures.Goods;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;
import structures.exceptions.MarketCreationException;

/**
 * Markets can be created in different ways.
 * This class implements logic to create Random Market Objects.
 * 
 * @author Enrique Areyan Viqueira
 */
public class RandomMarketFactory {
  
  public static double defaultMaxReward = 10.0;
  public static double defaultMinReward = 1.0;
  public static int defaultMaxSupplyPerGood = 10;
  public static int defaultMinSupplyPerBidder = 1;
  public static int defaultMaxDemandPerBidder = 10;
  public static int defaultMinDemandPerBidder = 1;
  
  /**
   * Creation of a fully parameterizable random market.
   * 
   * @param numberGoods - number of goods.
   * @param minSupplyPerGood - lower bound on the supply of a good.
   * @param maxSupplyPerGood - upper bound on the supply of a good.
   * @param numberBidders - number of bidders.
   * @param minDemandPerBidder - lower bound on the demand of a bidder.
   * @param maxDemandPerBidder - upper bound on the demand of a bidder.
   * @param minReward - lower bound on the reward of a bidder.
   * @param maxReward - upper bound on the reward of a bidder.
   * @param probabilityConnection - probability of a connection.
   * @return a Market object.
   * @throws BidderCreationException in case a bidder could not be created.
   * @throws GoodsCreationException in case a good could not be created.
   * @throws MarketCreationException 
   */
  public static Market<Goods, Bidder<Goods>> randomMarket(int numberGoods, int minSupplyPerGood, int maxSupplyPerGood, int numberBidders, int minDemandPerBidder, int maxDemandPerBidder, double minReward, double maxReward, double probabilityConnection) throws BidderCreationException, GoodsCreationException, MarketCreationException {
    
    Random generator = new Random();
    // Create Goods.
    ArrayList<Goods> goods = new ArrayList<Goods>();
    for (int i = 0; i < numberGoods; i++) {
      goods.add(new Goods(generator.nextInt((maxSupplyPerGood - minSupplyPerGood) + 1) + minSupplyPerGood));
    }
    // Create Bidders.
    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    for (int j = 0; j < numberBidders; j++) {
      // Create Demand Set.
      HashSet<Goods> bDemandSet = new HashSet<Goods>();
      for(int i = 0; i < numberGoods; i++){
        if(generator.nextDouble() <= probabilityConnection){
          bDemandSet.add(goods.get(i));
        }
      }
      bidders.add(new Bidder<Goods>( 
          generator.nextInt((maxDemandPerBidder - minDemandPerBidder) + 1) + minDemandPerBidder, 
          generator.nextDouble() * (maxReward - minReward) + minReward,
          bDemandSet));
    }
    return new Market<Goods, Bidder<Goods>>(goods, bidders);
  }
  
  /**
   * Shortcut method to create a random market by just providing the number of
   * goods, bidders, and probability of connection. Other parameters are given
   * by default (static members of this class).
   * 
   * @param numberGoods - number of goods.
   * @param numberBidders - number of bidders.
   * @param probabilityConnections - probability of a connection.
   * @return a Market object.
   * @throws BidderCreationException in case a bidder could not be created.
   * @throws GoodsCreationException  in case a good could not be created.
   * @throws MarketCreationException 
   */
  public static Market<Goods, Bidder<Goods>> randomMarket(int numberGoods, int numberBidders, double probabilityConnections) throws BidderCreationException, GoodsCreationException, MarketCreationException {
    return RandomMarketFactory.randomMarket(numberGoods,
        RandomMarketFactory.defaultMinSupplyPerBidder,
        RandomMarketFactory.defaultMaxSupplyPerGood, numberBidders,
        RandomMarketFactory.defaultMinDemandPerBidder,
        RandomMarketFactory.defaultMaxDemandPerBidder,
        RandomMarketFactory.defaultMinReward,
        RandomMarketFactory.defaultMaxReward, probabilityConnections);
  }
  
  /**
   * Generate over demanded markets.
   * 
   * @param numberGoods - number of goods.
   * @param numberBidders - number of bidders.
   * @param probabilityConnection - probability of a connection.
   * @param b - supply to demand ratio.
   * @return a Market object.
   * @throws BidderCreationException in case a bidder could not be created.
   * @throws GoodsCreationException in case a good could not be created.
   * @throws MarketCreationException 
   */
  public static Market<Goods, Bidder<Goods>> generateOverDemandedMarket(int numberGoods, int numberBidders, double probabilityConnection, int b) throws BidderCreationException, GoodsCreationException, MarketCreationException {
    return RandomKMarket(numberGoods, numberBidders, probabilityConnection, b);
  }

  /**
   * Generate over supplied markets
   * 
   * @param numberGoods - number of goods.
   * @param numberBidders - number of bidders.
   * @param probabilityConnection - probability of a connection.
   * @param b - supply to demand ratio.
   * @return a Market object.
   * @throws BidderCreationException in case a bidder could not be created.
   * @throws GoodsCreationException in case a good could not be created.
   * @throws MarketCreationException 
   */
  public static Market<Goods, Bidder<Goods>> generateOverSuppliedMarket(int numberGoods, int numberBidders, double probabilityConnection, int b) throws BidderCreationException, GoodsCreationException, MarketCreationException {
    return MarketFactory.transposeMarket(RandomKMarket(numberBidders, numberGoods, probabilityConnection, b));
  }
  
  /**
   * Generate a random market with a fixed supply to demand ratio.
   * 
   * @param numberGoods - number of goods.
   * @param numberBidders - number of bidders.
   * @param probabilityConnection - probability of a connection.
   * @param b - supply to demand ratio.
   * @return a Market object.
   * @throws BidderCreationException in case a bidder could not be created.
   * @throws GoodsCreationException 
   * @throws MarketCreationException 
   */
  public static Market<Goods, Bidder<Goods>> RandomKMarket(int numberGoods, int numberBidders, double probabilityConnection, int b) throws BidderCreationException, GoodsCreationException, MarketCreationException {
    Random generator = new Random();
    
    int[] bidderConnectedToGood = new int[numberGoods];
    boolean[][] connections = new boolean[numberGoods][numberBidders];
    for (int i = 0; i < numberGoods; i++) {
      for (int j = 0; j < numberBidders; j++) {
        connections[i][j] = generator.nextDouble() <= probabilityConnection;
        if (connections[i][j]) {
          bidderConnectedToGood[i]++;
        }
      }
    }
    // Generate supply and demand to maintain the ratio
    // Each bidder demands one initially. 
    // This is so that we don't have bidders demanding 0 items.
    int totalDemand = numberBidders; 
    int[] finalDemands = new int[numberBidders];
    for (int j = 0; j < numberBidders; j++) {
      finalDemands[j] = 1;
    }
    int[] finalSupply = new int[numberGoods];
    int x = 0, k = 0;
    int totalSupply = 0;
    for (int i = 0; i < numberGoods; i++) {
      finalSupply[i] = bidderConnectedToGood[i] + generator.nextInt((RandomMarketFactory.defaultMaxSupplyPerGood - RandomMarketFactory.defaultMinSupplyPerBidder) + 1) + RandomMarketFactory.defaultMinSupplyPerBidder;
      // System.out.println("finalSupply["+i+"] = " + finalSupply[i]);
      totalSupply += finalSupply[i];
      x = totalSupply * b - totalDemand;
      totalDemand += x;
      ArrayList<Integer> demands = generateRandomIntegerFixedSum(bidderConnectedToGood[i], x);
      k = 0;
      for (int j = 0; j < numberBidders; j++) {
        if (connections[i][j]) {
          finalDemands[j] += demands.get(k);
          k++;
        }
      }
    }
    
    ArrayList<Goods> goods = new ArrayList<Goods>();
    for (int i = 0; i < numberGoods; i++) {
      goods.add(new Goods(finalSupply[i]));
    }
    
    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();

    for (int j = 0; j < numberBidders; j++) {
      HashSet<Goods> bDemandSet = new HashSet<Goods>();
      for(int i=0; i< numberGoods; i ++){
        if(connections[i][j]){
          bDemandSet.add(goods.get(i));
        }
      }
      bidders.add(
          new Bidder<Goods>(finalDemands[j],
          generator.nextDouble() * (RandomMarketFactory.defaultMaxReward - RandomMarketFactory.defaultMinReward) + RandomMarketFactory.defaultMinReward,
          bDemandSet));
    }

    return new Market<Goods, Bidder<Goods>>(goods, bidders);
  }
  
  /**
   * This function takes a pair of positive integers (n,x) and returns a list of
   * n random integers that add up to x.
   * 
   * @param n - a positive integer.
   * @param x - a positive integer.
   * @return an ArrayList of n random integers that add up to x.
   */
  public static ArrayList<Integer> generateRandomIntegerFixedSum(int n, int x) {
    if (x == 1 || x <= 0) {
      ArrayList<Integer> listNumbers = new ArrayList<Integer>(n);
      listNumbers.add(1);
      for (int i = 0; i < n - 1; i++) {
        listNumbers.add(0);
      }
      return listNumbers;
    }
    n++;
    Random generator = new Random();
    ArrayList<Integer> listNumbers = new ArrayList<Integer>(n);
    int max = x - 1;
    int min = 1;
    // System.out.println("n = " + n +", x = " + x);
    for (int i = 0; i < n - 2; i++) {
      listNumbers.add(generator.nextInt((max - min) + 1) + min);
    }
    listNumbers.add(0);
    listNumbers.add(x);
    Collections.sort(listNumbers);
    ArrayList<Integer> finalList = new ArrayList<Integer>(n - 1);
    for (int i = 0; i < n - 1; i++) {
      finalList.add(listNumbers.get(i + 1) - listNumbers.get(i));
    }
    // System.out.println(finalList);
    return finalList;
  }

}
