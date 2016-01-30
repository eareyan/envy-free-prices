package singletonmarket;

import java.util.ArrayList;
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
	 * This method adds two dummy consumers that value item j at the given reserve price and all other items at 0.
	 * The method adds columns to account for these dummy consumers.
	 */
	public double[][] augmentValuationMatrix(double reserveprice){
		//System.out.println("Create augmented valuations matrix with reserve = " + reserveprice);
		int newNumberOfCols = (this.valuationMatrix.length)*2 + this.valuationMatrix[0].length;
		double[][] augmentedValMatrix = new double[this.valuationMatrix.length][newNumberOfCols];
		for(int i=0;i<this.valuationMatrix.length;i++){
			/*Create dummy reserve demand */
			double[] dummyReserveRow = new double[(this.valuationMatrix.length)*2];
			dummyReserveRow[i*2] = -1.0 * reserveprice;
			dummyReserveRow[(i*2)+1] = -1.0 * reserveprice;
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
		//System.out.println("Final Augmented Matrix");
		//Printer.printMatrix(augmentedValMatrix);
		return augmentedValMatrix;
	}
	/*
	 * Edge valuations are the values of the maximum weight matching
	 */
	public double[] getEdgeValuations(){
		/* First, find a maximum weight matching */
        int[] result = new HungarianAlgorithm(this.valuationMatrix).execute();
        double[] valuations = new double[this.valuationMatrix.length];
        for(int i=0;i<result.length;i++){
        	if(result[i] > -1){
        		/* If an assignment is possible, then the valuation for item j is the weight of that assignment, otherwise is zero*/
        		valuations[i] = -1.0 * this.valuationMatrix[i][result[i]];
        	}
        }
		return valuations;
	}
	/*
	 * Implements Envy-Free Pricing Approximation Algorithm as stated in Guruswami et al.
	 */
	public Matching Solve(){
		//System.out.println("============ SOLVE ============");
		double[] valuations = this.getEdgeValuations();
		ArrayList<Matching> setOfMatchings = new ArrayList<Matching>();
		/* For each item, run MaxWEQ_r with reserve prices given by the valuation*/
		for(int i=0;i<this.valuationMatrix.length;i++){
			//System.out.println("++++++++++ reserve price("+i+") = " + valuations[i] + "++++++++++");
			Matching M = new MaxWEQ(this.augmentValuationMatrix(valuations[i])).Solve();
			Matching deducedMatching = this.deduceMatching(M);
			setOfMatchings.add(deducedMatching);
		}
		Collections.sort(setOfMatchings,new MatchingComparatorBySellerRevenue());
		//System.out.println(setOfMatchings);
		//Printer.printMatrix(this.valuationMatrix);
		//System.out.println("Prices for max revenue");
		//Printer.printVector(setOfMatchings.get(0).getPrices());
		//System.out.println("Matching for max revenue");
		//Printer.printMatrix(setOfMatchings.get(0).getMatching());
		//System.out.println("Max revenue = " + setOfMatchings.get(0).getSellerRevenue());
		return setOfMatchings.get(0);
	}
	/*
	 * From a given matching, remove all dummy consumers and their edges.
	 * While there is an unsold item i in the demand set of a real consumer j that is not allocated,
	 * allocate item i to consumer j.
	 */
	public Matching deduceMatching(Matching inputMatching){
		int[][] matchingWithDummy = inputMatching.getMatching();
		double[] prices = inputMatching.getPrices();
		int[][] matching = new int[this.valuationMatrix.length][this.valuationMatrix[0].length];
		for(int i=0;i<this.valuationMatrix.length;i++){
			for(int j=0;j<this.valuationMatrix[0].length;j++){
				matching[i][j] = matchingWithDummy[i][j];
			}
		}
		//System.out.println("Matching with no dummies");
		//Printer.printMatrix(matching);
		/* while there is an unsold item in the demand set of a real consumer that is not allocated an item, allocate it */
		for(int i=0;i<this.valuationMatrix.length;i++){
			boolean itemAllocated = false;
			for(int j=0;j<this.valuationMatrix[0].length;j++){
				if(matching[i][j] == 1){
					itemAllocated = true;
				}
			}
			if(!itemAllocated){ //item i is not allocated
				//System.out.println("item #"+i+", is NOT allocated.. let us try to allocated it");
				for(int j=0;j<this.valuationMatrix[0].length;j++){
					boolean campaignAllocated = false;
					for(int i1=0;i1<this.valuationMatrix.length;i1++){
						if(matching[i1][j] == 1){
							campaignAllocated = true;
						}
					}
					if(!campaignAllocated){//campaign j is not allocated
						//System.out.println("Campaign #"+j+", is not allocated anything");
						if(-1.0*this.valuationMatrix[i][j] - prices[i] >= 0){//It makes sense to allocate this item.
							//System.out.println("IT DOES MAKE SENSE!: " + "="+(-1.0*this.valuationMatrix[i][j]) + " - " + prices[i]);
							matching[i][j] = 1;
						}
					}
				}
			}
		}
		//System.out.println("Matching with possible more allocated items");
		//Printer.printMatrix(matching);
		return new Matching(prices,matching);
	}
}
