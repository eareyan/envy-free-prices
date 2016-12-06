package allocations.optimal;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;

import java.util.HashMap;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import allocations.objectivefunction.SingleStepObjectiveFunction;

/**
 * Egalitarian Maximizing ILP.
 * 
 * @author Enrique Areyan Viqueira
 *
 * @param <M>
 * @param <G>
 * @param <B>
 */
public class EgalitarianMaxAllocation<M extends Market<G, B>, G extends Goods, B extends Bidder<G>> extends OptimalAllocILP<M, G, B> {

  @Override
  protected IloNumExpr getILPObjective(M market, IloNumVar[] indicatorVariable, HashMap<B, Integer> bidderToCPLEXIndex) throws IloException {
    // LP objective function. \sum_j y_j, i.e., of allocated bidders.
    IloLinearNumExpr obj = this.cplex.linearNumExpr();
    for (B bidder : market.getBidders()) {
      obj.addTerm(1.0, indicatorVariable[bidderToCPLEXIndex.get(bidder)]);
    }
    return obj;
  }
  
  @Override
  public SingleStepObjectiveFunction getObjectiveFunction() {
    return new SingleStepObjectiveFunction();
  }
  
  @Override
  public String toString() {
    return "SingleStepWelfareMaxAllocationILP which always uses SingleStepFunction objective";
  }

}
