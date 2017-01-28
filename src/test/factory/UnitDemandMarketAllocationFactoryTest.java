package test.factory;

import org.junit.Test;

import singleton.structures.SingletonMarket;
import structures.Bidder;
import structures.Goods;
import structures.factory.SingletonMarketFactory;
import structures.factory.UnitDemandMarketAllocationFactory;
import util.Printer;

public class UnitDemandMarketAllocationFactoryTest {

  @Test
  public void testGetValuationMatrixFromMarket() throws Exception {
    SingletonMarket<Goods, Bidder<Goods>> singletonMarket = SingletonMarketFactory.elitistRewardSingletonRandomMarket(5, 3, 0.95);
    System.out.println(singletonMarket);
    Printer.printMatrix(UnitDemandMarketAllocationFactory.getValuationMatrixFromMarket(singletonMarket));
  }

}
