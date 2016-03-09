package experiments;

import ilog.concert.IloException;

import java.sql.SQLException;

import log.SqlDB;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import structures.Market;
import structures.MarketPrices;
import structures.factory.MarketAllocationFactory;
import structures.factory.MarketFactory;
import unitdemand.Matching;
import unitdemand.evpapprox.AllConnectedDummies;
import unitdemand.evpapprox.EVPApproximation;

/*
 * This class implements experiments in the case of unit-uniform demand.
 * It compares EVPApp with LPReservePrices.
 * 
 * @author Enrique Areyan Viqueira
 */
public class UnitUniformDemandExperiments extends Experiments{
	
	public void runOneExperiment(int numUsers,int numCampaigns, double prob, SqlDB dbLogger) throws SQLException, IloException{
		if(!dbLogger.checkIfUnitDemandRowExists("unit_uniform_demand",numUsers, numCampaigns, prob)){
			System.out.println("\t Adding data ");		
			DescriptiveStatistics evpAppRevenue = new DescriptiveStatistics();
			DescriptiveStatistics lpReservePriceRevenue = new DescriptiveStatistics();
			
			for(int t=0;t<RunParameters.numTrials;t++){
				Market market = MarketFactory.randomUnitDemandMarket(numUsers, numCampaigns, prob);
				double [][] valuationMatrix = MarketAllocationFactory.getValuationMatrixFromMarket(market);
				/* * Measure EVPApp */
				EVPApproximation EVPAllConnected = new EVPApproximation(valuationMatrix,new AllConnectedDummies());
				Matching evpAllConnected = EVPAllConnected.Solve();
				evpAppRevenue.addValue(evpAllConnected.getSellerRevenue());
				/* * Measure LPSRP */				
				unitdemand.lp.LPReservePrices LPRPUnitDemand = new unitdemand.lp.LPReservePrices(market);
				MarketPrices LPRP = LPRPUnitDemand.Solve();
				lpReservePriceRevenue.addValue(LPRP.sellerRevenuePriceVector());
			}
			/* log results in database */
			dbLogger.saveUniformUnitDemandData(numUsers, numCampaigns, prob, evpAppRevenue.getMean(), lpReservePriceRevenue.getMean());			
		}else{
			System.out.println("\t Already have data");
		}
	}
}
