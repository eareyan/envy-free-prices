package allocations.objectivefunction;

import allocations.objectivefunction.interfaces.ObjectiveFunction;

/**
 * This class implements the all-or-nothing objective function.
 * 
 * @author Enrique Areyan Viqueira
 */
public class SingleStepObjectiveFunction implements ObjectiveFunction{

  /**
   * All-or-nothing objective.
   */
  public double getObjective(double reward, double total, double x) {
    return (x >= total) ? reward : 0.0;
  }

  @Override
  public boolean isSafeForReserve() {
    return true;
  }
}
