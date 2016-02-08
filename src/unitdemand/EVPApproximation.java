package unitdemand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import util.Printer;

/*
 * Implements Envy-Free Pricing Approximation Algorithm by Guruswami et. al.
 * 
 *  @author Enrique Areyan Viqueira
 */
public class EVPApproximation {
	/*
	 * valuation matrix. Provides a valuation v_ij of user i by campaign j.
	 */	
	double[][] valuationMatrix;
	
	public EVPApproximation(double[][] valuationMatrix){
		this.valuationMatrix = valuationMatrix;
	}

	/*
	 * Edge valuations are the values of the maximum weight matching.
	 * This will be used as the reserve prices.
	 */
	public Double[] getEdgeValuations(){
		/* First, find a maximum weight matching */
        int[] result = new HungarianAlgorithm(this.valuationMatrix).execute();
        Double[] valuations = new Double[this.valuationMatrix.length];
        for(int i=0;i<result.length;i++){
        	if(result[i] > -1){
        		/* If an assignment is possible, then the valuation for item j is the weight of that assignment, otherwise is zero*/
        		valuations[i] = -1.0 * this.valuationMatrix[i][result[i]];
        	}
        }
        Arrays.sort(valuations,Collections.reverseOrder());//Order valuations in descending order
		return valuations;
	}
	/*
	 * Implements Envy-Free Pricing Approximation Algorithm as stated in Guruswami et al.
	 */
	public Matching Solve(){
		//System.out.println("============ SOLVE ============");
		Double[] valuations = this.getEdgeValuations();
		double[] reservePrices = new double[this.valuationMatrix.length];
		//Printer.printVector(valuations);
		ArrayList<Matching> setOfMatchings = new ArrayList<Matching>();
		/* For each item, run MaxWEQ_r with reserve prices given by the valuation*/
		for(int i=0;i<this.valuationMatrix.length;i++){
			//System.out.println("++++++++++ reserve price("+i+") = " + valuations[i] + "++++++++++");
			Arrays.fill(reservePrices,valuations[i]);
			setOfMatchings.add(new MaxWEQReservePrices(this.valuationMatrix,reservePrices).Solve());
		}
		Collections.sort(setOfMatchings,new MatchingComparatorBySellerRevenue());
		/*System.out.println(setOfMatchings);
		Printer.printMatrix(this.valuationMatrix);
		System.out.println("Prices for max revenue");
		Printer.printVector(setOfMatchings.get(0).getPrices());
		System.out.println("Matching for max revenue");
		Printer.printMatrix(setOfMatchings.get(0).getMatching());
		System.out.println("Max revenue = " + setOfMatchings.get(0).getSellerRevenue());*/
		return setOfMatchings.get(0);
	}
}
