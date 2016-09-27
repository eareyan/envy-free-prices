package unitdemand.lp;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import structures.Market;
import structures.MarketAllocation;
import structures.MarketPrices;
import structures.comparators.MarketPricesComparatorBySellerRevenue;
import structures.factory.UnitMarketAllocationFactory;
import unitdemand.Link;
import util.Printer;
import algorithms.pricing.RestrictedEnvyFreePricesLP;

/**
 * Implements LP with reserve for unit-demand markets.
 * 
 * @author Enrique Areyan Viqueira
 */
public class UnitLPReservePrices {
  
  /**
   * Market object.
   */
  protected Market market;

  /**
   * Constructor.
   * @param market - a Market object.
   */
  public UnitLPReservePrices(Market market) {
    this.market = market;
  }

  /**
   * Implements LP with reserve prices for unit-demand.
   * @return a MarketPrices object.
   * @throws IloException in case the LP failed.
   */
  public MarketPrices Solve() throws IloException {
    // Compute MWM. 
    double[][] valuationMatrix = UnitMarketAllocationFactory.getValuationMatrixFromMarket(this.market);
    // Printer.printMatrix(valuationMatrix);
    ArrayList<Link> valuations = Link.getEdgeValuations(valuationMatrix);
    valuations.add(new Link(-1, 0.0)); // Add valuation of zero
    double[] reservePrices = new double[this.market.getNumberGoods()];
    ArrayList<MarketPrices> setOfSolutions = new ArrayList<MarketPrices>();
    for (Link valueLink : valuations) {// For each link
      IloCplex iloObject = new IloCplex();
      System.out.println("================= reserve price from campaign ("
          + valueLink.getJ() + ") = " + valueLink.getValue() + "++++++++++");
      // Printer.printMatrix(MarketAllocationFactory.getAllocationWithReservePrices(valuationMatrix,
      // valueLink.getValue()));
      // Get the new allocation.
      MarketAllocation marketAlloc = new MarketAllocation(this.market, UnitMarketAllocationFactory.getAllocationWithReservePrices(valuationMatrix, valueLink.getValue()));
      System.out.println("Alloc for reserve " + valueLink.getJ());
      Printer.printMatrix(marketAlloc.getAllocation());
      // Feed new allocation with initial market to LP and set reserve prices. 
      RestrictedEnvyFreePricesLP LP = new RestrictedEnvyFreePricesLP(marketAlloc, iloObject);
      LP.setMarketClearanceConditions(false);
      LP.createLP();
      LP.generateMarketClearanceConditionsWithReserve(valueLink.getValue());
      Arrays.fill(reservePrices, valueLink.getValue());
      LP.setReservePrices(reservePrices);
      setOfSolutions.add(LP.Solve());
      if (LP.Solve().getPriceVector() != null) {
        Printer.printVector(LP.Solve().getPriceVector());
      }
      System.out.println("********" + LP.Solve());
    }
    /* For debugging purposes only: */
    if (setOfSolutions.size() > 0) {
      System.out.println(setOfSolutions.get(0));
      System.out.println(setOfSolutions);
    }
    /*
     * for(MarketPrices sol:setOfSolutions){ System.out.println("Solution-->");
     * Printer.printMatrix(sol.getMarketAllocation().getAllocation());
     * Printer.printVector(sol.getPriceVector());
     * System.out.println(sol.sellerRevenuePriceVector()); }
     */
    Collections.sort(setOfSolutions, new MarketPricesComparatorBySellerRevenue());
    return setOfSolutions.get(0);
  }

}
