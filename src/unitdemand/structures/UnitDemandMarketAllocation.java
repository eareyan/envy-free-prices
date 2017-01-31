package unitdemand.structures;

import unitdemand.algorithms.MWBMatchingAlgorithm;
import util.NumberMethods;
import util.Printer;

/**
 * This class stores an allocation, defined here as a matrix of integers with a 1 at position (i,j) if good i is allocated to bidder j, and 0 otherwise.
 * 
 * @author Enrique Areyan Viqueira
 */
public class UnitDemandMarketAllocation {

  /**
   * valuation matrix. This is a matrix of doubles.
   */
  protected double[][] valuationMatrix;

  /**
   * Allocation. This is a matrix of integers.
   */
  protected int[][] allocation;

  /**
   * Value of the matrix. This is a double.
   */
  protected double valueOfAllocation = -1;

  /**
   * Constructor.
   * 
   * @param valuationMatrix - a matrix of valuations.
   * @param allocation - an integer matrix.
   * @param prices - a vector of prices.
   * @throws UnitDemandException
   */
  public UnitDemandMarketAllocation(double[][] valuationMatrix, int[][] allocation) throws UnitDemandException {
    if (valuationMatrix.length != allocation.length || valuationMatrix[0].length != allocation[0].length) {
      throw new UnitDemandException("The valuation matrix dimensions must agree with the allocation dimensions.");
    }
    this.valuationMatrix = valuationMatrix;
    this.allocation = allocation;
  }

  /**
   * Getter.
   * 
   * @return valuation matrix.
   */
  public double[][] getValuationMarix() {
    return this.valuationMatrix;
  }

  /**
   * Getter.
   * 
   * @return matrix of integers.
   */
  public int[][] getAllocation() {
    return this.allocation;
  }

  /**
   * Gets value of the allocation. Implements singleton.
   * 
   * @return value of the allocation.
   */
  public double getWelfare() {
    if (this.valueOfAllocation == -1) {
      double value = 0.0;
      for (int i = 0; i < this.valuationMatrix.length; i++) {
        for (int j = 0; j < this.valuationMatrix[0].length; j++) {
          if (this.allocation[i][j] == 1) {
            value += this.valuationMatrix[i][j];
          }
        }
      }
      this.valueOfAllocation = value;
    }
    return this.valueOfAllocation;
  }

  /**
   * Computes the allocation from good i.
   * 
   * @param i - good index.
   * @return the allocation from good i.
   */
  public int allocationFromGood(int i) {
    int totalAllocation = 0;
    for (int j = 0; j < this.valuationMatrix[0].length; j++) {
      totalAllocation += this.allocation[i][j];
    }
    return totalAllocation;
  }

  /**
   * Computes the maximum weight matching and its value for the argument matrix.
   * 
   * @param matrix - matrix of valuations.
   * @return the maximum weight matching and its value for the argument matrix.
   * @throws UnitDemandException
   */
  public static UnitDemandMarketAllocation computeMaximumWeightMatchingValue(double[][] matrix) throws UnitDemandException {
    int[] result = new MWBMatchingAlgorithm(matrix).getMatching();
    int[][] matching = new int[matrix.length][matrix[0].length];
    for (int i = 0; i < result.length; i++) {
      // If the assignment is possible and the good is actually connected to the bidder
      if (result[i] > -1 && matrix[i][result[i]] > Double.NEGATIVE_INFINITY) {
        matching[i][result[i]] = 1;
      }
    }
    return new UnitDemandMarketAllocation(matrix, matching);
  }

  /**
   * Computes the ratio of the welfare w.r.t a given value.
   * 
   * @param value
   * @return
   */
  public double getWelfareRatio(double value) {
    return NumberMethods.getRatio(this.getWelfare(), value);
  }

  /**
   * Prints the allocation.
   */
  public void printAllocation() {
    Printer.printMatrix(this.allocation);
  }

}
