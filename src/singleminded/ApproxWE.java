package singleminded;

import util.Printer;

/*
 * This class implements the approximation WE algorithm for single-minded bidders
 * as presented in Huang, L.S., Li, M., Zhang, B.: Approximation of walrasian equilibrium in single-
 * minded auctions. Theoretical computer science 337(1), 390–398 (2005)
 */

public class ApproxWE {

	public void Solve(){
		
		boolean[][] A = new boolean[][]{{true,	true,	true,	true},
										{true,	true,	true,	true},
										{true,	true,	true,	true}
										};
		int numberOfBidders = A[0].length;
		int numberOfItems = A.length;
		double[] R = new double[]{101.0, 100.0, 20.0, 10.0};
		
		boolean[] X = new boolean[numberOfBidders];
		double[] p = new double[numberOfItems];
		
		while(!matrixAllFalse(A)){
			/*
			 * Find the commodity which attracts most bidders.
			 */
			int mostPopularItem = -1, popularityOfItem = -1;
			for(int i = 0; i < numberOfItems; i++){
				int acum = 0;
				for(int j = 0; j < numberOfBidders; j++){
					acum += A[i][j] ?  1 : 0;
				}
				if(acum >= popularityOfItem){
					popularityOfItem = acum;
					mostPopularItem = i;
				}
			}
			/*
			 * Find the bidder with highest budget that wants the most popular item 
			 */
			int winner = -1;
			for(int j = 0; j < numberOfBidders; j++){
				if(A[mostPopularItem][j]){
					winner = j;
					break;
				}
			}
			/*
			 * Assign prices and bundle
			 */
			p[mostPopularItem] = R[winner];
			X[winner] = true;
			Printer.printMatrix(A);
			System.out.println("The most popular item is " + mostPopularItem + ", assigned it to " + winner + " at price " + p[mostPopularItem]);
			/*
			 * Remove any conflicts
			 */
			for(int i = 0; i < numberOfItems; i++){
				if(A[i][winner]){
					for(int j = 0 ; j < numberOfBidders; j++){
						if(j != winner && A[i][j]){
							for(int iPrime = 0; iPrime < numberOfItems; iPrime ++){
								A[iPrime][j] = false;
							}
						}
					}
				}
			}
			//Remove winner
			for(int i = 0; i < numberOfItems; i++){
				A[i][winner] = false;
			}
			Printer.printMatrix(A);
			Printer.printVector(p);
			Printer.printVector(X);
		}
	}
	/*
	 * This function returns true if all the
	 * entries of a 2x2 matrix are false.
	 * Otherwise, returns false.
	 */
	protected boolean matrixAllFalse(boolean[][] X){
		boolean result = true;
		
		for(int i = 0; i < X.length; i++){
			for(int j = 0; j < X[0].length; j++){
				if(X[i][j]) return false;
			}
		}
		return result;
	}
}
