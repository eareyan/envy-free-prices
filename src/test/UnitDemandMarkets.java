package test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;

import unitdemand.algorithms.MWBMatchingAlgorithm;
import unitdemand.structures.Link;
import util.Printer;

/**
 * This class has a library of Unit-Demand Markets. Each market is the output of a static function. These are mainly used for testing purposes. In this case, a
 * market is fully characterized by a valuation matrix.
 * 
 * @author Enrique Areyan Viqueira
 */
public class UnitDemandMarkets {

  public static double[][] UnitDemandMarket0() {
    /*
     * Example where All Connected EVPApp performs better than plus 1 EVPApp in NON-Uniform, unit demand case
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

    return valuationMatrix;
  }

  public static double[][] UnitDemandMarket1() {
    /*
     * Example where Plus 1 EVPApp performs better than All Connected EVPApp in NON-Uniform, unit demand case
     */
    double[][] valuationMatrix = new double[3][2];

    valuationMatrix[0][0] = 19.47;
    valuationMatrix[0][1] = Double.NEGATIVE_INFINITY;

    valuationMatrix[1][0] = Double.NEGATIVE_INFINITY;
    valuationMatrix[1][1] = 41.11;

    valuationMatrix[2][0] = Double.NEGATIVE_INFINITY;
    valuationMatrix[2][1] = 73.16;

    return valuationMatrix;
  }

  public static double[][] UnitDemandMarket2() {
    /*
     * Example where LP with all connected, simple reserve price Rj/Ij does better than regular EVP (All-connected)
     */
    double[][] valuationMatrix = new double[3][2];

    valuationMatrix[0][0] = 39.92;
    valuationMatrix[0][1] = Double.NEGATIVE_INFINITY;

    valuationMatrix[1][0] = Double.NEGATIVE_INFINITY;
    valuationMatrix[1][1] = 43.51;

    valuationMatrix[2][0] = Double.NEGATIVE_INFINITY;
    valuationMatrix[2][1] = 43.51;

    return valuationMatrix;
  }

  public static double[][] UnitDemandMarket3() {
    /*
     * Example to test LP in the uniform unit-demand case with and without walrasian conditions
     */
    double[][] valuationMatrix = new double[3][2];

    valuationMatrix[0][0] = Double.NEGATIVE_INFINITY;
    valuationMatrix[0][1] = 62;

    valuationMatrix[1][0] = 96;
    valuationMatrix[1][1] = 62;

    valuationMatrix[2][0] = Double.NEGATIVE_INFINITY;
    valuationMatrix[2][1] = 62;

    return valuationMatrix;
  }

  @Test
  public void TestMWB() {
    double[][] valuationMatrix = UnitDemandMarkets.UnitDemandMarket0();
    int[] result = new MWBMatchingAlgorithm(valuationMatrix).getMatching();
    int[] trueResult = new int[] { 0, -1, 2 };
    for (int i = 0; i < result.length; i++) {
      if (result[i] != trueResult[i]) {
        fail("Assignment is not a Maximum Weight Matching");
      }
    }

    valuationMatrix = UnitDemandMarkets.UnitDemandMarket1();
    result = new MWBMatchingAlgorithm(valuationMatrix).getMatching();
    trueResult = new int[] { 0, -1, 1 };
    for (int i = 0; i < result.length; i++) {
      if (result[i] != trueResult[i]) {
        fail("Assignment is not a Maximum Weight Matching");
      }
    }
    
    valuationMatrix = UnitDemandMarkets.UnitDemandMarket2();
    result = new MWBMatchingAlgorithm(valuationMatrix).getMatching();
    trueResult = new int[] { 0, -1, 1 };
    for (int i = 0; i < result.length; i++) {
      if (result[i] != trueResult[i]) {
        fail("Assignment is not a Maximum Weight Matching");
      }
    }
    
    valuationMatrix = UnitDemandMarkets.UnitDemandMarket3();
    result = new MWBMatchingAlgorithm(valuationMatrix).getMatching();
    trueResult = new int[] { -1, 0, 1 };
    for (int i = 0; i < result.length; i++) {
      if (result[i] != trueResult[i]) {
        fail("Assignment is not a Maximum Weight Matching");
      }
    }

  }

  @Test
  public void testLink() {
    double[][] valuationMatrix = UnitDemandMarkets.UnitDemandMarket0();
    ArrayList<Link> links = Link.getEdgeValuations(valuationMatrix);
    ArrayList<Link> trueLinks = new ArrayList<Link>();
    trueLinks.add(new Link(2, 97.06));
    trueLinks.add(new Link(0, 29.6));
    assertTrue(trueLinks.equals(links));

    valuationMatrix = UnitDemandMarkets.UnitDemandMarket1();
    links = Link.getEdgeValuations(valuationMatrix);
    trueLinks = new ArrayList<Link>();
    trueLinks.add(new Link(1, 73.16));
    trueLinks.add(new Link(0, 19.47));
    assertTrue(trueLinks.equals(links));

    valuationMatrix = UnitDemandMarkets.UnitDemandMarket2();
    links = Link.getEdgeValuations(valuationMatrix);
    trueLinks = new ArrayList<Link>();
    trueLinks.add(new Link(1, 43.51));
    trueLinks.add(new Link(0, 39.92));
    assertTrue(trueLinks.equals(links));
    
    valuationMatrix = UnitDemandMarkets.UnitDemandMarket3();
    links = Link.getEdgeValuations(valuationMatrix);
    trueLinks = new ArrayList<Link>();
    trueLinks.add(new Link(0, 96));
    trueLinks.add(new Link(1, 62));
    assertTrue(trueLinks.equals(links));

   
    Printer.printMatrix(valuationMatrix);
    System.out.println(links);
    System.out.println(trueLinks.equals(links));
  }

}
