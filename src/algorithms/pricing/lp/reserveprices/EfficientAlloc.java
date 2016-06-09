package algorithms.pricing.lp.reserveprices;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import allocations.error.AllocationException;
import allocations.optimal.SingleStepEfficientAllocationILP;
import structures.Market;
import structures.MarketAllocation;

public class EfficientAlloc implements AllocationAlgorithm{

	@Override
	public MarketAllocation getAllocWithReservePrice(Market market, double reserve) throws AllocationException{
		try {
			//System.out.println("EfficientAlloc with reserve = " + reserve);
			return new MarketAllocation(market,new SingleStepEfficientAllocationILP(market , reserve).Solve(new IloCplex()).get(0));
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
