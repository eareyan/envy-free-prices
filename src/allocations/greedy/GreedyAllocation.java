package allocations.greedy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import allocations.interfaces.AllocationAlgoInterface;
import structures.Campaign;
import structures.Market;
import structures.MarketAllocation;
import structures.exceptions.CampaignCreationException;

/*
 * This class implements greedy allocation.
 * 
 * @author Enrique Areyan Viqueira
 */
public class GreedyAllocation implements AllocationAlgoInterface{
	
	protected Comparator<Campaign> CampaignComparator;
	
	protected Comparator<UserSupply> UserSupplyComparator;
	
	public GreedyAllocation(){
		this.CampaignComparator = new CampaignComparatorByRewardToImpressionsRatio();
		this.UserSupplyComparator = new UsersSupplyComparatorByRemainingSupply(); 
	}
	
	public GreedyAllocation(Comparator<Campaign> CampaignComparator, Comparator<UserSupply> UserSupplyComparator){
		this.CampaignComparator = CampaignComparator; 
		this.UserSupplyComparator = UserSupplyComparator;
	}
	
	public MarketAllocation Solve(Market market) throws CampaignCreationException{
		/* First make a copy of the campaigns array and sort it by reward */
		Campaign[] campaigns = new Campaign[market.getNumberCampaigns()];
		for(int j=0;j<market.getNumberCampaigns();j++){
			campaigns[j] = new Campaign(market.getCampaign(j).getDemand(),market.getCampaign(j).getReward() - market.getCampaign(j).getDemand() * market.getCampaign(j).getReserve(),j);
		}
		Arrays.sort(campaigns, this.CampaignComparator); //Sort campaigns by the given comparator
		int[][] greedyAllocation = new int[market.getNumberUsers()][market.getNumberCampaigns()];
		int[] totalAllocationFromUserSoFar = new int[market.getNumberUsers()];
		/* Allocate each campaign, if possible, one at a time */
		for(int j=0;j<market.getNumberCampaigns();j++){
			if(campaigns[j].getReward() <= 0) continue; //Ignore campaigns with zero or negative reward.
			ArrayList<UserSupply> accessibleUsers = new ArrayList<UserSupply>();
			int totalAvailableSupply = 0;
			int totalAllocationToCampaignSoFar = 0;
			for(int i=0;i<market.getNumberUsers();i++){ //Compute the accessible users to this campaign
				/* z_ij computes the number of impressions available from user i to campaign j */
				int z_ij = (int) Math.min(market.getUser(i).getSupply() - totalAllocationFromUserSoFar[i], Math.floor(market.getCampaign(campaigns[j].getBackpointer()).getLevel()*market.getUser(i).getSupply()));
				if(market.isConnected(i, campaigns[j].getBackpointer()) &&  z_ij > 0){
					totalAvailableSupply += z_ij;
					accessibleUsers.add(new UserSupply(i,market.getUser(i).getSupply() - totalAllocationFromUserSoFar[i]));
				}
			}
			Collections.sort(accessibleUsers, this.UserSupplyComparator);
			if(totalAvailableSupply >= market.getCampaign(campaigns[j].getBackpointer()).getDemand()){
				/* Try to allocate */
				for (UserSupply user : accessibleUsers) {
					int i = user.getId(); // User Index
					int jIndex = campaigns[j].getBackpointer(); // Campaign Index
					int z_ij = (int) Math.min(market.getUser(i).getSupply() - totalAllocationFromUserSoFar[i], Math.floor(market.getCampaign(jIndex).getLevel()*market.getUser(i).getSupply()));
					greedyAllocation[i][jIndex] = Math.min(market.getCampaign(jIndex).getDemand() - totalAllocationToCampaignSoFar, z_ij);
					totalAllocationFromUserSoFar[i] += greedyAllocation[i][jIndex];
					totalAllocationToCampaignSoFar += greedyAllocation[i][jIndex];
					if(totalAllocationToCampaignSoFar == market.getCampaign(jIndex).getDemand()){
						break; //Break if the current campaign has been completely satisfied
					}
				}
			}
		}
		return new MarketAllocation(market,greedyAllocation);
	}
}
