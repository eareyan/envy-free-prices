package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import algorithms.Waterfall.Bid;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketFactory;
import structures.MarketPrices;
import util.Printer;

public class GeneralApproximation {

	protected Market market;
	protected int[][] efficientAllocation;
	
	public GeneralApproximation(Market market, int[][] efficientAllocation){
		this.market = market;
		this.efficientAllocation = efficientAllocation;
	}
	
	public MarketPrices Solve() throws IloException{
		/* Get a list with the users that were allocated */
		ArrayList<Integer> usersAllocated = GeneralApproximation.getAllocatedUsers(this.efficientAllocation);
		ArrayList<MarketPrices> solutions = new ArrayList<MarketPrices>();
		ArrayList<ReserveBundle> reserveBundles = new ArrayList<ReserveBundle>();
		/* For each Allocated User, compute WaterfallMAXWEQ with reserve prices*/
		double reserveReward = 0.0;
		for(int i=0;i<usersAllocated.size();i++){
			reserveReward = GeneralApproximation.getReservePrice(this.market, usersAllocated.get(i),this.efficientAllocation);
			System.out.println("reserveReward for user:" + usersAllocated.get(i) + " = " + reserveReward);
			for(int j=0;j<this.market.getNumberCampaigns();j++){
				if(this.efficientAllocation[usersAllocated.get(i)][j] > 0){
					System.out.println("\tcampaign "+j+", " + this.efficientAllocation[usersAllocated.get(i)][j]);
					reserveBundles.add(new ReserveBundle(reserveReward * this.efficientAllocation[usersAllocated.get(i)][j],this.efficientAllocation[usersAllocated.get(i)][j],j));
				}
			}
		}
		Printer.printMatrix(this.efficientAllocation);
		System.out.println(reserveBundles);
		for(ReserveBundle b: reserveBundles){
			System.out.println("BUNDLE WITH REWARD : " + b.getReward());
			System.out.println(MarketFactory.augmentMarketWithReserve(this.market,b.getReward(),b.getDemand()));
			GeneralApproximation.deduceMatching(new WaterfallMAXWEQ(MarketFactory.augmentMarketWithReserve(this.market,b.getReward(),b.getDemand())).Solve());
			//solutions.add();
		}
		for(MarketPrices s: solutions){
			System.out.println(s.sellerRevenuePriceVector());
		}
		System.out.println(solutions);
		Collections.sort(solutions,new SolutionComparatorBySellerRevenue());
		return solutions.get(0);
	}
	
	public static void deduceMatching(MarketPrices output){
		Printer.printMatrix(output.getMarketAllocation().getAllocation());
		Printer.printVector(output.getPriceVector());
		System.out.println(output.sellerRevenuePriceVector());
	}
	
	/*
	 * Given a market, a user index and an allocation,
	 * this method computes min_j(R_j / I_j})
	 */
	public static double getReservePrice(Market M,int userIndex,int[][] allocation){
		double min = Double.POSITIVE_INFINITY;
		for(int j=0;j<M.getNumberCampaigns();j++){
			if(allocation[userIndex][j]>0){
				if(M.getCampaign(j).getReward() / M.getCampaign(j).getDemand() < min){
					min = M.getCampaign(j).getReward() / M.getCampaign(j).getDemand();
				}
			}
		}
		return min;
	}
	/*
	 * Given an allocation matrix, return a list of indices corresponding 
	 * to user that provided at least one impression.
	 */
	public static ArrayList<Integer> getAllocatedUsers(int[][] allocation){
		ArrayList<Integer> usersAllocated = new ArrayList<Integer>();
		for(int i=0;i<allocation.length;i++){
			for(int j=0;j<allocation[0].length;j++){
				if(allocation[i][j]>0){
					usersAllocated.add(i);
					break;
				}
			}
		}
		return usersAllocated;		
	}
	/*
	 * Comparator used to order solutions from WaterfallMAXWEQ.
	 */
	public class SolutionComparatorBySellerRevenue implements Comparator<MarketPrices>{
		@Override
		public int compare(MarketPrices m1, MarketPrices m2) {
			if(m1.sellerRevenuePriceVector() < m2.sellerRevenuePriceVector()) return 1;
			if(m1.sellerRevenuePriceVector() > m2.sellerRevenuePriceVector()) return -1;
			return 0;
		}
	}
	class ReserveBundle{
		private final double reward;
		private final int demand;
		private final int campaignIndex;
		public ReserveBundle(double reward, int demand, int campaignIndex){
			this.reward = reward;
			this.demand = demand;
			this.campaignIndex = campaignIndex;
		}
		public double getReward(){ return this.reward; }
		public int getDemand(){ return this.demand; }
		public int getCampaignIndex(){ return this.campaignIndex; }
		public String toString(){ return "("+this.reward+","+this.demand+","+this.campaignIndex+")"; }
	}
}
