package algorithms.lp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import algorithms.EfficientAllocationLP;
import algorithms.EnvyFreePricesSolutionLP;
import algorithms.EnvyFreePricesSolutionLPComparatorBySellerRevenue;
import algorithms.EnvyFreePricesVectorLP;
import algorithms.Waterfall;
import algorithms.Waterfall.Bid;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketFactory;
import structures.MarketPrices;
import unitdemand.Matching;
import util.Printer;

public class GeneralApproximation {

	protected Market market;
	protected int[][] allocationMatrix;
	protected MarketAllocation marketAllocation;
	
	public GeneralApproximation(Market market,boolean efficient){
		try {
			this.market = market;
			if(!efficient){
				this.marketAllocation = new Waterfall(this.market).Solve().getMarketAllocation();
			}else{
				int[][] efficientAllocation = new EfficientAllocationLP(market).Solve(new IloCplex()).get(0);
				this.marketAllocation = new MarketAllocation(market,efficientAllocation);
			}
			this.allocationMatrix = this.marketAllocation.getAllocation();
		} catch (IloException e) {
			e.printStackTrace();
		}
	}
	
	
	public GeneralApproximation(MarketAllocation marketAllocation){
		this.market = marketAllocation.getMarket();
		this.marketAllocation = marketAllocation;
		this.allocationMatrix = this.marketAllocation.getAllocation();
	}
	
	public MarketPrices Solve() throws IloException{
		/* Get a list with the users that were allocated */
		ArrayList<Integer> usersAllocated = this.getAllocatedUsers();
		/* For each Allocated User, compute WaterfallMAXWEQ with reserve prices*/
		//Printer.printMatrix(this.allocationMatrix);
		ArrayList<EnvyFreePricesSolutionLP> setOfSolutions = new ArrayList<EnvyFreePricesSolutionLP>();
		EnvyFreePricesVectorLP EFPrices;
		for(int i=0;i<usersAllocated.size();i++){		
			EFPrices = new EnvyFreePricesVectorLP(this.marketAllocation,new IloCplex());
			EFPrices.createLP();
			EFPrices.setReservePriceForUser(usersAllocated.get(i), this.getReservePrice(usersAllocated.get(i)));
			setOfSolutions.add(EFPrices.Solve());
		}
		Collections.sort(setOfSolutions,new EnvyFreePricesSolutionLPComparatorBySellerRevenue());
		//System.out.println(setOfSolutions);
		return setOfSolutions.get(0);
	}

	/*
	 * Given an allocation matrix, return a list of indices corresponding 
	 * to user that provided at least one impression.
	 */
	protected ArrayList<Integer> getAllocatedUsers(){
		ArrayList<Integer> usersAllocated = new ArrayList<Integer>();
		for(int i=0;i<this.allocationMatrix.length;i++){
			for(int j=0;j<this.allocationMatrix[0].length;j++){
				if(this.allocationMatrix[i][j]>0){
					usersAllocated.add(i);
					break;
				}
			}
		}
		return usersAllocated;		
	}
	
	/*
	 * Given a user index, return  min_{j s.t. x_ij > 0} v_ij, where v_ij = R_j / I_j
	 */
	protected double getReservePrice(int i){
		double min = Double.POSITIVE_INFINITY;
		for(int j=0;j<this.market.getNumberCampaigns();j++){
			if(this.allocationMatrix[i][j]>0){
				if(this.market.getCampaign(j).getReward() / this.market.getCampaign(j).getDemand() < min){
					min = this.market.getCampaign(j).getReward() / this.market.getCampaign(j).getDemand();
				}
				/* This produces LOTS of infeasibles LP
				 * if(this.market.getCampaign(j).getReward()  < min){
					min = this.market.getCampaign(j).getReward();
				}*/
			}
		}
		return min;
	}
}
