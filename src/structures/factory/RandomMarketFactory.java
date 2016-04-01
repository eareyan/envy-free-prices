package structures.factory;

import java.util.Random;

import structures.Campaign;
import structures.Market;
import structures.User;

public class RandomMarketFactory {
	/*
	 * Creation of a fully parameterizable random market. 
	 */
	public static Market randomMarket(int numberUsers, int minSupplyPerUser, int maxSupplyPerUser, int numberCampaigns, int minDemandPerCampaign, int maxDemandPerCampaign, double minReward, double maxReward, double probabilityConnection){
		//Create Users
		Random generator = new Random();
		User[] users = new User[numberUsers];
		for(int i=0;i<numberUsers;i++){
			users[i] = new User(generator.nextInt((maxSupplyPerUser - minSupplyPerUser) + 1) + minSupplyPerUser);
		}
		//Create Campaigns
		Campaign[] campaigns = new Campaign[numberCampaigns];
		for(int j=0;j<numberCampaigns;j++){
			campaigns[j] = new Campaign(generator.nextInt((maxDemandPerCampaign - minDemandPerCampaign) + 1) + minDemandPerCampaign, generator.nextDouble() * (maxReward - minReward) + minReward);
		}
		/*
		 * Create random connections. Add a connection with probability 'probabilityConnections'.
		 * Each connection is added independently.
		 */
		boolean[][] connections = new boolean[numberUsers][numberCampaigns];
		for(int i=0;i<numberUsers;i++){
			for(int j=0;j<numberCampaigns;j++){
				connections[i][j] = generator.nextDouble() <= probabilityConnection;
			}
		}
		return new Market(users,campaigns,connections);
	}
	/*
	 * Shortcut method to create a random market by just providing the number of users, campaigns, and
	 * probability of connection.
	 */
	public static Market randomMarket(int numberUsers, int numberCampaigns, double probabilityConnections){
		return RandomMarketFactory.randomMarket(numberUsers, 1 ,10,  numberCampaigns, 1 , 10, 1.0, 100.0,  probabilityConnections);		
	}
	/*
	 * Shortcut method to create a random market by just providing the number of users, campaigns, and
	 * probability of connection.
	 */
	public static Market randomMarketMoreCampaignReach(int numberUsers, int numberCampaigns, double probabilityConnections){
		return RandomMarketFactory.randomMarket(numberUsers, 1 ,5,  numberCampaigns, 6 , 10, 1.0, 100.0,  probabilityConnections);		
	}	
	public static Market randomMarketMoreUserSupply(int numberUsers, int numberCampaigns, double probabilityConnections){
		return RandomMarketFactory.randomMarket(numberUsers, 6 ,10,  numberCampaigns, 1 , 5, 1.0, 100.0,  probabilityConnections);		
	}
	
	public static Market RandomKMarket(int numberUsers, int numberCampaigns, double probabilityConnections, int k){
		
		return null;
		
	}
}
