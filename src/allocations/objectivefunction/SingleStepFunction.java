package allocations.objectivefunction;

/**
 * This class implements the all-or-nothing objective function.
 * 
 * @author Enrique Areyan Viqueira
 */
public class SingleStepFunction implements ObjectiveFunction {

  /**
   * All-or-nothing objective.
   */
  public double getObjective(double reward, double total, double x) {
    return (x >= total) ? reward : 0.0;
  }
}
