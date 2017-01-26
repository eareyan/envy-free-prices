package test.algorithms;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketOutcome;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketCreationException;
import structures.exceptions.MarketOutcomeException;
import structures.factory.RandomMarketFactory;
import test.SizeInterchangeableMarkets;
import algorithms.pricing.SimplePricing;

public class SimplePricingTest {
  
  @Test
  public void isSatisfiableTest() throws GoodsCreationException, BidderCreationException, MarketCreationException {
    Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market0();
    Map<Goods, Integer> remainingSupply = new HashMap<Goods, Integer>();
    for (Goods good : market.getGoods()) {
      remainingSupply.put(good, good.getSupply());
    }
    SimplePricing sp = new SimplePricing(market);
    for (Bidder<Goods> bidder : market.getBidders())
      assertTrue(sp.isSatisfiable(0.0, bidder, remainingSupply));
    assertFalse(sp.isSatisfiable(12.5001, market.getBidders().get(0), remainingSupply));
    assertTrue(sp.isSatisfiable(12.5001, market.getBidders().get(1), remainingSupply));
    assertFalse(sp.isSatisfiable(12.5001, market.getBidders().get(2), remainingSupply));

    assertFalse(sp.isSatisfiable(45.00001 / 3, market.getBidders().get(1), remainingSupply));
  }

  @Test
  public void computeOutcomeTest() throws GoodsCreationException, BidderCreationException, MarketCreationException, MarketAllocationException,
      MarketOutcomeException {
    Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market1();
    System.out.println(market);
    SimplePricing sp = new SimplePricing(market);
    for (Bidder<Goods> bidder : market.getBidders()) {
      MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> outcome = sp.computeOutcome(bidder.getReward() / bidder.getDemand());
      outcome.getMarketAllocation().printAllocation();
      outcome.printPrices();
      System.out.println(outcome.sellerRevenue());
    }
  }

  @Test
  public void solveTest() throws Exception {
    for (int n = 1; n < 15; n++) {
      for (int m = 1; m < 15; m++) {
        for (int p = 1; p <= 4; p++) {
          Market<Goods, Bidder<Goods>> market = RandomMarketFactory.randomUniformRewardMarket(n, m, 0.25 * p);
          SimplePricing sp = new SimplePricing(market);
          PricesStatistics<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> z = sp.Solve();
          for (Bidder<Goods> bidder : market.getBidders()) {            
            if (z.listOfEnvyBidders() != null) {
              if (z.getMarketOutcome().getMarketAllocation().allocationToBidder(bidder) > 0) {
                assertFalse(z.listOfEnvyBidders().contains(bidder));
                //System.out.println(market);
                //z.getMarketOutcome().getMarketAllocation().printAllocation();
                //z.getMarketOutcome().printPrices();
                //System.out.println(z.listOfEnvyBidders());
              }
            }
          }
        }
      }
    }
  }
}
