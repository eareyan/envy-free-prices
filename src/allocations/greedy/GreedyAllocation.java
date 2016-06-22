package allocations.greedy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import structures.Campaign;
import structures.Market;
import structures.MarketAllocation;

/*
 * This class implements greedy allocation.
 * 
 * @author Enrique Areyan Viqueira
 */
public class GreedyAllocation {
	
	protected Market market;
	
	protected Comparator<Campaign> CampaignComparator;
	
	protected Comparator<UserSupply> UserSupplyComparator;
	
	public GreedyAllocation(Market market){
		this.market = market;
		this.CampaignComparator = new CampaignComparatorByRewardToImpressionsRatio();
		this.UserSupplyComparator = new UsersSupplyComparatorByRemainingSupply(); 
	}
	
	public GreedyAllocation(Market market, Comparator<Campaign> CampaignComparator, Comparator<UserSupply> UserSupplyComparator){
		this.market = market;
		this.CampaignComparator = CampaignComparator; 
		this.UserSupplyComparator = UserSupplyComparator;
	}
	public GreedyAllocation(Market market, Comparator<Campaign> CampaignComparator, Comparator<UserSupply> UserSupplyComparator, double reserve){
		this.market = market;
		this.CampaignComparator = CampaignComparator; 
		this.UserSupplyComparator = UserSupplyComparator;
		/* Set the reserve of every campaign to be the same reserve price, received by the constructor.*/
		for(int j=0;j<this.market.getNumberCampaigns();j++){
			this.market.getCampaign(j).setReserve(reserve);
		}
	}
	
	public MarketAllocation Solve(){
		/* First make a copy of the campaigns array and sort it by reward */
		Campaign[] campaigns = new Campaign[this.market.getNumberCampaigns()];
		for(int j=0;j<this.market.getNumberCampaigns();j++){
			campaigns[j] = new Campaign(this.market.getCampaign(j).getDemand(),this.market.getCampaign(j).getReward() - this.market.getCampaign(j).getDemand() * this.market.getCampaign(j).getReserve(),j);
		}
		Arrays.sort(campaigns, this.CampaignComparator); //Sort campaigns by the given comparator
		int[][] greedyAllocation = new int[this.market.getNumberUsers()][this.market.getNumberCampaigns()];
		int[] totalAllocationFromUserSoFar = new int[this.market.getNumberUsers()];
		/* Allocate each campaign, if possible, one at a time */
		for(int j=0;j<this.market.getNumberCampaigns();j++){
			if(campaigns[j].getReward() <= 0) continue; //Ignore campaigns with zero or negative reward.
			ArrayList<UserSupply> accessibleUsers = new ArrayList<UserSupply>();
			int totalAvailableSupply = 0;
			int totalAllocationToCampaignSoFar = 0;
			for(int i=0;i<this.market.getNumberUsers();i++){ //Compute the accessible users to this campaign
				/* z_ij computes the number of impressions available from user i to campaign j */
				int z_ij = (int) Math.min(this.market.getUser(i).getSupply() - totalAllocationFromUserSoFar[i], Math.floor(this.market.getCampaign(campaigns[j].getBackpointer()).getLevel()*this.market.getUser(i).getSupply()));
				if(this.market.isConnected(i, campaigns[j].getBackpointer()) &&  z_ij > 0){
					totalAvailableSupply += z_ij;
					accessibleUsers.add(new UserSupply(i,this.market.getUser(i).getSupply() - totalAllocationFromUserSoFar[i]));
				}
			}
			Collections.sort(accessibleUsers, this.UserSupplyComparator);
			if(totalAvailableSupply >= this.market.getCampaign(campaigns[j].getBackpointer()).getDemand()){
				/* Try to allocate */
				for (UserSupply user : accessibleUsers) {
					int i = user.getId(); // User Index
					int jIndex = campaigns[j].getBackpointer(); // Campaign Index
					int z_ij = (int) Math.min(this.market.getUser(i).getSupply() - totalAllocationFromUserSoFar[i], Math.floor(this.market.getCampaign(jIndex).getLevel()*this.market.getUser(i).getSupply()));
					greedyAllocation[i][jIndex] = Math.min(this.market.getCampaign(jIndex).getDemand() - totalAllocationToCampaignSoFar, z_ij);
					totalAllocationFromUserSoFar[i] += greedyAllocation[i][jIndex];
					totalAllocationToCampaignSoFar += greedyAllocation[i][jIndex];
					if(totalAllocationToCampaignSoFar == this.market.getCampaign(jIndex).getDemand()){
						break; //Break if the current campaign has been completely satisfied
					}
				}
			}
		}
		return new MarketAllocation(this.market,greedyAllocation);
	}
}
