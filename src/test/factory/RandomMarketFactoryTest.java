package test.factory;

import static org.junit.Assert.*;

import java.util.concurrent.Callable;

import org.junit.Test;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.factory.Parameters;
import structures.factory.RandomMarketFactory;
import structures.factory.RewardsGenerator;

public class RandomMarketFactoryTest {

  @Test
  public void testRewardGeneration0() throws Exception {
    Callable<Double> x = RewardsGenerator.getRandomUniformRewardFunction();
    for (int i = 0; i < 1000; i++) {
      double reward = x.call();
      if (reward < Parameters.defaultMinReward || reward > Parameters.defaultMaxReward) {
        fail("The uniform reward drawn was: " + reward + ", but the reward must be in [" + Parameters.defaultMinReward + "," + Parameters.defaultMaxReward + "]");
      }
    }
  }

  @Test
  public void testRewardGeneration1() throws Exception {
    Callable<Double> x = RewardsGenerator.getElitistRewardFunction();
    for (int i = 0; i < 1000; i++) {
      double reward = x.call();
      if (reward < 0) {
        fail("The elitist reward drawn was: " + reward + ", but it cannot be less than zero.");
      }
    }
  }

  @Test
  public void testRandomUniformRewardMarket() throws Exception {
    for (int i = 0; i < 100; i++) {
      Market<Goods, Bidder<Goods>> market = RandomMarketFactory.randomUniformRewardMarket(10, 1, 0.0);
      for (Bidder<Goods> b : market.getBidders()) {
        double reward = b.getReward();
        if (reward < Parameters.defaultMinReward || reward > Parameters.defaultMaxReward) {
          fail("The uniform reward drawn was: " + reward + ", but the reward must be in [" + Parameters.defaultMinReward + "," + Parameters.defaultMaxReward + "]");
        }
        int demand = b.getDemand();
        if (demand < Parameters.defaultMinDemandPerBidder || demand > Parameters.defaultMaxDemandPerBidder) {
          fail("The uniform demand drawn was: " + demand + ", but the demand must be in [" + Parameters.defaultMinDemandPerBidder + "," + Parameters.defaultMaxDemandPerBidder + "]");
        }
      }
    }
  }

  @Test
  public void testGenerateUniformOverDemandedMarket() throws Exception {
    for (int i = 0; i < 1000; i++) {
      Market<Goods, Bidder<Goods>> market = RandomMarketFactory.generateUniformRewardOverDemandedMarket(10, 10, 1.0, 3);
      //System.out.println(market + "\n" + market.getSupplyToDemandRatio());
      assertTrue(market.getSupplyToDemandRatio() == 1.0 / 3.0);
    }
  }
  
  @Test
  public void testGenerateUniformUnderDemandedMarket() throws Exception {
    for (int i = 0; i < 1000; i++) {
      Market<Goods, Bidder<Goods>> market = RandomMarketFactory.generateUniformRewardOverSuppliedMarket(10, 10, 1.0, 3);
      //System.out.println(market + "\n" + market.getSupplyToDemandRatio());
      assertTrue(market.getSupplyToDemandRatio() == 3.0);
    }
  }
  
  @Test
  public void testGenerateElitistOverDemandedMarket() throws Exception {
    for (int i = 0; i < 1000; i++) {
      Market<Goods, Bidder<Goods>> market = RandomMarketFactory.generateElitistRewardOverDemandedMarket(10, 10, 1.0, 3);
      //System.out.println(market + "\n" + market.getSupplyToDemandRatio());
      assertTrue(market.getSupplyToDemandRatio() == 1.0 / 3.0);
    }
  }
  
  @Test
  public void testGenerateElitistUnderDemandedMarket() throws Exception {
    for (int i = 0; i < 1000; i++) {
      Market<Goods, Bidder<Goods>> market = RandomMarketFactory.generateElitistRewardOverSuppliedMarket(10, 10, 1.0, 3);
      //System.out.println(market + "\n" + market.getSupplyToDemandRatio());
      assertTrue(market.getSupplyToDemandRatio() == 3.0);
    }
  }

}
