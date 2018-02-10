package test.algorithms;

import static org.junit.Assert.fail;
import ilog.concert.IloException;

import java.util.ArrayList;

import org.junit.Test;

import statistics.PricesStatistics;
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
import structures.factory.RandomMarketFactory;
import test.SizeInterchangeableMarkets;
import algorithms.pricing.RestrictedEnvyFreePricesLP;
import algorithms.pricing.RestrictedEnvyFreePricesLPSolution;
import algorithms.pricing.error.PrincingAlgoException;
import algorithms.pricing.helper.Output;
import algorithms.pricing.helper.SupplyHelper;
import algorithms.pricing.helper.SupplyHelperList;
import allocations.greedy.GreedyAllocationFactory;

public class RestrictedEnvyFreePricesLPTest {

  @Test
  public void testCreateLP() throws Exception {
    for (int n = 1; n < 10; n++) {
      for (int m = 1; m < 10; m++) {
        for (int p = 1; p <= 4; p++) {
          Market<Goods, Bidder<Goods>> market = RandomMarketFactory.randomUniformRewardMarket(n, m, p * 0.25);
          MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> ga = GreedyAllocationFactory.GreedyAllocation().Solve(market);
          PricesStatistics<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> refp = new RestrictedEnvyFreePricesLP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(
              ga).getStatistics();
          if (refp.listOfEnvyBidders() == null) {
            fail("Null list");
          }
        }
      }
    }
  }

  @SuppressWarnings("serial")
  @Test
  public void test() throws BidderCreationException, MarketCreationException, AllocationException, GoodsException, MarketAllocationException,
      PrincingAlgoException, IloException, MarketOutcomeException {
    // Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market0();
    // Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market1();
    // Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market2();
    // Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market3();
    // Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market4();
    // Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market5();
    // Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market6();
    // Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market7();
    // Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market8();
    // Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market9();
    Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market10();
    System.out.println(market);
    MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> ga = GreedyAllocationFactory.GreedyAllocation().Solve(market);
    ga.printAllocation();
    RestrictedEnvyFreePricesLP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> refp = new RestrictedEnvyFreePricesLP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(
        ga);
    refp.createLP();
    RestrictedEnvyFreePricesLPSolution<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> x = refp.Solve();
    x.printPrices();
    printCombinations(market.getBidders().get(0));

    SupplyHelper s1 = new SupplyHelper(market.getGoods().get(1), 13);
    SupplyHelper s2 = new SupplyHelper(market.getGoods().get(1), 1);

    SupplyHelperList l1 = new SupplyHelperList(new ArrayList<SupplyHelper>() {
      {
        add(s2);
        add(s1);
      }
    });
    SupplyHelperList l2 = new SupplyHelperList(new ArrayList<SupplyHelper>() {
      {
        add(s2);
        add(s1);
      }
    });
    System.out.println(l1.equals(l2));
    System.out.println(l1.getTotalSupply());
  }

  public static void printCombinations(Bidder<Goods> bidder) throws GoodsCreationException {
    System.out.println(bidder);
    System.out.println(bidder.getDemandSet());
    RestrictedEnvyFreePricesLPTest test = new RestrictedEnvyFreePricesLPTest();
    ArrayList<SupplyHelper> innerList = new ArrayList<SupplyHelper>();
    for (Goods g : bidder.getDemandSet()) {
      System.out.println(g.getSupply());
      innerList.add(new SupplyHelper(g, 0));
    }
    innerList.add(new SupplyHelper(new Goods(35), 0));
    System.out.println("innerList = " + innerList);
    SupplyHelperList list = new SupplyHelperList(innerList);
    System.out.println("supplyList = " + list);
    Output output = new Output();
    test.recursion(output, 2, list);
    System.out.println("output = " + output);
  }

  public void recursion(Output output, int n, SupplyHelperList list) {
    if (list.getList().size() == 0) {
      return;
    }
    if (list.getTotalSupply() == n) {
      output.addList(list);
    }
    int i = 0;
    for (SupplyHelper s : list.getList()) {
      if (s.getSupply() < s.getGood().getSupply()) {
        ArrayList<SupplyHelper> x = new ArrayList<SupplyHelper>(list.getList());
        x.set(i, new SupplyHelper(s.getGood(), s.getSupply() + 1));
        SupplyHelperList newList = new SupplyHelperList(x);
        if (!output.containsList(newList)) {
          recursion(output, n, newList);
        }
      }
      i++;
    }
  }

}
