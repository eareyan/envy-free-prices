package test.structures;

import static org.junit.Assert.assertEquals;
import ilog.concert.IloException;

import org.junit.Test;

import singleminded.algorithms.SingleMindedGreedyAllocation;
import singleminded.structures.SingleMindedMarket;
import structures.Bidder;
import structures.Goods;
import structures.MarketAllocation;
import structures.exceptions.AllocationException;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketCreationException;
import test.SingleMindedMarkets;
import allocations.error.AllocationAlgoException;

public class MarketAllocationTest {

  @Test
  public void testMarginalValue() throws MarketAllocationException, BidderCreationException, MarketCreationException, IloException, AllocationAlgoException, AllocationException, GoodsException {
    SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarkets.singleMinded0();
    SingleMindedGreedyAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> smga = new SingleMindedGreedyAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
    MarketAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> allocation = smga.Solve(market);
    assertEquals(allocation.marginalValue(market.getBidders().get(0)), 0.0, 0.00000001);
    assertEquals(allocation.marginalValue(market.getBidders().get(1)), 3.42, 0.00000001);
    assertEquals(allocation.marginalValue(market.getBidders().get(2)), 8.99, 0.00000001);

    market = SingleMindedMarkets.singleMinded1();
    smga = new SingleMindedGreedyAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
    allocation = smga.Solve(market);
    assertEquals(allocation.marginalValue(market.getBidders().get(0)), 4.39, 0.00000001);
    assertEquals(allocation.marginalValue(market.getBidders().get(1)), 0.0, 0.00000001);
    assertEquals(allocation.marginalValue(market.getBidders().get(2)), 9.79, 0.00000001);

    market = SingleMindedMarkets.singleMinded2();
    smga = new SingleMindedGreedyAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
    allocation = smga.Solve(market);
    assertEquals(allocation.marginalValue(market.getBidders().get(0)), 0.0, 0.00000001);
    assertEquals(allocation.marginalValue(market.getBidders().get(1)), 6.61, 0.00000001);
    assertEquals(allocation.marginalValue(market.getBidders().get(2)), 0.0, 0.00000001);

    market = SingleMindedMarkets.singleMinded3();
    smga = new SingleMindedGreedyAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
    allocation = smga.Solve(market);
    assertEquals(allocation.marginalValue(market.getBidders().get(0)), 10.0, 0.00000001);
    assertEquals(allocation.marginalValue(market.getBidders().get(1)), 0.0, 0.00000001);
    assertEquals(allocation.marginalValue(market.getBidders().get(2)), 4.0, 0.00000001);

    market = SingleMindedMarkets.singleMinded4();
    smga = new SingleMindedGreedyAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
    allocation = smga.Solve(market);
    assertEquals(allocation.marginalValue(market.getBidders().get(0)), 6.57, 0.00000001);

  }

  @Test
  public void testValueAndWinnners() throws BidderCreationException, MarketCreationException, IloException, AllocationAlgoException, AllocationException, GoodsException, MarketAllocationException {
    SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarkets.singleMinded0();
    SingleMindedGreedyAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> smga = new SingleMindedGreedyAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
    MarketAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> allocation = smga.Solve(market);
    assertEquals(allocation.getValue(), market.getBidders().get(1).getReward() + market.getBidders().get(2).getReward(), 0.00000001);

    market = SingleMindedMarkets.singleMinded1();
    smga = new SingleMindedGreedyAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
    allocation = smga.Solve(market);
    assertEquals(allocation.getValue(), market.getBidders().get(0).getReward() + market.getBidders().get(2).getReward(), 0.00000001);
    assertEquals(allocation.getNumberOfWinners(), 2);

    market = SingleMindedMarkets.singleMinded2();
    smga = new SingleMindedGreedyAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
    allocation = smga.Solve(market);
    assertEquals(allocation.getValue(), market.getBidders().get(1).getReward(), 0.00000001);
    assertEquals(allocation.getNumberOfWinners(), 1);

    market = SingleMindedMarkets.singleMinded3();
    smga = new SingleMindedGreedyAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
    allocation = smga.Solve(market);
    assertEquals(allocation.getValue(), market.getBidders().get(0).getReward() + market.getBidders().get(2).getReward(), 0.00000001);
    assertEquals(allocation.getNumberOfWinners(), 2);

    market = SingleMindedMarkets.singleMinded4();
    smga = new SingleMindedGreedyAllocation<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
    allocation = smga.Solve(market);
    assertEquals(allocation.getValue(), market.getBidders().get(0).getReward(), 0.00000001);
    assertEquals(allocation.getNumberOfWinners(), 1);

  }

}
