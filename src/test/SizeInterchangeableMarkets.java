package test;

import java.util.ArrayList;
import java.util.HashSet;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;
import structures.exceptions.MarketCreationException;

/**
 * This class has a library of Size Interchangeable Markets. Each market is the output of a static function. These are mainly used for testing purposes.
 * 
 * @author Enrique Areyan Viqueira
 */
public class SizeInterchangeableMarkets {

  public static Market<Goods, Bidder<Goods>> market0() throws BidderCreationException, GoodsCreationException, MarketCreationException {

    /* Example where real envy-free prices do not exists */

    Goods g0 = new Goods(2);
    Goods g1 = new Goods(2);
    ArrayList<Goods> goods = new ArrayList<Goods>();
    goods.add(g0);
    goods.add(g1);

    HashSet<Goods> b0DemandSet = new HashSet<Goods>();
    b0DemandSet.add(g0);
    Bidder<Goods> b0 = new Bidder<Goods>(2, 25, b0DemandSet);

    HashSet<Goods> b1DemandSet = new HashSet<Goods>();
    b1DemandSet.add(g0);
    b1DemandSet.add(g1);
    Bidder<Goods> b1 = new Bidder<Goods>(3, 45, b1DemandSet);

    HashSet<Goods> b2DemandSet = new HashSet<Goods>();
    b2DemandSet.add(g1);
    Bidder<Goods> b2 = new Bidder<Goods>(2, 25, b2DemandSet);

    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    bidders.add(b0);
    bidders.add(b1);
    bidders.add(b2);

    return new Market<Goods, Bidder<Goods>>(goods, bidders);
  }

  public static Market<Goods, Bidder<Goods>> market1() throws BidderCreationException, GoodsCreationException, MarketCreationException {
    /* Example where the first implementation of CK outputs envy prices */

    Goods g0 = new Goods(5);
    Goods g1 = new Goods(5);
    ArrayList<Goods> goods = new ArrayList<Goods>();
    goods.add(g0);
    goods.add(g1);

    HashSet<Goods> b0DemandSet = new HashSet<Goods>();
    b0DemandSet.add(g0);
    b0DemandSet.add(g1);
    Bidder<Goods> b0 = new Bidder<Goods>(5, 8.79, b0DemandSet);

    HashSet<Goods> b1DemandSet = new HashSet<Goods>();
    b1DemandSet.add(g0);
    b1DemandSet.add(g1);

    Bidder<Goods> b1 = new Bidder<Goods>(5, 6.45, b1DemandSet);
    Bidder<Goods> b2 = new Bidder<Goods>(4, 3.47, new HashSet<Goods>());

    ArrayList<Bidder<Goods>> campaigns = new ArrayList<Bidder<Goods>>();
    campaigns.add(b0);
    campaigns.add(b1);
    campaigns.add(b2);

    return new Market<Goods, Bidder<Goods>>(goods, campaigns);
  }

  public static Market<Goods, Bidder<Goods>> market2() throws BidderCreationException, GoodsCreationException, MarketCreationException {

    Goods g0 = new Goods(20);
    Goods g1 = new Goods(25);
    ArrayList<Goods> goods = new ArrayList<Goods>();
    goods.add(g0);
    goods.add(g1);

    HashSet<Goods> b0DemandSet = new HashSet<Goods>();
    b0DemandSet.add(g0);
    b0DemandSet.add(g1);
    Bidder<Goods> b0 = new Bidder<Goods>(10, 1234, b0DemandSet);

    HashSet<Goods> b1DemandSet = new HashSet<Goods>();
    b1DemandSet.add(g0);
    b1DemandSet.add(g1);
    Bidder<Goods> b1 = new Bidder<Goods>(454, 2856, b1DemandSet);

    ArrayList<Bidder<Goods>> campaigns = new ArrayList<Bidder<Goods>>();
    campaigns.add(b0);
    campaigns.add(b1);

    return new Market<Goods, Bidder<Goods>>(goods, campaigns);
  }

  public static Market<Goods, Bidder<Goods>> market3() throws BidderCreationException, GoodsCreationException, MarketCreationException {

    Goods g0 = new Goods(25);
    Goods g1 = new Goods(25);
    ArrayList<Goods> goods = new ArrayList<Goods>();
    goods.add(g0);
    goods.add(g1);

    HashSet<Goods> b0DemandSet = new HashSet<Goods>();
    b0DemandSet.add(g0);
    b0DemandSet.add(g1);
    Bidder<Goods> b0 = new Bidder<Goods>(50, 500, b0DemandSet);

    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    bidders.add(b0);

    return new Market<Goods, Bidder<Goods>>(goods, bidders);
  }

  public static Market<Goods, Bidder<Goods>> market4() throws BidderCreationException, GoodsCreationException, MarketCreationException {

    Goods g0 = new Goods(10);
    Goods g1 = new Goods(10);
    Goods g2 = new Goods(10);
    Goods g3 = new Goods(10);
    ArrayList<Goods> goods = new ArrayList<Goods>();
    goods.add(g0);
    goods.add(g1);
    goods.add(g2);
    goods.add(g3);

    HashSet<Goods> b0DemandSet = new HashSet<Goods>();
    b0DemandSet.add(g0);
    b0DemandSet.add(g2);
    Bidder<Goods> b0 = new Bidder<Goods>(10, 100.0, b0DemandSet);

    HashSet<Goods> b1DemandSet = new HashSet<Goods>();
    b1DemandSet.add(g1);
    Bidder<Goods> b1 = new Bidder<Goods>(10, 200.0, b1DemandSet);

    HashSet<Goods> b2DemandSet = new HashSet<Goods>();
    b2DemandSet.add(g3);
    Bidder<Goods> b2 = new Bidder<Goods>(10, 300.0, b2DemandSet);

    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    bidders.add(b0);
    bidders.add(b1);
    bidders.add(b2);

    return new Market<Goods, Bidder<Goods>>(goods, bidders);
  }

