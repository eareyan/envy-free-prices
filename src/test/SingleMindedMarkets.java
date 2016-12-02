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
 * This class has a library of Single-Minded Markets. Each market is the output of
 * a static function. These are mainly used for testing purposes.
 * 
 * @author Enrique Areyan Viqueira
 */
public class SingleMindedMarkets {
  
  public static Market<Goods, Bidder<Goods>> singleMinded0() throws BidderCreationException, GoodsCreationException, MarketCreationException {

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

    return new Market<Goods, Bidder<Goods>>(goods, bidders);
  }

  public static Market<Goods, Bidder<Goods>> singleMinded1() throws BidderCreationException, GoodsCreationException, MarketCreationException {

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

    return new Market<Goods, Bidder<Goods>>(goods, bidders);
  }
  
  public static Market<Goods, Bidder<Goods>> singleMinded2() throws BidderCreationException, GoodsCreationException, MarketCreationException {
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

    return new Market<Goods, Bidder<Goods>>(goods, bidders);
  }  

}
