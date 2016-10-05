package algorithms.pricing.reserveprices;

import ilog.concert.IloException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketOutcome;
import structures.comparators.MarketPricesComparatorBySellerRevenue;
import structures.exceptions.AllocationException;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketCreationException;
import structures.exceptions.MarketOutcomeException;
import algorithms.pricing.RestrictedEnvyFreePricesLPSolution;
import algorithms.pricing.RestrictedEnvyFreePricesLPWithReserve;
import algorithms.pricing.error.PrincingAlgoException;
import allocations.error.AllocationAlgoException;
import allocations.interfaces.AllocationAlgo;

/**
 * This class implements a strategy for searching for a revenue-maximizing solution.
 * 
 * @author Enrique Areyan Viqueira
 */
public class RevMaxHeuristic {

  /**
   * Market object.
   */
  protected Market<Goods, Bidder<Goods>> market;
  
  /**
   * MarketAllocation object.
   */
  protected MarketAllocation<Goods, Bidder<Goods>> initialMarketAllocation;
  
  /**
   * ArrayList of MarketPrices.
   */
  protected ArrayList<MarketOutcome<Goods, Bidder<Goods>>> setOfSolutions;
  
  /**
   * AllocationAlgorithm object. 
   */
  protected AllocationAlgo<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> AllocAlgo;

  /**
   * Constructor for LPReservePrices.
   * 
   * @param market - market on which to find a revenue-max solution
   * @param AllocAlgo - which allocation algorithm to use
   * @throws IloException if the LP fails
   * @throws AllocationAlgoException if the allocation algorithm fails
   * @throws BidderCreationException possibly thrown by the allocation algorithm
   * @throws GoodsException 
   * @throws MarketAllocationException 
   * @throws AllocationException 
   * @throws MarketOutcomeException 
   * @throws PrincingAlgoException 
   */
  public RevMaxHeuristic(Market<Goods, Bidder<Goods>> market, AllocationAlgo<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> AllocAlgo) throws IloException, AllocationAlgoException, BidderCreationException, AllocationException, MarketAllocationException, GoodsException, MarketOutcomeException, PrincingAlgoException {
    if(!AllocAlgo.getObjectiveFunction().isSafeForReserve()){
      throw new PrincingAlgoException("Try to run RevMaxHeuristic with an allocation algorithm that optimizes a function that is not reserve safe.");
    }
    this.market = market;
    this.AllocAlgo = AllocAlgo;
    // The initial allocation has reserve price of 0. The solution is the plain LP.
    this.initialMarketAllocation = this.AllocAlgo.Solve(market);
    //this.initialMarketAllocation.printAllocation();
    this.setOfSolutions = new ArrayList<MarketOutcome<Goods, Bidder<Goods>>>();
    // Create the first LP with no reserves.
    RestrictedEnvyFreePricesLPWithReserve initialLP = new RestrictedEnvyFreePricesLPWithReserve(this.initialMarketAllocation);
    initialLP.setMarketClearanceConditions(false);
    initialLP.createLP();
    RestrictedEnvyFreePricesLPSolution initialSolution = initialLP.Solve();
    //initialSolution.printPrices();
    this.setOfSolutions = new ArrayList<MarketOutcome<Goods, Bidder<Goods>>>();
    //Add initial solution to set of solutions, so that we have a baseline with reserve prices all zero.
    setOfSolutions.add(initialSolution);
  }

  /**
   * Solve method.
   * 
   * @throws IloException in case the LP failed.
   * @throws AllocationAlgoException in case the Allocation algorithm failed.
   * @throws BidderCreationException possibly thrown by the allocation algorithm
   * @return a MarketPrices object.
   * @throws GoodsException 
   * @throws AllocationException 
   * @throws MarketAllocationException 
   * @throws MarketOutcomeException 
   * @throws MarketCreationException 
   */
  public MarketOutcome<Goods, Bidder<Goods>> Solve() throws IloException, AllocationAlgoException, BidderCreationException, MarketAllocationException, AllocationException, GoodsException, MarketOutcomeException, MarketCreationException {
    HashSet<Double> seenReservePrices = new HashSet<Double>();
    for (Goods good : this.market.getGoods()) {
      for (Bidder<Goods> bidder : this.market.getBidders()) {
        if (this.initialMarketAllocation.getAllocation(good, bidder) > 0) {
          // For all x_{ij} > 0.
          double reserve = bidder.getReward() / this.initialMarketAllocation.getAllocation(good, bidder);
          if(!seenReservePrices.contains(reserve)){
            seenReservePrices.add(reserve);
            //System.out.println("---------- candidate reserve price = " + reserve);
            // Get the market with the reserve price.
            MarketWithReservePrice mwrp = new MarketWithReservePrice(this.market, reserve);
            // Test if there are bidders in the market with reserve.
            if (mwrp.thereBiddersInTheMarketWithReserve()) {
              // Solve for a MarketAllocation in the market with reserve.
              MarketAllocation<Goods, Bidder<Goods>> allocForMarketWithReserve = this.AllocAlgo.Solve(mwrp.getMarketWithReservePrice());
              //allocForMarketWithReserve.printAllocation();
              // Deduce a MarketAllocation for the original market.
              MarketAllocation<Goods, Bidder<Goods>> allocForOriginalMarket = mwrp.deduceAllocation(allocForMarketWithReserve);
              //allocForOriginalMarket.printAllocation();
              // Run LP with reserve prices.
              RestrictedEnvyFreePricesLPWithReserve efp = new RestrictedEnvyFreePricesLPWithReserve(allocForOriginalMarket);
              efp.setMarketClearanceConditions(false);
              efp.createLP();
              efp.setReservePrice(reserve);
              RestrictedEnvyFreePricesLPSolution refpSol = efp.Solve();
              //System.out.println("Status = " + refpSol.getStatus());
              //refpSol.printPrices();
              //System.out.println(refpSol.sellerRevenue());
              setOfSolutions.add(refpSol);
            }
          }
        }
      }
    }
    //System.out.println(setOfSolutions.size());
    //System.out.println(setOfSolutions);
    Collections.sort(setOfSolutions, new MarketPricesComparatorBySellerRevenue());
    return setOfSolutions.get(0);
  }

}
