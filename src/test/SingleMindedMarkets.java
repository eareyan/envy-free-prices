package test;

import java.util.ArrayList;
import java.util.HashSet;

import singleminded.structures.SingleMindedMarket;
import structures.Bidder;
import structures.Goods;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;
import structures.exceptions.MarketCreationException;

/**
 * This class has a library of Single-Minded Markets. Each market is the output of
 * a static function. These are mainly used for testing purposes.
 * 
 * @author Enrique Areyan Viqueira
 */
public class SingleMindedMarkets {
  
  public static SingleMindedMarket<Goods, Bidder<Goods>> singleMinded() throws BidderCreationException, GoodsCreationException, MarketCreationException {
    Goods g0 = new Goods(1);
    ArrayList<Goods> goods = new ArrayList<Goods>();
    goods.add(g0);
    HashSet<Goods> b0DemandSet = new HashSet<Goods>();
    b0DemandSet.add(g0);
    Bidder<Goods> b0 = new Bidder<Goods>(1, 10, b0DemandSet);
    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    bidders.add(b0);
    
    return new SingleMindedMarket<Goods, Bidder<Goods>>(goods, bidders);
  }
  public static SingleMindedMarket<Goods, Bidder<Goods>> singleMinded0() throws BidderCreationException, GoodsCreationException, MarketCreationException {

    Goods g0 = new Goods(1);
    Goods g1 = new Goods(1);
    Goods g2 = new Goods(1);

    ArrayList<Goods> goods = new ArrayList<Goods>();
    goods.add(g0);
    goods.add(g1);
    goods.add(g2);

    HashSet<Goods> b0DemandSet = new HashSet<Goods>();
    b0DemandSet.add(g0);
    b0DemandSet.add(g1);
    b0DemandSet.add(g2);
    Bidder<Goods> b0 = new Bidder<Goods>(3, 5.35 , b0DemandSet);
    
    HashSet<Goods> b1DemandSet = new HashSet<Goods>();
    b1DemandSet.add(g1);
    Bidder<Goods> b1 = new Bidder<Goods>(1, 3.42, b1DemandSet);
    
    HashSet<Goods> b2DemandSet = new HashSet<Goods>();
    b2DemandSet.add(g0);
    Bidder<Goods> b2 = new Bidder<Goods>(1, 8.99, b2DemandSet);

    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    bidders.add(b0);
    bidders.add(b1);
    bidders.add(b2);

    return new SingleMindedMarket<Goods, Bidder<Goods>>(goods, bidders);
  }

  public static SingleMindedMarket<Goods, Bidder<Goods>> singleMinded1() throws BidderCreationException, GoodsCreationException, MarketCreationException {

    Goods g0 = new Goods(1);
    Goods g1 = new Goods(1);
    Goods g2 = new Goods(1);
    Goods g3 = new Goods(1);

    ArrayList<Goods> goods = new ArrayList<Goods>();
    goods.add(g0);
    goods.add(g1);
    goods.add(g2);
    goods.add(g3);

    HashSet<Goods> b0DemandSet = new HashSet<Goods>();
    b0DemandSet.add(g2);
    Bidder<Goods> b0 = new Bidder<Goods>(1, 4.39, b0DemandSet);
    
    HashSet<Goods> b1DemandSet = new HashSet<Goods>();
    b1DemandSet.add(g0);
    b1DemandSet.add(g2);
    b1DemandSet.add(g3);
    Bidder<Goods> b1 = new Bidder<Goods>(3, 2.48, b1DemandSet);
    
    HashSet<Goods> b2DemandSet = new HashSet<Goods>();
    b2DemandSet.add(g3);
    Bidder<Goods> b2 = new Bidder<Goods>(1, 9.79, b2DemandSet);

    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    bidders.add(b0);
    bidders.add(b1);
    bidders.add(b2);

    return new SingleMindedMarket<Goods, Bidder<Goods>>(goods, bidders);
  }
  
  public static SingleMindedMarket<Goods, Bidder<Goods>> singleMinded2() throws BidderCreationException, GoodsCreationException, MarketCreationException {
    Goods g0 = new Goods(1);
    Goods g1 = new Goods(1);
    Goods g2 = new Goods(1);
    
    ArrayList<Goods> goods = new ArrayList<Goods>();
    goods.add(g0);
    goods.add(g1);
    goods.add(g2);

    HashSet<Goods> b0DemandSet = new HashSet<Goods>();
    b0DemandSet.add(g1);
    b0DemandSet.add(g2);
    Bidder<Goods> b0 = new Bidder<Goods>(2, 2.23, b0DemandSet);

    HashSet<Goods> b1DemandSet = new HashSet<Goods>();
    b1DemandSet.add(g1);
    b1DemandSet.add(g2);
    Bidder<Goods> b1 = new Bidder<Goods>(2, 6.61, b1DemandSet);

    HashSet<Goods> b2DemandSet = new HashSet<Goods>();
    b2DemandSet.add(g0);
    b2DemandSet.add(g1);
    b2DemandSet.add(g2);
    Bidder<Goods> b2 = new Bidder<Goods>(3, 7.36, b2DemandSet);
    
    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    bidders.add(b0);
    bidders.add(b1);
    bidders.add(b2);

    return new SingleMindedMarket<Goods, Bidder<Goods>>(goods, bidders);
  } 
  
