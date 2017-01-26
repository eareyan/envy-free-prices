package test.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import structures.factory.UnitDemandMarketFactory;
import unitdemand.algorithms.MaxWEQ;
import unitdemand.algorithms.MaxWEQReservePrices;
import unitdemand.structures.Matching;
import algorithms.pricing.error.PrincingAlgoException;

public class MaxWEQReservePricesTest {

  @Test(expected = PrincingAlgoException.class)
  public void testMaxWEQReservePrices() throws PrincingAlgoException {
    double[][] V = new double[99][13];
    double[] reservePrices = new double[10];
    new MaxWEQReservePrices(V, reservePrices);
  }

  @Test
  public void testAugmentValuationMatrix() throws PrincingAlgoException {

    for (int n = 1; n < 30; n++) {
      for (int m = 1; m < 30; m++) {
        for (int p = 1; p < 5; p++) {
          double[][] V = UnitDemandMarketFactory.getValuationMatrix(n, m, 0.25 * p);

          double[] reservePrices = new double[n];
          for (int r = 0; r < n; r++) {
            reservePrices[r] = Math.random();
          }
          MaxWEQReservePrices maxWEQReservePrices = new MaxWEQReservePrices(V, reservePrices);
          double[][] VPrime = maxWEQReservePrices.augmentValuationMatrix();
          // Printer.printMatrix(VPrime);
          assertEquals(VPrime.length, V.length);
          assertEquals(VPrime[0].length, V[0].length + (2 * n));
          for (int i = 0; i < VPrime.length; i++) {
            assertTrue(VPrime[i][(i * 2) + V[0].length] == reservePrices[i]);
            assertTrue(VPrime[i][(i * 2) + V[0].length] == VPrime[i][(i * 2) + 1 + V[0].length]);
            for (int j = 0; j < VPrime[0].length; j++) {
              if (j != ((i * 2) + V[0].length) && j != ((i * 2) + 1 + V[0].length)) {
                if (j < V[0].length) {
                  assertTrue(VPrime[i][j] == V[i][j]);
                } else {
                  assertTrue(VPrime[i][j] == 0.0);
                }
              }
            }
          }
        }
      }
    }
  }

  @Test
  public void testDeduceMatching() throws PrincingAlgoException {

    for (int n = 2; n < 15; n++) {
      for (int m = 2; m < 15; m++) {
        for (int p = 1; p < 5; p++) {
          double[][] V = UnitDemandMarketFactory.getValuationMatrix(n, m, 0.25 * p);
          double[] reservePrices = new double[V.length];
          for (int i = 0; i < reservePrices.length; i++) {
            reservePrices[i] = Math.random();
          }
          MaxWEQReservePrices maxWEQReservePrices = new MaxWEQReservePrices(V, reservePrices);
          Matching M = new MaxWEQ(maxWEQReservePrices.augmentValuationMatrix()).Solve();
          Matching deducedMatching = maxWEQReservePrices.deduceMatching(M);
          double[] prices = deducedMatching.getPrices();
          for (int i = 0; i < prices.length; i++) {
            assertTrue(Math.abs(prices[i] - reservePrices[i]) <= 0.0001 || prices[i] > reservePrices[i]);
          }
        }
      }
    }
  }

}
