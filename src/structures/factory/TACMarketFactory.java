package structures.factory;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import simulator.sampling.MarketSegmentSampler;
import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;
import structures.exceptions.MarketCreationException;
import tau.tac.adx.props.AdxQuery;
import adx.exceptions.AdXException;
import adx.structures.Campaign;
import adx.structures.MarketSegment;
import adx.util.Sampling;

/**
 * Generates a random TAC Market.
 * 
 * @author Enrique Areyan Viqueira
 */
public class TACMarketFactory {

  /**
   * Samples a TAC market given the number of impressions m.
   * 
   * @param m
   * @return
   * @throws GoodsCreationException
   * @throws AdXException
   * @throws BidderCreationException
   * @throws MarketCreationException
   */
  public static Market<Goods, Bidder<Goods>> RandomTACMarket(int m) throws GoodsCreationException, AdXException, BidderCreationException,
      MarketCreationException {
    MarketSegmentSampler mss = new MarketSegmentSampler("/home/eareyanv/workspace/history/tac-adx/AdX/adx-server/config/tac13adx_sim.conf");
    mss.pContinue = 0.3;
    mss.samplePublishers();
    mss.sampleUsers();

    ArrayList<Entry<MarketSegment, Goods>> queries = new ArrayList<Entry<MarketSegment, Goods>>();
    ArrayList<Goods> goods = new ArrayList<Goods>();
    for (Entry<AdxQuery, Integer> sample : mss.getAdxQuerySize().entrySet()) {
      Goods g = new Goods(sample.getValue());
      queries.add(new AbstractMap.SimpleEntry<MarketSegment, Goods>(TACMarketFactory.translateMarketSegment(sample.getKey().getMarketSegments()), g));
      goods.add(g);
    }
    ArrayList<Campaign> campaigns = new ArrayList<Campaign>();
    for (int i = 0; i < m; i++) {
      campaigns.add(Sampling.sampleCampaign(0));
    }
    // System.out.println(campaigns);
    Random random = new Random();
    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    for (int i = 0; i < m; i++) {
      HashSet<Goods> demandSet = new HashSet<Goods>();
      for (Entry<MarketSegment, Goods> good : queries) {
        if (MarketSegment.marketSegmentSubset(campaigns.get(i).getMarketSegment(), good.getKey())) {
          demandSet.add(good.getValue());
        }
      }
      bidders.add(new Bidder<Goods>(campaigns.get(i).getReach(), campaigns.get(i).getReach() * random.nextDouble(), demandSet));
    }
    Market<Goods, Bidder<Goods>> market = new Market<Goods, Bidder<Goods>>(goods, bidders);

    // System.out.println(market);
    // System.out.println(market.getSupplyToDemandRatio());
    // System.out.println(market.getNumberGoods());
    return market;
  }

  public static MarketSegment translateMarketSegment(Set<tau.tac.adx.report.adn.MarketSegment> setOfMarketSegments) {
    if (setOfMarketSegments.contains(tau.tac.adx.report.adn.MarketSegment.MALE)) {
      if (setOfMarketSegments.contains(tau.tac.adx.report.adn.MarketSegment.YOUNG)) {
        if (setOfMarketSegments.contains(tau.tac.adx.report.adn.MarketSegment.LOW_INCOME)) {
          return MarketSegment.MALE_YOUNG_LOW_INCOME;
        } else {
          return MarketSegment.MALE_YOUNG_HIGH_INCOME;
        }
      } else {
        if (setOfMarketSegments.contains(tau.tac.adx.report.adn.MarketSegment.LOW_INCOME)) {
          return MarketSegment.MALE_OLD_LOW_INCOME;
        } else {
          return MarketSegment.MALE_OLD_HIGH_INCOME;
        }
      }
    } else {
      if (setOfMarketSegments.contains(tau.tac.adx.report.adn.MarketSegment.YOUNG)) {
        if (setOfMarketSegments.contains(tau.tac.adx.report.adn.MarketSegment.LOW_INCOME)) {
          return MarketSegment.FEMALE_YOUNG_LOW_INCOME;
        } else {
          return MarketSegment.FEMALE_YOUNG_HIGH_INCOME;
        }
      } else {
        if (setOfMarketSegments.contains(tau.tac.adx.report.adn.MarketSegment.LOW_INCOME)) {
          return MarketSegment.FEMALE_OLD_LOW_INCOME;
        } else {
          return MarketSegment.FEMALE_OLD_HIGH_INCOME;
        }
      }
    }
  }
}
