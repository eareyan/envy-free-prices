package structures.factory;

import structures.Campaign;
import structures.Market;
import structures.User;

public class UnitMarketFactory {
	
	/*
	 * Create a singleton market from a valuation matrix
	 */
	public static Market createMarketFromValuationMatrix(double [][] valuationMatrix){
		User[] users = new User[valuationMatrix.length];
		for(int i=0;i<valuationMatrix.length;i++){
			users[i] = new User(1);
		}
		Campaign[] campaigns = new Campaign[valuationMatrix[0].length];
		for(int j=0;j<valuationMatrix[0].length;j++){
			double reward = Double.NEGATIVE_INFINITY;
			for(int i=0;i<valuationMatrix.length;i++){
				if(valuationMatrix[i][j] > Double.NEGATIVE_INFINITY){
					reward = valuationMatrix[i][j];
					break;
				}
			}
			campaigns[j] = new Campaign(1,reward);
		}
		boolean[][] connections = new boolean[valuationMatrix.length][valuationMatrix[0].length];
		for(int i=0;i<valuationMatrix.length;i++){
			for(int j=0;j<valuationMatrix[0].length;j++){
				connections[i][j] = valuationMatrix[i][j] > Double.NEGATIVE_INFINITY;
			}
		}
		return new Market(users,campaigns,connections);
	}
	/*
	 * Shortcut method to create a unit demand random market by just providing the number of users, campaigns, and
	 * probability of connection.
	 */
	public static Market randomUnitDemandMarket(int numberUsers, int numberCampaigns, double probabilityConnections){
		return RandomMarketFactory.randomMarket(numberUsers, 1 ,1,  numberCampaigns, 1 , 1, RandomMarketFactory.defaultMinReward, RandomMarketFactory.defaultMaxReward,  probabilityConnections);		
	}	
	
	/*
	 * Create a singleton market given all other parameters (connections matrix and rewards)
	 */
	public static Market singletonMarket(int numberUsers, int numberCampaigns, boolean[][] connections, double[] rewards){
		//Create Users
		User[] users = new User[numberUsers];
		for(int i=0;i<numberUsers;i++){
			users[i] = new User(1);
		}
		//Create Campaigns
		Campaign[] campaigns = new Campaign[numberCampaigns];
		for(int j=0;j<numberCampaigns;j++){
			campaigns[j] = new Campaign(1,rewards[j]);
		}
		return new Market(users,campaigns,connections);
	}	

}
