package test.algorithms;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import test.UnitDemandMarkets;
import unitdemand.algorithms.EVPApproximation;
import unitdemand.structures.Matching;
import algorithms.pricing.error.PrincingAlgoException;

public class EVPApproximationTest {

  @Test
  public void testSolve() throws PrincingAlgoException {
    double[][] V = UnitDemandMarkets.UnitDemandMarket0();
    EVPApproximation evp = new EVPApproximation(V);
    Matching x = evp.Solve();
    assertTrue(x.getSellerRevenue() == 126.65999999999998);

    V = UnitDemandMarkets.UnitDemandMarket1();
    evp = new EVPApproximation(V);
    x = evp.Solve();
    assertTrue(x.getSellerRevenue() == 73.16);

    V = UnitDemandMarkets.UnitDemandMarket2();
    evp = new EVPApproximation(V);
    x = evp.Solve();
    assertTrue(x.getSellerRevenue() == 79.84);

    
    V = UnitDemandMarkets.UnitDemandMarket3();
    evp = new EVPApproximation(V);
    x = evp.Solve();
    assertTrue(x.getSellerRevenue() == 158.0);

  }

}
