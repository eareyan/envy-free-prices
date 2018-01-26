package test.algorithms;

import static org.junit.Assert.fail;
import ilog.concert.IloException;

import org.junit.Test;

import singleton.structures.SingletonMarket;
import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketOutcome;
import structures.exceptions.AllocationException;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketCreationException;
import structures.exceptions.MarketOutcomeException;
import structures.factory.SingletonMarketFactory;
import test.SizeInterchangeableMarkets;
import waterfall.Waterfall;
import algorithms.pricing.RestrictedEnvyFreePricesLP;
import algorithms.pricing.error.PrincingAlgoException;
import algorithms.pricing.reserveprices.RevMaxHeuristic;
import allocations.error.AllocationAlgoException;
import allocations.greedy.GreedyAllocation;
import allocations.greedy.GreedyAllocationFactory;
import allocations.optimal.WelfareMaxAllocationILP;

public class RevMaxHeuristicTest<M extends Market<G, B>, G extends Goods, B extends Bidder<G>> {

  @Test
  public void testSearchMetaHeuristicOnSingleton() throws Exception {
    for (int n = 1; n < 8; n++) {
      for (int m = 1; m < 8; m++) {
        for (int p = 1; p <= 4; p++) {
          // SingletonMarket<Goods, Bidder<Goods>> market = SingletonMarketFactory.uniformRewardSingletonRandomMarket(n, m, p*0.25);
          SingletonMarket<Goods, Bidder<Goods>> market = SingletonMarketFactory.elitistRewardSingletonRandomMarket(n, m, p * 0.25);
          GreedyAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> ga = GreedyAllocationFactory.GreedyAllocation();
          PricesStatistics<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> stat = new RestrictedEnvyFreePricesLP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(
              ga.Solve(market)).getStatistics();
          PricesStatistics<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> rmhStats = new RevMaxHeuristic(market, ga).getStatistics();

          if (stat.getSellerRevenue() > rmhStats.getSellerRevenue()) {
            System.out.println(market);
            stat.getMarketOutcome().getMarketAllocation().printAllocation();
            stat.getMarketOutcome().printPrices();
            System.out.println("Revenue of LP = " + stat.getSellerRevenue());
            System.out.println("EF violations = " + stat.listOfEnvyBidders() + "\n");
            rmhStats.getMarketOutcome().getMarketAllocation().printAllocation();
            rmhStats.getMarketOutcome().printPrices();
            System.out.println("Revenue of rev Max = " + rmhStats.getSellerRevenue());
            System.out.println("EF violations = " + rmhStats.listOfEnvyBidders());
            fail("The revenue max heuristic should be as good as the LP");
          }
        }
      }
    }
  }

  @Test
  public void test() throws BidderCreationException, MarketCreationException, AllocationException, GoodsException, MarketAllocationException,
      AllocationAlgoException, IloException, PrincingAlgoException, MarketOutcomeException {
    GreedyAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> greedy = GreedyAllocationFactory.GreedyAllocation();
    WelfareMaxAllocationILP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> optimal = new WelfareMaxAllocationILP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();

    //Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market0();
    //Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market1();
    //Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market2();
    //Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market3();
    //Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market4();
    //Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market5();
    //Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market6();
    Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market7();

    Waterfall<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> waterfall = new Waterfall<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(market);

    MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> x = greedy.Solve(market);
    x.printAllocation();

    MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> y = optimal.Solve(market);
    y.printAllocation();

    MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> z = waterfall.Solve(null);
    z.printAllocation();

    System.out.println("----");
    RevMaxHeuristic a = new RevMaxHeuristic(market, greedy);
    MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> sol = a.Solve();
    sol.getMarketAllocation().printAllocation();
    sol.printPrices();
    System.out.println(sol.sellerRevenue());

    RevMaxHeuristic b = new RevMaxHeuristic(market, optimal);
    MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> sol1 = b.Solve();
    sol1.getMarketAllocation().printAllocation();
    sol1.printPrices();
    System.out.println(sol1.sellerRevenue());
    
    RevMaxHeuristic c = new RevMaxHeuristic(market, waterfall);
    MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> sol2 = c.Solve();
    sol2.getMarketAllocation().printAllocation();
    sol2.printPrices();
    System.out.println(sol2.sellerRevenue());
}

}
