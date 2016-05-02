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
import structures.factory.MarketFactory;
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
		String tablename ="" , unittablename = "";
		if(this.underdemand){
			tablename = "fancy_underdemand";
			unittablename = "fancy_unitsupply_underdemand";
		}else{
			tablename = "fancy_overdemand";
			unittablename = "fancy_unitsupply_overdemand";
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
			
			
			DescriptiveStatistics lpUnitOptEfficiency = new DescriptiveStatistics();
			DescriptiveStatistics lpUnitOptRevenue = new DescriptiveStatistics();
			DescriptiveStatistics lpUnitOptTime = new DescriptiveStatistics();
			DescriptiveStatistics lpUnitOptWE1 = new DescriptiveStatistics();
			DescriptiveStatistics lpUnitOptWE2 = new DescriptiveStatistics();

			DescriptiveStatistics lpUnitWFEfficiency = new DescriptiveStatistics();
			DescriptiveStatistics lpUnitWFRevenue = new DescriptiveStatistics();
			DescriptiveStatistics lpUnitWFTime = new DescriptiveStatistics();
			DescriptiveStatistics lpUnitWFWE1 = new DescriptiveStatistics();
			DescriptiveStatistics lpUnitWFWE2 = new DescriptiveStatistics();
			
			DescriptiveStatistics lpUnitG1Efficiency = new DescriptiveStatistics();
			DescriptiveStatistics lpUnitG1Revenue = new DescriptiveStatistics();
			DescriptiveStatistics lpUnitG1Time = new DescriptiveStatistics();
			DescriptiveStatistics lpUnitG1WE1 = new DescriptiveStatistics();
			DescriptiveStatistics lpUnitG1WE2 = new DescriptiveStatistics();			

			DescriptiveStatistics lpUnitG2Efficiency = new DescriptiveStatistics();
			DescriptiveStatistics lpUnitG2Revenue = new DescriptiveStatistics();
			DescriptiveStatistics lpUnitG2Time = new DescriptiveStatistics();
			DescriptiveStatistics lpUnitG2WE1 = new DescriptiveStatistics();
			DescriptiveStatistics lpUnitG2WE2 = new DescriptiveStatistics();			

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
				ckRevenue.addValue(NumberMethods.getRatio(ckSol.sellerRevenuePriceVector() , valueOptAllocaction));
				ckTime.addValue(endTime - startTime);
				PricesStatistics ckStat = new PricesStatistics(ckSol);
				ckWE1.addValue((double) ckStat.numberOfEnvyCampaigns() / numCampaigns);
				ckWE2.addValue((double) ckStat.computeWalrasianViolations()[0] / numUsers);
				/*
				 * Measure lpOPT
				 */
				LPReservePrices lpOPT = new LPReservePrices(market,new EfficientAlloc());
				startTime = System.nanoTime();
				MarketPrices lpOPTSol = lpOPT.Solve();
				endTime = System.nanoTime();
				lpOptEfficiency.addValue(NumberMethods.getRatio(lpOPTSol.getMarketAllocation().value(),valueOptAllocaction));
				lpOptRevenue.addValue(NumberMethods.getRatio(lpOPTSol.sellerRevenuePriceVector() , valueOptAllocaction));
				lpOptTime.addValue(endTime - startTime);
				PricesStatistics lpOPTStat = new PricesStatistics(lpOPTSol);
				lpOptWE1.addValue((double) lpOPTStat.numberOfEnvyCampaigns() / numCampaigns);
				lpOptWE2.addValue((double) lpOPTStat.computeWalrasianViolations()[0] / numUsers);
				/*
				 * Measure lpWF
				 */
				LPReservePrices lpWF = new LPReservePrices(market,new WFAlloc());
				startTime = System.nanoTime();
				MarketPrices lpWFSol = lpWF.Solve();
				endTime = System.nanoTime();
				lpWFEfficiency.addValue(NumberMethods.getRatio(lpWFSol.getMarketAllocation().value(),valueOptAllocaction));
				lpWFRevenue.addValue(NumberMethods.getRatio(lpWFSol.sellerRevenuePriceVector() , valueOptAllocaction));
				lpWFTime.addValue(endTime - startTime);
				PricesStatistics lpWFStat = new PricesStatistics(lpWFSol);
				lpWFWE1.addValue((double) lpWFStat.numberOfEnvyCampaigns() / numCampaigns);
				lpWFWE2.addValue((double) lpWFStat.computeWalrasianViolations()[0] / numUsers);
				/*
				 * Measure lpG1
				 */
				LPReservePrices lpG1 = new LPReservePrices(market,new GreedyAlloc(1));
				startTime = System.nanoTime();
				MarketPrices lpG1Sol = lpG1.Solve();
				endTime = System.nanoTime();
				lpG1Efficiency.addValue(NumberMethods.getRatio(lpG1Sol.getMarketAllocation().value(),valueOptAllocaction));
				lpG1Revenue.addValue(NumberMethods.getRatio(lpG1Sol.sellerRevenuePriceVector() , valueOptAllocaction));
				lpG1Time.addValue(endTime - startTime);
				PricesStatistics lpG1Stat = new PricesStatistics(lpG1Sol);
				lpG1WE1.addValue((double) lpG1Stat.numberOfEnvyCampaigns() / numCampaigns);
				lpG1WE2.addValue((double) lpG1Stat.computeWalrasianViolations()[0] / numUsers);
				/*
				 * Measure lpG1
				 */
				LPReservePrices lpG2 = new LPReservePrices(market,new GreedyAlloc(-1));
				startTime = System.nanoTime();
				MarketPrices lpG2Sol = lpG2.Solve();
				endTime = System.nanoTime();
				lpG2Efficiency.addValue(NumberMethods.getRatio(lpG2Sol.getMarketAllocation().value(),valueOptAllocaction));
				lpG2Revenue.addValue(NumberMethods.getRatio(lpG2Sol.sellerRevenuePriceVector() , valueOptAllocaction));
				lpG2Time.addValue(endTime - startTime);
				PricesStatistics lpG2Stat = new PricesStatistics(lpG2Sol);
				lpG2WE1.addValue((double) lpG2Stat.numberOfEnvyCampaigns() / numCampaigns);
				lpG2WE2.addValue((double) lpG2Stat.computeWalrasianViolations()[0] / numUsers);	
				
				/****
				 * Unit Supply
				 */
				Market unitSupplyMarket = MarketFactory.createUnitSupplyMarket(market);
				MarketAllocation efficientUnit = new MarketAllocation(unitSupplyMarket,new EfficientAllocationILP(unitSupplyMarket).Solve(new IloCplex()).get(0));
				double valueOptAllocactionUnit = efficientUnit.value();				
				/*
				 * Measure lpOPT
				 */
				LPReservePrices LPUnit = new LPReservePrices(unitSupplyMarket,new EfficientAlloc());
				startTime = System.nanoTime();
				MarketPrices lpOPTUnitSol = LPUnit.Solve();
				endTime = System.nanoTime();
				lpUnitOptEfficiency.addValue(NumberMethods.getRatio(lpOPTUnitSol.getMarketAllocation().value(),valueOptAllocactionUnit));
				lpUnitOptRevenue.addValue(NumberMethods.getRatio(lpOPTUnitSol.sellerRevenuePriceVector() , valueOptAllocactionUnit));
				lpUnitOptTime.addValue(endTime - startTime);
				PricesStatistics lpOPTUnitStat = new PricesStatistics(lpOPTUnitSol);
				lpUnitOptWE1.addValue((double) lpOPTUnitStat.numberOfEnvyCampaigns() / numCampaigns);
				lpUnitOptWE2.addValue((double) lpOPTUnitStat.computeWalrasianViolations()[0] / numUsers);
				/*
				 * Measure lpWF
				 */
				LPReservePrices lpUnitWF = new LPReservePrices(unitSupplyMarket,new WFAlloc());
				startTime = System.nanoTime();
				MarketPrices lpUnitWFSol = lpUnitWF.Solve();
				endTime = System.nanoTime();
				lpUnitWFEfficiency.addValue(NumberMethods.getRatio(lpUnitWFSol.getMarketAllocation().value(),valueOptAllocactionUnit));
				lpUnitWFRevenue.addValue(NumberMethods.getRatio(lpUnitWFSol.sellerRevenuePriceVector() , valueOptAllocactionUnit));
				lpUnitWFTime.addValue(endTime - startTime);
				PricesStatistics lpUnitWFStat = new PricesStatistics(lpUnitWFSol);
				lpUnitWFWE1.addValue((double) lpUnitWFStat.numberOfEnvyCampaigns() / numCampaigns);
				lpUnitWFWE2.addValue((double) lpUnitWFStat.computeWalrasianViolations()[0] / numUsers);
				/*
				 * Measure lpG1
				 */
				LPReservePrices lpUnitG1 = new LPReservePrices(unitSupplyMarket,new GreedyAlloc(1));
				startTime = System.nanoTime();
				MarketPrices lpUnitG1Sol = lpUnitG1.Solve();
				endTime = System.nanoTime();
				lpUnitG1Efficiency.addValue(NumberMethods.getRatio(lpUnitG1Sol.getMarketAllocation().value(),valueOptAllocactionUnit));
				lpUnitG1Revenue.addValue(NumberMethods.getRatio(lpUnitG1Sol.sellerRevenuePriceVector() , valueOptAllocactionUnit));
				lpUnitG1Time.addValue(endTime - startTime);
				PricesStatistics lpUnitG1Stat = new PricesStatistics(lpUnitG1Sol);
				lpUnitG1WE1.addValue((double) lpUnitG1Stat.numberOfEnvyCampaigns() / numCampaigns);
				lpUnitG1WE2.addValue((double) lpUnitG1Stat.computeWalrasianViolations()[0] / numUsers);
				/*
				 * Measure lpG1
				 */
				LPReservePrices lpUnitG2 = new LPReservePrices(unitSupplyMarket,new GreedyAlloc(-1));
				startTime = System.nanoTime();
				MarketPrices lpUnitG2Sol = lpUnitG2.Solve();
				endTime = System.nanoTime();
				lpUnitG2Efficiency.addValue(NumberMethods.getRatio(lpUnitG2Sol.getMarketAllocation().value(),valueOptAllocactionUnit));
				lpUnitG2Revenue.addValue(NumberMethods.getRatio(lpUnitG2Sol.sellerRevenuePriceVector() , valueOptAllocactionUnit));
				lpUnitG2Time.addValue(endTime - startTime);
				PricesStatistics lpUnitG2Stat = new PricesStatistics(lpUnitG2Sol);
				lpUnitG2WE1.addValue((double) lpUnitG2Stat.numberOfEnvyCampaigns() / numCampaigns);
				lpUnitG2WE2.addValue((double) lpUnitG2Stat.computeWalrasianViolations()[0] / numUsers);					
				
				
			}
			dbLogger.save_fancy_demand(tablename, numUsers, numCampaigns, prob, b,
					ckEfficiency.getMean(), ckRevenue.getMean(), ckTime.getMean() / 1000000, ckWE1.getMean() , ckWE2.getMean() ,
					lpOptEfficiency.getMean(), lpOptRevenue.getMean(), lpOptTime.getMean() / 1000000, lpOptWE1.getMean() , lpOptWE2.getMean() ,
					lpWFEfficiency.getMean(), lpWFRevenue.getMean(), lpWFTime.getMean() / 1000000, lpWFWE1.getMean() , lpWFWE2.getMean() ,
					lpG1Efficiency.getMean(), lpG1Revenue.getMean(), lpG1Time.getMean() / 1000000, lpG1WE1.getMean() , lpG1WE2.getMean() ,
					lpG2Efficiency.getMean(), lpG2Revenue.getMean(), lpG2Time.getMean() / 1000000, lpG2WE1.getMean() , lpG2WE2.getMean()
					);
			
			/****
			 * UNIT SUPPLY
			 */
			dbLogger.save_fancy_unitsupply(unittablename, numUsers, numCampaigns, prob, b,
					lpUnitOptEfficiency.getMean(), lpUnitOptRevenue.getMean(), lpUnitOptTime.getMean() / 1000000, lpUnitOptWE1.getMean() , lpUnitOptWE2.getMean(), 
					lpUnitWFEfficiency.getMean(), lpUnitWFRevenue.getMean(), lpUnitWFTime.getMean() / 1000000, lpUnitWFWE1.getMean() , lpUnitWFWE2.getMean() ,
					lpUnitG1Efficiency.getMean(), lpUnitG1Revenue.getMean(), lpUnitG1Time.getMean() / 1000000, lpUnitG1WE1.getMean() , lpUnitG1WE2.getMean() ,
					lpUnitG2Efficiency.getMean(), lpUnitG2Revenue.getMean(), lpUnitG2Time.getMean() / 1000000, lpUnitG2WE1.getMean() , lpUnitG2WE2.getMean()
					);

			//System.exit(-1); /* stop execution... for debugging purposes */		
		}else{
			System.out.println("\t Already have data ");			
		}
	}
}