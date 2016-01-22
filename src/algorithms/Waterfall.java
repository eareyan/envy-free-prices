package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import structures.Market;

/*
 * Implements waterfall algorithm.
 * 
 *  @author Enrique Areyan Viqueira
 */
public class Waterfall {
	
	protected Market market;
	
	public Waterfall(Market market){
		this.market = market;
	}
	
	public void Solve(){
		/*
		 * Initialize the supply
		 */
		int[] supply = new int[this.market.getNumberUsers()];
		for(int i=0;i<this.market.getNumberUsers();i++){
			supply[i] = this.market.getUser(i).getSupply();
		}

		/*
		 * Initialize users and campaigns sets. This preprocessing step 
		 * takes care of users and campaigns with no connections.
		 */
		ArrayList<Integer> Users = new ArrayList<Integer>();
		for(int i=0;i<this.market.getNumberUsers();i++){
			if(this.market.hasConnectionsUser(i)){
				Users.add(i);
			}
		}
		ArrayList<Integer> Campaigns = new ArrayList<Integer>();		
		for(int j=0;j<this.market.getNumberCampaigns();j++){
			if(this.market.hasConnectionsCampaign(j)){
				Campaigns.add(j);
			}
		}		
		System.out.println("Users: " + Users);
		System.out.println("Campaigns: " + Campaigns);
		
		/*
		 * Main loop of the algorithm.
		 */
		
		/*
		 * Compute Feasible Campaigns
		 */
		ArrayList<Integer> feasibleCampaigns = new ArrayList<Integer>();
		for(int l=0;l<Campaigns.size();l++){
			if(isSatisfiable(Campaigns.get(l),supply)){
				feasibleCampaigns.add(Campaigns.get(l));
			}
		}
		System.out.println(feasibleCampaigns);
		
		/*
		 * Compute bid vector.
		 */
		ArrayList<Bid> HighestBids = new ArrayList<Bid>(); 
		ArrayList<Bid> SecondHighestBids = new ArrayList<Bid>(); 
		for(int k=0;k<Users.size();k++){
			ArrayList<Bid> bidVector = new ArrayList<Bid>();
			bidVector.add(new Bid(this.market.getUser(Users.get(k)).getReservePrice(),Users.get(k),-1));
			for(int l=0;l<feasibleCampaigns.size();l++){
				if(this.market.isConnected(Users.get(k), feasibleCampaigns.get(l))){
					bidVector.add(new Bid(this.market.getCampaign(feasibleCampaigns.get(l)).getReward() / this.market.getCampaign(feasibleCampaigns.get(l)).getDemand(),Users.get(k),feasibleCampaigns.get(l)));
				}
			}
			//System.out.println("before: " + bidVector);
			Collections.sort(bidVector, new BidComparator());
			HighestBids.add(bidVector.get(0));
			if(bidVector.size() > 1){
				SecondHighestBids.add(bidVector.get(1));
			}else{
				SecondHighestBids.add(bidVector.get(0));
			}
			System.out.println("after: " + bidVector);
		}
		Collections.sort(SecondHighestBids, new BidComparator());
		System.out.println("HighestBids \t  = " + HighestBids);		
		System.out.println("SecondHighestBids = " + SecondHighestBids);
		int cheapestUser = SecondHighestBids.get(SecondHighestBids.size()-1).getUserIndex();
		double secondHighestCheapestMarket = SecondHighestBids.get(SecondHighestBids.size()-1).getValue();
		Bid winningBid = this.getBid(cheapestUser, HighestBids);
		double valueWinningBid = winningBid.getValue();
		int campaignWinningBid = winningBid.getCampaignIndex();
		int allocation = Math.min(this.market.getCampaign(campaignWinningBid).getDemand(),supply[cheapestUser]);
		System.out.println("k* = " + cheapestUser + ", p* = "+ secondHighestCheapestMarket + ", b* = " + valueWinningBid + ", l* = " + campaignWinningBid + ", q* = " + allocation);
	}
	/*
	 * Get a bid by userindex from a list of bids
	 */
	public Bid getBid(int userIndex, ArrayList<Bid> bids){
		for(int i=0;i<bids.size();i++){
			if(bids.get(i).getUserIndex() == userIndex){
				return bids.get(i);
			}
		}
		return null;
	}
	/*
	 * This method checks whether campaign j is satisfiable 
	 * with the given supply, for this market.
	 */
	public boolean isSatisfiable(int j, int[] supply){
		int demand = this.market.getCampaign(j).getDemand();
		for(int i=0;i<this.market.getNumberCampaigns();i++){
			if(this.market.isConnected(i, j)){
				demand -= supply[i];
			}
			if(demand <= 0){
				return true;
			}
		}
		return false;
	}
	
	class Bid{
		private final double value;
		private final int userIndex;
		private final int campaignIndex;
		public Bid(double bid, int userIndex, int campaignIndex){
			this.value = bid;
			this.userIndex = userIndex;
			this.campaignIndex = campaignIndex;
		}
		public double getValue(){ return this.value; }
		public int getUserIndex(){ return this.userIndex; }
		public int getCampaignIndex(){ return this.campaignIndex; }
		public String toString(){ return "("+this.value+","+this.userIndex+","+this.campaignIndex+")"; }
	}
	public class BidComparator implements Comparator<Bid>{
		@Override
		public int compare(Bid b1, Bid b2) {
			return (int) (b2.getValue() - b1.getValue());
		}
	}
}
