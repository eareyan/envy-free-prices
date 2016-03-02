package unitdemand;

import util.Printer;
/*
 * This class implements MaxEQ: Maximum Walrasian Prices, as states in Guruswami et al.
 * 
 * @author Enrique Areyan Viqueira
 */
public class MaxWEQ {
	
	/*
	 * valuation matrix. Provides a valuation v_ij of user i by campaign j.
	 */
	double[][] valuationMatrix;
	
	public MaxWEQ(double[][] valuationMatrix){
		this.valuationMatrix = valuationMatrix;
	}
	/*
	 * Computes the maximum weight matching and its value for the argument matrix.
	 */
	protected Matching computeMaximumWeightMatchingValue(double[][] matrix){
		int[] result  = new MWBMatchingAlgorithm(matrix).getMatching();
        double valueOfMatching = 0.0;
        int[][] matching = new int[matrix.length][matrix[0].length];
        for(int i=0;i<result.length;i++){
        	//System.out.println("--" + result[i]);
        	//If the assignment is possible and the user is actually connected to the campaigns
        	if(result[i] > -1 && matrix[i][result[i]] > Double.NEGATIVE_INFINITY){
        		valueOfMatching += matrix[i][result[i]];
        		matching[i][result[i]] = 1;
        	}
        }
        return new Matching(matching,valueOfMatching);
	}
	/*
	 * Returns the valuation matrix without row indexItem.
	 */
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
	/*
	 * Implements MaxWEQ as stated in Guruswami et al.
	 */
	public Matching Solve(){
		double[] prices = new double[this.valuationMatrix.length]; 
		Matching matchingCompleteV = this.computeMaximumWeightMatchingValue(this.valuationMatrix);
		double maxWeightCompleteV = matchingCompleteV.getValueOfMatching();
		//System.out.println("w(V) = " + maxWeightCompleteV);
		for(int i=0;i<this.valuationMatrix.length;i++){
			prices[i] =Math.round((maxWeightCompleteV - this.computeMaximumWeightMatchingValue(this.valuationMatrixWithNoi(i)).getValueOfMatching()) * 100000.0) / 100000.0;
			/*System.out.println("\t Matrix without row "+i);
			Printer.printMatrix(this.valuationMatrixWithNoi(i));
			System.out.println("w(V_i) = " + this.computeMaximumWeightMatchingValue(this.valuationMatrixWithNoi(i)).getValueOfMatching());
			System.out.println("price["+i+"] = " + prices[i]);*/
		}
		return new Matching(prices,matchingCompleteV.getMatching());
	}
}
