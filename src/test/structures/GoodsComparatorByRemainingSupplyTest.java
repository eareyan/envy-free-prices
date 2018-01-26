package test.structures;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.comparators.GoodsComparatorByRemainingSupply;
import structures.factory.RandomMarketFactory;

public class GoodsComparatorByRemainingSupplyTest {

  @Test
  public void test() throws Exception {

    for (int n = 1; n < 5; n++) {
      for (int m = 1; m < 5; m++) {
        for (int p = 1; p < 5; p++) {
          Market<Goods, Bidder<Goods>> market = RandomMarketFactory.generateUniformRewardOverSuppliedMarket(n, m, p * 0.25, 1);
          ArrayList<Goods> goods = new ArrayList<Goods>(market.getGoods());
          for (Goods good : goods) {
            good.setRemainingSupply(good.getSupply());
          }
          // Test ascending ordering
          Collections.sort(goods, new GoodsComparatorByRemainingSupply<Goods>(GoodsComparatorByRemainingSupply.Order.ascending));
          int i = 0;
          for (Goods g : goods) {
            if (i < goods.size() - 1) {
              if (g.getRemainingSupply() > goods.get(i + 1).getSupply()) {
                fail("Ascending ordering fails");
              }
            }
            i++;
          }
          // Test descending ordering
          Collections.sort(goods, new GoodsComparatorByRemainingSupply<Goods>(GoodsComparatorByRemainingSupply.Order.descending));
          i = 0;
          for (Goods g : goods) {
            if (i < goods.size() - 1) {
              if (g.getRemainingSupply() < goods.get(i + 1).getSupply()) {
                fail("Descending ordering fails");
              }
            }
            i++;
          }
        }
      }
    }
  }
}
