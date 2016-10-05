package allocations.interfaces;

import ilog.concert.IloException;
import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.exceptions.AllocationException;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketAllocationException;
import allocations.error.AllocationAlgoException;
import allocations.objectivefunction.interfaces.ObjectiveFunction;

/**
 * This interface defines an allocation algorithm. The interface defines two
 * methods:
 * 
 * The first a method is Solve which, given a market returns a MarketAllocation
 * object.
 * 
 * The second method returns an Objective Function. The idea is that an
 * allocation algorithm optimizes some objective function which is returned by
 * this methods.
 * 
 * @author Enrique Areyan Viqueira
 * @param <G>
 * @param <B>
 */
public interface AllocationAlgo<M extends Market<G, B>, G extends Goods, B extends Bidder<G>, O extends ObjectiveFunction> {

  /**
   * The most important method of an allocation algorithm. Given a Market,
   * return a MarketAllocation object.
   * 
   * @param market
   * @return
   * @throws IloException
   * @throws AllocationAlgoException
   * @throws BidderCreationException
   * @throws GoodsCreationException
   * @throws AllocationException 
   * @throws GoodsException 
   * @throws MarketAllocationException 
   */
  public MarketAllocation<G, B, O> Solve(M market) throws IloException, AllocationAlgoException, BidderCreationException, GoodsCreationException, AllocationException, GoodsException, MarketAllocationException;

  /**
   * This method defines the objective function that was used to allocate the
   * market.
   */
  public O getObjectiveFunction();
}
