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

public class WEExistenceExperiments extends Experiments {

  @Override
  public void runOneExperiment(int numGoods, int numBidders, int k, double p, String distribution, SqlDB dbLogger) throws SQLException, IloException,
      AllocationAlgoException, BidderCreationException, MarketAllocationException, MarketOutcomeException, GoodsCreationException, GoodsException,
      AllocationException, MarketCreationException, PrincingAlgoException, Exception {

    for (int i = 0; i < RunParameters.numTrials; i++) {
      SingleMindedMarket<Goods, Bidder<Goods>> singleMindedMarket = SingleMindedMarketFactory.uniformIntegerRewardRandomSingleMindedMarket(numGoods, numBidders, k);
      //SingleMindedMarket<Goods, Bidder<Goods>> singleMindedMarket = SingleMindedMarketFactory.uniformRewardRandomSingleMindedMarket(numGoods, numBidders, k);
      String edges = singleMindedMarket.getEdgesStringRepresentation();
      String rewards = singleMindedMarket.getRewardStringRepresentation();
      if (!dbLogger.checkIfWEExistenceRowExists(numGoods, numBidders, k, edges, rewards)) {
        
        final long startTime = System.currentTimeMillis();
        boolean existence = WEExistence.decideWE(singleMindedMarket, false);
        final long endTime = System.currentTimeMillis();
        double executionTime = (endTime - startTime);
        
        dbLogger.saveWEExistenceRow(numGoods, numBidders, k, edges, rewards, executionTime, (existence) ? 1 : 0);
        //System.out.println("\t Adding data ");
      } else {
        System.out.println("\t Already have data ");
      }

    }

  }

  @Override
  public void runExperiments(SqlDB dbLogger) throws Exception {
    for (int n = 1; n < 10; n++) {
      for (int m = 1; m < 10; m++) {
        for (int k = 1; k <= n; k++) {
          System.out.println("(n , m , k) = (" + n + ", " + m + ", " + k + ")");
          this.runOneExperiment(n, m, k, -1, "", dbLogger);
          //this.runOneExperiment(10, 30, 3, -1, "", dbLogger);
        }
      }
    }
  }

}
