package structures.factory;

import structures.Campaign;
import structures.Market;
import structures.User;
import structures.exceptions.CampaignCreationException;

public class UnitMarketFactory {
	
	/*
	 * Create a singleton market from a valuation matrix
	 */
	public static Market createMarketFromValuationMatrix(double [][] valuationMatrix) throws CampaignCreationException{
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
	public static Market randomUnitDemandMarket(int numberUsers, int numberCampaigns, double probabilityConnections) throws CampaignCreationException{
		return RandomMarketFactory.randomMarket(numberUsers, 1 ,1,  numberCampaigns, 1 , 1, RandomMarketFactory.defaultMinReward, RandomMarketFactory.defaultMaxReward,  probabilityConnections);		
	}	
	
	/*
	 * Create a singleton market given all other parameters (connections matrix and rewards)
	 */
	public static Market singletonMarket(int numberUsers, int numberCampaigns, boolean[][] connections, double[] rewards) throws CampaignCreationException{
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
	
	/*
	 * Produces a random valuation matrix V_ij, where valuations are within the default range
	 */
	public static double[][] getValuationMatrix(int n,int m, double prob,double minReward,double maxReward) {
		//Random generator = new Random();
		double[][] valuationMatrix = new double[n][m];
		for(int i=0;i<n;i++){
			for(int j=0;j<m;j++){
				//valuationMatrix[i][j] = (generator.nextDouble() <= prob) ? (generator.nextDouble() * (maxReward - minReward) + minReward) : Double.NEGATIVE_INFINITY;
				valuationMatrix[i][j] = (Math.random() <= prob) ? (Math.random() * (maxReward - minReward) + minReward) : Double.NEGATIVE_INFINITY;
				//valuationMatrix[i][j] = Math.random();
			}
		}
		return valuationMatrix;
	}
	public static double[][] getValuationMatrix(int n,int m,double prob){
		return getValuationMatrix(n,m,prob,RandomMarketFactory.defaultMinReward,RandomMarketFactory.defaultMaxReward);
	}
	/*
	 * Given a valuation matrix V_ij, returns a valuation V_ij' that respects resever price r.
	 * Respect in this context means that any valuation below r becomes -infinity and all others are
	 * decreased by r. You can think of this operation as "shiftting" the matrix by r.
	 */
	public static double[][] getValuationReserve(double[][] X,double r){
		double[][] XReserve = new double[X.length][X[0].length]; 
		for(int i=0;i<X.length;i++){
			for(int j=0;j<X[0].length;j++){
				XReserve[i][j] = (X[i][j] - r <= 0) ? Double.NEGATIVE_INFINITY : X[i][j] - r ;
			}
		}
		return XReserve;
	}	

}
