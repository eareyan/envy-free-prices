package allocations.objectivefunction;

/**
 * This interface defines a single method that all
 * objective functions must implement. 
 * @author Enrique Areyan Viqueira
 */
public interface ObjectiveFunction {
  
  /**
   * This method receives the total impressions demanded by a campaign and an
   * argument x, and returns the partial reward to be given to campaign for
   * attaining x impressions.
   * @param reward - reward of a campaign.
   * @param total - total demand of a campaign.
   * @param x - the allocation to a campaign.
   * @return the value of the reward to the campaign for attaining x impressions.
   */
  public double getObjective(double reward, double total, double x);
  
}
