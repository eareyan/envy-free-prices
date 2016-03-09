package structures.factory;

import structures.Market;
import structures.MarketAllocation;
import unitdemand.MWBMatchingAlgorithm;

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

}
