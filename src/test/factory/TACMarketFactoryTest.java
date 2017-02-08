package test.factory;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;
import structures.exceptions.MarketCreationException;
import structures.factory.TACMarketFactory;
import adx.exceptions.AdXException;
import adx.structures.MarketSegment;

public class TACMarketFactoryTest {

  @Test
  public void testRandomTACMarket() throws AdXException, GoodsCreationException, BidderCreationException, MarketCreationException {
    for (int m = 1; m < 2; m++) {
      double total = 0.0;
      for (int t = 1; t < 10; t++) {
        total += TACMarketFactory.RandomTACMarket_Old(m).getSupplyToDemandRatio();
      }
      System.out.println(m + " = " + (total / 10.0));
    }
  }
  
  @Test
  public void testRandomTACMarket2() throws GoodsCreationException, AdXException, BidderCreationException, MarketCreationException {
    Market<Goods, Bidder<Goods>> market = TACMarketFactory.RandomTACMarket_Old(10);
    System.out.println(market);
  }
  
  @Test
  public void testSampler() throws GoodsCreationException, AdXException, BidderCreationException, MarketCreationException {
    for (int m = 1; m < 2; m++) {
      double total = 0.0;
      for (int t = 1; t < 10; t++) {
        total += TACMarketFactory.RandomTACMarket(m).getSupplyToDemandRatio();
      }
      System.out.println(m + " = " + (total / 10.0));
    }
  }
  
  @Test
  public void testTranslateMarketSegment() {
    assertEquals(TACMarketFactory.translateMarketSegment(tau.tac.adx.report.adn.MarketSegment.compundMarketSegment3(tau.tac.adx.report.adn.MarketSegment.MALE, tau.tac.adx.report.adn.MarketSegment.YOUNG, tau.tac.adx.report.adn.MarketSegment.LOW_INCOME)) , MarketSegment.MALE_YOUNG_LOW_INCOME);
    assertEquals(TACMarketFactory.translateMarketSegment(tau.tac.adx.report.adn.MarketSegment.compundMarketSegment3(tau.tac.adx.report.adn.MarketSegment.MALE, tau.tac.adx.report.adn.MarketSegment.YOUNG, tau.tac.adx.report.adn.MarketSegment.HIGH_INCOME)) , MarketSegment.MALE_YOUNG_HIGH_INCOME);
    assertEquals(TACMarketFactory.translateMarketSegment(tau.tac.adx.report.adn.MarketSegment.compundMarketSegment3(tau.tac.adx.report.adn.MarketSegment.MALE, tau.tac.adx.report.adn.MarketSegment.OLD, tau.tac.adx.report.adn.MarketSegment.LOW_INCOME)) , MarketSegment.MALE_OLD_LOW_INCOME);
    assertEquals(TACMarketFactory.translateMarketSegment(tau.tac.adx.report.adn.MarketSegment.compundMarketSegment3(tau.tac.adx.report.adn.MarketSegment.MALE, tau.tac.adx.report.adn.MarketSegment.OLD, tau.tac.adx.report.adn.MarketSegment.HIGH_INCOME)) , MarketSegment.MALE_OLD_HIGH_INCOME);
    assertEquals(TACMarketFactory.translateMarketSegment(tau.tac.adx.report.adn.MarketSegment.compundMarketSegment3(tau.tac.adx.report.adn.MarketSegment.FEMALE, tau.tac.adx.report.adn.MarketSegment.YOUNG, tau.tac.adx.report.adn.MarketSegment.LOW_INCOME)) , MarketSegment.FEMALE_YOUNG_LOW_INCOME);
    assertEquals(TACMarketFactory.translateMarketSegment(tau.tac.adx.report.adn.MarketSegment.compundMarketSegment3(tau.tac.adx.report.adn.MarketSegment.FEMALE, tau.tac.adx.report.adn.MarketSegment.YOUNG, tau.tac.adx.report.adn.MarketSegment.HIGH_INCOME)) , MarketSegment.FEMALE_YOUNG_HIGH_INCOME);
    assertEquals(TACMarketFactory.translateMarketSegment(tau.tac.adx.report.adn.MarketSegment.compundMarketSegment3(tau.tac.adx.report.adn.MarketSegment.FEMALE, tau.tac.adx.report.adn.MarketSegment.OLD, tau.tac.adx.report.adn.MarketSegment.LOW_INCOME)) , MarketSegment.FEMALE_OLD_LOW_INCOME);
    assertEquals(TACMarketFactory.translateMarketSegment(tau.tac.adx.report.adn.MarketSegment.compundMarketSegment3(tau.tac.adx.report.adn.MarketSegment.FEMALE, tau.tac.adx.report.adn.MarketSegment.OLD, tau.tac.adx.report.adn.MarketSegment.HIGH_INCOME)) , MarketSegment.FEMALE_OLD_HIGH_INCOME);
  }
}
