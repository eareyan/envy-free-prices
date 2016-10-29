package experiments;

import ilog.concert.IloException;

import java.sql.SQLException;

import log.SqlDB;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.exceptions.AllocationException;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketCreationException;
import structures.factory.RandomMarketFactory;
import util.NumberMethods;
import allocations.error.AllocationAlgoException;
import allocations.greedy.GreedyMultiStepAllocation;
import allocations.objectivefunction.EffectiveReachRatio;
import allocations.objectivefunction.SingleStepObjectiveFunction;
import allocations.objectivefunction.interfaces.ObjectiveFunction;
import allocations.optimal.SingleStepWelfareMaxAllocationILP;

@SuppressWarnings("unused")
/**
 * Allocation algorithms experiments. 
 * 
 * @author Enrique Areyan Viqueira
 */
public class allocation extends Experiments {

  public void runOneExperiment(int numUsers, int numCampaigns, double prob, int b, SqlDB dbLogger) throws SQLException, IloException, AllocationAlgoException, BidderCreationException, MarketAllocationException, GoodsException, AllocationException, MarketCreationException {
    if (!dbLogger.checkIfUnitDemandRowExists("allocation", numUsers, numCampaigns, prob)) {
      System.out.println("\t Add data ");
      DescriptiveStatistics greedyToEfficient = new DescriptiveStatistics();

      for (int t = 0; t < RunParameters.numTrials; t++) {
        // Create a random Market.
        Market<Goods, Bidder<Goods>> randomMarket = RandomMarketFactory.randomMarket(numUsers, numCampaigns, prob);
        // Compute different allocations.
        MarketAllocation<Goods, Bidder<Goods>> efficient = new SingleStepWelfareMaxAllocationILP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>().Solve(randomMarket);
        MarketAllocation<Goods, Bidder<Goods>> greedy = new GreedyMultiStepAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(1, new EffectiveReachRatio()).Solve(randomMarket);
        /* Compute statistics */
        double greedyValue = greedy.value();
        double efficientValue = efficient.value();
        double ratio = 0.0;
        if ((greedyValue == 0.0 && efficientValue == 0.0) || efficientValue == 0.0) {
          ratio = 1.0;
        } else {
          ratio = greedyValue / efficientValue;
        }
        greedyToEfficient.addValue(ratio);
      }
      dbLogger.saveAllocationData(numUsers, numCampaigns, prob, greedyToEfficient.getMean());
    } else {
      System.out.println("\t Already have data ");
    }
  }
}
