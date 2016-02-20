package algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

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
	protected int[][] wfAllocation;
	protected MarketAllocation marketAllocation;
	
	public GeneralApproximation(Market market){
		this.market = market;
		this.marketAllocation = new Waterfall(this.market).Solve().getMarketAllocation(); 
		this.wfAllocation = this.marketAllocation.getAllocation();
	}
	
	public MarketPrices Solve() throws IloException{
		/* Get a list with the users that were allocated */
		ArrayList<Integer> usersAllocated = GeneralApproximation.getAllocatedUsers(this.wfAllocation);
		/* For each Allocated User, compute WaterfallMAXWEQ with reserve prices*/
		Printer.printMatrix(this.wfAllocation);
		double reservePrice = 0.0;
		double[] reservePricesVector = new double[this.market.getNumberUsers()];
		ArrayList<EnvyFreePricesSolutionLP> setOfSolutions = new ArrayList<EnvyFreePricesSolutionLP>();
		IloCplex iloObject = new IloCplex();
		setOfSolutions.add(new EnvyFreePricesVectorLP(this.marketAllocation).Solve(iloObject));
		for(int i=0;i<usersAllocated.size();i++){
			for(int j=0;j<this.market.getNumberCampaigns();j++){
				if(this.wfAllocation[usersAllocated.get(i)][j] > 0){
					IloCplex iloObject0 = new IloCplex();
					reservePrice = this.market.getCampaign(j).getReward() / this.wfAllocation[usersAllocated.get(i)][j];
					Arrays.fill(reservePricesVector,reservePrice);
					System.out.println("\tcampaign "+j+", " + (reservePrice));
					Printer.printVector(reservePricesVector);
					EnvyFreePricesSolutionLP VectorSolEfficientAllocation = new EnvyFreePricesVectorLP(this.marketAllocation,reservePricesVector).Solve(iloObject0);
					if(VectorSolEfficientAllocation.getPriceVector() != null){
						System.out.println("ONe solution from LP");
						Printer.printVector(VectorSolEfficientAllocation.getPriceVector());
						Printer.printMatrix(this.marketAllocation.getAllocation());
						System.out.println();
						setOfSolutions.add(VectorSolEfficientAllocation);
					}
				}
			}
		}
		System.out.println(setOfSolutions);
		return null;
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
}
