package test.factory;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import structures.factory.Parameters;
import structures.factory.UnitDemandMarketFactory;

public class UnitMarketFactoryTest {

  @Test
  public void testGetValuationMatrix() {
    for (int t = 0; t < 1000; t++) {
      double[][] x = UnitDemandMarketFactory.getValuationMatrix(10, 10, 1.0);
      // Printer.printMatrix(x);
      for (int i = 0; i < x.length; i++) {
        for (int j = 0; j < x[0].length; j++) {
          if (x[i][j] < Parameters.defaultMinReward || x[i][j] > Parameters.defaultMaxReward) {
            fail("The uniform reward drawn was: " + x[i][j] + ", but the reward must be in [" + Parameters.defaultMinReward + "," + Parameters.defaultMaxReward + "]");
          }
        }
      }
    }
  }

  @Test
  public void testGetValuationMatrixWithReserve() {

    for (int t = 0; t < 1000; t++) {
      double reserve = Math.random();
      double[][] x = UnitDemandMarketFactory.getValuationMatrix(10, 10, 1.0);
      double[][] xr = UnitDemandMarketFactory.getValuationReserve(x, reserve);
      for (int i = 0; i < x.length; i++) {
        for (int j = 0; j < x[0].length; j++) {
          if (x[i][j] - reserve <= 0) {
            assertTrue(xr[i][j] == Double.NEGATIVE_INFINITY);
          } else {
            assertTrue(xr[i][j] == x[i][j] - reserve);
          }
        }
      }
      //Printer.printMatrix(x);
      //System.out.println("");
      //Printer.printMatrix(xr);
    }
  }
}
