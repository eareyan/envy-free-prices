package test;

import util.Printer;

/**
 * This class has a library of Unit-Demand Markets. Each market is the output of
 * a static function. These are mainly used for testing purposes. In this case,
 * a market is fully characterized by a valuation matrix.
 * 
 * @author Enrique Areyan Viqueira
 */
public class UnitDemandMarkets {
  
  public static void UnitDemandMarket1() {
    /*
     * Example where All Connected EVPApp performs better than plus 1 EVPApp in
     * NON-Uniform, unit demand case
     */
    double[][] valuationMatrix = new double[3][3];

    valuationMatrix[0][0] = 29.60;
    valuationMatrix[0][1] = 18.66;
    valuationMatrix[0][2] = Double.NEGATIVE_INFINITY;

    valuationMatrix[1][0] = Double.NEGATIVE_INFINITY;
    valuationMatrix[1][1] = Double.NEGATIVE_INFINITY;
    valuationMatrix[1][2] = 27.10;

    valuationMatrix[2][0] = Double.NEGATIVE_INFINITY;
    valuationMatrix[2][1] = 58.66;
    valuationMatrix[2][2] = 97.06;

    Printer.printMatrix(valuationMatrix);
    /*
     * Example where Plus 1 EVPApp performs better than All Connected EVPApp in
     * NON-Uniform, unit demand case
     */
    valuationMatrix = new double[3][2];
    valuationMatrix[0][0] = 19.47;
    valuationMatrix[0][1] = Double.NEGATIVE_INFINITY;

    valuationMatrix[1][0] = Double.NEGATIVE_INFINITY;
    valuationMatrix[1][1] = 41.11;

    valuationMatrix[2][0] = Double.NEGATIVE_INFINITY;
    valuationMatrix[2][1] = 73.16;

    /*
     * Example where LP with all connected, simple reserve price Rj/Ij does
     * better than regular EVP (All-connected)
     */
    valuationMatrix = new double[3][2];
    valuationMatrix[0][0] = 39.92;
    valuationMatrix[0][1] = Double.NEGATIVE_INFINITY;

    valuationMatrix[1][0] = Double.NEGATIVE_INFINITY;
    valuationMatrix[1][1] = 43.51;

    valuationMatrix[2][0] = Double.NEGATIVE_INFINITY;
    valuationMatrix[2][1] = 43.51;

    /*
     * Example to test LP in the uniform unit-demand case with and without
     * walrasian conditions
     */
    valuationMatrix = new double[3][2];
    valuationMatrix[0][0] = Double.NEGATIVE_INFINITY;
    valuationMatrix[0][1] = 62;

    valuationMatrix[1][0] = 96;
    valuationMatrix[1][1] = 62;

    valuationMatrix[2][0] = Double.NEGATIVE_INFINITY;
    valuationMatrix[2][1] = 62;
  }

}
