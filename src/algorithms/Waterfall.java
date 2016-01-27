package algorithms;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import structures.Market;
import structures.MarketAllocation;

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
	
	public WaterfallPrices Solve(){
		/*
		 * Initialize structures to return results
		 */
		double[][] bids = new double[this.market.getNumberUsers()][this.market.getNumberCampaigns()]; 
		double[][] prices = new double[this.market.getNumberUsers()][this.market.getNumberCampaigns()]; 
		int[][] allocation = new int[this.market.getNumberUsers()][this.market.getNumberCampaigns()]; 
		/*
		 * Initialize the supply
		 */
		int[] supply = new int[this.market.getNumberUsers()];
		for(int i=0;i<this.market.getNumberUsers();i++){
			supply[i] = this.market.getUser(i).getSupply();
		}
		/*
		 * Initialize demand and budget
		 */
		int[] demand = new int[this.market.getNumberCampaigns()];
		double[] budget = new double[this.market.getNumberCampaigns()];
		for(int j=0;j<this.market.getNumberCampaigns();j++){
			demand[j] = this.market.getCampaign(j).getDemand();
			budget[j] = this.market.getCampaign(j).getReward();
		}
		/*
		 * Find out which campaigns actually have connections
		 */
		ArrayList<Integer> Campaigns = new ArrayList<Integer>();		
		for(int j=0;j<this.market.getNumberCampaigns();j++){
			if(this.market.hasConnectionsCampaign(j)){
				Campaigns.add(j);
			}
		}		
		//System.out.println("Campaigns: " + Campaigns);
		
		/*
		 * Main loop of the algorithm.
		 */		
		while(true){
			/*
			 * Compute Feasible Campaigns and their corresponding users.
			 */			
			ArrayList<Integer> Users = new ArrayList<Integer>();
			ArrayList<Integer> feasibleCampaigns = new ArrayList<Integer>();
			for(int l=0;l<Campaigns.size();l++){
				if(isSatisfiable(Campaigns.get(l),supply,demand)){ //Add campaign to set of feasible campaigns only if it is actually feasible
					feasibleCampaigns.add(Campaigns.get(l));
					for(int i=0;i<this.market.getNumberUsers();i++){
						if(!(supply[i] == 0) && !Users.contains(i) && this.market.isConnected(i, Campaigns.get(l))){
							Users.add(i);
						}
					}
				}
			}
			//System.out.println("Users: " + Users);
			//System.out.println("feasibleCampaigns = " + feasibleCampaigns);
			
			if(!(feasibleCampaigns.size()>0)) break;
			
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
						bidVector.add(new Bid(budget[feasibleCampaigns.get(l)] / demand[feasibleCampaigns.get(l)] , Users.get(k), feasibleCampaigns.get(l)));
					}
				}
				Collections.sort(bidVector, new BidComparator());
				HighestBids.add(bidVector.get(0));
				SecondHighestBids.add(bidVector.get(1));
				//System.out.println("bidvector ordered: " + bidVector);
			}
			Collections.sort(SecondHighestBids, new BidComparator());
			//System.out.println("HighestBids \t  = " + HighestBids);		
			//System.out.println("SecondHighestBids = " + SecondHighestBids);
			int cheapestUser = SecondHighestBids.get(SecondHighestBids.size()-1).getUserIndex();
			double secondHighestCheapestMarket = SecondHighestBids.get(SecondHighestBids.size()-1).getValue();
			Bid winningBid = this.getBid(cheapestUser, HighestBids);
			double valueWinningBid = winningBid.getValue();
			int indexCampaignWinningBid = winningBid.getCampaignIndex();
			int alloc = Math.min(demand[indexCampaignWinningBid],supply[cheapestUser]);
			//System.out.println("k* = " + cheapestUser + ", p* = "+ secondHighestCheapestMarket + ", b* = " + valueWinningBid + ", l* = " + indexCampaignWinningBid + ", q* = " + alloc);
			supply[cheapestUser] = Math.max(0, supply[cheapestUser] - alloc);
			//for(int i=0;i<this.market.getNumberUsers();i++){
			//	System.out.println("supply["+i+"] = " + supply[i]);
			//}
			demand[indexCampaignWinningBid] = Math.max(0, demand[indexCampaignWinningBid] - alloc);
			budget[indexCampaignWinningBid] = Math.max(0.0, budget[indexCampaignWinningBid] - secondHighestCheapestMarket*alloc);
			//for(int j=0;j<this.market.getNumberCampaigns();j++){
			//	System.out.println("demand["+j+"] = " + demand[j]);
			//	System.out.println("budget["+j+"] = " + budget[j]);
			//}
			
			bids[cheapestUser][indexCampaignWinningBid] = valueWinningBid;
			prices[cheapestUser][indexCampaignWinningBid] = secondHighestCheapestMarket;
			allocation[cheapestUser][indexCampaignWinningBid] = alloc;
			
			if(demand[indexCampaignWinningBid] == 0){
				Campaigns.remove(new Integer(indexCampaignWinningBid));
			}
		}
		//printMatrix(bids);
		//System.out.println("\n-");
		//printMatrix(allocation);
		//System.out.println("\n-");
		//printMatrix(prices);
		return new WaterfallPrices(new MarketAllocation(this.market,allocation),prices);
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
	public boolean isSatisfiable(int j, int[] supply, int[] demand){
		int currentDemand = demand[j];
		for(int i=0;i<this.market.getNumberUsers();i++){
			if(this.market.isConnected(i, j)){
				currentDemand -= supply[i];
			}
			if(currentDemand <= 0){
				return true;
			}
		}
		return false;
	}
	public void printMatrix(int[][] matrix){
		for(int i=0;i<matrix.length;i++){
			System.out.println();
			for(int j=0;j<matrix[0].length;j++){
				System.out.print(matrix[i][j] + "\t");
			}
		}
	}
	public void printMatrix(double[][] matrix){
		DecimalFormat df = new DecimalFormat("#.00"); 
		for(int i=0;i<matrix.length;i++){
			System.out.println();
			for(int j=0;j<matrix[0].length;j++){
				System.out.print(df.format(matrix[i][j]) + "\t");
			}
		}
	}	
	class Bid{
		private final double value;
		private final int userIndex;
		private final int campaignIndex;
		public Bid(double value, int userIndex, int campaignIndex){
			this.value = value;
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
			if(b1.getValue() < b2.getValue()) return 1;
			if(b1.getValue() > b2.getValue()) return -1;
			return 0;
		}
	}
}
