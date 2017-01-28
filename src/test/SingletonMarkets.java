package test;

import static org.junit.Assert.assertEquals;
import ilog.concert.IloException;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Test;

import singleton.algorithms.SingletonPricingLP;
import singleton.structures.SingletonMarket;
import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.exceptions.AllocationException;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketCreationException;
import structures.exceptions.MarketOutcomeException;
import algorithms.pricing.error.PrincingAlgoException;
import allocations.greedy.GreedyAllocation;
import allocations.objectivefunction.SingleStepObjectiveFunction;

import com.google.common.collect.HashBasedTable;

public class SingletonMarkets {

  @SuppressWarnings("serial")
  public static SingletonMarket<Goods, Bidder<Goods>> singletonMarket0() throws BidderCreationException, GoodsCreationException, MarketCreationException {
    Goods good0 = new Goods(1);
    Goods good1 = new Goods(1);
    Goods good2 = new Goods(1);
    Goods good3 = new Goods(1);
    Goods good4 = new Goods(1);
    ArrayList<Goods> goods = new ArrayList<Goods>();
    goods.add(good0);
    goods.add(good1);
    goods.add(good2);
    goods.add(good3);
    goods.add(good4);
        
    Bidder<Goods> bidder0 = new Bidder<Goods>(1, 5.35, new HashSet<Goods>(){{add(good1); add(good2);}});
    Bidder<Goods> bidder1 = new Bidder<Goods>(1, 3.39, new HashSet<Goods>());
    Bidder<Goods> bidder2 = new Bidder<Goods>(1, 4.44, new HashSet<Goods>());
    Bidder<Goods> bidder3 = new Bidder<Goods>(1, 2.96, new HashSet<Goods>(){{add(good1);}});
    Bidder<Goods> bidder4 = new Bidder<Goods>(1, 1.36, new HashSet<Goods>(){{add(good2);}});
    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();

    bidders.add(bidder0);
    bidders.add(bidder1);
    bidders.add(bidder2);
    bidders.add(bidder3);
    bidders.add(bidder4);
    
    return new SingletonMarket<Goods, Bidder<Goods>>(new Market<Goods, Bidder<Goods>>(goods, bidders));
  }
  
  @Test
  public void testSingletonMarket0() throws BidderCreationException, MarketCreationException, AllocationException, GoodsException, MarketAllocationException, IloException, MarketOutcomeException, PrincingAlgoException {
    SingletonMarket<Goods, Bidder<Goods>> market = SingletonMarkets.singletonMarket0();
    System.out.println(market);
    MarketAllocation<SingletonMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> ga = new GreedyAllocation<SingletonMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>().Solve(market);
    ga.printAllocation();
    SingletonPricingLP<SingletonMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> splp = new SingletonPricingLP<SingletonMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(ga);
    splp.getStatistics().getMarketOutcome().printPrices();
    System.out.println(splp.getStatistics().getSellerRevenue());
    System.out.println(splp.getStatistics().listOfEnvyBidders());
    assertEquals(splp.getStatistics().getSellerRevenue() , 2.72, 0.00000001);
    
    HashBasedTable<Goods, Bidder<Goods>, Integer> alloc = HashBasedTable.create();
    for(Goods good : market.getGoods())
      for(Bidder<Goods> bidder: market.getBidders())
        alloc.put(good, bidder, 0);
    alloc.put(market.getGoods().get(1), market.getBidders().get(0), 1);
    alloc.put(market.getGoods().get(2), market.getBidders().get(3), 1);
    
    MarketAllocation<SingletonMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> allocation = new MarketAllocation<SingletonMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(market, alloc, new SingleStepObjectiveFunction());
    allocation.printAllocation();
    splp = new SingletonPricingLP<SingletonMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(allocation);
    splp.getStatistics().getMarketOutcome().printPrices();
    System.out.println(splp.getStatistics().getSellerRevenue());
    System.out.println(splp.getStatistics().listOfEnvyBidders());
    assertEquals(splp.getStatistics().getSellerRevenue() , 5.92, 0.00000001);

  }
}
