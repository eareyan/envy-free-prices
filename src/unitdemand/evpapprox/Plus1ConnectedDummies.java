package unitdemand.evpapprox;

import util.Printer;

/**
 * Adds dummy only to connected goods plus one more level.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Plus1ConnectedDummies extends AbstractMaxWEQReservePrices {

  /**
   * Constructor.
   * @param valuationMatrix - a matrix of valuations.
   * @param reservePrices - a vector of reserve prices.
   */
  public Plus1ConnectedDummies(double[][] valuationMatrix, double[] reservePrices) {
    super(valuationMatrix, reservePrices);
  }

  /**
   * Constructor.
   * @param valuationMatrix - a matrix of valuations.
   */
  public Plus1ConnectedDummies(double[][] valuationMatrix) {
    super(valuationMatrix);
  }

  /**
   * Constructor.
   */
  public Plus1ConnectedDummies() {
    super();
  }
  
  /**
   * This method adds two dummy consumers that value item i at reserve price
   * only in case (i,j)\in E and all other items at 0. The method adds columns
   * to account for these dummy consumers.
   * 
   * @param j - campaign index.
   * @return valuation matrix.
   */
  public double[][] augmentValuationMatrix(int j) {
    System.out.println("------Dummies connected plus 1 items");
    int newNumberOfCols = (this.valuationMatrix.length) * 2
        + this.valuationMatrix[0].length;
    double[][] augmentedValMatrix = new double[this.valuationMatrix.length][newNumberOfCols];
    for (int i = 0; i < this.valuationMatrix.length; i++) {
      /* Create dummy reserve demand */
      double[] dummyReserveRow = new double[(this.valuationMatrix.length) * 2];
      if (includeGood(i, j)) {
        dummyReserveRow[i * 2] = this.reservePrices[i];
        dummyReserveRow[(i * 2) + 1] = this.reservePrices[i];
      }
      /* copy original row */
      double[] originalrow = new double[this.valuationMatrix[0].length];
      System.arraycopy(this.valuationMatrix[i], 0, originalrow, 0,
          this.valuationMatrix[i].length);
      double[] finalrow = new double[newNumberOfCols];
      /* concatenate original and dummy rows together */
      System.arraycopy(originalrow, 0, finalrow, 0, originalrow.length);
      System.arraycopy(dummyReserveRow, 0, finalrow, originalrow.length,
          dummyReserveRow.length);
      /* add final row to the augmented matrix */
      augmentedValMatrix[i] = finalrow;
    }
    System.out.println("Final Augmented Matrix");
    Printer.printMatrix(augmentedValMatrix);
    return augmentedValMatrix;
  }

  /**
   * Computes whether or not to include good i.
   * 
   * @param goodIndex - good index.
   * @param bidderIndex - bidder index.
   * @return true if goodIndex should be included
   */
  public boolean includeGood(int goodIndex, int bidderIndex) {

    for (int j = 0; j < this.valuationMatrix[0].length; j++) {
      if (this.valuationMatrix[goodIndex][j] > Double.NEGATIVE_INFINITY) {
        for (int i = 0; i < this.valuationMatrix.length; i++) {
          if (bidderIndex > -1) {
            if (this.valuationMatrix[i][j] > Double.NEGATIVE_INFINITY
                && this.valuationMatrix[i][bidderIndex] > Double.NEGATIVE_INFINITY) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }
  
}
