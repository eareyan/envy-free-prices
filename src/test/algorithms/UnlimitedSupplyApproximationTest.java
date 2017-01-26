package test.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import singleminded.algorithms.UnlimitedSupplyApproximation;
import singleminded.structures.SingleMindedMarket;
import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketCreationException;
import structures.exceptions.MarketOutcomeException;
import structures.factory.SingleMindedMarketFactory;
import test.SingleMindedMarkets;

import com.google.common.collect.ImmutableList;

public class UnlimitedSupplyApproximationTest {

  @Test
  public void testSolve0() throws GoodsCreationException, BidderCreationException, MarketCreationException, MarketAllocationException, MarketOutcomeException {
    SingleMindedMarket<Goods, Bidder<Goods>> singleMindedMarket = SingleMindedMarkets.singleMinded4();
    PricesStatistics<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> stats = new UnlimitedSupplyApproximation(singleMindedMarket).Solve();
    assertEquals(stats.getMarketOutcome().sellerRevenue(), 6.57, 0.000000000000001);

    singleMindedMarket = SingleMindedMarkets.singleMinded0();
    stats = new UnlimitedSupplyApproximation(singleMindedMarket).Solve();
    assertEquals(stats.getMarketOutcome().sellerRevenue(), 8.99, 0.000000000000001);
  }

  @Test
  public void testSolve() throws Exception {

    for (int n = 1; n < 10; n++) {
      for (int m = 1; m < 10; m++) {
        for (int k = 1; k <= n; k++) {
          SingleMindedMarket<Goods, Bidder<Goods>> singleMindedMarket = SingleMindedMarketFactory.createUniformRewardRandomSingleMindedMarket(n, m, k);
          // System.out.println(singleMindedMarket);
          UnlimitedSupplyApproximation usa = new UnlimitedSupplyApproximation(singleMindedMarket);
          PricesStatistics<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> stats = usa.Solve();
          ImmutableList<Bidder<Goods>> list = stats.listOfEnvyBidders();
          boolean someAllocated = false;
          for (Bidder<Goods> bidder : singleMindedMarket.getBidders()) {
            if (stats.getMarketOutcome().getMarketAllocation().allocationToBidder(bidder) > 0) {
              someAllocated = true;
              if (list != null && list.contains(bidder)) {
                fail("Winners are suppose to be envy-free");
              }
            }
          }
          if (!someAllocated) {
            System.out.println(singleMindedMarket);
            stats.getMarketOutcome().getMarketAllocation().printAllocation();
            stats.getMarketOutcome().printPrices();
            fail("The algorithm is suppose to allocate at least one bidder");
          }
        }
      }
    }
  }

}
