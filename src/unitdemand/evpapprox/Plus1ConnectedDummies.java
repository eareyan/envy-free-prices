package unitdemand.evpapprox;

import util.Printer;

public class Plus1ConnectedDummies extends AbstractMaxWEQReservePrices{

	public Plus1ConnectedDummies(double[][] valuationMatrix, double[] reservePrices) {
		super(valuationMatrix, reservePrices);
	}
	public Plus1ConnectedDummies(double[][] valuationMatrix) {
		super(valuationMatrix);
	}
	/*
	 * This method adds two dummy consumers that value item i at reserve price
	 * only in case (i,j)\in E and all other items at 0.
	 * The method adds columns to account for these dummy consumers.
	 */	
	public double[][] augmentValuationMatrix(int j){
		System.out.println("Dummies connected to all items");
		int newNumberOfCols = (this.valuationMatrix.length)*2 + this.valuationMatrix[0].length;
		double[][] augmentedValMatrix = new double[this.valuationMatrix.length][newNumberOfCols];
		for(int i=0;i<this.valuationMatrix.length;i++){
			/*Create dummy reserve demand */
			double[] dummyReserveRow = new double[(this.valuationMatrix.length)*2];
			if(this.valuationMatrix[i][j]>Double.NEGATIVE_INFINITY){			
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
