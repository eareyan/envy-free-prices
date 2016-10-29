package allocations.objectivefunction;

import allocations.objectivefunction.interfaces.ObjectiveFunction;

/**
 * Approximation of the Effective Reach Ratio as a convex function.
 * 
 * @author Enrique Areyan Viqueira
 */
public class ConvexObjectiveFunction  implements ObjectiveFunction {

  @Override
  public double getObjective(double reward, double total, double x) {
    return ((reward / (total * total)) * x * x);
  }

  @Override
  public boolean isSafeForReserve() {
    return false;
  }

}
