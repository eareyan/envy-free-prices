package algorithms.pricing.lp.heuristicreserveprices;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.Collections;

import structures.Market;
import structures.MarketAllocation;
import structures.MarketPrices;
import structures.comparators.MarketPricesComparatorBySellerRevenue;
import structures.exceptions.MarketPricesException;
import util.Printer;
import algorithms.pricing.EnvyFreePricesSolutionLP;
import algorithms.pricing.EnvyFreePricesVectorLP;
import algorithms.pricing.lp.heuristicreserveprices.interfaces.SelectUsers;
import algorithms.pricing.lp.heuristicreserveprices.interfaces.SetReservePrices;

/**
 * This class implements the main methods for LP with reserve prices.
 * It receives two types of objects: SelectUsers and SetReservePrices.
 * These objects define two methods: selectUsers and setReservePrices respectively.
 * The first determines which users should get a reserve price and the second
 * determines what these reserve prices should be.
 * 
 * @author Enrique Areyan Viqueira
 */
public class HeuristicLPReservePrices {

  protected Market market;
  protected MarketAllocation initialMarketAllocation;
  protected double[] initialPrices;
  protected SelectUsers selectUsersObject;
  protected SetReservePrices setReservePricesObject;
  protected ArrayList<MarketPrices> setOfSolutions;

  /**
   * Constructo. 
   * @param market - the market in which to operate.
   * @param initialMarketAllocation - the initial allocation.
   * @param selectUsersObject - the type of user selection object.
   * @param setReservePricesObject - the type of reserve price object.
   * @throws IloException in case the LP failed. 
   */
  public HeuristicLPReservePrices(Market market, MarketAllocation initialMarketAllocation, SelectUsers selectUsersObject, SetReservePrices setReservePricesObject) throws IloException {
    this.market = market;
    this.selectUsersObject = selectUsersObject;
    this.setReservePricesObject = setReservePricesObject;
    this.initialMarketAllocation = initialMarketAllocation;
    // Create the first LP with no reserves.
    EnvyFreePricesVectorLP initialLP = new EnvyFreePricesVectorLP(this.initialMarketAllocation, new IloCplex());
    initialLP.setMarketClearanceConditions(false);
    initialLP.createLP();
    EnvyFreePricesSolutionLP initialSolution = initialLP.Solve();
    this.initialPrices = initialSolution.getPriceVector();
    this.setOfSolutions = new ArrayList<MarketPrices>();
    // Add initial solution to set of solutions, so that we have a baseline with reserve prices all zero. 
    setOfSolutions.add(initialSolution);
  }

  public MarketPrices Solve() throws IloException, MarketPricesException {
    // System.out.println("LP with reserve Price");
    // Compute Initial Prices. 
    // Prints for debugging purposes. 
    Printer.printMatrix(this.initialMarketAllocation.getAllocation());
    Printer.printVector(this.initialPrices);
    System.out.println(new EnvyFreePricesVectorLP(this.initialMarketAllocation, new IloCplex(), true).Solve().sellerRevenuePriceVector());

    // For each x_{ij}.
    for (int i = 0; i < this.market.getNumberUsers(); i++) {
      for (int j = 0; j < this.market.getNumberCampaigns(); j++) {
        //If x_{ij} > 0.
        if (this.initialMarketAllocation.getAllocation()[i][j] > 0) {
          // System.out.println("x["+i+"]["+j+"] = " +
          // this.initialMarketAllocation.getAllocation()[i][j]);
          EnvyFreePricesVectorLP efpLP = new EnvyFreePricesVectorLP(
              new MarketAllocation(this.market,
                  this.copyInitialAllocationMatrixWihtoutCampaignJ(j)),
              new IloCplex());
          efpLP.setMarketClearanceConditions(false);
          efpLP.createLP();
          // Select users to set reserve prices
          ArrayList<Integer> users = this.selectUsersObject.selectUsers(j, this.market);
          for (Integer userIndex : users) {
            this.setReservePricesObject.setReservePrices(userIndex, j, efpLP,
                this.initialPrices, this.market, this.initialMarketAllocation);
          }
          EnvyFreePricesSolutionLP sol = efpLP.Solve();
          // If we obtained an optimal solution from this LP.
          if (sol.getStatus().equals("Optimal")) {
            System.out.println("LP was " + sol.getStatus());
            // Printer.printVector(sol.getPriceVector());
            this.tryReallocate(j, sol);// Try to reallocate
            // Printer.printMatrix(sol.getMarketAllocation().getAllocation());
            // System.out.println("Total revenue = " +
            // sol.sellerRevenuePriceVector());
            this.setOfSolutions.add(sol);
          } else {
            System.out.println("LP was " + sol.getStatus());
          }
        }
      }
    }
    Collections.sort(this.setOfSolutions, new MarketPricesComparatorBySellerRevenue());
    // For debugging purposes only:
    System.out.println(this.setOfSolutions);
    for (MarketPrices sol : this.setOfSolutions) {
      System.out.println("Solution-->");
      Printer.printMatrix(sol.getMarketAllocation().getAllocation());
      Printer.printVector(sol.getPriceVector());
      System.out.println(sol.sellerRevenuePriceVector());
    }
    return this.setOfSolutions.get(0);
  }
  
