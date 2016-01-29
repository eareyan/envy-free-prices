package singletonmarket;

import util.Printer;
/*
 * This class implements MaxEQ: Maximum Walrasian Prices, as states in Guruswami et al.
 * 
 * @author Enrique Areyan Viqueira
 */
public class MaxWEQ {
	
	double[][] valuationMatrix;
	
	public MaxWEQ(double[][] valuationMatrix){
		this.valuationMatrix = valuationMatrix;
	}

	protected double computeMaximumWeightMatchingValue(double[][] matrix){
		HungarianAlgorithm H = new HungarianAlgorithm(matrix);
        int[] result = H.execute();
        double valueOfMatching = 0.0;
        for(int i=0;i<result.length;i++){
        	//System.out.println("--" + result[i]);
        	//If the assignment is possible and the user is actually connected to the campaigns
        	if(result[i] > -1 && matrix[i][result[i]] < Double.MAX_VALUE){
        		valueOfMatching += matrix[i][result[i]];
        	}
        }
        return -1.0*valueOfMatching;
		
	}
	
	protected double[][] valuationMatrixWithNoi(int indexItem){
		double[][] newValuationMatrix = new double[this.valuationMatrix.length-1][];
		int k=0;
		for(int i=0;i<this.valuationMatrix.length;i++){
			if(i!=indexItem){
				newValuationMatrix[k] = this.valuationMatrix[i];
				k++;
			}
		}
		return newValuationMatrix;
	}
	public double[] Solve(){
		
		double[] prices = new double[this.valuationMatrix.length]; 
		double maxWeightCompleteV = this.computeMaximumWeightMatchingValue(this.valuationMatrix);
		//System.out.println("w(V) = " + maxWeightCompleteV);
		for(int i=0;i<this.valuationMatrix.length;i++){
			//Printer.printMatrix(this.valuationMatrixWithNoi(i));
			//System.out.println("w(V_i) = " + this.computeMaximumWeightMatchingValue(this.valuationMatrixWithNoi(i)));
			prices[i] = maxWeightCompleteV - this.computeMaximumWeightMatchingValue(this.valuationMatrixWithNoi(i));
			//System.out.println("price["+i+"] = " + prices[i]);
		}
		return prices;
	}
	
	public double MaxWEQSellerRevenue(){
		double[] prices = this.Solve();
		double sellerRevenue = 0.0;
		for(int i=0;i<prices.length;i++){
			sellerRevenue += prices[i]; 
		}
		return sellerRevenue;
	}
}
