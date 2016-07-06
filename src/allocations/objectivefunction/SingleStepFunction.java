package allocations.objectivefunction;

public class SingleStepFunction implements ObjectiveFunction{
	
	public double getObjective(double reward, double total, double x){
		return (x >= total) ? reward : 0.0;
	}
}
