package unitdemand.evpapprox;

import unitdemand.Matching;
import unitdemand.MaxWEQ;
import util.Printer;

abstract public class AbstractMaxWEQReservePrices {
	
	/*
	 * valuation matrix. Provides a valuation v_ij of user i by campaign j.
	 * Reserve Prices.
	 */	
	protected double[][] valuationMatrix;
	protected double[] reservePrices;
	
	public AbstractMaxWEQReservePrices(double[][] valuationMatrix, double[] reservePrices){
		this.valuationMatrix = valuationMatrix;
		this.reservePrices = reservePrices;
	}
	public AbstractMaxWEQReservePrices(double[][] valuationMatrix){
		this.valuationMatrix = valuationMatrix;
	}
	/*
	 * The method solve runs MaxWEQ on the augmented valuation matrix
	 * and then deduces a matching. 
	 */
	public Matching Solve(int j){
		Matching M = new MaxWEQ(this.augmentValuationMatrix(j)).Solve();
		Matching deducedMatching = this.deduceMatching(M);
		return new Matching(M.getPrices(),deducedMatching.getMatching());
	}
	/*
	 * Set reserve prices.
	 */
	public void setReservePrices(double[] reservePrices){
		this.reservePrices = reservePrices;
	}
	/*
 	 * This method is to be extended by an implemented class.
 	 * The idea is that a valuation matrix can be extended with dummies in many
 	 * different ways. 	
 	 */
	abstract public double[][] augmentValuationMatrix(int j);
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
		System.out.println("++++++Start Deducing Matching+++++++++");
		System.out.println("Matching with no dummies");
		Printer.printMatrix(matching);
		/* while there is an unsold item in the demand set of a real consumer that is not allocated an item, allocate it */
		for(int i=0;i<this.valuationMatrix.length;i++){
			boolean itemAllocated = false;
			for(int j=0;j<this.valuationMatrix[0].length;j++){
				if(matching[i][j] == 1){
					itemAllocated = true;
				}
			}
			if(!itemAllocated){ //item i is not allocated
				System.out.println("item #"+i+", is NOT allocated.. let us try to allocated it");
				for(int j=0;j<this.valuationMatrix[0].length;j++){
					boolean campaignAllocated = false;
					for(int i1=0;i1<this.valuationMatrix.length;i1++){
						if(matching[i1][j] == 1){
							campaignAllocated = true;
						}
					}
					if(!campaignAllocated){//campaign j is not allocated
						System.out.println("Campaign #"+j+", is not allocated anything");
						System.out.println("\t*** " + this.valuationMatrix[i][j]);
						System.out.println("\t*** " + prices[i]);
						/* This if statement seems to have numerical issues...
						 * if(this.valuationMatrix[i][j] - prices[i] >= 0){//It makes sense to allocate this item.
						 */
						if(Math.abs(this.valuationMatrix[i][j] - prices[i]) < 0.0001){
							System.out.println("IT DOES MAKE SENSE!: " + "="+(this.valuationMatrix[i][j]) + " - " + prices[i]);
							matching[i][j] = 1;
						}
					}
				}
			}
		}
		System.out.println("Matching with possible more allocated items");
		Printer.printMatrix(matching);
		System.out.println("++++++End Deducing Matching+++++++++");
		Printer.printVector(prices);
		return new Matching(prices,matching);
	}
}
