package structures;

import java.util.Random;

/*
 * Markets can be created in different ways.
 * This class implements logic to create Market Objects.
 * 
 * @author Enrique Areyan Viqueira
 */
public class MarketFactory {
	
	/*
	 * Creation of a fully parameterisable random market. 
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
		return MarketFactory.randomMarket(numberUsers, 1 ,10,  numberCampaigns, 1 , 10, 1.0, 100.0,  probabilityConnections);		
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
	/*
	 * Copies the given market without user i, deleting the corresponding row from connection matrix
	 */
	public static Market copyMarketWithoutUser(Market market, int userToDelete){
		//Create Users
		User[] users = new User[market.getNumberUsers()-1];
		int k = 0;
		for(int i=0;i<market.getNumberUsers();i++){
			if(i != userToDelete){
				users[k] = new User(market.getUser(i).getSupply());
				k++;
			}
		}
		//Create Campaigns
		Campaign[] campaigns = new Campaign[market.getNumberCampaigns()];
		for(int j=0;j<market.getNumberCampaigns();j++){
			campaigns[j] = new Campaign(market.getCampaign(j).getDemand() , market.getCampaign(j).getReward());
		}
		//Create Connections
		boolean[][] connections = new boolean[market.getNumberUsers()-1][market.getNumberCampaigns()];
		int l = 0;
		for(int i=0;i<market.getNumberUsers();i++){
			if(i != userToDelete){
				for(int j=0;j<market.getNumberCampaigns();j++){
					connections[l][j] = market.isConnected(i, j);
				}
				l++;
			}
		}
		return new Market(users,campaigns,connections);
	}
}
