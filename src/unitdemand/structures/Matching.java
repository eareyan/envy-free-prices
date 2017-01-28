package unitdemand.structures;

import unitdemand.algorithms.MWBMatchingAlgorithm;
import util.NumberMethods;
import util.Printer;

/**
 * This class stores a matching, defined here as a matrix of integers with a 1 at position (i,j) if good i is allocated to bidder j, and 0 otherwise. It also
 * stores the prices of a matching and implements method to get the value of a matching.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Matching {

  /**
   * valuation matrix. This is a matrix of doubles.
   */
  protected double[][] valuationMatrix;

  /**
   * vector of prices. This is an array of doubles.
   */
  protected double[] prices;

  /**
   * Matching. This is a matrix of integers.
   */
  protected int[][] matching;

  /**
   * Value of the matrix. This is a double.
   */
  protected double valueOfMatching = -1;

  /**
   * Seller revenue, a double.
   */
  protected double sellerRevenue = -1;
  
  /**
   * Time
   */
  private long time = -1;

  /**
   * Epsilon parameter. Used when computing number of envy bidders.
   */
  protected static double epsilon = 0.00001;

  /**
   * Constructor.
   * 
   * @param valuationMatrix - a matrix of valuations.
   * @param matching - an integer matrix.
   * @param prices - a vector of prices.
   */
  public Matching(double[][] valuationMatrix, int[][] matching, double[] prices) {
    this.valuationMatrix = valuationMatrix;
    this.matching = matching;
    this.prices = prices;
  }
  
  /**
   * Constructor.
   * 
   * @param valuationMatrix - a matrix of valuations.
   * @param matching - an integer matrix.
   * @param prices - a vector of prices.
   */
  public Matching(double[][] valuationMatrix, int[][] matching, double[] prices, long time) {
    this.valuationMatrix = valuationMatrix;
    this.matching = matching;
    this.prices = prices;
    this.time = time;
  }

  /**
   * Constructor.
   * 
   * @param valuationMatrix - a matrix of valuations.
   * @param matching - an integer matrix.
   */
  public Matching(double[][] valuationMatrix, int[][] matching) {
    this.valuationMatrix = valuationMatrix;
    this.matching = matching;
  }

  /**
   * Getter.
   * 
   * @return array of prices.
   */
  public double[] getPrices() {
    return this.prices;
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
   * Gets value of matching. Implements singleton.
   * 
   * @return value of matching.
   */
  public double getWelfare() {
    if (this.valueOfMatching == -1) {
      double value = 0.0;
      for (int i = 0; i < this.valuationMatrix.length; i++) {
        for (int j = 0; j < this.valuationMatrix[0].length; j++) {
          if (this.matching[i][j] == 1) {
            value += this.valuationMatrix[i][j];
          }
        }
      }
      this.valueOfMatching = value;
    }
    return this.valueOfMatching;
  }

  /**
   * Getter.
   * 
   * @return matrix of integers.
   */
  public int[][] getMatching() {
    return this.matching;
  }

  /**
   * Getter. Implements singleton.
   * 
   * @return seller revenue under this matching.
   */
  public double getSellerRevenue() {
    if (this.sellerRevenue == -1) {
      if (this.matching == null) {
        return 0.0;
      } else {
        double revenue = 0.0;
        for (int i = 0; i < this.matching.length; i++) {
          for (int j = 0; j < this.matching[0].length; j++) {
            if (this.matching[i][j] == 1) {
              revenue += prices[i];
            }
          }
        }
        this.sellerRevenue = revenue;
      }
    }
    return this.sellerRevenue;
  }

  /**
   * Computes the utility of bidder j under this matching.
   * 
   * @param j - bidder index.
   * @return utility of bidder j under this matching.
   */
  public double getBidderUtility(int j) {
    for (int i = 0; i < this.valuationMatrix.length; i++) {
      if (this.matching[i][j] == 1) {
        return this.valuationMatrix[i][j] - this.prices[i];
      }
    }
    return 0.0;
  }

  /**
   * Compute number of envy-bidders.
   * 
   * @return number of envy-bidders.
   */
  public int numberOfEnvyBidders() {
    int totalNumberEnvy = 0;
    for (int j = 0; j < this.valuationMatrix[0].length; j++) {
      double bidderUtility = this.getBidderUtility(j);
      for (int i = 0; i < this.valuationMatrix.length; i++) {
        if ((this.valuationMatrix[i][j] - this.prices[i]) - bidderUtility > Matching.epsilon) {
          totalNumberEnvy++;
        }
      }
    }
    return totalNumberEnvy;
  }

  /**
   * Compute the number of goods that are not allocated and not priced at zero.
   * 
   * @return the number of goods that are not allocated and not priced at zero.
   */
  public int computeMarketClearanceViolations() {
    int violations = 0;
    for (int i = 0; i < this.valuationMatrix.length; i++) {
      if (this.allocationFromGood(i) == 0 && this.prices[i] > 0) {
        violations++;
      }
    }
    return violations;
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
      totalAllocation += this.matching[i][j];
    }
    return totalAllocation;
  }

  /**
   * Computes the maximum weight matching and its value for the argument matrix.
   * 
   * @param matrix - matrix of valuations.
   * @return the maximum weight matching and its value for the argument matrix.
   */
  public static Matching computeMaximumWeightMatchingValue(double[][] matrix) {
    int[] result = new MWBMatchingAlgorithm(matrix).getMatching();
    int[][] matching = new int[matrix.length][matrix[0].length];
    for (int i = 0; i < result.length; i++) {
      // If the assignment is possible and the good is actually connected to the bidder
      if (result[i] > -1 && matrix[i][result[i]] > Double.NEGATIVE_INFINITY) {
        matching[i][result[i]] = 1;
      }
    }
    return new Matching(matrix, matching);
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
   * Computes the ratio of the seller revenue w.r.t a given value.
   * 
   * @param value
   * @return
   */
  public double getSellerRevenueRatio(double value) {
    return NumberMethods.getRatio(this.getSellerRevenue(), value);
  }
  
  /**
   * Computes the ratio of EF violations to the total number of bidders.
   * 
   * @return
   */
  public double getEFViolationsRatio() {
    return (double) this.numberOfEnvyBidders() / this.valuationMatrix[0].length;
  }
  
  /**
   * Computes the ratio of MC violations to the total number of goods.
   * 
   * @return
   */
  public double getMCViolationsRatio() {
    return (double) this.computeMarketClearanceViolations() / this.valuationMatrix.length;
  }
  
  /**
   * Returns the time.
   * 
   * @return
   */
  public double getTime() {
    return this.time;
  }

  @Override
  public String toString() {
    Printer.printMatrix(this.matching);
    System.out.println("");
    Printer.printVector(this.prices);
    return "";
  }
}
