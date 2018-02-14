package singleminded.algorithms.complete;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.HashMap;
import java.util.HashSet;

import singleminded.structures.SingleMindedMarket;
import structures.Bidder;
import structures.Goods;
import util.Cplex;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * Implements the LP to maximize EFP prices under the assumption of some bidders beign winners. The LP could be infeasible.
 * 
 * @author Enrique Areyan Viqueira
 */
public class LP {

  /**
   * Solve the LP.
   * 
   * @param market
   * @param winners - all other bidders outside this set are assumed to be losers.
   * @return
   * @throws IloException 
   * @throws Exception
   */
  public static LPSolution solve(SingleMindedMarket<Goods, Bidder<Goods>> market, HashSet<Bidder<Goods>> winners) throws LPException, IloException {
    IloCplex cplex = Cplex.getCplex();
    cplex.setOut(null);
    // Create a map from goods to a numbers. This gives ordering of the goods.
    HashMap<Goods, Integer> goodToPriceIndex = new HashMap<Goods, Integer>();
    for (int i = 0; i < market.getNumberGoods(); i++) {
      goodToPriceIndex.put(market.getGoods().get(i), i);
    }
    IloNumVar[] prices = cplex.numVarArray(market.getNumberGoods(), 0.0, Double.MAX_VALUE);
    // Create constraints
    for (Bidder<Goods> bidder : market.getBidders()) {
      // System.out.println("bidder " + bidder + " is a winner = " + winners.contains(bidder));
      IloLinearNumExpr expr = cplex.linearNumExpr();
      for (Goods good : bidder.getDemandSet()) {
        expr.addTerm(1.0, prices[goodToPriceIndex.get(good)]);
      }
      if (winners.contains(bidder)) {
        // If the bidder is a winner, enforce IR.
        cplex.addGe(bidder.getReward(), expr);
      } else {
        // If the bidder is a loser, enforce negation of IR
        cplex.addLe(bidder.getReward(), expr);
      }
    }
    // An artificial constraint - the price of any good cannot be more than the max reward
    for (Goods good : market.getGoods()) {
      cplex.addLe(prices[goodToPriceIndex.get(good)], market.getHighestReward());

    }
    // Create Objective function
    HashSet<Goods> coveredGoods = new HashSet<Goods>();
    IloLinearNumExpr expr = cplex.linearNumExpr();
    for (Bidder<Goods> bidder : market.getBidders()) {
      // Add the price of only goods that are actually allocated.
      if (winners.contains(bidder)) {
        for (Goods good : bidder.getDemandSet()) {
          if (!coveredGoods.contains(good)) {
            coveredGoods.add(good);
            expr.addTerm(1.0, prices[goodToPriceIndex.get(good)]);
          } else {
            throw new LPException("There are two bidders that demand the same good and they are both winners!");
          }
        }
      }
    }
    cplex.addMaximize(expr);
    // Solve the LP.
    if (cplex.solve()) {
      // System.out.println("Status = " + cplex.getStatus());
      // System.out.println("ObjValue = " + cplex.getObjValue());
      double[] LP_Prices = cplex.getValues(prices);
      Builder<Goods, Double> result = ImmutableMap.<Goods, Double> builder();
      for (Goods good : market.getGoods()) {
        result.put(good, LP_Prices[goodToPriceIndex.get(good)]);
      }
      return new LPSolution(LPSolution.Status.Optimal, result.build(), cplex.getObjValue());
    } else {
      // if (!cplex.isDualFeasible()) {
      // System.out.println("Primal is unbounded! - discovered during presolve");
      // return new LPSolution(LPSolution.Status.Unbounded, null, Double.NEGATIVE_INFINITY);
      // }
      if (!cplex.isPrimalFeasible()) {
        // System.out.println("Primal is infeasible! - discovered during presolve");
        return new LPSolution(LPSolution.Status.Infeasible, null, Double.NEGATIVE_INFINITY);
      }
    }
    throw new LPException("Unclear what the LP is doing.");
  }

}
