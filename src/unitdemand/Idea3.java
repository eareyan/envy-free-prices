package unitdemand;

import util.Printer;

public class Idea3 extends MaxWEQReservePrices{
	
	protected int currentCampaignIndex;

	public Idea3(double[][] valuationMatrix, double[] reservePrices) {
		super(valuationMatrix, reservePrices);
		// TODO Auto-generated constructor stub
	}
	
	public void setCurrentCampaignIndex(int currentCampaignIndex){
		this.currentCampaignIndex = currentCampaignIndex;
	}
	
	public double[][] augmentValuationMatrix(){
		System.out.println("IDEA 3: create a second copy of campaign j. Here j = " + this.currentCampaignIndex);
		double[][] augmentedValMatrix = new double[this.valuationMatrix.length][this.valuationMatrix[0].length+1];
		for(int i=0;i<augmentedValMatrix.length;i++){
			for(int j=0;j<augmentedValMatrix[0].length;j++){
				if(j<this.valuationMatrix[0].length){
					augmentedValMatrix[i][j] = this.valuationMatrix[i][j];
				}else{
					augmentedValMatrix[i][j] = this.valuationMatrix[i][this.currentCampaignIndex];
				}
			}
		}
		System.out.println("Final Augmented Matrix");
		Printer.printMatrix(augmentedValMatrix);
		return augmentedValMatrix;
	}

}