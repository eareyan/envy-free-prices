package structures.factory;


/**
 * This class implements methods to create unit-demand markets.
 * 
 * @author Enrique Areyan Viqueira
 */
public class UnitDemandMarketFactory {
  
  /**
   * Produces a random valuation matrix V_ij, where valuations are within the default range.
   * 
   * @param n - number of goods.
   * @param m - number of bidders.
   * @param prob - probability of a connection.
   * @return a matrix of valuations.
   */
  public static double[][] getValuationMatrix(int n, int m, double prob) {
    double[][] valuationMatrix = new double[n][m];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        valuationMatrix[i][j] = (Math.random() <= prob) ? (Math.random() * (Parameters.defaultMaxReward - Parameters.defaultMinReward) + Parameters.defaultMinReward) : Double.NEGATIVE_INFINITY;
      }
    }
    return valuationMatrix;
  }

  /**
   * Given a valuation matrix V_ij, returns a valuation V_ij' that respects
   * reserve price r. Respect in this context means that any valuation below r
   * becomes -infinity and all others are decreased by r. You can think of this
   * operation as "shifting" the matrix by r.
   * 
   * @param X - a valuation matrix.
   * @param r - a reserve price.
   * @return a valuation matrix.
   */
  public static double[][] getValuationReserve(double[][] X, double r) {
    double[][] XReserve = new double[X.length][X[0].length];
    for (int i = 0; i < X.length; i++) {
      for (int j = 0; j < X[0].length; j++) {
        XReserve[i][j] = (X[i][j] - r <= 0) ? Double.NEGATIVE_INFINITY : X[i][j] - r;
      }
    }
    return XReserve;
  }

}