  public static SingleMindedMarket<Goods, Bidder<Goods>> singleMinded3() throws BidderCreationException, GoodsCreationException, MarketCreationException {
    Goods g0 = new Goods(1);
    Goods g1 = new Goods(1);
    Goods g2 = new Goods(1);
    
    ArrayList<Goods> goods = new ArrayList<Goods>();
    goods.add(g0);
    goods.add(g1);
    goods.add(g2);

    HashSet<Goods> b0DemandSet = new HashSet<Goods>();
    b0DemandSet.add(g0);
    b0DemandSet.add(g1);
    Bidder<Goods> b0 = new Bidder<Goods>(2, 10, b0DemandSet);

    HashSet<Goods> b1DemandSet = new HashSet<Goods>();
    b1DemandSet.add(g1);
    Bidder<Goods> b1 = new Bidder<Goods>(1, 4, b1DemandSet);

    HashSet<Goods> b2DemandSet = new HashSet<Goods>();
    b2DemandSet.add(g2);
    Bidder<Goods> b2 = new Bidder<Goods>(1, 4, b2DemandSet);
    
    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    bidders.add(b0);
    bidders.add(b1);
    bidders.add(b2);

    return new SingleMindedMarket<Goods, Bidder<Goods>>(goods, bidders);
  } 
  
  public static SingleMindedMarket<Goods, Bidder<Goods>> singleMinded4() throws BidderCreationException, GoodsCreationException, MarketCreationException {
    Goods g0 = new Goods(1);
    Goods g1 = new Goods(1);
    Goods g2 = new Goods(1);
    Goods g3 = new Goods(1);
    Goods g4 = new Goods(1);
    Goods g5 = new Goods(1);
    Goods g6 = new Goods(1);
    Goods g7 = new Goods(1);
    
    ArrayList<Goods> goods = new ArrayList<Goods>();
    goods.add(g0);
    goods.add(g1);
    goods.add(g2);
    goods.add(g3);
    goods.add(g4);
    goods.add(g5);
    goods.add(g6);
    goods.add(g7);

    HashSet<Goods> b0DemandSet = new HashSet<Goods>();
    b0DemandSet.add(g1);
    b0DemandSet.add(g2);
    b0DemandSet.add(g3);
    b0DemandSet.add(g4);
    b0DemandSet.add(g5);
    b0DemandSet.add(g7);
    Bidder<Goods> b0 = new Bidder<Goods>(6, 6.57, b0DemandSet);
    
    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    bidders.add(b0);

    return new SingleMindedMarket<Goods, Bidder<Goods>>(goods, bidders);
  } 
  
  public static SingleMindedMarket<Goods, Bidder<Goods>> singleMinded5() throws BidderCreationException, GoodsCreationException, MarketCreationException {
    Goods g0 = new Goods(1);
    Goods g1 = new Goods(1);

    ArrayList<Goods> goods = new ArrayList<Goods>();
    goods.add(g0);
    goods.add(g1);
    
    HashSet<Goods> bDemandSet = new HashSet<Goods>();
    bDemandSet.add(g0);
    bDemandSet.add(g1);
    
    Bidder<Goods> b0 = new Bidder<Goods>(2, 4.41, bDemandSet);
    Bidder<Goods> b1 = new Bidder<Goods>(2, 3.14, bDemandSet);
    Bidder<Goods> b2 = new Bidder<Goods>(2, 5.53, bDemandSet);
    //Bidder<Goods> b3 = new Bidder<Goods>(2, 5.95, bDemandSet);
    Bidder<Goods> b3 = new Bidder<Goods>(2, 6.0, bDemandSet);

    
    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    bidders.add(b0);
    bidders.add(b1);
    bidders.add(b2);
    bidders.add(b3);
    
    return new SingleMindedMarket<Goods, Bidder<Goods>>(goods, bidders);

  }
  
  public static SingleMindedMarket<Goods, Bidder<Goods>> singleMinded6() throws GoodsCreationException, BidderCreationException, MarketCreationException {
    Goods g0 = new Goods(1);
    Goods g1 = new Goods(1);
    Goods g2 = new Goods(1);
    
    ArrayList<Goods> goods = new ArrayList<Goods>();
    goods.add(g0);
    goods.add(g1);
    goods.add(g2);
    
    HashSet<Goods> b0DemandSet = new HashSet<Goods>();
    b0DemandSet.add(g0);
    b0DemandSet.add(g1);
    Bidder<Goods> b0 = new Bidder<Goods>(2, 3, b0DemandSet);

    HashSet<Goods> b1DemandSet = new HashSet<Goods>();
    b1DemandSet.add(g1);
    b1DemandSet.add(g2);
    Bidder<Goods> b1 = new Bidder<Goods>(2, 2, b1DemandSet);

    HashSet<Goods> b2DemandSet = new HashSet<Goods>();
    b2DemandSet.add(g0);
    b2DemandSet.add(g2);
    Bidder<Goods> b2 = new Bidder<Goods>(2, 2, b2DemandSet);

    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    bidders.add(b0);
    bidders.add(b1);
    bidders.add(b2);
    
    return new SingleMindedMarket<Goods, Bidder<Goods>>(goods, bidders);
    
  }

}
