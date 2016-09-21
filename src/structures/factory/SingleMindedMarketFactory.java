package structures.factory;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import structures.Campaign;
import structures.Market;
import structures.User;
import structures.exceptions.CampaignCreationException;

public class SingleMindedMarketFactory {
	
	/*
	 * Inputs: 	n, number of items
	 *			m, number of bidders 			
	 * Output: 	a single-minded market. 
	 * 
	 */
	public static Market createSingleMindedMarket(int n, int m) throws CampaignCreationException {
		User[] users = new User[n];
		for(int i = 0; i < n; i++) {
			users[i] = new User(1);
		}		
		Random generator = new Random();
		Campaign[] campaigns = new Campaign[m];
		for(int j = 0; j < m; j++) {
			campaigns[j] = new Campaign(generator.nextInt(n) + 1, generator.nextDouble() * (RandomMarketFactory.defaultMaxReward - RandomMarketFactory.defaultMinReward) + RandomMarketFactory.defaultMinReward);
		}
		boolean[][] connections = new boolean[n][m];
		for(int j = 0; j < m; j++) {
			int demand = campaigns[j].getDemand();
			/* Each bidder connects exactly with I_j users*/
			Set<Integer> connectTo = SingleMindedMarketFactory.randomNumbers(demand, n);
			//System.out.println("Bidder " + j + " connect to "  + connectTo);		
			for(Integer i : connectTo){
				connections[i][j] = true;
			}
		}
		return new Market(users,campaigns,connections);
	}
	
	/*
	 * Input:	a single-minded market
	 * 			reserve price r (double)
	 * Output:	a single-minded market
	 * 			where bidders that can't afford the reserve
	 * 			are dropped out.
	 */
	public static Market discountSingleMindedMarket(Market M, double reserve) {
		//Construct a map of the campaigns that "survive" the reserve price
		HashMap<Integer,Campaign> remainingCampaigns = new HashMap<Integer,Campaign>();
		for(int j = 0; j < M.getNumberCampaigns(); j++){
			if(M.getCampaign(j).getReward() - reserve * M.getCampaign(j).getDemand() >= 0 ) {
				remainingCampaigns.put(j, M.getCampaign(j));
			}
		}
		//Construct a new market only with "surviving" campaigns
		Campaign[] campaigns = new Campaign[remainingCampaigns.size()];
		int j = 0;
		boolean[][] connections = new boolean[M.getNumberUsers()][remainingCampaigns.size()];
		for (HashMap.Entry<Integer, Campaign> entry : remainingCampaigns.entrySet()) {
			Integer key = entry.getKey();
			Campaign value = entry.getValue();
			campaigns[j] = value;
			for(int i = 0; i < M.getNumberUsers(); i++){
				connections[i][j] = M.getConnections()[i][key];
			}
			j++;
		}
		return new Market(M.getUsers(),campaigns,connections);
	}
	
	/*
	 * Input: 	an integer n
	 * 			an integer max
	 * Output:	A set of n distinct, random integers, between 0 and max.
	 * 			If n>=max, returns the set of integers 0...max
	 */
	public static Set<Integer> randomNumbers(int n, int max) {
		Set<Integer> generated = new LinkedHashSet<Integer>();
		if (n >= max){
			/*
			 * If we want more numbers than the max, it means
			 * we want all numbers from 1...max.
			 */
			for(int i = 0; i < max; i++){
				generated.add(i);
			}
			return generated;
		} else {
			Random rng = new Random(); // Ideally just create one instance globally. Note: use LinkedHashSet to maintain insertion order
			while (generated.size() < n) {
				//System.out.println("11");
				Integer next = rng.nextInt(max);
				//As we're adding to a set, this will automatically do a containment check
				generated.add(next);
			}
		}
		return generated;
	}
}
