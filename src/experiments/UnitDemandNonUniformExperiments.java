package experiments;

import java.sql.SQLException;

import log.SqlDB;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import unitdemand.MaxWEQ;
import unitdemand.evpapprox.AllConnectedDummies;
import unitdemand.evpapprox.EVPApproximation;
import unitdemand.evpapprox.OnlyConnectedDummies;
import unitdemand.evpapprox.Plus1ConnectedDummies;
import util.Printer;

public class UnitDemandNonUniformExperiments extends Experiments{
	
	public void runOneExperiment(int numUsers,int numCampaigns, double prob, SqlDB dbLogger) throws SQLException{
		if(!dbLogger.checkIfUnitDemandRowExists("unit_nonuniform_comparison",numUsers, numCampaigns, prob)){
			System.out.println("\t Adding data ");		
			DescriptiveStatistics onlyConnectedRevenue = new DescriptiveStatistics();
			DescriptiveStatistics plusOneRevenue = new DescriptiveStatistics();
			DescriptiveStatistics allConnectedRevenue = new DescriptiveStatistics();
			
			for(int t=0;t<RunParameters.numTrials;t++){
				double[][] valuationMatrix = UnitDemandComparison.getValuationMatrix(numUsers, numCampaigns, prob) ;
				//Printer.printMatrix(valuationMatrix);
				/* * Measure OnlyConnected */
				EVPApproximation EVPOnlyConnected = new EVPApproximation(valuationMatrix,new OnlyConnectedDummies(valuationMatrix));				
				onlyConnectedRevenue.addValue(EVPOnlyConnected.Solve().getSellerRevenue());
				/* * Measure Plus1 */
				EVPApproximation EVPPlus1 = new EVPApproximation(valuationMatrix,new Plus1ConnectedDummies(valuationMatrix));				
				plusOneRevenue.addValue(EVPPlus1.Solve().getSellerRevenue());
				/* * Measure AllConnected */
				EVPApproximation EVPAllConnected = new EVPApproximation(valuationMatrix,new AllConnectedDummies(valuationMatrix));				
				allConnectedRevenue.addValue(EVPAllConnected.Solve().getSellerRevenue());
			}
			/* log results in database */
			dbLogger.saveNonUniformUnitComparisonData(numUsers, numCampaigns, prob, onlyConnectedRevenue.getMean(), plusOneRevenue.getMean() ,allConnectedRevenue.getMean());			
		}else{
			System.out.println("\t Already have data");
		}
	}

}
