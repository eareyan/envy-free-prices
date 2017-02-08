package test.algorithms;

import org.junit.Test;

import singleminded.structures.SingleMindedMarket;
import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.factory.SingleMindedMarketFactory;
import allocations.optimal.WelfareMaxAllocationILP;

public class WelfareMaxAllocationILPTest {

  @Test
  public void testSolve() throws Exception {
    SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarketFactory.uniformRewardRandomSingleMindedMarket(3, 3, 1);
    System.out.println(market);
    MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> egalitarianMaxAlloc = new WelfareMaxAllocationILP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>().Solve(market);
    egalitarianMaxAlloc.printAllocation();
    System.out.println((double) egalitarianMaxAlloc.getNumberOfWinners() / 3);

  }

}
