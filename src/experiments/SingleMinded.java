package experiments;

import ilog.concert.IloException;

import java.sql.SQLException;
import java.util.HashMap;

import log.SqlDB;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import singleminded.SingleMindedApproxWE;
import singleminded.SingleMindedMarket;
import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketAllocation;
import structures.exceptions.AllocationException;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketCreationException;
import structures.exceptions.MarketOutcomeException;
import structures.factory.SingleMindedMarketFactory;
import algorithms.pricing.error.PrincingAlgoException;
import allocations.error.AllocationAlgoException;
import allocations.optimal.SingleStepWelfareMaxAllocationILP;

public class SingleMinded extends Experiments{

  /**
   * Run single-minded experiments.
   * @throws PrincingAlgoException 
   */
	@Override
	public void runOneExperiment(int numUsers, int numCampaigns, int k, SqlDB dbLogger) throws SQLException, IloException, AllocationAlgoException, BidderCreationException, MarketAllocationException, MarketOutcomeException, AllocationException, GoodsException, MarketCreationException, PrincingAlgoException {
	
		if(!dbLogger.checkIfSingleMindedRowExists("singleminded2", numUsers, numCampaigns, k)){
      System.out.println("\t Adding data ");
      HashMap<String, DescriptiveStatistics> stats = new HashMap<String, DescriptiveStatistics>();
			for(int i = 0; i < RunParameters.numTrials; i ++){
				// Generate Single-minded random market.
				SingleMindedMarket<Goods, Bidder<Goods>> M = SingleMindedMarketFactory.createRandomSingleMindedMarket(numUsers , numCampaigns, k);
				//System.out.println(M);
				// Efficient Allocation.
				MarketAllocation<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> efficientAlloc = new SingleStepWelfareMaxAllocationILP<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>().Solve(M);
				double optimalWelfare = efficientAlloc.value();				
				// Obtain statistics from all algorithms.				
        this.populateStats(stats, new SingleMindedApproxWE(M).Solve(), "approx", optimalWelfare);
        this.populateStats(stats, LPWrapper.getMarketPrices(M, LPWrapper.Allocations.GreedyWelfare), "gw", optimalWelfare);
        this.populateStats(stats, LPWrapper.getMarketPrices(M, LPWrapper.Allocations.GreedyEgalitarian), "ge", optimalWelfare);
        this.populateStats(stats, LPWrapper.getMarketPrices(M, LPWrapper.Allocations.OptimalWelfare), "ow", optimalWelfare);
        this.populateStats(stats, LPWrapper.getMarketPrices(M, LPWrapper.Allocations.OptimalEgalitarian), "oe", optimalWelfare);
			}
			dbLogger.saveSingleMinded("singleminded2", numUsers, numCampaigns, k, stats);
		} else {
			System.out.println("\t Already have data ");			
		}
	}
	
	/**
	 * Keeps tracks of the statistics.
	 * 
	 * @param stats
	 * @param ps
	 * @param id
	 * @param optimalWelfare
	 * @throws MarketAllocationException
	 * @throws MarketOutcomeException
	 */
	public void populateStats(HashMap<String,DescriptiveStatistics> stats, PricesStatistics<SingleMindedMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> ps, String id, double optimalWelfare) throws MarketAllocationException, MarketOutcomeException {
    this.getDS(stats, id + "Welfare").addValue(ps.getWelfareRatio(optimalWelfare));
    this.getDS(stats, id + "Revenue").addValue(ps.getSellerRevenueRatio(optimalWelfare));
    this.getDS(stats, id + "EF").addValue(ps.getEFViolationsRatio());
    this.getDS(stats, id + "EFLoss").addValue(ps.getRatioLossUtility());
    this.getDS(stats, id + "Time").addValue(ps.getTime() / 1000000000.0);
	}

	/**
	 * Returns the DescriptiveStatistics objects if it exists
	 * or creates it.
	 * 
	 * @param stats
	 * @param id
	 * @return
	 */
  public DescriptiveStatistics getDS(HashMap<String, DescriptiveStatistics> stats, String id) {
    DescriptiveStatistics ds;
    if (stats.containsKey(id)) {
      ds = stats.get(id);
    } else {
      ds = new DescriptiveStatistics();
      stats.put(id, ds);
    }
    return ds;
  }
}
