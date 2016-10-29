package allocations.objectivefunction;

import allocations.objectivefunction.interfaces.ObjectiveFunction;

/**
 * This function is identity for the first half the reach and then sigmoid for
 * the second half.
 * 
 * @author Enrique Areyan Viqueira
 */
public class LinearSigmoidObjectiveFunction implements ObjectiveFunction {

  @Override
  public double getObjective(double reward, double total, double x) {
    if(x > (total / 2.0)){
      return new EffectiveReachRatio().getObjective(reward, total, x);
    }else{
      return new IdentityObjectiveFunction().getObjective(reward, total, x);
    }
  }

  @Override
  public boolean isSafeForReserve() {
    return true;
  }

}
