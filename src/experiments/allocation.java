package experiments;

import ilog.concert.IloException;

import java.sql.SQLException;

import log.SqlDB;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import structures.Market;
import structures.MarketAllocation;
import structures.exceptions.CampaignCreationException;
import structures.exceptions.MarketAllocationException;
import structures.factory.RandomMarketFactory;
import util.NumberMethods;
import allocations.error.AllocationException;
import allocations.greedy.multistep.GreedyMultiStepAllocation;
import allocations.objectivefunction.EffectiveReachRatio;
import allocations.optimal.MultiStepEfficientAllocationILP;

@SuppressWarnings("unused")
public class allocation extends Experiments{
	
	public void runOneExperiment(int numUsers,int numCampaigns, double prob, int b, SqlDB dbLogger) throws SQLException, IloException, AllocationException, CampaignCreationException, MarketAllocationException {
		if(!dbLogger.checkIfUnitDemandRowExists("allocation",numUsers, numCampaigns, prob)){
			System.out.println("\t Add data ");
			DescriptiveStatistics greedyToEfficient = new DescriptiveStatistics();

			for(int t=0;t<RunParameters.numTrials;t++){
				/* Create a random Market*/
				Market randomMarket = RandomMarketFactory.randomMarket(numUsers, numCampaigns, prob);
				/* Compute different allocations */
				MarketAllocation efficient = new MarketAllocation(randomMarket,new MultiStepEfficientAllocationILP(1,new EffectiveReachRatio()).Solve(randomMarket).getAllocation());
				MarketAllocation greedy = new GreedyMultiStepAllocation(1, new EffectiveReachRatio()).Solve(randomMarket);
				/* Compute statistics */
				double greedyValue = greedy.value();
				double efficientValue = efficient.value();
				double ratio = 0.0;
				if((greedyValue == 0.0 && efficientValue == 0.0) || efficientValue == 0.0){
					ratio = 1.0;
				}else{
					ratio = greedyValue / efficientValue;
				}
				greedyToEfficient.addValue(ratio);
			}
			dbLogger.saveAllocationData(numUsers, numCampaigns, prob, greedyToEfficient.getMean());
		}else{
			System.out.println("\t Already have data ");			
		}
	}
}
