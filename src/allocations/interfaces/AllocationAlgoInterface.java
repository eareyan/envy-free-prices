package allocations.interfaces;

import ilog.concert.IloException;
import allocations.error.AllocationException;
import allocations.objectivefunction.ObjectiveFunction;
import structures.Market;
import structures.MarketAllocation;
import structures.exceptions.CampaignCreationException;

/**
 * This interface defines an allocation algorithm.
 * The interface defines two methods: 
 * 
 * The first a method is Solve which,
 * given a market returns a MarketAllocation object.
 * 
 * The second method returns an Objective Function. The idea is that
 * an allocation algorithm optimizes some objective function which is 
 * returned by this methods.
 * 
 * @author Enrique Areyan Viqueira
 */
public interface AllocationAlgoInterface {
  
  /**
   * The most important method of an allocation algorithm. Given a Market,
   * return a MarketAllocation object.
   */
  public MarketAllocation Solve(Market market) throws IloException,
      AllocationException, CampaignCreationException;

  /**
   * This method defines the objective function that was used to allocate the
   * market.
   */
  public ObjectiveFunction getObjectiveFunction();
}
