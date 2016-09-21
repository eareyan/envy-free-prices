package unitdemand.evpapprox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import unitdemand.Link;
import unitdemand.Matching;
import unitdemand.MatchingComparatorBySellerRevenue;
import util.Printer;

/**
 * Implements Envy-Free Pricing Approximation Algorithm by Guruswami et. al.
 * Note that this class receives and object AbstractMaxWEQReservePrices that defines
 * how and where dummies are to be included. The original Guruswami et. al. implementation
 * uses class AllConnectedDummies in this same package.
 * 
 *  @author Enrique Areyan Viqueira
 */
public class EVPApproximation {
  
  /**
   * valuation matrix. Provides a valuation v_ij of user i by campaign j.
   */
  protected double[][] valuationMatrix;
  
  /**
   * An object AbstractMaxWEQReservePrices
   */
  protected AbstractMaxWEQReservePrices MWRP;

  /**
   * Constructor.
   * @param valuationMatrix - a matrix of valuations.
   * @param MWRP - an AbstractMaxWEQReservePrices object.
   */
  public EVPApproximation(double[][] valuationMatrix, AbstractMaxWEQReservePrices MWRP) {
    this.valuationMatrix = valuationMatrix;
    this.MWRP = MWRP;
    this.MWRP.setValuationMatrix(valuationMatrix);
  }
  
  /**
   * Implements Envy-Free Pricing Approx. Algorithm as stated in Guruswami et al.
   * 
   * @return a Matching object.
   */
  public Matching Solve() {
    // System.out.println("============ SOLVE ============");
    ArrayList<Link> valuations = Link.getEdgeValuations(this.valuationMatrix);
    // We add reserve price of zero always. This makes EVPApp at least as good as MaxWEQ.
    valuations.add(new Link(-1, 0.0)); 
    // System.out.println("valuations = " + valuations);
    double[] reservePrices = new double[this.valuationMatrix.length];
    ArrayList<Matching> setOfSolutionMatchings = new ArrayList<Matching>();
    // For each item, run MaxWEQ_r with reserve prices given by the valuation. 
    for (Link valueLink : valuations) {
      System.out.println("================= reserve price from campaign (" + valueLink.getJ() + ") = " + valueLink.getValue() + "++++++++++");
      Arrays.fill(reservePrices, valueLink.getValue());
      this.MWRP.setReservePrices(reservePrices);
      Matching x = this.MWRP.Solve(valueLink.getJ());
      setOfSolutionMatchings.add(x);
      System.out.println("''''''");
      Printer.printMatrix(x.getMatching());
      Printer.printVector(x.getPrices());
      System.out.println(x.getSellerRevenue());
      System.out.println("''''''");
    }
    System.out.println(setOfSolutionMatchings);
    Collections.sort(setOfSolutionMatchings, new MatchingComparatorBySellerRevenue());
    return setOfSolutionMatchings.get(0);
  }

}
