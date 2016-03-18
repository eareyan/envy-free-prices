package structures.factory;

import structures.Market;
import structures.MarketAllocation;
import unitdemand.MWBMatchingAlgorithm;
import unitdemand.Matching;

public class MarketAllocationFactory {
	
	/*
	 * Given a market, return a cost matrix to be input to the Hungarian algorithm. 
	 * This matrix encodes the fact that a campaign is not connected to a user by 
	 * assigning an infinite cost. For a connected campaign, we add a random valuation
	 * between 1 and 100 for each edge. 
	 */
	public static double[][] getValuationMatrixFromMarket(Market market){
		double[][] costMatrix = new double[market.getNumberUsers()][market.getNumberCampaigns()];        
        for(int i=0;i<market.getNumberUsers();i++){
        	for(int j=0;j<market.getNumberCampaigns();j++){
        		if(market.isConnected(i, j)){
        			costMatrix[i][j] = market.getCampaign(j).getReward();
        		}else{
        			costMatrix[i][j] = Double.NEGATIVE_INFINITY;
        		}
        	}
        }
        return costMatrix;
	}
	
	/*
	 * Given a cost matrix, run the Hungarian algorithm and return the max weight matching.
	 */
	public static int[][] getMaximumMatchingFromValuationMatrix(double[][] valuationMatrix){
		int[] result  = new MWBMatchingAlgorithm(valuationMatrix).getMatching();
        /* Initialize allocation as all disconnected */
        int[][] allocationMatrix = new int[valuationMatrix.length][valuationMatrix[0].length];
        for(int i=0;i<result.length;i++){
        	//If the assignment is possible and the user is actually connected to the campaigns
        	if(result[i] > -1 && valuationMatrix[i][result[i]] > Double.NEGATIVE_INFINITY){
            	allocationMatrix[i][result[i]] = 1;
        	}
        }
        return allocationMatrix;
	}
	
	/*
	 * Given a unit-demand market, return the MarketAllocation object corresponding to the 
	 * maximum weight matching 
	 */
	public static MarketAllocation getMaxWeightMatchingAllocation(Market market){
		return new MarketAllocation(market,MarketAllocationFactory.getMaximumMatchingFromValuationMatrix(MarketAllocationFactory.getValuationMatrixFromMarket(market)));
	}
	
	/*
	 * This method is for the unit demand case only
	 * This method first computes a new valuation matrix by subtracting the reserve values
	 * and setting negative values to negative infinity.
	 * Then, get an allocation for the new valuation matrix by solving for the MWM with new valuation matrix	
	 */
	public static int[][] getAllocationWithReservePrices(double[][] valuationMatrix, double reserve){
		double[][] valuationMatrixWithReserve = new double[valuationMatrix.length][valuationMatrix[0].length];
		for(int i=0;i<valuationMatrix.length;i++){
			for(int j=0;j<valuationMatrix[0].length;j++){
				valuationMatrixWithReserve[i][j] = (valuationMatrix[i][j] - reserve < 0) ? Double.NEGATIVE_INFINITY : (valuationMatrix[i][j] - reserve);
			}
		}
		//Printer.printMatrix(valuationMatrixWithReserve);
		return Matching.computeMaximumWeightMatchingValue(valuationMatrixWithReserve).getMatching();
	}	

}
