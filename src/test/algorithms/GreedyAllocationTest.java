package test.algorithms;

import static org.junit.Assert.fail;

import org.junit.Test;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.factory.RandomMarketFactory;
import allocations.greedy.GreedyAllocation;
import allocations.greedy.GreedyAllocationFactory;

public class GreedyAllocationTest {

  @Test
  public void test() throws Exception {
    for (int n = 1; n < 15; n++) {
      for (int m = 1; m < 15; m++) {
        for (int p = 1; p < 5; p++) {
          Market<Goods, Bidder<Goods>> market = RandomMarketFactory.generateUniformRewardOverSuppliedMarket(n, m, p * 0.25, 1);
          //GreedyAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> greedyAllocAlgo = GreedyAllocationFactory.GreedyAllocation();
          GreedyAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> greedyMaxAllocAlgo = GreedyAllocationFactory.GreedyMaxBidderAllocation();
          //System.out.println(market);
          //MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> greedyAllocation = greedyAllocAlgo.Solve(market);
          // System.out.println("Greedy Allocation:");
          // greedyAllocation.printAllocation();
          MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> greedyMaxAllocation = greedyMaxAllocAlgo.Solve(market);
          // System.out.println("Greedy Max Allocation had: " + greedyMaxAllocation.getNumberOfWinners() + " winner.");
          // greedyMaxAllocation.printAllocation();
          if (greedyMaxAllocation.getNumberOfWinners() > 1) {
            fail("The greedyMaxAllocation can allocate at most one bidder.");
          }
        }
      }
    }
  }

}
