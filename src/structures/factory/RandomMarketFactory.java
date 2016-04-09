package structures.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import structures.Campaign;
import structures.Market;
import structures.User;

public class RandomMarketFactory {
	public static double defaultMaxReward = 10.0;
	public static double defaultMinReward = 1.0;
	public static int defaultMaxSupplyPerUser = 10;
	public static int defaultMinSupplyPerUser = 1;
	public static int defaultMaxDemandPerCampaign = 10;
	public static int defaultMinDemandPerCampaign = 1;
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
	 * probability of connection. Other parameters are given by default (static members of this class).
	 */
	public static Market randomMarket(int numberUsers, int numberCampaigns, double probabilityConnections){
		return RandomMarketFactory.randomMarket(numberUsers, RandomMarketFactory.defaultMinSupplyPerUser ,RandomMarketFactory.defaultMaxSupplyPerUser,  numberCampaigns, RandomMarketFactory.defaultMinDemandPerCampaign , RandomMarketFactory.defaultMaxDemandPerCampaign, RandomMarketFactory.defaultMinReward, RandomMarketFactory.defaultMaxReward,  probabilityConnections);		
	}
	/*
	 * Generate over demanded and over supplied markets
	 */
	public static Market generateOverDemandedMarket(int numberUsers, int numberCampaigns,double probabilityConnection, int b){
		return RandomKMarket(numberUsers, numberCampaigns, probabilityConnection, b);
	}
	public static Market generateOverSuppliedMarket(int numberUsers, int numberCampaigns,double probabilityConnection, int b){
		return MarketFactory.transposeMarket(RandomKMarket(numberCampaigns, numberUsers, probabilityConnection, b));
	}
	/*
	 * Generate a random market with a fixed supply to demand ratio.
	 */
	public static Market RandomKMarket(int numberUsers, int numberCampaigns, double probabilityConnection, int b){
		Random generator = new Random();
		User[] users = new User[numberUsers];
		Campaign[] campaigns = new Campaign[numberCampaigns];
		int[] campaignConnectedToUser = new int[numberUsers];
		boolean[][] connections = new boolean[numberUsers][numberCampaigns];
		for(int i=0;i<numberUsers;i++){
			for(int j=0;j<numberCampaigns;j++){
				connections[i][j] = generator.nextDouble() <= probabilityConnection;
				if(connections[i][j]){
					campaignConnectedToUser[i]++;
				}
			}
		}
		/*
		 * Generate supply and demand to maintain the ratio
		 */
		int totalDemand = numberCampaigns; //Each campaign demands one initially. This is so that we don't have campaigns demanding 0 impressions.
		int[] finalDemands = new int[numberCampaigns];
		for(int j=0;j<numberCampaigns;j++){
			finalDemands[j] = 1;
		}
		int[] finalSupply = new int[numberUsers]; 
		int x = 0 , k = 0;
		int totalSupply = 0;
		for(int i=0;i<numberUsers;i++){
			finalSupply[i] = campaignConnectedToUser[i] + generator.nextInt((RandomMarketFactory.defaultMaxSupplyPerUser - RandomMarketFactory.defaultMinSupplyPerUser) + 1) + RandomMarketFactory.defaultMinSupplyPerUser;
			//System.out.println("finalSupply["+i+"] = " + finalSupply[i]);
			totalSupply += finalSupply[i];
			x = totalSupply * b - totalDemand;
			totalDemand += x;
			ArrayList<Integer> demands = generateRandomIntegerFixedSum(campaignConnectedToUser[i],x);
			k = 0;
			for(int j=0;j<numberCampaigns;j++){
				if(connections[i][j]){
					finalDemands[j] += demands.get(k);
					k++;
				}
			}
		}
		for(int i=0;i<numberUsers;i++){
			users[i] = new User(finalSupply[i]);
		}
		for(int j=0;j<numberCampaigns;j++){
			campaigns[j] = new Campaign(finalDemands[j],generator.nextDouble() * (RandomMarketFactory.defaultMaxReward - RandomMarketFactory.defaultMinReward) + RandomMarketFactory.defaultMinReward);			
		}
				
		return new Market(users,campaigns,connections);
	}
	/*
	 * This function takes a pair of positive integers (n,x) and returns 
	 * a list of n random integers that add up to x.
	 */
	public static ArrayList<Integer> generateRandomIntegerFixedSum(int n, int x){
		if(x == 1 || x <= 0){
			ArrayList<Integer> listNumbers = new ArrayList<Integer>(n);
			listNumbers.add(1);
			for(int i=0;i<n-1;i++){
				listNumbers.add(0);
			}
			return listNumbers;
		}
		n++;
		Random generator = new Random();
		ArrayList<Integer> listNumbers = new ArrayList<Integer>(n);
		int max = x-1;
		int min = 1;
		//System.out.println("n = " + n +", x = " + x);
		for(int i=0;i<n-2;i++){
			listNumbers.add(generator.nextInt((max-min) + 1) + min);
		}
		listNumbers.add(0);
		listNumbers.add(x);
		Collections.sort(listNumbers);
		ArrayList<Integer> finalList = new ArrayList<Integer>(n-1);
		for(int i=0;i<n-1;i++){
			finalList.add(listNumbers.get(i+1)-listNumbers.get(i));
		}
		//System.out.println(finalList);
		return finalList;
	}
}
