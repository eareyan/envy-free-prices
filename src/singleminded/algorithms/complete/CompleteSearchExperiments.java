package singleminded.algorithms.complete;

import ilog.concert.IloException;

import java.sql.SQLException;

import log.SqlDB;
import singleminded.structures.SingleMindedMarket;
import structures.Bidder;
import structures.Goods;
import structures.exceptions.AllocationException;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketCreationException;
import structures.exceptions.MarketOutcomeException;
import structures.factory.SingleMindedMarketFactory;
import algorithms.pricing.error.PrincingAlgoException;
import allocations.error.AllocationAlgoException;
import experiments.Experiments;
import experiments.RunParameters;

public class CompleteSearchExperiments extends Experiments {
  
  @Override
  public void runOneExperiment(int numGoods, int numBidders, int k, double p, String distribution, SqlDB dbLogger) throws SQLException, IloException, AllocationAlgoException, BidderCreationException, MarketAllocationException, MarketOutcomeException, GoodsCreationException, GoodsException, AllocationException, MarketCreationException, PrincingAlgoException, Exception {
    for (int i = 0; i < RunParameters.numTrials; i++) {
      if (!dbLogger.checkIfCompleteSearchRowExists(i, numGoods, numBidders, k, distribution)) {
        SingleMindedMarket<Goods, Bidder<Goods>> market = SingleMindedMarketFactory.uniformRewardRandomSingleMindedMarket(numGoods, numBidders, k);
        CompleteSearch completeSearch = new CompleteSearch(market);
        completeSearch.search();
        //System.out.println("\t Adding data... ");
        dbLogger.saveCompleteSearchRow(i, numGoods, numBidders, k, distribution, completeSearch.getExecutionTime(), completeSearch.getNumberOfExploredStates(), completeSearch.getNumberOfInfeasibleStates(), completeSearch.getNumberOfRevBoundStates());
      } else {
        //System.out.println("\t Already have data ");
      }
    }
    System.out.println("\t... done!");
  }

  @Override
  public void runExperiments(SqlDB dbLogger) throws Exception {
    String distribution = RunParameters.distribution;
    System.out.println("Distribution = " + distribution);
    //for (int n = 1; n <= RunParameters.numGoods; n++) {
    //  for (int m = 1; m <= RunParameters.numBidder; m++) {
    int n = 100;
    int m = 100;
        for (int k = n; k > 0; k--) {
          System.out.print(distribution + ": (n, m, k) = (" + n + ", " + m + ", " + k + ")");
          this.runOneExperiment(n, m, k, -1, distribution, dbLogger);
        }
      //}
    //}
  }

}
