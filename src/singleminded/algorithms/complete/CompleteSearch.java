package singleminded.algorithms.complete;

import ilog.concert.IloException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import singleminded.structures.SingleMindedMarket;
import structures.Bidder;
import structures.Goods;

import com.google.common.collect.ImmutableMap;

public class CompleteSearch {

  /**
   * A variable to keep track of the number of branches explored by the search.
   */
  private int numberOfBranches = 0;

  /**
   * A variable to keep track of the execution time of the algorithm.
   */
  private double executionTime;

  /**
   * A variable to keep a global lower bound on revenue.
   */
  private double globalRevenueLowerBound;

  /**
   * The single-minded market on which this search acts.
   */
  private final SingleMindedMarket<Goods, Bidder<Goods>> market;

  /**
   * Constructor
   * 
   * @param market
   */
  public CompleteSearch(SingleMindedMarket<Goods, Bidder<Goods>> market) {
    this.market = market;
    this.globalRevenueLowerBound = market.getHighestReward();
  }

  /**
   * Initialize the search.
   * 
   * @return
   * @throws IloException 
   * @throws LPException
   */
  public SearchSolution search() throws LPException, IloException {
    final long startTime = System.currentTimeMillis();
    SearchSolution OPT = this.searchProcedure(market.getBidders(), new HashSet<Bidder<Goods>>(), new HashSet<Bidder<Goods>>());
    final long endTime = System.currentTimeMillis();
    this.executionTime = (endTime - startTime) / 1000.0;
    return OPT;
  }

  /**
   * Recursive function
   * 
   * @param market
   * @param candidates
   * @param winners
   * @param losers
   * @param revenuebound
   * @return
   * @throws IloException 
   * @throws LPException 
   */
  private SearchSolution searchProcedure(List<Bidder<Goods>> candidates, HashSet<Bidder<Goods>> winners, HashSet<Bidder<Goods>> losers) throws LPException, IloException  { 
    // Keep track of the number of branches explored.
    this.numberOfBranches++;
    // Create a fresh copy of the decision variables for this recursion level. Perhaps a more efficient strategy would be to change the values and then change
    // them back before recurring.
    List<Bidder<Goods>> candidatesCopy = new ArrayList<Bidder<Goods>>(candidates);
    HashSet<Bidder<Goods>> winnersCopy = new HashSet<Bidder<Goods>>(winners);
    HashSet<Bidder<Goods>> losersCopy = new HashSet<Bidder<Goods>>(losers);
    // Propagate all loss.
    propagateLoss(candidatesCopy, winnersCopy, losersCopy);
    // Solve LP.
    LPSolution lpSol = LP.solve(market, winnersCopy);
    if (lpSol.getStatus() == LPSolution.Status.Infeasible) {
      // If the LP is infeasible with the current allocation, allocating more bidders is not possible. Prune this branch.
      // System.out.println("An EFP with this allocation DNE, skipping");
      return new SearchSolution(Double.NEGATIVE_INFINITY, null, null);
    }
    // If there are no more candidates, return the current solution (propagation could have eliminated all possible candidates.
    if (candidatesCopy.size() == 0) {
      return new SearchSolution(lpSol.getObjValue(), winnersCopy, lpSol.getPrices());
    }
    // Compute the revenue bound.
    double branchUpperBoundRev = getRevenueUpperBound(lpSol.getPrices(), winnersCopy, losersCopy);
    if (branchUpperBoundRev < this.globalRevenueLowerBound) {
      // System.out.println("This branch promises revenue at most " + branchUpperBoundRev + ", which is lower than our current lower bound " +
      // CompleteSearch.globalRevenueLowerBound + ", skipping.");
      return new SearchSolution(lpSol.getObjValue(), winnersCopy, lpSol.getPrices());
    }
    // If the current solution is a better lower bound than our current global lower bound, update the current global lower bound.
    if (lpSol.getObjValue() > this.globalRevenueLowerBound) {
      this.globalRevenueLowerBound = lpSol.getObjValue();
    }
    Bidder<Goods> nextBidder = chooseNextBidder(candidatesCopy);
    winnersCopy.add(nextBidder);
    // Search for a solution where the selected bidder is a winner.
    SearchSolution sol1 = searchProcedure(candidatesCopy, winnersCopy, losersCopy);
    winnersCopy.remove(nextBidder);
    losersCopy.add(nextBidder);
    // Search for a solution where the selected bidder is a loser.
    SearchSolution sol2 = searchProcedure(candidatesCopy, winnersCopy, losersCopy);
    return (sol1.getRevenueOfSolution() > sol2.getRevenueOfSolution()) ? sol1 : sol2;
  }

  /**
   * Returns the next bidder.
   * 
   * @param market
   * @param candidates
   * @return
   */
  private Bidder<Goods> chooseNextBidder(List<Bidder<Goods>> candidates) {
    return candidates.remove(0);
  }

  /**
   * Given the candidate list, the set of losers, and a winner; add to the set of losers all candidates that the winner blocks.
   * 
   * @param market
   * @param candidates
   * @param losers
   * @param winner
   */
  private static void propagateLoss(List<Bidder<Goods>> candidates, HashSet<Bidder<Goods>> winners, HashSet<Bidder<Goods>> losers) {
    ArrayList<Bidder<Goods>> losingBidders = new ArrayList<Bidder<Goods>>();
    for (Bidder<Goods> winner : winners) {
      outer: for (Bidder<Goods> candidate : candidates) {
        for (Goods good : winner.getDemandSet()) {
          if (candidate.demandsGood(good)) {
            losingBidders.add(candidate);
            continue outer;
          }

        }
      }
    }
    candidates.removeAll(losingBidders);
    losers.addAll(losingBidders);
  }

  /**
   * Computes the revenue bound. The most one can get is W + C (i.e., the losers cannot be recovered).
   * 
   * @param market
   * @param prices
   * @param winners
   * @param losers
   * @return
   */
  private double getRevenueUpperBound(ImmutableMap<Goods, Double> prices, HashSet<Bidder<Goods>> winners, HashSet<Bidder<Goods>> losers) {
    double bound = 0;
    for (Bidder<Goods> bidder : this.market.getBidders()) {
      if (winners.contains(bidder)) {
        for (Goods good : bidder.getDemandSet()) {
          bound += prices.get(good);
        }
      } else if (losers.contains(bidder)) {
        // bound -= bidder.getReward();
      } else {
        bound += bidder.getReward();
      }
    }
    return bound;
  }

  /**
   * Returns the number of branches explored by the complete search
   * 
   * @return
   */
  public int getNumberOfBranches() {
    return this.numberOfBranches;
  }

  /**
   * Returns the number of seconds that the algorithm took to find the solution
   * 
   * @return
   */
  public double getExecutionTime() {
    return this.executionTime;
  }
}
