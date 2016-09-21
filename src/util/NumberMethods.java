package util;

/**
 * Library with common methods for number manipulation.
 * 
 * @author Enrique Areyan Viqueira
 */
public class NumberMethods {

  /**
   * Method that rounded a vector of doubles.
   * 
   * @param prices - a vector of doubles.
   * @return a vector of doubles.
   */
  public static double[] roundPrices(double[] prices) {
    for (int i = 0; i < prices.length; i++) {
      prices[i] = Math.round(prices[i] * 100000.0) / 100000.0;
    }
    return prices;
  }

  /**
   * Method that computes the ratio between two doubles.
   * If both inputs are zero, this method returns zero.
   * 
   * @param v1 - double value
   * @param v2 - double value
   * @return the ratio between v1 and v2, or 0 if both are zero.
   */
  public static double getRatio(double v1, double v2) {
    if (v1 == 0.0 && v2 == 0.0) {
      return 1.0;
    } else {
      return v1 / v2;
    }
  }
  
}
