package unitdemand;

/**
 * This class implements MaxEQ: Maximum Walrasian Prices, as stated in Guruswami et al.
 * 
 * @author Enrique Areyan Viqueira
 */
public class MaxWEQ {

  /**
   * valuation matrix. Provides a valuation v_ij of user i by campaign j.
   */
  double[][] valuationMatrix;

  /**
   * Constructor.
   * @param valuationMatrix - a matrix of valuations.
   */
  public MaxWEQ(double[][] valuationMatrix) {
    this.valuationMatrix = valuationMatrix;
  }
  
  /**
   * Computes the valuation matrix without row indexItem.
   * @param indexItem - index of item.
   * @return the valuation matrix without row indexItem.
   */
  protected double[][] valuationMatrixWithNoi(int indexItem) {
    double[][] newValuationMatrix = new double[this.valuationMatrix.length - 1][];
    int k = 0;
    for (int i = 0; i < this.valuationMatrix.length; i++) {
      if (i != indexItem) {
        newValuationMatrix[k] = this.valuationMatrix[i];
        k++;
      }
    }
    return newValuationMatrix;
  }
  
  /**
   * Implements MaxWEQ as stated in Guruswami et al.
   * @return a Matching object.
   */
  public Matching Solve() {
    double[] prices = new double[this.valuationMatrix.length];
    Matching matchingCompleteV = Matching.computeMaximumWeightMatchingValue(this.valuationMatrix);
    double maxWeightCompleteV = matchingCompleteV.getValueOfMatching();
    // System.out.println("w(V) = " + maxWeightCompleteV);
    for (int i = 0; i < this.valuationMatrix.length; i++) {
      // prices[i] =Math.round((maxWeightCompleteV -
      // Matching.computeMaximumWeightMatchingValue(this.valuationMatrixWithNoi(i)).getValueOfMatching())
      // * 100000.0) / 100000.0;
      prices[i] = maxWeightCompleteV - Matching.computeMaximumWeightMatchingValue(this.valuationMatrixWithNoi(i)).getValueOfMatching();
      /*
       * System.out.println("\t Matrix without row "+i);
       * Printer.printMatrix(this.valuationMatrixWithNoi(i));
       * System.out.println("w(V_i) = " +
       * this.computeMaximumWeightMatchingValue(
       * this.valuationMatrixWithNoi(i)).getValueOfMatching());
       * System.out.println("price["+i+"] = " + prices[i]);
       */
    }
    return new Matching(this.valuationMatrix, matchingCompleteV.getMatching(), prices);
  }
}
