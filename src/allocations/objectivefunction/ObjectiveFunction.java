package allocations.objectivefunction;

public interface ObjectiveFunction {
	/*
	 * This method receives the total impressions demanded by a campaign
	 * and an argument x, and returns the partial reward to be given to
	 * campaign for attaining a reward of x.
	 */
	public double getObjective(double reward, double total, double x);
}
