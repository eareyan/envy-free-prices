package allocations.objectivefunction;

import allocations.objectivefunction.interfaces.SafeReserveFunction;

/**
 * Identity function.
 * 
 * @author Enrique Areyan Viqueira
 */
public class IdentityObjectiveFunction implements SafeReserveFunction {
  
  /**
   * Implements an identity objective function. 
   */
  public double getObjective(double reward, double total, double x) {
    return (x <= total) ? (reward / total) * x : reward;
  }
  
}
