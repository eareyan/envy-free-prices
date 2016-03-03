package experiments;

import ilog.concert.IloException;

import java.sql.SQLException;
import java.util.Random;

import log.SqlDB;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import algorithms.lp.reserveprices.LPReservePrices;
import algorithms.lp.reserveprices.SelectAllConnectedUsers;
import algorithms.lp.reserveprices.SetReservePricesSimple;
import structures.Market;
import structures.MarketFactory;
import structures.MarketPrices;
import unitdemand.Matching;
import unitdemand.MaxWEQ;
import unitdemand.evpapprox.AllConnectedDummies;
import unitdemand.evpapprox.EVPApproximation;
import util.Printer;

public class UnitUniformDemandExperiments extends Experiments{
	
	public void runOneExperiment(int numUsers,int numCampaigns, double prob, SqlDB dbLogger) throws SQLException, IloException{
		if(!dbLogger.checkIfUnitDemandRowExists("unit_uniform_demand",numUsers, numCampaigns, prob)){
			System.out.println("\t Adding data ");		
			DescriptiveStatistics evpAppRevenue = new DescriptiveStatistics();
			DescriptiveStatistics lpReservePriceRevenue = new DescriptiveStatistics();
			
			for(int t=0;t<RunParameters.numTrials;t++){
				Market market = MarketFactory.randomUnitDemandMarket(numUsers, numCampaigns, prob);
				double [][] valuationMatrix = UnitDemandExperiments.getValuationMatrixFromMarket(market);
				/* * Measure EVPApp */
				EVPApproximation EVPAllConnected = new EVPApproximation(valuationMatrix,new AllConnectedDummies());
				Matching evpAllConnected = EVPAllConnected.Solve();
				evpAppRevenue.addValue(evpAllConnected.getSellerRevenue());
				/* * Measure LPSRP */				
				LPReservePrices SRPAllConnected = new LPReservePrices(market,new SelectAllConnectedUsers(), new SetReservePricesSimple());
				MarketPrices LPRP = SRPAllConnected.Solve();
				lpReservePriceRevenue.addValue(LPRP.sellerRevenuePriceVector());
			}
			/* log results in database */
			dbLogger.saveUniformUnitDemandData(numUsers, numCampaigns, prob, evpAppRevenue.getMean(), lpReservePriceRevenue.getMean());			
		}else{
			System.out.println("\t Already have data");
		}
	}
}
