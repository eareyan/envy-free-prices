package algorithms.pricing.reserveprices;

import ilog.concert.IloException;

import java.util.ArrayList;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.exceptions.AllocationException;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketAllocationException;
import util.MyRandom;
import algorithms.pricing.error.PrincingAlgoException;
import allocations.error.AllocationAlgoException;
import allocations.interfaces.AllocationAlgo;

/**
 * Generates a list of n random reserve prices between 0 and the max_j R_j/I_j.
 * 
 * @author Enrique Areyan Viqueira
 */
public class RandomSearch extends SearchMetaHeuristic{
  
  /**
   * Lower bound on the reserve price.
   */
  protected final double minReserve = 0.0;
  
  /**
   * Upper bound on the reserve price.
   */
  protected final double maxReserve;
  
  /**
   * Number of Samples.
   */
  protected int numberOfSamples;
  
  /**
   * Default number of samples;
   */
  protected static final int defaultNumberOfSamples = 100;

  /**
   * Constructor.
   * 
   * @param market
   * @param AllocAlgo
   * @throws MarketAllocationException 
   * @throws GoodsException 
   * @throws AllocationException 
   * @throws BidderCreationException 
   * @throws AllocationAlgoException 
   * @throws IloException 
   * @throws GoodsCreationException 
   * @throws PrincingAlgoException 
   */
  public RandomSearch(Market<Goods, Bidder<Goods>> market, AllocationAlgo<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> AllocAlgo) throws GoodsCreationException, IloException, AllocationAlgoException, BidderCreationException, AllocationException, GoodsException, MarketAllocationException, PrincingAlgoException {
    super(market, AllocAlgo);
    // Compute as the maxReserve the maximum ratio R_j/I_j over all j.
    double maxRatio = Double.NEGATIVE_INFINITY;
    for(Bidder<Goods> bidder: this.market.getBidders()) {
      if((bidder.getReward() / bidder.getDemand()) > maxRatio){
        maxRatio = bidder.getReward() / bidder.getDemand();
      }
    }
    this.maxReserve = maxRatio;
    this.numberOfSamples = RandomSearch.defaultNumberOfSamples;
  }
  
  /**
   * Constructor. 
   * @throws PrincingAlgoException 
   *
   */
  public RandomSearch(Market<Goods, Bidder<Goods>> market, AllocationAlgo<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> AllocAlgo, int numberOfSamples) throws GoodsCreationException, IloException, AllocationAlgoException, BidderCreationException, AllocationException, GoodsException, MarketAllocationException, PrincingAlgoException {
    this(market, AllocAlgo);
    this.numberOfSamples = numberOfSamples;
  }

  @Override
  protected ArrayList<Double> getListOfReservePrices() {
    ArrayList<Double> reservePrices = new ArrayList<Double>();
    for(int sample = 0 ; sample < this.numberOfSamples; sample++) {
      reservePrices.add(MyRandom.generator.nextDouble() * this.maxReserve);
    }
    return reservePrices;
  }

}
