package structures.factory;

import ilog.concert.IloException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.spectrumauctions.sats.core.bidlang.xor.SizeBasedUniqueRandomXOR;
import org.spectrumauctions.sats.core.bidlang.xor.XORValue;
import org.spectrumauctions.sats.core.model.Bidder;
import org.spectrumauctions.sats.core.model.UnsupportedBiddingLanguageException;
import org.spectrumauctions.sats.core.model.mrvm.MRVMBidder;
import org.spectrumauctions.sats.core.model.mrvm.MRVMLicense;
import org.spectrumauctions.sats.core.model.mrvm.MRVMNationalBidder;
import org.spectrumauctions.sats.core.model.mrvm.MRVMRegionsMap.Region;
import org.spectrumauctions.sats.core.model.mrvm.MultiRegionModel;

import singleminded.structures.SingleMindedMarket;
import structures.Goods;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;
import structures.exceptions.MarketCreationException;

public class SATSFactory {
  /**
   * Main testing c lass
   * 
   * @param args
   * @throws UnsupportedBiddingLanguageException
   * @throws GoodsCreationException
   * @throws BidderCreationException
   * @throws MarketCreationException
   * @throws IloException
   */
  public static void main(String[] args) {
    System.out.println("Testing SATS");
    Collection<MRVMBidder> satsBidders = getDefaultCanadianAuctionBidders();
    System.out.println(satsBidders);
    for (MRVMBidder bidder : satsBidders) {
      System.out.println(bidder.getSetupType());
      System.out.println("\t Alpha = " + bidder.getAlpha());
      for (Region region : bidder.getWorld().getRegionsMap().getRegions()) {
        System.out.println("\t" + region);
        System.out.println("\t\t Beta(r) = " + bidder.getBeta(region));
        System.out.println("\t\t ZHigh(r) = " + bidder.getzHigh(region));
        System.out.println("\t\t Zlow(r) = " + bidder.getzLow(region));
      }
      if (bidder instanceof MRVMNationalBidder) {
        System.out.println("This is a national bidder. Print national bidder specific parameters ");
        MRVMNationalBidder national = (MRVMNationalBidder) bidder;
        System.out.println("\t\t KMax = " + national.getKMax());
        for (int i = 0; i < 10; i++) {
          System.out.println("\t\t GammaUncovered(" + i + ") = " + national.getGamma(i));
        }
      }
    }
    // Collection<MRVMBidder> satsBidders = getCustomBidders();
    // SingleMindedMarket<Goods, structures.Bidder<Goods>> singleMindedMarket = createSingleMindedMarket(satsBidders);
    // System.out.println("#Bidders = " + singleMindedMarket.getNumberBidders());
    // System.out.println("#Goods = " + singleMindedMarket.getNumberGoods());
    // System.out.println(singleMindedMarket);
    // boolean weExists = WEExistence.decideWE(singleMindedMarket, true);
    // System.out.println("WE Exists? " + weExists);
  }

  public static Collection<MRVMBidder> getDefaultCanadianAuctionBidders() {
    // A one-liner that by default mimics the canadian auction.
    return new MultiRegionModel().createNewPopulation();
  }

  public static Collection<MRVMBidder> getCustomBidders() {
    MultiRegionModel multiRegionModel = new MultiRegionModel();
    multiRegionModel.setNumberOfLocalBidders(1);
    multiRegionModel.setNumberOfRegionalBidders(0);
    multiRegionModel.setNumberOfNationalBidders(0);
    return multiRegionModel.createNewPopulation();
  }

  /**
   * Given a collection of SATS bidders, return a single-minded market.
   * 
   * @param satsBidders
   * @return
   * @throws UnsupportedBiddingLanguageException
   * @throws GoodsCreationException
   * @throws BidderCreationException
   * @throws MarketCreationException
   */
  @SuppressWarnings("unchecked")
  public static SingleMindedMarket<Goods, structures.Bidder<Goods>> createSingleMindedMarket(Collection<MRVMBidder> satsBidders)
      throws UnsupportedBiddingLanguageException, GoodsCreationException, BidderCreationException, MarketCreationException {

    long dummyGoodIndex = 0;
    ArrayList<structures.Bidder<Goods>> listOfSingleMindedBidders = new ArrayList<structures.Bidder<Goods>>();
    Map<Long, Goods> goodsMap = new HashMap<Long, Goods>();
    HashSet<Goods> setOfDummyGoods = new HashSet<Goods>();
    for (Bidder<?> bidder : satsBidders) {
      SizeBasedUniqueRandomXOR<MRVMLicense> xorBids = bidder.getValueFunction(SizeBasedUniqueRandomXOR.class);
      // xorBids.setDistribution(100, 25, 200);
      xorBids.setDistribution(10, 2, 2);
      Iterator<? extends XORValue<MRVMLicense>> bidsIterator = xorBids.iterator();
      System.out.println("\n\nBidder " + bidder.getId() + "," + bidder.getSetupType());
      Goods dummyGoodBidder_i = new Goods(1);
      goodsMap.put(--dummyGoodIndex, dummyGoodBidder_i);
      setOfDummyGoods.add(dummyGoodBidder_i);
      while (bidsIterator.hasNext()) {
        // Each bid represents a bidder in our single-minded market
        HashSet<Goods> demandSet = new HashSet<Goods>();
        demandSet.add(dummyGoodBidder_i);
        XORValue<MRVMLicense> bid = bidsIterator.next();
        System.out.println("\t XOR Bid with value = " + bid.value().doubleValue());
        if (bid.value().doubleValue() > 0) {
          for (MRVMLicense lic : bid.getLicenses()) {
            System.out.println("\t\t " + lic.getId());
            if (!goodsMap.containsKey(lic.getId())) {
              goodsMap.put(lic.getId(), new Goods(1));
            }
            demandSet.add(goodsMap.get(lic.getId()));
          }
          listOfSingleMindedBidders.add(new structures.Bidder<Goods>(demandSet.size(), bid.value().doubleValue(), demandSet));
        }
      }
    }
    // System.out.println("Goods Map = " + goodsMap + ", size: " + goodsMap.size());
    // System.out.println("Bidders = " + listOfSingleMindedBidders);
    // System.out.println("Dummy Goods = " + setOfDummyGoods);
    ArrayList<Goods> listOfGoods = new ArrayList<Goods>();
    for (Entry<Long, Goods> entry : goodsMap.entrySet()) {
      listOfGoods.add(entry.getValue());
    }
    SingleMindedMarket<Goods, structures.Bidder<Goods>> singleMindedMarket = new SingleMindedMarket<Goods, structures.Bidder<Goods>>(listOfGoods,
        listOfSingleMindedBidders);
    singleMindedMarket.setDummyGoods(setOfDummyGoods);
    return singleMindedMarket;
  }
}
