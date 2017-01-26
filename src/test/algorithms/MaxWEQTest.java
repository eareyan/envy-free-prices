package test.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import structures.factory.UnitDemandMarketFactory;
import unitdemand.algorithms.MaxWEQ;
import unitdemand.structures.Matching;

public class MaxWEQTest {

  @Test
  public void testValuationMatrixWithNoi() {
    for (int n = 1; n < 50; n++) {
      for (int m = 1; m < 50; m++) {
        for (int p = 1; p < 5; p++) {
          double[][] V = UnitDemandMarketFactory.getValuationMatrix(n, m, 0.25 * p);
          MaxWEQ maxWEQ = new MaxWEQ(V);
          double[][] vWithNoi = maxWEQ.valuationMatrixWithNoi(0);
          assertEquals(V.length - 1, vWithNoi.length);
          for (int i = 0; i < vWithNoi.length; i++) {
            for (int j = 0; j < vWithNoi[0].length; j++) {
              assertTrue(vWithNoi[i][j] == V[i + 1][j]);
            }
          }
        }
      }
    }
  }

  @Test
  public void testMaxWEQ() {
    for (int n = 2; n < 20; n++) {
      for (int m = 2; m < 20; m++) {
        for (int p = 1; p < 5; p++) {
          double[][] V = UnitDemandMarketFactory.getValuationMatrix(n, m, 0.25 * p);
          Matching matching = new MaxWEQ(V).Solve();
          assertTrue(matching.numberOfEnvyBidders() == 0);
          assertTrue(matching.computeMarketClearanceViolations() == 0);
        }
      }
    }
  }

}
