package unitdemand.evpapprox;

import unitdemand.Matching;
import unitdemand.MaxWEQ;
import util.Printer;

/**
 * This abstract class implements the common methods for EVPApp algorithm.
 * Extending classes should implement augmentValuationMatrix, which defines
 * where and how many dummies to add at each iteration of the algorithm.
 * 
 * @author Enrique Areyan Viqueira
 */
abstract public class AbstractMaxWEQReservePrices {

  /**
   * valuation matrix. Provides a valuation v_ij of good i by bidder j.
   */
  protected double[][] valuationMatrix;
  
  /**
   * Reserve Prices.
   */
  protected double[] reservePrices;

  /**
   * Constructor.
   */
  public AbstractMaxWEQReservePrices() {
    
  }

  /**
   * Constructor. 
   * 
   * @param valuationMatrix - a matrix of valuations. 
   * @param reservePrices - a vector of reserve prices.
   */
  public AbstractMaxWEQReservePrices(double[][] valuationMatrix, double[] reservePrices) {
    this.valuationMatrix = valuationMatrix;
    this.reservePrices = reservePrices;
  }

  /**
   * Constructor.
   * 
   * @param valuationMatrix - a matrix of valuations.
   */
  public AbstractMaxWEQReservePrices(double[][] valuationMatrix) {
    this.valuationMatrix = valuationMatrix;
  }

  /**
   * The method solve runs MaxWEQ on the augmented valuation matrix and then
   * deduces a matching.
   * 
   * @param j - a bidder index.
   * @return a Matching object.
   */
  public Matching Solve(int j) {
    Matching M = new MaxWEQ(this.augmentValuationMatrix(j)).Solve();
    Matching deducedMatching = this.deduceMatching(M);
    return new Matching(this.valuationMatrix, deducedMatching.getMatching(),
        M.getPrices());
  }
  /**
   * Set reserve prices.
   *  
   * @param reservePrices - a vector of reserve prices.
   */
  public void setReservePrices(double[] reservePrices) {
    this.reservePrices = reservePrices;
  }
  
  /**
   * Set valuation matrix. 
   * @param valuationMatrix - a matrix of valuations.
   */
  public void setValuationMatrix(double[][] valuationMatrix) {
    this.valuationMatrix = valuationMatrix;
  }
  
  /**
   * This method is to be extended by an implemented class. The idea is that a
   * valuation matrix can be extended with dummies in many different ways.
   * 
   * @param j - a bidder index.
   * @return a matrix of valuations.
   */
  abstract public double[][] augmentValuationMatrix(int j);

  /**
   * From a given matching, remove all dummy consumers and their edges. While
   * there is an unsold item i in the demand set of a real consumer j that is
   * not allocated, allocate item i to consumer j.
   * 
   * @param inputMatching a Matching object.
   * @return a Matching object.
   */
  public Matching deduceMatching(Matching inputMatching) {
    int[][] matchingWithDummy = inputMatching.getMatching();
    double[] prices = inputMatching.getPrices();
    int[][] matching = new int[this.valuationMatrix.length][this.valuationMatrix[0].length];
    for (int i = 0; i < this.valuationMatrix.length; i++) {
      for (int j = 0; j < this.valuationMatrix[0].length; j++) {
        matching[i][j] = matchingWithDummy[i][j];
      }
    }
    // System.out.println("++++++Start Deducing Matching+++++++++");
    System.out.println("Matching with no dummies");
    Printer.printMatrix(matching);
    /*
     * while there is an unsold item in the demand set of a real consumer that
     * is not allocated an item, allocate it
     */
    mainloop: for (int i = 0; i < this.valuationMatrix.length; i++) {
      boolean itemAllocated = false;
      for (int j = 0; j < this.valuationMatrix[0].length; j++) {
        if (matching[i][j] == 1) {
          itemAllocated = true;
        }
      }
      if (!itemAllocated) { // item i is not allocated
        // System.out.println("item #"+i+", is NOT allocated.. let us try to allocated it");
        for (int j = 0; j < this.valuationMatrix[0].length; j++) {
          boolean bidderAllocated = false;
          for (int i1 = 0; i1 < this.valuationMatrix.length; i1++) {
            if (matching[i1][j] == 1) {
              bidderAllocated = true;
            }
          }
          if (!bidderAllocated) {// bidder j is not allocated
            // System.out.println("Bidder #"+j+", is not allocated anything");
            // System.out.println("\t*** " + this.valuationMatrix[i][j]);
            // System.out.println("\t*** " + prices[i]);
            /*
             * This if statement seems to have numerical issues...
             * if(this.valuationMatrix[i][j] - prices[i] >= 0){//It makes sense
             * to allocate this item.
             */
            if (Math.abs(this.valuationMatrix[i][j] - prices[i]) <= 0.0001) { // NUmerical
                                                                              // Issues
              // if(this.valuationMatrix[i][j] >= prices[i]){
              // System.out.println("IT DOES MAKE SENSE!: " +
              // "="+(this.valuationMatrix[i][j]) + " - " + prices[i]);
              matching[i][j] = 1;
              continue mainloop;
            }
          }
        }
      }
    }
    // System.out.println("Matching with possible more allocated items");
    // Printer.printMatrix(matching);
    // System.out.println("++++++End Deducing Matching+++++++++");
    // Printer.printVector(prices);
    return new Matching(inputMatching.getValuationMarix(), matching, prices);
  }
  
}
