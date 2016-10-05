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
import allocations.error.AllocationAlgoException;
import allocations.interfaces.AllocationAlgo;
import allocations.objectivefunction.interfaces.SafeReserveFunction;

import com.google.common.collect.HashBasedTable;

/**
 * This class implements a strategy for searching for a revenue-maximizing solution.
 * 
 * @author Enrique Areyan Viqueira
 */
public class RevMaxHeuristic<T extends SafeReserveFunction>{

  /**
   * Market object.
   */
  protected Market<Goods, Bidder<Goods>> market;
  
  /**
   * MarketAllocation object.
   */
  protected MarketAllocation<Goods, Bidder<Goods>, T> initialMarketAllocation;
  
  /**
   * ArrayList of MarketPrices.
   */
  protected ArrayList<MarketOutcome<Goods, Bidder<Goods>, T>> setOfSolutions;
  
  /**
   * AllocationAlgorithm object. 
   */
  protected AllocationAlgo<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>, T> AllocAlgo;

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
   */
  public RevMaxHeuristic(Market<Goods, Bidder<Goods>> market, AllocationAlgo<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>, T> AllocAlgo) throws IloException, AllocationAlgoException, BidderCreationException, AllocationException, MarketAllocationException, GoodsException, MarketOutcomeException {
    this.market = market;
    this.AllocAlgo = AllocAlgo;
    // The initial allocation has reserve price of 0. The solution is the plain LP.
    this.initialMarketAllocation = this.AllocAlgo.Solve(market);
    //this.initialMarketAllocation.printAllocation();
    this.setOfSolutions = new ArrayList<MarketOutcome<Goods, Bidder<Goods>, T>>();
    // Create the first LP with no reserves.
    RestrictedEnvyFreePricesLPWithReserve<T> initialLP = new RestrictedEnvyFreePricesLPWithReserve<T>(this.initialMarketAllocation);
    initialLP.setMarketClearanceConditions(false);
    initialLP.createLP();
    RestrictedEnvyFreePricesLPSolution<T> initialSolution = initialLP.Solve();
    //initialSolution.printPrices();
    this.setOfSolutions = new ArrayList<MarketOutcome<Goods, Bidder<Goods>, T>>();
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
  public MarketOutcome<Goods, Bidder<Goods>, T> Solve() throws IloException, AllocationAlgoException, BidderCreationException, MarketAllocationException, AllocationException, GoodsException, MarketOutcomeException, MarketCreationException {
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
            // If we get a null market with reserve prices, it means that there are no bidders.
            if(mwrp.getMarketWithReservePrice() != null){
              // Solve for a MarketAllocation in the market with reserve.
              MarketAllocation<Goods, Bidder<Goods>, T> allocForMarketWithReserve = this.AllocAlgo.Solve(mwrp.getMarketWithReservePrice());
              //allocForMarketWithReserve.printAllocation();
              // Deduce a MarketAllocation for the original market.
              MarketAllocation<Goods, Bidder<Goods>, T> allocForOriginalMarket = this.deduceAllocation(mwrp, allocForMarketWithReserve, this.AllocAlgo.getObjectiveFunction());
              //allocForOriginalMarket.printAllocation();
              // Run LP with reserve prices.
              RestrictedEnvyFreePricesLPWithReserve<T> efp = new RestrictedEnvyFreePricesLPWithReserve<T>(allocForOriginalMarket);
              efp.setMarketClearanceConditions(false);
              efp.createLP();
              efp.setReservePrice(reserve);
              RestrictedEnvyFreePricesLPSolution<T> refpSol = efp.Solve();
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
    Collections.sort(setOfSolutions, new MarketPricesComparatorBySellerRevenue<T>());
    return setOfSolutions.get(0);
  }
  
  /**
   * Given a MarketWithReservePrice object and an allocation for a
   * marketWithReserve, deduce an allocation for the original market from which
   * the market with reserve was produced.
   * 
   * @param marketWithReserveObject
   * @param allocForMarketWithReserve
   * @return
   * @throws MarketAllocationException
   */
  private MarketAllocation<Goods, Bidder<Goods>, T> deduceAllocation(MarketWithReservePrice marketWithReserveObject, MarketAllocation<Goods, Bidder<Goods>, T> allocForMarketWithReserve, T f) throws MarketAllocationException{
    
    HashBasedTable<Goods,Bidder<Goods>,Integer> deducedAllocation = HashBasedTable.create();
    for(Goods good : marketWithReserveObject.getMarket().getGoods()){
      for(Bidder<Goods> bidder : marketWithReserveObject.getMarket().getBidders()){
        if(marketWithReserveObject.getBidderToBidderMap().containsKey(bidder)){
          deducedAllocation.put(good, bidder, allocForMarketWithReserve.getAllocation(good, marketWithReserveObject.getBidderToBidderMap().get(bidder)));
        }else{
          deducedAllocation.put(good, bidder, 0);
        }
      }
    }
    return new MarketAllocation<Goods, Bidder<Goods>, T>(marketWithReserveObject.getMarket(), deducedAllocation, f);
  }

}
