package allocations.objectivefunction;

import allocations.objectivefunction.interfaces.ObjectiveFunction;

/**
 * Approximation of the Effective Reach Ratio as a quadratic function.
 * 
 * @author Enrique Areyan Viqueira
 */
public class QuadraticObjectiveFunction implements ObjectiveFunction {

  @Override
  public double getObjective(double reward, double total, double x) {
    return (-1 * (reward / (total * total)) * x * x) + 2 * (reward / total) * x;
  }

  @Override
  public boolean isSafeForReserve() {
    return true;
  }
  
}
