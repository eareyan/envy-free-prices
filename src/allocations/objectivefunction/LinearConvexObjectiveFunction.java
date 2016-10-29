package allocations.objectivefunction;

import allocations.objectivefunction.interfaces.ObjectiveFunction;

/**
 * Constant objective function, always equal to reward.
 * 
 * @author Enrique Areyan Viqueira
 */
public class LinearConvexObjectiveFunction implements ObjectiveFunction {

  @Override
  public double getObjective(double reward, double total, double x) {
    if(x > (total / 2.0)){
      return new ConvexObjectiveFunction().getObjective(reward, total, x);
    }else{
      return new IdentityObjectiveFunction().getObjective(reward, total, x);
    }
  }

  @Override
  public boolean isSafeForReserve() {
    return true;
  }

}
