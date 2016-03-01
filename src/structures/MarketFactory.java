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
	 * Shortcut method to create a random market by just providing the number of users, campaigns, and
	 * probability of connection.
	 */
	public static Market randomMarketMoreCampaignReach(int numberUsers, int numberCampaigns, double probabilityConnections){
		return MarketFactory.randomMarket(numberUsers, 1 ,5,  numberCampaigns, 6 , 10, 1.0, 100.0,  probabilityConnections);		
	}	
	public static Market randomMarketMoreUserSupply(int numberUsers, int numberCampaigns, double probabilityConnections){
		return MarketFactory.randomMarket(numberUsers, 6 ,10,  numberCampaigns, 1 , 5, 1.0, 100.0,  probabilityConnections);		
	}	
	
	/*
	 * Shortcut method to create a unit demand random market by just providing the number of users, campaigns, and
	 * probability of connection.
	 */
	public static Market randomUnitDemandMarket(int numberUsers, int numberCampaigns, double probabilityConnections){
		return MarketFactory.randomMarket(numberUsers, 1 ,1,  numberCampaigns, 1 , 1, 1.0, 100.0,  probabilityConnections);		
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
	
	/*
	 * Adds a dummy campaign per item that demands impressions from that item at the given value
	 * and is not connected to any other item
	 */
	public static Market augmentMarketWithReserve(Market M, double reserveValue, int reserveDemand){
		int totalNewCampaigns = M.getNumberCampaigns() + M.getNumberUsers();
		Campaign[] campaigns = new Campaign[totalNewCampaigns];
		for(int j=0;j<M.getNumberCampaigns();j++){
			campaigns[j] = M.getCampaign(j);
		}
		for(int j=M.getNumberCampaigns();j<totalNewCampaigns;j++){
			campaigns[j] = new Campaign(reserveDemand , reserveValue);
		}
		boolean[][] connections = new boolean[M.getNumberUsers()][totalNewCampaigns];
		for(int i=0;i<M.getNumberUsers();i++){
			for(int j=0;j<M.getNumberCampaigns();j++){
				connections[i][j] = M.isConnected(i, j);
			}
		}
		int i=0;
		for(int j=M.getNumberCampaigns();j<totalNewCampaigns;j++){
			connections[i][j] = (j-M.getNumberCampaigns() == i);
			i++;
		}
		return new Market(M.users,campaigns,connections);
	}
	/*
	 * Clones a market
	 */
	public static Market cloneMarket(Market market){
		User[] users = new User[market.getNumberUsers()];
		for(int i=0;i<market.getNumberUsers();i++){
			users[i] = new User(market.getUser(i).getSupply());
		}
		Campaign[] campaigns = new Campaign[market.getNumberCampaigns()];
		for(int j=0;j<market.getNumberCampaigns();j++){
			campaigns[j] = new Campaign(market.getCampaign(j).getDemand(),market.getCampaign(j).getReward());
		}
		boolean[][] connections = new boolean[market.getNumberUsers()][market.getNumberCampaigns()];
		for(int i=0;i<market.getNumberUsers();i++){
			for(int j=0;j<market.getNumberCampaigns();j++){
				connections[i][j] = market.isConnected(i, j);
			}
		}
		return new Market(users,campaigns,connections);
	}
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
}
