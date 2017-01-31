package test.structures;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import structures.factory.UnitDemandMarketFactory;
import test.UnitDemandMarkets;
import unitdemand.structures.UnitDemandException;
import unitdemand.structures.UnitDemandMarketAllocation;
import unitdemand.structures.UnitDemandMarketOutcome;
import util.Printer;

public class MatchingTest {

  @Test
  public void testEnvyBidders() throws UnitDemandException {
    for (int n = 1; n < 10; n++) {
      for (int m = 1; m < 10; m++) {
        for (int p = 1; p <= 4; p++) {
          UnitDemandMarketAllocation x = new UnitDemandMarketAllocation(UnitDemandMarketFactory.getValuationMatrix(n, m, p * 0.25), new int[n][m]);
          UnitDemandMarketOutcome o = new UnitDemandMarketOutcome(x, new double[n]);
          assertEquals(o.getSellerRevenue(), 0.0, 0.000000001);
          assertEquals(o.getMCViolationsRatio(), 0.0, 0.0000000001);

          double[] prices = new double[n];
          for (int k = 0; k < n; k++) {
            prices[k] = Double.MAX_VALUE;
          }
          o = new UnitDemandMarketOutcome(x, prices);
          assertEquals(o.getEFViolationsRatio(), 0.0, 0.000000001);
        }
      }
    }
  }

  @Test
  public void test() throws UnitDemandException {
    double[][] V = UnitDemandMarkets.UnitDemandMarket0();
    Printer.printMatrix(V);
    UnitDemandMarketOutcome m = new UnitDemandMarketOutcome(new UnitDemandMarketAllocation(V, new int[3][3]), new double[3]);
    assertEquals(m.numberOfEnvyBidders(), 3);
    assertEquals(m.getSellerRevenue(), 0.0, 0.0000000001);
    assertEquals(m.getMCViolationsRatio(), 0.0, 0.0000000001);
    assertEquals(m.getEFViolationsRatio(), 1.0, 0.0000000001);
    m.printPrices();
    System.out.println(m.getSellerRevenue());
    System.out.println(m.listOfEnvyBidders());
    System.out.println(m.numberOfEnvyBidders());
  }

  @Test
  public void test1() throws UnitDemandException {
    double[][] V = UnitDemandMarkets.UnitDemandMarket0();
    double[] p = new double[3];
    p[0] = Double.MAX_VALUE;
    p[1] = Double.MAX_VALUE;
    p[2] = Double.MAX_VALUE;
    UnitDemandMarketOutcome m = new UnitDemandMarketOutcome(new UnitDemandMarketAllocation(V, new int[3][3]), p);
    assertEquals(m.getEFViolationsRatio(), 0.0, 0.000000001);

  }

}
