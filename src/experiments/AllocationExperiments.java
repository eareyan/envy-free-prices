package experiments;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;

import java.sql.SQLException;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import structures.Market;
import structures.MarketAllocation;
import structures.factory.MarketFactory;
import util.NumberMethods;
import algorithms.Waterfall;
import algorithms.allocations.EfficientAllocationILP;
import algorithms.allocations.GreedyAllocation;
import log.SqlDB;

public class AllocationExperiments extends Experiments{
	
	public void runOneExperiment(int numUsers,int numCampaigns, double prob, SqlDB dbLogger) throws SQLException, IloException {
		if(!dbLogger.checkIfUnitDemandRowExists("allocation",numUsers, numCampaigns, prob)){
			System.out.println("\t Add data ");
			DescriptiveStatistics greedy0ToEfficient = new DescriptiveStatistics();
			DescriptiveStatistics greedy1ToEfficient = new DescriptiveStatistics();
			DescriptiveStatistics greedy2ToEfficient = new DescriptiveStatistics();
			DescriptiveStatistics wfToEfficient = new DescriptiveStatistics();
			DescriptiveStatistics wfToGreedy0 = new DescriptiveStatistics();
			DescriptiveStatistics wfToGreedy1 = new DescriptiveStatistics();
			DescriptiveStatistics wfToGreedy2 = new DescriptiveStatistics();

			for(int t=0;t<RunParameters.numTrials;t++){
				/* Create a random Market*/
				Market randomMarket = MarketFactory.randomMarket(numUsers, numCampaigns, prob);
				/* Compute different allocations */
				MarketAllocation efficient = new MarketAllocation(randomMarket,new EfficientAllocationILP(randomMarket).Solve(new IloCplex()).get(0));
				MarketAllocation greedy0 = new GreedyAllocation(randomMarket).Solve();
				MarketAllocation greedy1 = new GreedyAllocation(randomMarket,1).Solve();
				MarketAllocation greedy2 = new GreedyAllocation(randomMarket,-1).Solve();
				MarketAllocation wf = new Waterfall(randomMarket).Solve().getMarketAllocation();
				/* Compute statistics */
				greedy0ToEfficient.addValue(NumberMethods.getRatio(greedy0.value() , efficient.value()));
				greedy1ToEfficient.addValue(NumberMethods.getRatio(greedy1.value() , efficient.value()));
				greedy2ToEfficient.addValue(NumberMethods.getRatio(greedy2.value() , efficient.value()));
				wfToEfficient.addValue(NumberMethods.getRatio(wf.value() , efficient.value()));
				wfToGreedy0.addValue(NumberMethods.getRatio(wf.value() , greedy0.value()));
				wfToGreedy1.addValue(NumberMethods.getRatio(wf.value() , greedy1.value()));
				wfToGreedy2.addValue(NumberMethods.getRatio(wf.value() , greedy2.value()));
				
			}
			dbLogger.saveAllocationData(numUsers, numCampaigns, prob, greedy0ToEfficient.getMean(), greedy1ToEfficient.getMean(), greedy2ToEfficient.getMean(), wfToEfficient.getMean(), wfToGreedy0.getMean(), wfToGreedy1.getMean(), wfToGreedy2.getMean());
		}else{
			System.out.println("\t Already have data ");			
		}
	}
}
