package structures.factory;

import java.util.Random;

import structures.Campaign;
import structures.Market;
import structures.User;

/*
 * Markets can be created in different ways.
 * This class implements logic to create Market Objects.
 * 
 * @author Enrique Areyan Viqueira
 */
public class MarketFactory {
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
	 * Copies the given market without user i, deleting the corresponding row from connection matrix
	 */
	public static Market copyMarketWithoutCampaign(Market market, int campaignToDelete){
		//Create Users
		User[] users = new User[market.getNumberUsers()];
		for(int i=0;i<market.getNumberUsers();i++){
			users[i] = new User(market.getUser(i).getSupply());
		}
		//Create Campaigns
		Campaign[] campaigns = new Campaign[market.getNumberCampaigns()-1];
		int k = 0;
		for(int j=0;j<market.getNumberCampaigns();j++){
			if(j != campaignToDelete){
				campaigns[k] = new Campaign(market.getCampaign(j).getDemand() , market.getCampaign(j).getReward());
				k++;
			}
		}
		//Create Connections
		boolean[][] connections = new boolean[market.getNumberUsers()][market.getNumberCampaigns()-1];
		int l = 0;
		for(int j=0;j<market.getNumberCampaigns();j++){
			if(j != campaignToDelete){
				for(int i=0;i<market.getNumberUsers();i++){
					connections[i][l] = market.isConnected(i, j);
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
		return new Market(M.getUsers(),campaigns,connections);
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
	 * Transpose market, i.e., interchange users for campaigns.
	 * This means: a user i with supply N_i becomes a campaign j with demand N_i and a new random reward.
	 * A campaign j with demand I_j becomes a user i with supply I_j. 
	 * The connections are preserved, i.e. if (i,j) is connected in the original market, then
	 * it will be in the transposed market.
	 */
	public static Market transposeMarket(Market market){
		User[] users = new User[market.getNumberCampaigns()];
		for(int j=0;j<market.getNumberCampaigns();j++){
			users[j] = new User(market.getCampaign(j).getDemand());
		}
		Campaign[] campaigns = new Campaign[market.getNumberUsers()];
		int maxReward = 100;
		int minReward = 1;
		Random generator = new Random();
		for(int i=0;i<market.getNumberUsers();i++){
			campaigns[i] = new Campaign(market.getUser(i).getSupply(), generator.nextDouble() * (maxReward - minReward) + minReward);
		}
		boolean[][] connections = new boolean[market.getNumberCampaigns()][market.getNumberUsers()];
		for(int i=0;i<market.getNumberUsers();i++){
			for(int j=0;j<market.getNumberCampaigns();j++){
				connections[j][i] = market.isConnected(i, j);				
			}
		}
		return new Market(users,campaigns,connections);
	}
	/*
	 * Creates a clone market but 
	 * Subtract the rewards of all campaign by the parameter r times number of impressions.
	 * Call this market with reserve price r.
	 */
	public static Market createReservePriceMarket(Market market, double r){
		Market clone = cloneMarket(market);
		for(int j=0;j<clone.getNumberCampaigns();j++){
			clone.getCampaign(j).setReward(market.getCampaign(j).getReward() - r*market.getCampaign(j).getDemand());
		}
		return clone;
	}
}
