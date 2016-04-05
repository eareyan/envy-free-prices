package experiments;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;

import java.sql.SQLException;

import log.SqlDB;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import statistics.PricesStatistics;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketPrices;
import structures.factory.RandomMarketFactory;
import util.NumberMethods;
import algorithms.allocations.EfficientAllocationILP;
import algorithms.pricing.lp.reserveprices.GeneralLPReserve;

/*
 * This class implements experiments in the general demand case.
 * It compares LP (using WF and Efficient Allocation) and WF MaxWEQ.
 * 
 * @author Enrique Areyan Viqueira
 */
public class fancy_underdemand extends Experiments{

	public void runOneExperiment(int numUsers,int numCampaigns, double prob, int b, SqlDB dbLogger) throws SQLException, IloException{
		if(!dbLogger.checkIfUnitDemandRowExists("fancy_underdemand",numUsers, numCampaigns, prob)){
			System.out.println("\t Add data ");
			
			DescriptiveStatistics ckEfficiency = new DescriptiveStatistics();
			DescriptiveStatistics ckRevenue = new DescriptiveStatistics();
			DescriptiveStatistics ckTime = new DescriptiveStatistics();
			DescriptiveStatistics ckWE1 = new DescriptiveStatistics();
			DescriptiveStatistics ckWE2 = new DescriptiveStatistics();

			DescriptiveStatistics lpOptEfficiency = new DescriptiveStatistics();
			DescriptiveStatistics lpOptRevenue = new DescriptiveStatistics();
			DescriptiveStatistics lpOptTime = new DescriptiveStatistics();
			DescriptiveStatistics lpOptWE1 = new DescriptiveStatistics();
			DescriptiveStatistics lpOptWE2 = new DescriptiveStatistics();

			long startTime , endTime ;
			IloCplex iloObject0,iloObject1,iloObject2;
			for(int t=0;t<RunParameters.numTrials;t++){
				Market market = RandomMarketFactory.generateOverSuppliedMarket(numUsers, numCampaigns, prob, b);
				/*
				 * Compute the efficient allocation, in this case, the maximum weight matching.
				 */
				MarketAllocation efficient = new MarketAllocation(market,new EfficientAllocationILP(market).Solve(new IloCplex()).get(0));
				double valueOptAllocaction = efficient.value();
				/*
				 * Measure CK
				 */
				algorithms.ascendingauction.AscendingAuction ck = new algorithms.ascendingauction.AscendingAuction(market);
				startTime = System.nanoTime();
				MarketPrices ckSol = ck.Solve();
				endTime = System.nanoTime();

				ckEfficiency.addValue(NumberMethods.getRatio(ckSol.getMarketAllocation().value() , valueOptAllocaction));
				ckRevenue.addValue(ckSol.sellerRevenuePriceVector());
				ckTime.addValue(endTime - startTime);
				PricesStatistics ckStat = new PricesStatistics(ckSol);
				ckWE1.addValue(ckStat.numberOfEnvyCampaigns());
				ckWE2.addValue(ckStat.computeWalrasianViolations()[0]);
				/*
				 * Measure lpOPT
				 */
				GeneralLPReserve lpr = new GeneralLPReserve(market,efficient);
				startTime = System.nanoTime();
				MarketPrices lprSol = lpr.Solve();
				endTime = System.nanoTime();
				lpOptEfficiency.addValue(NumberMethods.getRatio(lprSol.getMarketAllocation().value(),valueOptAllocaction));
				lpOptRevenue.addValue(lprSol.sellerRevenuePriceVector());
				lpOptTime.addValue(endTime - startTime);
				PricesStatistics lprStat = new PricesStatistics(lprSol);
				lpOptWE1.addValue(lprStat.numberOfEnvyCampaigns());
				lpOptWE2.addValue(lprStat.computeWalrasianViolations()[0]);
				
			}
			//dbLogger.saveGeneralCaseData(numUsers, numCampaigns, prob, ratioEfficiency.getMean(), effAllocationRevenue.getMean(), effAllocationTime.getMean() / 1000000, effAllocWEViolation.getMean(),effAllocWERelativeViolations.getMean(), effAllocEFViolation.getMean(), wfAllocationRevenue.getMean(), wfAllocationTime.getMean() / 1000000, wfAllocWEViolation.getMean(),wfAllocWERelativeViolations.getMean(), wfAllocEFViolation.getMean(), wfMaxWEQRevenue.getMean(),wfMaxWEQTime.getMean() / 1000000);
			//System.exit(-1); /* stop execution... for debugging purposes */		
		}else{
			System.out.println("\t Already have data ");			
		}
	}
}
