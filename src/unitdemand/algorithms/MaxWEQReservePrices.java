package unitdemand.algorithms;

import unitdemand.structures.UnitDemandException;
import unitdemand.structures.UnitDemandMarketAllocation;
import unitdemand.structures.UnitDemandMarketOutcome;
import algorithms.pricing.error.PrincingAlgoException;

/**
 * This class implements MaxWEQ with reserve prices as given by Guruswami et.al. On profit-maximizing envy-free pricing.
 * 
 * @author Enrique Areyan Viqueira
 */
public class MaxWEQReservePrices {

  /**
   * valuation matrix. Provides a valuation v_ij of good i by bidder j.
   */
  private final double[][] valuationMatrix;

  /**
   * Reserve Prices.
   */
  private final double[] reservePrices;

  /**
   * Constructor.
   * 
   * @param valuationMatrix - a matrix of valuations.
   * @param reservePrices - a vector of reserve prices.
   * @throws PrincingAlgoException
   */
  public MaxWEQReservePrices(double[][] valuationMatrix, double[] reservePrices) throws PrincingAlgoException {
    if (reservePrices.length != valuationMatrix.length) {
      throw new PrincingAlgoException("The reserve price vector must be of length equal to the number of items.");
    }
    this.valuationMatrix = valuationMatrix;
    this.reservePrices = reservePrices;
  }

  /**
   * The method solve runs MaxWEQ on the augmented valuation matrix and then deduces a matching.
   * 
   * @return a Matching object.
   * @throws UnitDemandException 
   */
  public UnitDemandMarketOutcome Solve() throws UnitDemandException {
    UnitDemandMarketOutcome M = new MaxWEQ(this.augmentValuationMatrix()).Solve();
    return this.deduceMatching(M);
  }

  /**
   * This method adds two dummy consumers to all items at the given reserve price. The method adds columns to account for these dummy consumers.
   * 
   * @return a matrix of valuations.
   */
  public double[][] augmentValuationMatrix() {
    int newNumberOfCols = (this.valuationMatrix.length) * 2 + this.valuationMatrix[0].length;
    double[][] augmentedValMatrix = new double[this.valuationMatrix.length][newNumberOfCols];
    for (int i = 0; i < this.valuationMatrix.length; i++) {
      // Create dummy reserve demand
      double[] dummyReserveRow = new double[(this.valuationMatrix.length) * 2];
      dummyReserveRow[i * 2] = this.reservePrices[i];
      dummyReserveRow[(i * 2) + 1] = this.reservePrices[i];
      // copy original row
      double[] originalrow = new double[this.valuationMatrix[0].length];
      System.arraycopy(this.valuationMatrix[i], 0, originalrow, 0, this.valuationMatrix[i].length);
      double[] finalrow = new double[newNumberOfCols];
      // concatenate original and dummy rows together
      System.arraycopy(originalrow, 0, finalrow, 0, originalrow.length);
      System.arraycopy(dummyReserveRow, 0, finalrow, originalrow.length, dummyReserveRow.length);
      // add final row to the augmented matrix
      augmentedValMatrix[i] = finalrow;
    }
    return augmentedValMatrix;
  }

  /**
   * From a given matching, remove all dummy consumers and their edges. While there is an unsold item i in the demand set of a real consumer j that is not
   * allocated, allocate item i to consumer j.
   * 
   * @param inputMatching a Matching object.
   * @return a Matching object.
   * @throws UnitDemandException 
   */
  public UnitDemandMarketOutcome deduceMatching(UnitDemandMarketOutcome inputMatching) throws UnitDemandException {
    int[][] matchingWithDummy = inputMatching.getMarketAllocation().getAllocation();
    double[] prices = inputMatching.getPrices();
    int[][] matching = new int[this.valuationMatrix.length][this.valuationMatrix[0].length];
    for (int i = 0; i < this.valuationMatrix.length; i++) {
      for (int j = 0; j < this.valuationMatrix[0].length; j++) {
        matching[i][j] = matchingWithDummy[i][j];
      }
    }
    // System.out.println("++++++Start Deducing Matching+++++++++");
    // System.out.println("Matching with dummies");
    // Printer.printMatrix(matchingWithDummy);
    // System.out.println("Matching with no dummies");
    // Printer.printMatrix(matching);

    // While there is an unsold item in the demand set of a real consumer that is not allocated an item, allocate it
    mainloop: for (int i = 0; i < this.valuationMatrix.length; i++) {
      boolean itemAllocated = false;
      for (int j = 0; j < this.valuationMatrix[0].length && !itemAllocated; j++) {
        if (matching[i][j] == 1) {
          itemAllocated = true;
        }
      }
      // item i is not allocated
      if (!itemAllocated) {
        // System.out.println("item #" + i + ", is NOT allocated.. let us try to allocated it");
        for (int j = 0; j < this.valuationMatrix[0].length; j++) {
          boolean bidderAllocated = false;
          for (int i1 = 0; i1 < this.valuationMatrix.length; i1++) {
            if (matching[i1][j] == 1) {
              bidderAllocated = true;
            }
          }
          // bidder j is not allocated
          if (!bidderAllocated) {
            // System.out.println("Bidder #" + j + ", is not allocated anything, allocating " + i + ", yields " + (this.valuationMatrix[i][j] - prices[i]) +
            // " utility");
            // This if statement seems to have numerical issues...
            if (Math.abs(this.valuationMatrix[i][j] - prices[i]) <= 0.0001) {
              matching[i][j] = 1;
              continue mainloop;
            }
          }
        }
      }
    }
    return new UnitDemandMarketOutcome(new UnitDemandMarketAllocation(this.valuationMatrix, matching), prices);
  }

}
