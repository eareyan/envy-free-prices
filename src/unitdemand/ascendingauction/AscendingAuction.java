package unitdemand.ascendingauction;

import java.util.ArrayList;
import java.util.Collections;

import util.Printer;

public class AscendingAuction {
	
	protected double[][] valuationMatrix;
	
	protected int[][] allocation;
	
	protected double[] prices;
	
	protected static double epsilon = 1.0;
	
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
		
		//while(true){
			ArrayList<Bid> setOfBids = new ArrayList<Bid>();
			for(int j=0;j<this.valuationMatrix[0].length;j++){
				ArrayList<Bid> setOfBidsOfCampaign = new ArrayList<Bid>();
				for(int i=0;i<this.valuationMatrix.length;i++){
					double bid = this.valuationMatrix[i][j] - (this.prices[i] + AscendingAuction.epsilon);
					if(bid >0){
						setOfBidsOfCampaign.add(new Bid(i,j,bid));
					}
				}
				System.out.println(setOfBidsOfCampaign);
				if(setOfBidsOfCampaign.size()>0){
					Collections.sort(setOfBidsOfCampaign,new BidsComparatorByBid());
					setOfBids.add(setOfBidsOfCampaign.get(0));
				}
			}
			if(setOfBids.size()>0)
			System.out.println(setOfBids);
		//}
	}
}
