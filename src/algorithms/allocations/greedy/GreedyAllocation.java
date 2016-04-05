package algorithms.allocations.greedy;

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
	
	protected double reserve = 0.0;
	
	public GreedyAllocation(Market market){
		this.market = market;
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
		this.reserve = reserve;
	}
	
	public MarketAllocation Solve(){
		/* First make a copy of the campaigns array and sort it by reward */
		Campaign[] campaigns = new Campaign[this.market.getNumberCampaigns()];
		for(int j=0;j<this.market.getNumberCampaigns();j++){
			campaigns[j] = new Campaign(this.market.getCampaign(j).getDemand(),this.market.getCampaign(j).getReward() - this.market.getCampaign(j).getDemand() * this.reserve,j);
		}
		Arrays.sort(campaigns, this.CampaignComparator); //Sort campaigns by the given comparator
		int[][] greedyAllocation = new int[this.market.getNumberUsers()][this.market.getNumberCampaigns()];
		int[] totalAllocationFromUserSoFar = new int[this.market.getNumberUsers()];
		for(int j=0;j<this.market.getNumberCampaigns();j++){
			if(campaigns[j].getReward() <= 0) continue; //Ignore campaigns with zero or negative reward.
			//Backpointer of the campaign points to the campaign in the original market
			//System.out.println(campaigns[j].getBackpointer() + " = " + campaigns[j].getReward());
			ArrayList<UserSupply> accessibleUsers = new ArrayList<UserSupply>();
			int totalAvailableSupply = 0;
			int totalAllocationToCampaignSoFar = 0;
			for(int i=0;i<this.market.getNumberUsers();i++){ //Compute the accessible users to this campaign
				if(this.market.isConnected(i, campaigns[j].getBackpointer()) && totalAllocationFromUserSoFar[i] < this.market.getUser(i).getSupply()){
					totalAvailableSupply += this.market.getUser(i).getSupply() - totalAllocationFromUserSoFar[i];
					accessibleUsers.add(new UserSupply(i,this.market.getUser(i).getSupply() - totalAllocationFromUserSoFar[i]));
				}
			}
			//System.out.println(accessibleUsers);
			Collections.sort(accessibleUsers, this.UserSupplyComparator);
			//System.out.println(accessibleUsers);
			if(totalAvailableSupply >= this.market.getCampaign(campaigns[j].getBackpointer()).getDemand()){
				/* Try to allocate */
				for (UserSupply user : accessibleUsers) {
					int i = user.getId();
					//System.out.println("Try to allocate user " + i + " to campaign " + campaigns[j].getBackpointer() + ", from " + (this.market.getUser(i).getSupply() - totalAllocationFromUserSoFar[i]) + " many left");
					greedyAllocation[i][campaigns[j].getBackpointer()] = Math.min(this.market.getCampaign(campaigns[j].getBackpointer()).getDemand() - totalAllocationToCampaignSoFar, this.market.getUser(i).getSupply() - totalAllocationFromUserSoFar[i]);
					totalAllocationFromUserSoFar[i] += greedyAllocation[i][campaigns[j].getBackpointer()];
					totalAllocationToCampaignSoFar += greedyAllocation[i][campaigns[j].getBackpointer()];
					if(totalAllocationToCampaignSoFar == this.market.getCampaign(campaigns[j].getBackpointer()).getDemand()){
						break; //Break if the current campaign has been completely satisfied
					}
				}
			}
		}
		//Printer.printMatrix(greedyAllocation);
		return new MarketAllocation(this.market,greedyAllocation);
	}
}
