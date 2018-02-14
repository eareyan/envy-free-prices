package singleminded.algorithms.complete;

import ilog.concert.IloException;
import singleminded.structures.SingleMindedMarket;
import structures.Bidder;
import structures.Goods;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;
import structures.exceptions.MarketCreationException;
import structures.factory.SingleMindedMarketFactory;

public class Experiments {
  
  public static void main(String[] args) throws LPException, IloException, GoodsCreationException, BidderCreationException, MarketCreationException {
    // Testing.
    // SingleMindedMarket<Goods, Bidder<Goods>> market = CompleteSearch.simpleSMMarket();
    // SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarkets.singleMinded0();
    // SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarkets.singleMinded1();
    int n = 20;
    int m = 20;
    int k = 2;
    // int m = 3;
    // SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarkets.singleMinded0();
    // SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarkets.singleMinded1();
    // SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarkets.singleMinded2();
    // SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarkets.singleMinded3();
    // SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarkets.singleMinded4();
    //SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarketFactory.uniformRewardRandomSingleMindedMarket(n, m, k);
    SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarketFactory.elitistRewardRandomSingleMindedMarket(n, m, k);

    System.out.println(market);
    // Search for a revenue-maximizing EFP.
    CompleteSearch completeSearch = new CompleteSearch(market);
    SearchSolution OPT = completeSearch.search();
    OPT.printPrices(market);
    OPT.printWinners(market);
    System.out.println("\n\n *** Optimal revenue = " + OPT.getRevenueOfSolution());
    // Print some statistics.
    System.out.println("Total execution time: " + completeSearch.getExecutionTime());
    System.out.println("numberOfBranches = " + completeSearch.getNumberOfBranches());
    System.out.println("total search space = " + (Math.pow(2, m) - 1));
    System.out.println(completeSearch.getNumberOfBranches() / (Math.pow(2, m) - 1));
  }
  
  public static void runExperiment(int n, int m, int k) throws GoodsCreationException, BidderCreationException, MarketCreationException {
    SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarketFactory.uniformRewardRandomSingleMindedMarket(n, m, k);
    System.out.println(market);
  }

}
