package algorithms.pricing.reserveprices;

import ilog.concert.IloException;

import java.util.ArrayList;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.exceptions.AllocationException;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketAllocationException;
import algorithms.pricing.error.PrincingAlgoException;
import allocations.error.AllocationAlgoException;
import allocations.interfaces.AllocationAlgo;

/**
 * This class implements a strategy for searching for a revenue-maximizing
 * solution.
 * 
 * @author Enrique Areyan Viqueira
 */
public class RevMaxHeuristic extends SearchMetaHeuristic {

  /**
   * Constructor.
   * 
   * @param market
   * @param AllocAlgo
   * @throws PrincingAlgoException
   */
  public RevMaxHeuristic(Market<Goods, Bidder<Goods>> market, AllocationAlgo<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> AllocAlgo) throws PrincingAlgoException {
    super(market, AllocAlgo);
  }

  @Override
  protected ArrayList<Double> getListOfReservePrices() {
    ArrayList<Double> reservePrices = new ArrayList<Double>();
    try {
      MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> initialMarketAllocation = this.AllocAlgo.Solve(this.market);
      // Generate the list of reserve prices.
      for (Goods good : this.market.getGoods()) {
        for (Bidder<Goods> bidder : this.market.getBidders()) {
          if (initialMarketAllocation.getAllocation(good, bidder) > 0) {
            // For all x_{ij} > 0., set reserve price R_j / x_{ij}
            reservePrices.add(bidder.getReward() / (double) initialMarketAllocation.getAllocation(good, bidder));
          }
        }
      }
    } catch (IloException | MarketAllocationException | AllocationAlgoException | BidderCreationException | AllocationException | GoodsException e) {
      System.out.println("An exception occurred in RevMaxHeuristic! ");
      e.printStackTrace();
    }
    return reservePrices;
  }

}
