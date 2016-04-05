package algorithms.pricing.lp.reserveprices;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import algorithms.allocations.EfficientAllocationILP;
import structures.Market;
import structures.MarketAllocation;

public class EfficientAlloc implements AllocationAlgorithm{

	@Override
	public MarketAllocation getAllocWithReservePrice(Market market, double reserve){
		try {
			//System.out.println("EfficientAlloc with reserve = " + reserve);
			return new MarketAllocation(market,new EfficientAllocationILP(market , reserve).Solve(new IloCplex()).get(0));
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