  /**
   * After solving an LP with reserve prices, we try to reallocate the campaign
   * that was deallocated.
   * @param j - a campaign index
   * @param sol - an EnvyFreePricesSolutionLP object
   * @return an EnvyFreePricesSolutionLP object, possibly with reallocated campaigns.
   */
  protected EnvyFreePricesSolutionLP tryReallocate(int j, EnvyFreePricesSolutionLP sol) {
    // System.out.println("Reallocating campaign " + j);
    double currentcost = 0.0;
    boolean compactcondition = true;
    for (int i = 0; i < this.market.getNumberUsers(); i++) {
      currentcost += sol.getPriceVectorComponent(i) * this.initialMarketAllocation.getAllocation()[i][j];
      if (this.initialMarketAllocation.getAllocation()[i][j] > 0) {
        for (int k = 0; k < this.market.getNumberUsers(); k++) {
          if (this.market.isConnected(k, j)) {
            if (this.initialMarketAllocation.getAllocation()[k][j] < this.market.getUser(k).getSupply()
                && sol.getPriceVectorComponent(i) > sol.getPriceVectorComponent(k)) {
              compactcondition = false;
            }
          }
        }
      }
    }
    // If it makes Sense to reallocate.
    if (currentcost <= this.market.getCampaign(j).getReward() && compactcondition) { 
      System.out.println("MAKES SENSE TO RE-ALLOCATE!!!");
      for (int i = 0; i < this.market.getNumberUsers(); i++) {
        sol.getMarketAllocation().updateAllocationEntry(i, j, this.initialMarketAllocation.getAllocation()[i][j]);
      }
    } else {
      // Only for debugging purposes.
      if (currentcost > this.market.getCampaign(j).getReward()) {
        System.out.println("\t\t --- >I.R. failed");
      }
      if (!compactcondition) {
        System.out.println("\t\t --- >C.C. failed");
      }
    }
    return sol;
  }
  
  /**
   * This methods makes a copy of the initial allocation
   * without campaign j.
   * @param campaignIndex - index of campaign to be ignored.
   * @return a copy of the initial allocation without campaign campaignIndex
   */
  protected int[][] copyInitialAllocationMatrixWihtoutCampaignJ(int campaignIndex) {
    int[][] newAllocation = new int[this.market.getNumberUsers()][this.market.getNumberCampaigns()];
    for (int i = 0; i < this.market.getNumberUsers(); i++) {
      for (int j = 0; j < this.market.getNumberCampaigns(); j++) {
        if (j != campaignIndex) {
          newAllocation[i][j] = this.initialMarketAllocation.getAllocation()[i][j];
        } else {
          newAllocation[i][j] = 0;
        }
      }
    }
    return newAllocation;
  }

}
