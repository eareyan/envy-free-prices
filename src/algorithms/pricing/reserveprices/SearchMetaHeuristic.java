package algorithms.pricing.reserveprices;

import ilog.concert.IloException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketOutcome;
import structures.comparators.MarketOutcomeComparatorBySellerRevenue;
import structures.exceptions.AllocationException;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketCreationException;
import structures.exceptions.MarketOutcomeException;
import structures.factory.reserve.BasicMarketWithReserve;
import algorithms.pricing.RestrictedEnvyFreePricesLPSolution;
import algorithms.pricing.RestrictedEnvyFreePricesLPWithReserve;
import algorithms.pricing.error.PrincingAlgoException;
import allocations.error.AllocationAlgoException;
import allocations.interfaces.AllocationAlgo;

/**
 * This abstract class implements a simple meta heuristic search. Given a list of reserve prices, find an allocation that respects reserve then price to obtain
 * an outcome. The outcome with maximal seller revenue is then returned.
 * 
 * The list of reserve prices must be given by an implementing class.
 * 
 * @author Enrique Areyan Viqueira
 */
public abstract class SearchMetaHeuristic {

  /**
   * Market object.
   */
  protected final Market<Goods, Bidder<Goods>> market;

  /**
   * AllocationAlgorithm object.
   */
  protected final AllocationAlgo<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> AllocAlgo;

  /**
   * ArrayList of MarketPrices.
   */
  protected final ArrayList<MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>> setOfSolutions = new ArrayList<MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>>();

  /**
   * Map from reserve to ouctome.
   */
  protected final Map<MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>, Double> reserveToOutcomeMap = new HashMap<MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>, Double>();

  /**
   * Constructor.
   * 
   * @param market
   * @param AllocAlgo
   * @throws PrincingAlgoException
   */
  public SearchMetaHeuristic(Market<Goods, Bidder<Goods>> market, AllocationAlgo<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> AllocAlgo) throws PrincingAlgoException {
    if (!AllocAlgo.getObjectiveFunction().isSafeForReserve()) {
      throw new PrincingAlgoException("Try to run RevMaxHeuristic with an allocation algorithm that optimizes a function that is not reserve safe.");
    }
    this.market = market;
    this.AllocAlgo = AllocAlgo;
  }

  /**
   * 
   * @return
   * @throws IloException
   * @throws AllocationAlgoException
   * @throws BidderCreationException
   * @throws MarketAllocationException
   * @throws AllocationException
   * @throws GoodsException
   * @throws MarketOutcomeException
   * @throws MarketCreationException
   * @throws PrincingAlgoException
   */
  public MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> Solve() throws IloException, AllocationAlgoException, BidderCreationException, MarketAllocationException, AllocationException, GoodsException, MarketOutcomeException, MarketCreationException, PrincingAlgoException {
    // Maintain a hashmap of seen reserve prices so that we don't repeat computation.
    HashSet<Double> seenReservePrices = new HashSet<Double>();
    ArrayList<Double> listOfReservePrices = this.getListOfReservePrices();
    // Check if the list of reserve prices is null
    if (listOfReservePrices == null) {
      throw new MarketOutcomeException("Trying to run search metaheuristic with a null list of reserve prices.");
    } else {
      // Add reserve 0.0 as a default value.
      listOfReservePrices.add(0.0);
      // System.out.println("List of reserves: " + listOfReservePrices);
    }
    for (Double reserve : listOfReservePrices) {
      if (!seenReservePrices.contains(reserve)) {
        // Mark the reserve price as seen.
        seenReservePrices.add(reserve);
        // System.out.println("---------- candidate reserve price = " + reserve);
        // Get the market with the reserve price.
        BasicMarketWithReserve mwrp = new BasicMarketWithReserve(this.market, reserve);
        // System.out.println(mwrp.areThereBiddersInTheMarketWithReserve());
        // Test if there are bidders in the market with reserve.
        if (mwrp.areThereBiddersInTheMarketWithReserve()) {
          // Solve for a MarketAllocation in the market with reserve.
          MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> allocForMarketWithReserve = this.AllocAlgo.Solve(mwrp.getMarketWithReservePrice());
          // Deduce a MarketAllocation for the original market.
          MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> allocForOriginalMarket = mwrp.deduceAllocation(allocForMarketWithReserve);
          // Run LP with reserve prices.
          RestrictedEnvyFreePricesLPWithReserve<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> efp = new RestrictedEnvyFreePricesLPWithReserve<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(allocForOriginalMarket, reserve);
          efp.createLP();
          RestrictedEnvyFreePricesLPSolution<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> refpSol = efp.Solve();
          if (refpSol.getStatus().equals("Infeasible")) {
            throw new PrincingAlgoException("Found an infeasible LP!");
          }
          this.setOfSolutions.add(refpSol);
          this.reserveToOutcomeMap.put(refpSol, reserve);
        }
      }
    }
    Collections.sort(this.setOfSolutions, new MarketOutcomeComparatorBySellerRevenue<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>());
    //System.out.println("Max-Rev reserve = " + this.reserveToOutcomeMap.get(setOfSolutions.get(0)));
    return setOfSolutions.get(0);
  }

  /**
   * Getter.
   * 
   * @return the set of solutions found by the algorithm.
   */
  public ArrayList<MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>> getSetOfSolutions() {
    return this.setOfSolutions;
  }

  /**
   * Implemented by the client.
   * 
   * @return a list of candidate reserve prices.
   */
  abstract protected ArrayList<Double> getListOfReservePrices();

}
