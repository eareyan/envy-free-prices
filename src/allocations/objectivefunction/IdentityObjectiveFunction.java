package allocations.objectivefunction;

public class IdentityObjectiveFunction implements ObjectiveFunction{
	public double getObjective(double reward, double total, double x){
		return (x<=total)?(reward/total)*x : reward; 
	}
}
