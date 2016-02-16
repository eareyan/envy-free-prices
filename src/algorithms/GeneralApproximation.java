package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
		/* For each Allocated User, compute WaterfallMAXWEQ with reserve prices*/
		for(int i=0;i<usersAllocated.size();i++){
			solutions.add(new WaterfallMAXWEQ(MarketFactory.augmentMarketWithReserve(this.market, GeneralApproximation.getReservePrice(this.market, usersAllocated.get(i),efficientAllocation))).Solve());
			System.out.println(solutions.get(i).sellerRevenuePriceVector());
		}
		Collections.sort(solutions,new SolutionComparatorBySellerRevenue());
		return solutions.get(0);
	}
	/*
	 * Given a market, a user index and an allocation,
	 * this method computes min_j(R_j / x_{usersAllocated.get(i)j})
	 */
	public static double getReservePrice(Market M,int userIndex,int[][] allocation){
		double min = Double.POSITIVE_INFINITY;
		for(int j=0;j<M.getNumberCampaigns();j++){
			if(allocation[userIndex][j]>0){
				if(M.getCampaign(j).getReward() / allocation[userIndex][j] < min){
					min = M.getCampaign(j).getReward() / allocation[userIndex][j];
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
}
