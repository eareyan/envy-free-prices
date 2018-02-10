package singleminded.algorithms.complete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import singleminded.structures.SingleMindedMarket;
import structures.Bidder;
import structures.Goods;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;
import structures.exceptions.MarketCreationException;
import structures.factory.SingleMindedMarketFactory;

public class CompleteSearch {

  public static SingleMindedMarket<Goods, Bidder<Goods>> simpleSMMarket() throws GoodsCreationException, BidderCreationException, MarketCreationException {
    ArrayList<Goods> goods = new ArrayList<Goods>();
    goods.add(new Goods(1));

    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    HashSet<Goods> bDemandSet = new HashSet<Goods>();
    bDemandSet.add(goods.get(0));
    bidders.add(new Bidder<Goods>(1, 1, bDemandSet));
    bidders.add(new Bidder<Goods>(1, 2, bDemandSet));

    return new SingleMindedMarket<Goods, Bidder<Goods>>(goods, bidders);

  }

  public static void main(String[] args) throws Exception {
    System.out.println("Singleminded Complete Search");

    ArrayList<Goods> goods = new ArrayList<Goods>();
    for (int i = 0; i < 5; i++) {
      goods.add(new Goods(1));
    }

    // Create bidders
    ArrayList<Bidder<Goods>> bidders = new ArrayList<Bidder<Goods>>();
    for (int j = 0; j < 3; j++) {
      HashSet<Goods> bDemandSet = new HashSet<Goods>();
      bDemandSet.add(goods.get(j));
      bidders.add(new Bidder<Goods>(1, 1, bDemandSet));
    }

    // SingleMindedMarket<Goods, Bidder<Goods>> market = new SingleMindedMarket<Goods, Bidder<Goods>>(goods, bidders);
    // SingleMindedMarket<Goods, Bidder<Goods>> market = simpleSMMarket();
    // SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarkets.singleMinded0();
    // SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarkets.singleMinded1();
    // SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarkets.singleMinded2();
    // SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarkets.singleMinded3();
    // SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarkets.singleMinded4();
    // SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarketFactory.uniformRewardRandomSingleMindedMarket(20, 25, 5);
    SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarketFactory.elitistRewardRandomSingleMindedMarket(20, 50, 5);

    HashMap<Bidder<Goods>, Boolean> allocation = new HashMap<Bidder<Goods>, Boolean>();
    ArrayList<Bidder<Goods>> listOfBidders = new ArrayList<Bidder<Goods>>();
    for (Bidder<Goods> bidder : market.getBidders()) {
      allocation.put(bidder, false);
      listOfBidders.add(bidder);
    }

    System.out.println("\n\n\n\nStart search...");
    SearchSolution OPT = search(listOfBidders, market, allocation, Double.NEGATIVE_INFINITY);
    System.out.println("\n\n *** OPT = " + OPT);

    System.out.println(market);
    for (Goods good : market.getGoods()) {
      System.out.print(OPT.getGoodPrice(good) + "\t");
    }
    System.out.println("\n");
    for (Bidder<Goods> bidder : market.getBidders()) {
      System.out.println(bidder + "->" + OPT.getAllocation().get(bidder));
    }
  }

  public static SearchSolution search(List<Bidder<Goods>> currentBidders, SingleMindedMarket<Goods, Bidder<Goods>> market,
      HashMap<Bidder<Goods>, Boolean> allocation, double revenuebound) throws Exception {
    // System.out.println("\ncurrentBidders = " + currentBidders + "\n");
    // for (Bidder<Goods> bidder : market.getBidders()) {
    // System.out.println(allocation.get(bidder));
    // }
    if (!CompleteSearch.validAllocation(market, allocation)) {
      // If the allocation is infeasible, skip.
      System.out.println("Not a valid allocation, skipping");
      return null;
    }
    // Solve the revenue maximizing LP.
    LPSolution lpSol = LP.solve(market, allocation);
    System.out.println("LP Status = " + lpSol.getStatus());

    if (lpSol.getStatus() == LPSolution.Status.Infeasible) {
      // If the LP is infeasible with the current allocation, allocating more bidders is not possible.
      System.out.println("An EFP with this allocation DNE, skipping");
      return null;
    }
    // If the LP is feasible, can we bound REVENUE??
    SearchSolution sol1;
    SearchSolution sol2;
    if (currentBidders.size() > 0) {
      HashMap<Bidder<Goods>, Boolean> newAllocation = new HashMap<Bidder<Goods>, Boolean>(allocation);
      HashMap<Bidder<Goods>, Boolean> newAllocation2 = new HashMap<Bidder<Goods>, Boolean>(allocation);
      List<Bidder<Goods>> currentBidderstemp = new ArrayList<Bidder<Goods>>(currentBidders);
      Bidder<Goods> bidder = currentBidderstemp.remove(0);
      newAllocation.put(bidder, true);
      sol1 = search(currentBidderstemp, market, newAllocation, 0);
      newAllocation.put(bidder, false);
      sol2 = search(currentBidderstemp, market, newAllocation2, 0);
      if (sol1 == null) {
        return sol2;
      }
      if (sol2 == null) {
        return sol1;
      }
      return (sol1.getObjValue() > sol2.getObjValue()) ? sol1 : sol2;
    }
    return new SearchSolution(lpSol.getObjValue(), allocation, lpSol.getPrices());
  }

  /**
   * Returns true iff the allocation is valid, i.e., items are not overallocated.
   * 
   * @param market
   * @param allocation
   * @return
   */
  public static boolean validAllocation(SingleMindedMarket<Goods, Bidder<Goods>> market, HashMap<Bidder<Goods>, Boolean> allocation) {
    HashSet<Goods> coveredGoods = new HashSet<Goods>();

    for (Bidder<Goods> bidder : market.getBidders()) {
      if (allocation.get(bidder)) {
        for (Goods good : bidder.getDemandSet()) {
          if (!coveredGoods.contains(good)) {
            coveredGoods.add(good);
          } else {
            return false;
          }
        }
      }
    }
    return true;
  }
}
