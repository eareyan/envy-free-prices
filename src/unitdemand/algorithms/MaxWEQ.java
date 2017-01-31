package unitdemand.algorithms;

import unitdemand.structures.UnitDemandMarketAllocation;
import unitdemand.structures.UnitDemandException;
import unitdemand.structures.UnitDemandMarketOutcome;

/**
 * This class implements MaxWEQ: Maximum Walrasian Prices, as stated in Guruswami et al.
 * 
 * @author Enrique Areyan Viqueira
 */
public class MaxWEQ {

  /**
   * Valuation matrix. Provides a valuation v_ij of good i by bidder j.
   */
  private final double[][] valuationMatrix;

  /**
   * Constructor.
   * 
   * @param valuationMatrix - a matrix of valuations.
   */
  public MaxWEQ(double[][] valuationMatrix) {
    this.valuationMatrix = valuationMatrix;
  }

  /**
   * Computes the valuation matrix without row indexItem.
   * 
   * @param indexItem - index of item.
   * @return the valuation matrix without row indexItem.
   */
  public double[][] valuationMatrixWithNoi(int indexItem) {
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
   * 
   * @return a Matching object.
   * @throws UnitDemandException 
   */
  public UnitDemandMarketOutcome Solve() throws UnitDemandException {
    double[] prices = new double[this.valuationMatrix.length];
    UnitDemandMarketAllocation matchingCompleteV = UnitDemandMarketAllocation.computeMaximumWeightMatchingValue(this.valuationMatrix);
    double maxWeightCompleteV = matchingCompleteV.getWelfare();
    for (int i = 0; i < this.valuationMatrix.length; i++) {
      prices[i] = maxWeightCompleteV - UnitDemandMarketAllocation.computeMaximumWeightMatchingValue(this.valuationMatrixWithNoi(i)).getWelfare();
    }
    return new UnitDemandMarketOutcome(new UnitDemandMarketAllocation(this.valuationMatrix, matchingCompleteV.getAllocation()), prices);
  }

}
