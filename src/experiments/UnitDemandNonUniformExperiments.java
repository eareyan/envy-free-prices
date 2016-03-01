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
		//if(!dbLogger.checkIfUnitDemandRowExists("unit_nonuniform_comparison",numUsers, numCampaigns, prob)){
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
				if(EVPPlus1.Solve().getSellerRevenue() - EVPAllConnected.Solve().getSellerRevenue() > 0.1){
					System.out.println("*************START****************");
					Printer.printMatrix(valuationMatrix);
					System.out.println("\t-> EVPPlus1");
					Printer.printMatrix(EVPPlus1.Solve().getMatching());
					Printer.printVector(EVPPlus1.Solve().getPrices());
					System.out.println(EVPPlus1.Solve().getSellerRevenue());
					System.out.println("\t-> EVPAllConnected");
					Printer.printMatrix(EVPAllConnected.Solve().getMatching());
					Printer.printVector(EVPAllConnected.Solve().getPrices());
					System.out.println(EVPAllConnected.Solve().getSellerRevenue());
					System.out.println("*************END*********************");
					System.exit(-1);
				}
				
			}
			/* log results in database */
			dbLogger.saveNonUniformUnitComparisonData(numUsers, numCampaigns, prob, onlyConnectedRevenue.getMean(), plusOneRevenue.getMean() ,allConnectedRevenue.getMean());			
		//}else{
		//	System.out.println("\t Already have data");
		//}
	}

}
