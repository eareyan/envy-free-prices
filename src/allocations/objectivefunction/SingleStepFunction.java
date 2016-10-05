package allocations.objectivefunction;

import allocations.objectivefunction.interfaces.SafeReserveFunction;

/**
 * This class implements the all-or-nothing objective function.
 * 
 * @author Enrique Areyan Viqueira
 */
public class SingleStepFunction implements SafeReserveFunction {

  /**
   * All-or-nothing objective.
   */
  public double getObjective(double reward, double total, double x) {
    return (x >= total) ? reward : 0.0;
  }
}
