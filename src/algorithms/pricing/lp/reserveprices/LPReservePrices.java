package algorithms.pricing.lp.reserveprices;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import structures.Market;
import structures.MarketAllocation;
import structures.MarketPrices;
import structures.comparators.MarketPricesComparatorBySellerRevenue;
import structures.exceptions.CampaignCreationException;
import algorithms.pricing.EnvyFreePricesSolutionLP;
import algorithms.pricing.EnvyFreePricesVectorLP;
import allocations.error.AllocationException;

/**
 * This class implements a strategy for searching for a revenue-maximizing solution.
 * 
 * @author Enrique Areyan Viqueira
 */
public class LPReservePrices {

  protected Market market;
  protected MarketAllocation initialMarketAllocation;
  protected ArrayList<MarketPrices> setOfSolutions;
  protected AllocationAlgorithm AllocAlgo;

  /**
   * Constructor for LPReservePrices.
   * 
   * @param market - market on which to find a revenue-max solution
   * @param AllocAlgo - which allocation algorithm to use
   * @throws IloException - if the LP fails
   * @throws AllocationException - if the allocation algorithm fails
   * @throws CampaignCreationException - possibly thrown by the allocation algorithm
   */
  public LPReservePrices(Market market, AllocationAlgorithm AllocAlgo) throws IloException, AllocationException, CampaignCreationException {
    this.market = market;
    this.AllocAlgo = AllocAlgo;
    // The initial allocation has reserve price of 0. The solution is the plain flavor LP.
    this.initialMarketAllocation = this.AllocAlgo.getAllocWithReservePrice(this.market, 0.0);
    this.setOfSolutions = new ArrayList<MarketPrices>();
    // Create the first LP with no reserves.
    EnvyFreePricesVectorLP initialLP = new EnvyFreePricesVectorLP(this.initialMarketAllocation, new IloCplex());
    initialLP.setMarketClearanceConditions(false);
    initialLP.createLP();
    EnvyFreePricesSolutionLP initialSolution = initialLP.Solve();
    this.setOfSolutions = new ArrayList<MarketPrices>();
    //Add initial solution to set of solutions, so that we have a baseline with reserve prices all zero.
    setOfSolutions.add(initialSolution);
  }

  /**
   * 
   * @throws IloException
   * @throws AllocationException
   * @throws CampaignCreationException
   * @return a MarketPrices object.
   */
  public MarketPrices Solve() throws IloException, AllocationException, CampaignCreationException {
    /*
     * For debugging only. System.out.println("Initial allocation:");
     * Printer.printMatrix(this.initialMarketAllocation.getAllocation());
     */
    double[] reservePrices = new double[this.market.getNumberUsers()];
    for (int i = 0; i < this.market.getNumberUsers(); i++) {
      for (int j = 0; j < this.market.getNumberCampaigns(); j++) {
        if (this.initialMarketAllocation.getAllocation()[i][j] > 0) { 
          //For all x_{ij} > 0.
          double reserve = this.market.getCampaign(j).getReward() / this.initialMarketAllocation.getAllocation()[i][j];
          // Solve for an allocation that respects the reserve price R_j / x_{ij}.
          int[][] allocRespectReserve = this.AllocAlgo.getAllocWithReservePrice(market, reserve).getAllocation();
          // Run LP with reserve prices.
          EnvyFreePricesVectorLP efp = new EnvyFreePricesVectorLP(new MarketAllocation(this.market, allocRespectReserve));
          efp.setMarketClearanceConditions(false);
          efp.createLP();
          Arrays.fill(reservePrices, reserve);
          efp.setReservePrices(reservePrices);
          setOfSolutions.add(efp.Solve());
          /*
           * Only for debugging System.out.println("Reserve: x["+i+"]["+j+"] = "
           * + this.initialMarketAllocation.getAllocation()[i][j]);
           * System.out.println
           * ("R_"+j+"/ x_{"+i+""+j+"} = "+this.market.getCampaign
           * (j).getReward() + " / " +
           * this.initialMarketAllocation.getAllocation()[i][j] +" = "+reserve);
           * Printer.printMatrix(allocRespectReserve);
           * System.out.println(efp.Solve().getStatus());
           * Printer.printVector(efp.Solve().getPriceVector());
           */
        }
      }
    }
    Collections.sort(setOfSolutions, new MarketPricesComparatorBySellerRevenue());
    // System.out.println(setOfSolutions);
    /*
     * For debugging purposes only: System.out.println(setOfSolutions);
     * for(MarketPrices sol:setOfSolutions){ System.out.println("Solution-->");
     * Printer.printMatrix(sol.getMarketAllocation().getAllocation());
     * Printer.printVector(sol.getPriceVector());
     * System.out.println(sol.sellerRevenuePriceVector()); }
     */
    return setOfSolutions.get(0);
  }

}
