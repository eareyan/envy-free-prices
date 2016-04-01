package algorithms.allocations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import structures.Campaign;
import structures.CampaignComparatorByReward;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketPrices;
import util.Printer;

/*
 * This class implements greedy allocation.
 * 
 * @author Enrique Areyan Viqueira
 */
public class GreedyAllocation {
	
	protected Market market;
	
	protected int Order = 0;
	
	public GreedyAllocation(Market market){
		this.market = market;
	}
	
	public GreedyAllocation(Market market, int Order){
		this.market = market;
		this.Order = Order;
	}
	
	public MarketAllocation Solve(){
		/* First make a copy of the campaigns array and sort it by reward */
		Campaign[] campaigns = new Campaign[this.market.getNumberCampaigns()];
		for(int j=0;j<this.market.getNumberCampaigns();j++){
			campaigns[j] = new Campaign(this.market.getCampaign(j).getDemand(),this.market.getCampaign(j).getReward(),j);
		}
		Arrays.sort(campaigns, new CampaignComparatorByReward()); //Sort campaigns by descending order of reward
		int[][] greedyAllocation = new int[this.market.getNumberUsers()][this.market.getNumberCampaigns()];
		//Printer.printMatrix(greedyAllocation);
		int[] totalAllocationFromUserSoFar = new int[this.market.getNumberUsers()];
		for(int j=0;j<this.market.getNumberCampaigns();j++){
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
			Collections.sort(accessibleUsers, new UsersSupplyComparatorByRemainingSupply(this.Order));
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
	/*
	 * This auxiliary data structure keeps track of the remaining supply of users
	 * so that they can be ordered before being assigned to campaigns.
	 */
	public class UserSupply{
		protected int i;
		protected int remainingSupply;
		public UserSupply(int i,int remainingSupply){
			this.i = i;
			this.remainingSupply = remainingSupply;
		}
		public int getId(){
			return this.i;
		}
		public int getRemainingSupply(){
			return this.remainingSupply;
		}
		public String toString(){
			return "("+i+","+this.remainingSupply+")";
		}
	}
	public class UsersSupplyComparatorByRemainingSupply implements Comparator<UserSupply>{
		protected int Order = 0; /*order 1 means ASC and -1 means DESC, any other means no order*/
		public UsersSupplyComparatorByRemainingSupply(int Order){
			this.Order = Order;
		}
		public UsersSupplyComparatorByRemainingSupply(){
			
		}
		@Override
		public int compare(UserSupply U1, UserSupply U2) {
			if(this.Order == -1){
				if(U1.getRemainingSupply() < U2.getRemainingSupply()) return 1;
				if(U1.getRemainingSupply() > U2.getRemainingSupply()) return -1;
			}else if(this.Order == 1){
				if(U1.getRemainingSupply() < U2.getRemainingSupply()) return -1;
				if(U1.getRemainingSupply() > U2.getRemainingSupply()) return 1;				
			}
			return 0;
		}		
	}
}
