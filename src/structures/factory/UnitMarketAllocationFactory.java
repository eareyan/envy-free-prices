package structures.factory;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import unitdemand.MWBMatchingAlgorithm;
import unitdemand.Matching;

/**
 * Factory to work with allocated markets.
 * 
 * @author Enrique Areyan Viqueira
 */
public class UnitMarketAllocationFactory {

  /**
   * Given a market, return a cost matrix to be input to the Hungarian
   * algorithm. This matrix encodes the fact that a bidder is not connected to
   * a good by assigning an infinite cost. For a connected bidder, we add a
   * random valuation between 1 and 100 for each edge.
   * 
   * @param market - a market object. 
   * @return a matrix of valuations.
   */
  public static double[][] getValuationMatrixFromMarket(Market<Goods, Bidder<Goods>> market) {
    double[][] costMatrix = new double[market.getNumberGoods()][market.getNumberBidders()];
    for (int i = 0; i < market.getNumberGoods(); i++) {
      for (int j = 0; j < market.getNumberBidders(); j++) {
        if (market.getBidders().get(j).demandsGood(market.getGoods().get(i))) {
          costMatrix[i][j] = market.getBidders().get(j).getReward();
        } else {
          costMatrix[i][j] = Double.NEGATIVE_INFINITY;
        }
      }
    }
    return costMatrix;
  }
  
  /**
   * Given a cost matrix, run the Hungarian algorithm and return the max weight matching.
   * @param valuationMatrix - a matrix of valuations.
   * @return an integer matrix corresponding to the allocation of a maximum weight matching.
   */
  public static int[][] getMaximumMatchingFromValuationMatrix(double[][] valuationMatrix) {
    int[] result = new MWBMatchingAlgorithm(valuationMatrix).getMatching();
    // Initialize allocation as all disconnected.
    int[][] allocationMatrix = new int[valuationMatrix.length][valuationMatrix[0].length];
    for (int i = 0; i < result.length; i++) {
      // If the assignment is possible and the good is actually connected to the bidder
      if (result[i] > -1 && valuationMatrix[i][result[i]] > Double.NEGATIVE_INFINITY) {
        allocationMatrix[i][result[i]] = 1;
      }
    }
    return allocationMatrix;
  }

  /**
   * Given a unit-demand market, return the MarketAllocation object
   * corresponding to the maximum weight matching
   * 
   * @param market - a market object. 
   * @return a MarketAllocation object.
   
  public static MarketAllocation<Goods, Bidder<Goods>> getMaxWeightMatchingAllocation(Market<Goods, Bidder<Goods>> market) {
    return new MarketAllocation<Goods, Bidder<Goods>>(market, UnitMarketAllocationFactory.getMaximumMatchingFromValuationMatrix(UnitMarketAllocationFactory.getValuationMatrixFromMarket(market)));
  }
  
  /**
   * This method is for the unit demand case only. This method first computes a
   * new valuation matrix by subtracting the reserve values and setting negative
   * values to negative infinity. Then, get an allocation for the new valuation
   * matrix by solving for the MWM with new valuation matrix
   * 
   * @param valuationMatrix - a matrix of valuations.
   * @param reserve - a reserve price.
   * @return an integer matrix corresponding to the allocation of a maximum weight matching.
   */
  public static int[][] getAllocationWithReservePrices(double[][] valuationMatrix, double reserve) {
    double[][] valuationMatrixWithReserve = new double[valuationMatrix.length][valuationMatrix[0].length];
    for (int i = 0; i < valuationMatrix.length; i++) {
      for (int j = 0; j < valuationMatrix[0].length; j++) {
        valuationMatrixWithReserve[i][j] = (valuationMatrix[i][j] - reserve < 0) ? Double.NEGATIVE_INFINITY : (valuationMatrix[i][j] - reserve);
      }
    }
    // Printer.printMatrix(valuationMatrixWithReserve);
    return Matching.computeMaximumWeightMatchingValue(valuationMatrixWithReserve).getMatching();
  }

}
