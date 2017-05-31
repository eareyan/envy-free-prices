package test.algorithms;

import org.junit.Test;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketCreationException;
import test.SizeInterchangeableMarkets;
import waterfall.Waterfall;

public class WaterfallTest {

  @Test
  public void test() throws BidderCreationException, MarketCreationException, GoodsException {
    // Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market0();
    // Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market1();
    // Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market2();
    //Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market3();
    // Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market4();
    //Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market5();
    // Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market6();
     Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market7();
    // Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market8();
    // Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market9();
    System.out.println(market);
    Waterfall<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> waterfall = new Waterfall<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(market);
    waterfall.run();
  }

}
