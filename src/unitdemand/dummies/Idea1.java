package unitdemand.dummies;

import unitdemand.MaxWEQReservePrices;
import util.Printer;

public class Idea1 extends MaxWEQReservePrices{
	
	protected int currentCampaignIndex;

	public Idea1(double[][] valuationMatrix, double[] reservePrices) {
		super(valuationMatrix, reservePrices);
		// TODO Auto-generated constructor stub
	}
	
	public void setCurrentCampaignIndex(int currentCampaignIndex){
		this.currentCampaignIndex = currentCampaignIndex;
	}
	
	public double[][] augmentValuationMatrix(){
		System.out.println("IDEA 1: create TWO dummies only for users k such that (k,j) in E. Here j = " + this.currentCampaignIndex);
		int newNumberOfCols = (this.valuationMatrix.length)*2 + this.valuationMatrix[0].length;
		double[][] augmentedValMatrix = new double[this.valuationMatrix.length][newNumberOfCols];
		for(int i=0;i<this.valuationMatrix.length;i++){
			double[] dummyReserveRow = new double[(this.valuationMatrix.length)*2];
			if(this.valuationMatrix[i][this.currentCampaignIndex]>Double.NEGATIVE_INFINITY){
				/*Create dummy reserve demand */
				dummyReserveRow[i*2] = this.reservePrices[i];
				dummyReserveRow[(i*2)+1] = this.reservePrices[i];
			}
			/* copy original row*/
			double[] originalrow = new double[this.valuationMatrix[0].length];
			System.arraycopy(this.valuationMatrix[i], 0, originalrow, 0,this.valuationMatrix[i].length);
			double[] finalrow = new double[newNumberOfCols];
			/* concatenate original and dummy rows together*/
			System.arraycopy(originalrow,0,finalrow,0,originalrow.length);
			System.arraycopy(dummyReserveRow,0,finalrow,originalrow.length,dummyReserveRow.length);
			/* add final row to the augmented matrix*/
			augmentedValMatrix[i] = finalrow;
			
		}
		System.out.println("Final Augmented Matrix");
		Printer.printMatrix(augmentedValMatrix);
		return augmentedValMatrix;
	}

}