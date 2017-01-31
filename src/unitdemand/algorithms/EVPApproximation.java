package unitdemand.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import unitdemand.structures.Link;
import unitdemand.structures.UnitDemandException;
import unitdemand.structures.UnitDemandMarketOutcome;
import unitdemand.structures.UnitDemandMarketOutcomeComparatorBySellerRevenue;
import algorithms.pricing.error.PrincingAlgoException;

/**
 * Implements Envy-Free Pricing Approximation Algorithm by Guruswami et. al. Note that this class receives and object AbstractMaxWEQReservePrices that defines
 * how and where dummies are to be included. The original Guruswami et. al. implementation uses class AllConnectedDummies in this same package.
 * 
 * @author Enrique Areyan Viqueira
 */
public class EVPApproximation {
  /**
   * Start time, for statistics purposes.
   */
  private final long startTime;

  /**
   * valuation matrix. Provides a valuation v_ij of good i by bidder j.
   */
  protected double[][] valuationMatrix;

  /**
   * Constructor.
   * 
   * @param valuationMatrix - a matrix of valuations.
   * @param MWRP - an AbstractMaxWEQReservePrices object.
   */
  public EVPApproximation(double[][] valuationMatrix) {
    this.startTime = System.nanoTime();
    this.valuationMatrix = valuationMatrix;
  }

  /**
   * Implements Envy-Free Pricing Approx. Algorithm as stated in Guruswami et al.
   * 
   * @return a Matching object.
   * @throws PrincingAlgoException
   * @throws UnitDemandException
   */
  public UnitDemandMarketOutcome Solve() throws PrincingAlgoException, UnitDemandException {
    // System.out.println("============ SOLVE ============");
    ArrayList<Link> valuations = Link.getEdgeValuations(this.valuationMatrix);
    // We add reserve price of zero always. This makes EVPApp at least as good as MaxWEQ.
    valuations.add(new Link(-1, 0.0));
    double[] reservePrices = new double[this.valuationMatrix.length];
    ArrayList<UnitDemandMarketOutcome> setOfSolutionMatchings = new ArrayList<UnitDemandMarketOutcome>();
    // For each item, run MaxWEQ_r with reserve prices given by the valuation.
    for (Link valueLink : valuations) {
      Arrays.fill(reservePrices, valueLink.getValue());
      MaxWEQReservePrices m = new MaxWEQReservePrices(this.valuationMatrix, reservePrices);
      UnitDemandMarketOutcome x = m.Solve();
      setOfSolutionMatchings.add(x);
      // System.out.println("================= reserve price from bidder (" + valueLink.getJ() + ") = " + valueLink.getValue() + "++++++++++");
      // Printer.printMatrix(x.getMatching());
      // Printer.printVector(x.getPrices());
      // System.out.println(x.getSellerRevenue());
    }
    Collections.sort(setOfSolutionMatchings, new UnitDemandMarketOutcomeComparatorBySellerRevenue());
    UnitDemandMarketOutcome winnerOutcome = setOfSolutionMatchings.get(0);
    return new UnitDemandMarketOutcome(winnerOutcome.getMarketAllocation(), winnerOutcome.getPrices(), System.nanoTime() - this.startTime);
  }

}
