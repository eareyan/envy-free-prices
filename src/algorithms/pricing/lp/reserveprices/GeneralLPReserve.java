package algorithms.pricing.lp.reserveprices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import algorithms.EnvyFreePricesSolutionLP;
import algorithms.EnvyFreePricesVectorLP;
import algorithms.allocations.EfficientAllocationILP;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketPrices;
import structures.MarketPricesComparatorBySellerRevenue;
import util.Printer;

public class GeneralLPReserve {
	
	protected Market market;
	
	protected MarketAllocation initialMarketAllocation;
	
	protected ArrayList<MarketPrices> setOfSolutions;
	
	public GeneralLPReserve(Market market, MarketAllocation initialMarketAllocation) throws IloException{
		this.market = market;
		this.initialMarketAllocation = initialMarketAllocation;
		this.setOfSolutions = new ArrayList<MarketPrices>();
		/* Create the first LP with no reserves */
		EnvyFreePricesVectorLP initialLP = new EnvyFreePricesVectorLP(this.initialMarketAllocation,new IloCplex());
		initialLP.setWalrasianConditions(false);
		initialLP.createLP();
		EnvyFreePricesSolutionLP initialSolution = initialLP.Solve();
		this.setOfSolutions = new ArrayList<MarketPrices>();
		/* * Add initial solution to set of solutions, so that we have a baseline with reserve prices all zero */
		setOfSolutions.add(initialSolution);
	}
	
	public MarketPrices Solve() throws IloException{
		System.out.println("Initial allocation:");
		Printer.printMatrix(this.initialMarketAllocation.getAllocation());
		
		double[] reservePrices = new double[this.market.getNumberUsers()];
		for(int i=0;i<this.market.getNumberUsers();i++){
			for(int j=0;j<this.market.getNumberCampaigns();j++){
				if(this.initialMarketAllocation.getAllocation()[i][j] > 0){ /* For all x_{ij} > 0*/
					double reserve = this.market.getCampaign(j).getReward() / this.initialMarketAllocation.getAllocation()[i][j];
					/* Solve for an allocation that respects the reserve price R_j / x_{ij}*/
					int[][] allocRespectReserve = new EfficientAllocationILP(market,reserve).Solve(new IloCplex()).get(0);
					/* Run LP with reserve prices*/
					EnvyFreePricesVectorLP efp = new EnvyFreePricesVectorLP(new MarketAllocation(this.market,allocRespectReserve));
					efp.setWalrasianConditions(false);
					efp.createLP();
					Arrays.fill(reservePrices, reserve);
					efp.setReservePrices(reservePrices);
					setOfSolutions.add(efp.Solve());
					/* Only for debugging 
					System.out.println("Reserve: x["+i+"]["+j+"] = " + this.initialMarketAllocation.getAllocation()[i][j]);
					System.out.println("R_"+j+"/ x_{"+i+""+j+"} = "+this.market.getCampaign(j).getReward() + " / " + this.initialMarketAllocation.getAllocation()[i][j] +" = "+reserve);
					Printer.printMatrix(allocRespectReserve);
					System.out.println(efp.Solve().getStatus());*/
				}
			}
		}
		Collections.sort(setOfSolutions,new MarketPricesComparatorBySellerRevenue());
		System.out.println(setOfSolutions);
		/* For debugging purposes only: 
		System.out.println(setOfSolutions);
		for(MarketPrices sol:setOfSolutions){
			System.out.println("Solution-->");
			Printer.printMatrix(sol.getMarketAllocation().getAllocation());
			Printer.printVector(sol.getPriceVector());
			System.out.println(sol.sellerRevenuePriceVector());
		}*/
		return setOfSolutions.get(0);
	}

}
