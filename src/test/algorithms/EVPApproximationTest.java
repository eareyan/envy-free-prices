package test.algorithms;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import test.UnitDemandMarkets;
import unitdemand.algorithms.EVPApproximation;
import unitdemand.structures.UnitDemandException;
import unitdemand.structures.UnitDemandMarketOutcome;
import algorithms.pricing.error.PrincingAlgoException;

public class EVPApproximationTest {

  @Test
  public void testSolve() throws PrincingAlgoException, UnitDemandException {
    double[][] V = UnitDemandMarkets.UnitDemandMarket0();
    EVPApproximation evp = new EVPApproximation(V);
    UnitDemandMarketOutcome x = evp.Solve();
    assertTrue(x.getSellerRevenue() == 126.65999999999998);
    System.out.println("EVP time = " + x.getTime());

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
