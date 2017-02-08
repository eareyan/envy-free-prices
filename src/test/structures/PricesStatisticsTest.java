package test.structures;

import static org.junit.Assert.assertEquals;

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
import test.SingleMindedMarkets;

public class PricesStatisticsTest {

  @Test
  public void testGetNumberOfWinnersRatio() throws GoodsCreationException, BidderCreationException, MarketCreationException, MarketAllocationException, MarketOutcomeException {
    SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarkets.singleMinded0();
    UnlimitedSupplyApproximation us = new UnlimitedSupplyApproximation(market);
    PricesStatistics<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> ps = us.Solve();
    assertEquals(ps.getNumberOfWinnersRatio(3), 1.0 / 3.0, 0.00000001);
  }

}
