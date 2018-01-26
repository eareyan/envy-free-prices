package test.structures;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.comparators.BiddersComparatorByRToSqrtIRatio;
import structures.factory.RandomMarketFactory;

public class BiddersComparatorByRToSqrtIRatioTest {

  @Test
  public void test() throws Exception {
    for (int n = 1; n < 5; n++) {
      for (int m = 1; m < 5; m++) {
        for (int p = 1; p < 5; p++) {
          Market<Goods, Bidder<Goods>> market = RandomMarketFactory.generateUniformRewardOverSuppliedMarket(n, m, p * 0.25, 1);
          ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>(market.getBidders());
          Collections.sort(bidders, new BiddersComparatorByRToSqrtIRatio<Goods, Bidder<Goods>>());
          int i = 0;
          for (Bidder<Goods> b : bidders) {
            if (i < bidders.size() - 1) {
              if ((b.getReward() / Math.sqrt(b.getDemand())) < (bidders.get(i + 1).getReward() / Math.sqrt(bidders.get(i + 1).getDemand()))) {
                fail("Wrong ordering!");
              }
            }
            i++;
            // System.out.println(b + ", ratio = " + (b.getReward() / Math.sqrt(b.getDemand())));
          }
        }
      }
    }
  }

}
