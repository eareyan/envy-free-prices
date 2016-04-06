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
import algorithms.pricing.lp.reserveprices.EfficientAlloc;
import algorithms.pricing.lp.reserveprices.GreedyAlloc;
import algorithms.pricing.lp.reserveprices.LPReservePrices;
import algorithms.pricing.lp.reserveprices.WFAlloc;

/*
 * This class implements experiments in the general demand case.
 * It compares LP (using WF and Efficient Allocation) and WF MaxWEQ.
 * 
 * @author Enrique Areyan Viqueira
 */
public class fancy_demand extends Experiments{
	
	public boolean underdemand; //Boolean switch. True if underdemand, false otherwise.

	public void runOneExperiment(int numUsers,int numCampaigns, double prob, int b, SqlDB dbLogger) throws SQLException, IloException{
		String tablename ="";
		if(this.underdemand){
			tablename = "fancy_underdemand";
		}else{
			tablename = "fancy_overdemand";
		}
		if(!dbLogger.checkIfFancyDemandRowExists(tablename,numUsers, numCampaigns, prob, b)){
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

			DescriptiveStatistics lpWFEfficiency = new DescriptiveStatistics();
			DescriptiveStatistics lpWFRevenue = new DescriptiveStatistics();
			DescriptiveStatistics lpWFTime = new DescriptiveStatistics();
			DescriptiveStatistics lpWFWE1 = new DescriptiveStatistics();
			DescriptiveStatistics lpWFWE2 = new DescriptiveStatistics();
			
			DescriptiveStatistics lpG1Efficiency = new DescriptiveStatistics();
			DescriptiveStatistics lpG1Revenue = new DescriptiveStatistics();
			DescriptiveStatistics lpG1Time = new DescriptiveStatistics();
			DescriptiveStatistics lpG1WE1 = new DescriptiveStatistics();
			DescriptiveStatistics lpG1WE2 = new DescriptiveStatistics();			

			DescriptiveStatistics lpG2Efficiency = new DescriptiveStatistics();
			DescriptiveStatistics lpG2Revenue = new DescriptiveStatistics();
			DescriptiveStatistics lpG2Time = new DescriptiveStatistics();
			DescriptiveStatistics lpG2WE1 = new DescriptiveStatistics();
			DescriptiveStatistics lpG2WE2 = new DescriptiveStatistics();			

			long startTime , endTime ;
			for(int t=0;t<RunParameters.numTrials;t++){
				Market market;
				if(this.underdemand){
					market = RandomMarketFactory.generateOverSuppliedMarket(numUsers, numCampaigns, prob, b);
				}else{
					market = RandomMarketFactory.generateOverDemandedMarket(numUsers, numCampaigns, prob, b);
				}
				/*
				 * Compute the efficient allocation, in this case, the ILP efficient alloc
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
				LPReservePrices lpOPT = new LPReservePrices(market,new EfficientAlloc());
				startTime = System.nanoTime();
				MarketPrices lpOPTSol = lpOPT.Solve();
				endTime = System.nanoTime();
				lpOptEfficiency.addValue(NumberMethods.getRatio(lpOPTSol.getMarketAllocation().value(),valueOptAllocaction));
				lpOptRevenue.addValue(lpOPTSol.sellerRevenuePriceVector());
				lpOptTime.addValue(endTime - startTime);
				PricesStatistics lpOPTStat = new PricesStatistics(lpOPTSol);
				lpOptWE1.addValue(lpOPTStat.numberOfEnvyCampaigns());
				lpOptWE2.addValue(lpOPTStat.computeWalrasianViolations()[0]);
				/*
				 * Measure lpWF
				 */
				LPReservePrices lpWF = new LPReservePrices(market,new WFAlloc());
				startTime = System.nanoTime();
				MarketPrices lpWFSol = lpWF.Solve();
				endTime = System.nanoTime();
				lpWFEfficiency.addValue(NumberMethods.getRatio(lpWFSol.getMarketAllocation().value(),valueOptAllocaction));
				lpWFRevenue.addValue(lpWFSol.sellerRevenuePriceVector());
				lpWFTime.addValue(endTime - startTime);
				PricesStatistics lpWFStat = new PricesStatistics(lpWFSol);
				lpWFWE1.addValue(lpWFStat.numberOfEnvyCampaigns());
				lpWFWE2.addValue(lpWFStat.computeWalrasianViolations()[0]);
				/*
				 * Measure lpG1
				 */
				LPReservePrices lpG1 = new LPReservePrices(market,new GreedyAlloc(1));
				startTime = System.nanoTime();
				MarketPrices lpG1Sol = lpG1.Solve();
				endTime = System.nanoTime();
				lpG1Efficiency.addValue(NumberMethods.getRatio(lpG1Sol.getMarketAllocation().value(),valueOptAllocaction));
				lpG1Revenue.addValue(lpG1Sol.sellerRevenuePriceVector());
				lpG1Time.addValue(endTime - startTime);
				PricesStatistics lpG1Stat = new PricesStatistics(lpG1Sol);
				lpG1WE1.addValue(lpG1Stat.numberOfEnvyCampaigns());
				lpG1WE2.addValue(lpG1Stat.computeWalrasianViolations()[0]);
				/*
				 * Measure lpG1
				 */
				LPReservePrices lpG2 = new LPReservePrices(market,new GreedyAlloc(-1));
				startTime = System.nanoTime();
				MarketPrices lpG2Sol = lpG2.Solve();
				endTime = System.nanoTime();
				lpG2Efficiency.addValue(NumberMethods.getRatio(lpG2Sol.getMarketAllocation().value(),valueOptAllocaction));
				lpG2Revenue.addValue(lpG2Sol.sellerRevenuePriceVector());
				lpG2Time.addValue(endTime - startTime);
				PricesStatistics lpG2Stat = new PricesStatistics(lpG2Sol);
				lpG2WE1.addValue(lpG2Stat.numberOfEnvyCampaigns());
				lpG2WE2.addValue(lpG2Stat.computeWalrasianViolations()[0]);				
			}
			dbLogger.save_fancy_demand(tablename, numUsers, numCampaigns, prob, b,
					ckEfficiency.getMean(), ckRevenue.getMean(), ckTime.getMean() / 1000000, ckWE1.getMean() , ckWE2.getMean() ,
					lpOptEfficiency.getMean(), lpOptRevenue.getMean(), lpOptTime.getMean() / 1000000, lpOptWE1.getMean() , lpOptWE2.getMean() ,
					lpWFEfficiency.getMean(), lpWFRevenue.getMean(), lpWFTime.getMean() / 1000000, lpWFWE1.getMean() , lpWFWE2.getMean() ,
					lpG1Efficiency.getMean(), lpG1Revenue.getMean(), lpG1Time.getMean() / 1000000, lpG1WE1.getMean() , lpG1WE2.getMean() ,
					lpG2Efficiency.getMean(), lpG2Revenue.getMean(), lpG2Time.getMean() / 1000000, lpG2WE1.getMean() , lpG2WE2.getMean()
					);
			//System.exit(-1); /* stop execution... for debugging purposes */		
		}else{
			System.out.println("\t Already have data ");			
		}
	}
}