package experiments;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;

import java.sql.SQLException;

import log.SqlDB;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import structures.Market;
import structures.MarketAllocation;
import structures.factory.RandomMarketFactory;
import util.NumberMethods;
import algorithms.Waterfall;
import algorithms.allocations.EfficientAllocationILP;
import algorithms.allocations.greedy.CampaignComparatorByRewardToImpressionsRatio;
import algorithms.allocations.greedy.GreedyAllocation;
import algorithms.allocations.greedy.UsersSupplyComparatorByRemainingSupply;

public class allocation extends Experiments{
	
	public void runOneExperiment(int numUsers,int numCampaigns, double prob, int b, SqlDB dbLogger) throws SQLException, IloException {
		if(!dbLogger.checkIfUnitDemandRowExists("allocation",numUsers, numCampaigns, prob)){
			System.out.println("\t Add data ");
			DescriptiveStatistics greedyToEfficient = new DescriptiveStatistics();
			DescriptiveStatistics wfToEfficient = new DescriptiveStatistics();
			DescriptiveStatistics wfToGreedy = new DescriptiveStatistics();

			for(int t=0;t<RunParameters.numTrials;t++){
				/* Create a random Market*/
				Market randomMarket = RandomMarketFactory.randomMarket(numUsers, numCampaigns, prob);
				/* Compute different allocations */
				MarketAllocation efficient = new MarketAllocation(randomMarket,new EfficientAllocationILP(randomMarket).Solve(new IloCplex()).get(0));
				MarketAllocation greedy = new GreedyAllocation(randomMarket,new CampaignComparatorByRewardToImpressionsRatio(), new UsersSupplyComparatorByRemainingSupply(1),true).Solve();
				//MarketAllocation greedy2 = new GreedyAllocation(randomMarket,new CampaignComparatorByRewardToImpressionsRatio(), new UsersSupplyComparatorByRemainingSupply(-1)).Solve();
				MarketAllocation wf = new Waterfall(randomMarket).Solve().getMarketAllocation();
				/* Compute statistics */
				greedyToEfficient.addValue(NumberMethods.getRatio(greedy.value() , efficient.value()));
				wfToEfficient.addValue(NumberMethods.getRatio(wf.value() , efficient.value()));
				wfToGreedy.addValue(NumberMethods.getRatio(wf.value() , greedy.value()));
				
			}
			dbLogger.saveAllocationData(numUsers, numCampaigns, prob, greedyToEfficient.getMean(), wfToEfficient.getMean(), wfToGreedy.getMean());
		}else{
			System.out.println("\t Already have data ");			
		}
	}
}
