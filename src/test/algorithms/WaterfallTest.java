package test.algorithms;

import org.junit.Test;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.exceptions.AllocationException;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketCreationException;
import test.SizeInterchangeableMarkets;
import waterfall.Waterfall;
import waterfall.WaterfallSolution;
import allocations.error.AllocationAlgoException;
import allocations.optimal.OptimalAllocILP;
import allocations.optimal.WelfareMaxAllocationILP;

public class WaterfallTest {

  @Test
  public void test() throws BidderCreationException, MarketCreationException, GoodsException {
     Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market0();
    // Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market1();
    // Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market2();
    // Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market3();
    // Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market4();
    // Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market5();
    // Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market6();
    //Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market7();
    // Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market8();
    // Market<Goods, Bidder<Goods>> market = SizeInterchangeableMarkets.market9();
    System.out.println(market);
    //Waterfall<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> waterfall = new Waterfall<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(market);
    Waterfall<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> waterfall = new Waterfall<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(market, 2);
    WaterfallSolution<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> waterfallSolution = waterfall.run();
    waterfallSolution.printAllocationTable();
    waterfallSolution.printPricesTable();
    
    WelfareMaxAllocationILP<Market<Goods,Bidder<Goods>>, Goods, Bidder<Goods>> optimalAlloc = new WelfareMaxAllocationILP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
    try {
      MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> optAlloc = optimalAlloc.Solve(market);
      optAlloc.printAllocation();
    } catch (AllocationAlgoException | AllocationException | MarketAllocationException e) {
      e.printStackTrace();
    }
    
  }

}
