package algorithms.waterfall;

import structures.MarketAllocation;
import structures.MarketPrices;

/**
 * This class implements operations on waterfall prices.
 * 
 * @author Enrique Areyan Viqueira
 */
public class WaterfallPrices extends MarketPrices {

  /**
   * A matrix of prices.
   */
  protected double[][] pricesMatrix;

  /**
   * Constructor.
   * @param marketAllocation - a MarketAllocation object.
   * @param pricesMatrix - a matrix of prices.
   */
  public WaterfallPrices(MarketAllocation marketAllocation, double[][] pricesMatrix) {
    this.marketAllocation = marketAllocation;
    this.pricesMatrix = pricesMatrix;
  }
  
  /**
   * This method computes the sum of the errors on the compact condition
   * @return
   */
  public double computeViolations() {
    double error = 0.0;
    // For each i,j
    for (int i = 0; i < this.marketAllocation.getMarket().getNumberUsers(); i++) {
      for (int j = 0; j < this.marketAllocation.getMarket().getNumberCampaigns(); j++) {
        // If something is allocated
        if (this.marketAllocation.getAllocation()[i][j] > 0) {
          // For all users k, check if k is connected to i and k is not exhausted by j.
          for (int k = 0; k < this.marketAllocation.getMarket().getNumberUsers(); k++) {
            if (this.marketAllocation.getMarket().isConnected(k, j)
                && this.marketAllocation.getAllocation()[k][j] < this.getMarketAllocation().getMarket().getUser(k).getSupply()) {
              // Finally, check if there is a violation in the prices.
              if (this.pricesMatrix[i][j] > this.pricesMatrix[k][j]) {
                error += this.pricesMatrix[i][j] - this.pricesMatrix[k][j];
              }
            }
          }
        }
      }
    }
    return error;
  }
  
}