  public static Market<Goods, Bidder<Goods>> market5() throws BidderCreationException, GoodsCreationException, MarketCreationException {

    ArrayList<Goods> goods = new ArrayList<Goods>();
    goods.add(new Goods(100));

    HashSet<Goods> bDemandSet = new HashSet<Goods>();
    bDemandSet.add(goods.get(0));

    Bidder<Goods> b0 = new Bidder<Goods>(10, 100, bDemandSet);
    Bidder<Goods> b1 = new Bidder<Goods>(20, 201, bDemandSet);
    Bidder<Goods> b2 = new Bidder<Goods>(30, 300, bDemandSet);

    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    bidders.add(b0);
    bidders.add(b1);
    bidders.add(b2);

    return new Market<Goods, Bidder<Goods>>(goods, bidders);
  }

  public static Market<Goods, Bidder<Goods>> market6() throws GoodsCreationException, BidderCreationException, MarketCreationException {
    int numberGoods = 3;
    int numberBidders = 6;

    ArrayList<Goods> goods = new ArrayList<Goods>(numberGoods);
    for (int i = 0; i < numberGoods; i++) {
      goods.add(new Goods(10));
    }

    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>(numberBidders);
    for (int j = 0; j < numberBidders; j++) {
      HashSet<Goods> bDemandSet = new HashSet<Goods>();
      for (Goods g : goods) {
        bDemandSet.add(g);
      }
      bidders.add(new Bidder<Goods>(10, 10 + j, bDemandSet));
    }

    return new Market<Goods, Bidder<Goods>>(goods, bidders);
  }

  public static Market<Goods, Bidder<Goods>> market7() throws BidderCreationException, GoodsCreationException, MarketCreationException {

    ArrayList<Goods> goods = new ArrayList<Goods>();
    goods.add(new Goods(2812));
    goods.add(new Goods(3410));
    goods.add(new Goods(735));
    goods.add(new Goods(1148));
    goods.add(new Goods(2549));
    goods.add(new Goods(578));
    goods.add(new Goods(2608));
    goods.add(new Goods(364));

    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();

    HashSet<Goods> b0DemandSet = new HashSet<Goods>();
    b0DemandSet.add(goods.get(2));
    b0DemandSet.add(goods.get(3));
    b0DemandSet.add(goods.get(5));
    b0DemandSet.add(goods.get(7));
    bidders.add(new Bidder<Goods>(4142, 4622.0, b0DemandSet));

    HashSet<Goods> b1DemandSet = new HashSet<Goods>();
    b1DemandSet.add(goods.get(0));
    bidders.add(new Bidder<Goods>(1920, 671.0, b1DemandSet));

    HashSet<Goods> b2DemandSet = new HashSet<Goods>();
    b2DemandSet.add(goods.get(1));
    bidders.add(new Bidder<Goods>(481, 900.0, b2DemandSet));

    HashSet<Goods> b3DemandSet = new HashSet<Goods>();
    b3DemandSet.add(goods.get(2));
    b3DemandSet.add(goods.get(3));
    b3DemandSet.add(goods.get(4));
    b3DemandSet.add(goods.get(6));
    bidders.add(new Bidder<Goods>(2478, 6195.0, b3DemandSet));

    HashSet<Goods> b4DemandSet = new HashSet<Goods>();
    b4DemandSet.add(goods.get(2));
    bidders.add(new Bidder<Goods>(259, 387.0, b4DemandSet));

    HashSet<Goods> b5DemandSet = new HashSet<Goods>();
    b5DemandSet.add(goods.get(1));
    bidders.add(new Bidder<Goods>(1921, 4802.0, b5DemandSet));

    return new Market<Goods, Bidder<Goods>>(goods, bidders);
  }

  public static Market<Goods, Bidder<Goods>> market8() throws GoodsCreationException, BidderCreationException, MarketCreationException {
    ArrayList<Goods> goods = new ArrayList<Goods>();
    goods.add(new Goods(6));
    goods.add(new Goods(7));
    goods.add(new Goods(8));
    goods.add(new Goods(13));

    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    HashSet<Goods> bDemandSet = new HashSet<Goods>();
    for (Goods good : goods) {
      bDemandSet.add(good);
    }
    bidders.add(new Bidder<Goods>(25, 2.65, bDemandSet));
    bidders.add(new Bidder<Goods>(20, 2.33, bDemandSet));
    bidders.add(new Bidder<Goods>(6, 4.99, bDemandSet));
    bidders.add(new Bidder<Goods>(17, 5.99, bDemandSet));

    return new Market<Goods, Bidder<Goods>>(goods, bidders);

  }

  public static Market<Goods, Bidder<Goods>> market9() throws GoodsCreationException, BidderCreationException, MarketCreationException {
    ArrayList<Goods> goods = new ArrayList<Goods>();
    goods.add(new Goods(10));
    goods.add(new Goods(20));
    goods.add(new Goods(30));

    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    HashSet<Goods> b1DemandSet = new HashSet<Goods>();
    b1DemandSet.add(goods.get(0));
    b1DemandSet.add(goods.get(1));
    b1DemandSet.add(goods.get(2));

    HashSet<Goods> b2DemandSet = new HashSet<Goods>();
    b2DemandSet.add(goods.get(1));
    b2DemandSet.add(goods.get(2));

    bidders.add(new Bidder<Goods>(30, 100, b1DemandSet));
    bidders.add(new Bidder<Goods>(30, 200, b2DemandSet));

    return new Market<Goods, Bidder<Goods>>(goods, bidders);

  }

}
