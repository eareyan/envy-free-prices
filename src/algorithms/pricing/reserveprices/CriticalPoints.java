package algorithms.pricing.reserveprices;

import java.util.ArrayList;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import algorithms.pricing.error.PrincingAlgoException;
import allocations.interfaces.AllocationAlgo;

/**
 * This heuristic test only the "critical points"
 * defined to be R_j / I_j for every bidder j.
 * 
 * @author Enrique Areyan Viqueira
 */
public class CriticalPoints extends SearchMetaHeuristic {

  /**
   * Constructor.
   * 
   * @param market
   * @param AllocAlgo
   * @throws PrincingAlgoException
   */
  public CriticalPoints(Market<Goods, Bidder<Goods>> market, AllocationAlgo<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> AllocAlgo) throws PrincingAlgoException {
    super(market, AllocAlgo);
  }

  @Override
  protected ArrayList<Double> getListOfReservePrices() {
    ArrayList<Double> reservePrices = new ArrayList<Double>();
    for(Bidder<Goods> b : this.market.getBidders()) {
      reservePrices.add(b.getReward() / (double) b.getDemand());
    }
    System.out.println(reservePrices);
    return reservePrices;
  }

}
