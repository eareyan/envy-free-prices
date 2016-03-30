package unitdemand.ascendingauction;

import java.util.ArrayList;
import java.util.Collections;

import util.Printer;

public class AscendingAuction {
	
	protected double[][] valuationMatrix;
	
	protected int[][] allocation;
	
	protected double[] prices;
	
	protected static double epsilon = 0.001;
	
	public AscendingAuction(double[][] valuationMatrix){
		this.valuationMatrix = valuationMatrix;
		this.prices = new double[this.valuationMatrix.length];
		this.allocation = new int[this.valuationMatrix.length][this.valuationMatrix[0].length];
	}
	
	public void Solve(){
		System.out.println("Initial Prices : ");
		Printer.printVector(this.prices);
		System.out.println("Initial Allocation : ");
		Printer.printMatrix(this.allocation);
		
		ArrayList<Integer> unallocatedCampaigns = new ArrayList<Integer>();
		for(int j=0;j<this.valuationMatrix[0].length;j++){ /* Initially all campaigns are unallocated*/
			unallocatedCampaigns.add(j);
		}		
		while(true){
			//System.out.println("unallocatedCampaigns = " + unallocatedCampaigns);
			ArrayList<Bid> setOfBids = new ArrayList<Bid>();
			for(Integer j: unallocatedCampaigns){
				ArrayList<Bid> setOfBidsOfCampaign = new ArrayList<Bid>();
				for(int i=0;i<this.valuationMatrix.length;i++){
					double bid = this.valuationMatrix[i][j] - (this.prices[i] + AscendingAuction.epsilon);
					if(bid >= 0){
						setOfBidsOfCampaign.add(new Bid(i,j,bid));
					}
				}
				//System.out.println(setOfBidsOfCampaign);
				if(setOfBidsOfCampaign.size()>0){
					Collections.sort(setOfBidsOfCampaign,new BidsComparatorByBid());
					setOfBids.add(setOfBidsOfCampaign.get(0));
				}
			}
			if(setOfBids.size()>0){
				//System.out.println("Set of Bids = " + setOfBids);
				Bid b = setOfBids.get(0);
				//System.out.println("Set x["+b.getUser()+"]["+b.getCampaign()+"] = 1, and p["+b.getUser()+"] = p + e, and try to reallocate");
				this.allocation[b.getUser()][b.getCampaign()] = 1;
				unallocatedCampaigns.remove(new Integer(b.getCampaign()));
				this.prices[b.getUser()] += AscendingAuction.epsilon;
				for(int j=0;j<this.valuationMatrix[0].length;j++){
					if(j!=b.getCampaign() && this.allocation[b.getUser()][j] == 1){
						this.allocation[b.getUser()][j] = 0;
						unallocatedCampaigns.add(new Integer(j));
						//System.out.println("We have to unallocate");
					}
				}
			}else{ //No more bids, halt with current allocation and prices
				break;
			}
		}
		System.out.println("Final Prices : ");
		Printer.printVector(this.prices);
		System.out.println("Final Allocation : ");
		Printer.printMatrix(this.allocation);
		
		for(int i=0;i<this.valuationMatrix.length;i++){
			System.out.println(this.prices[i]);
		}
		
	}
}
