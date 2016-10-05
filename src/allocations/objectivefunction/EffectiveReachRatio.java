package allocations.objectivefunction;

import allocations.objectivefunction.interfaces.ObjectiveFunction;

/**
 * Effective Reach Ratio as defined in the TAC Game.
 * 
 * @author Enrique Areyan Viqueira
 */
public class EffectiveReachRatio implements ObjectiveFunction {
  
  /**
   * Implements the ERR objective.
   */
  public double getObjective(double reward, double total, double x) {
    return reward * (2 / 4.08577) * (Math.atan(4.08577 * (x / total) - 3.08577) - Math.atan(-3.08577));
  }
  
}
