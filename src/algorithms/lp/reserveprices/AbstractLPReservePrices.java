package algorithms.lp.reserveprices;

import java.util.ArrayList;
import java.util.Collections;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import algorithms.EnvyFreePricesSolutionLP;
import algorithms.EnvyFreePricesVectorLP;
import algorithms.Waterfall;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketPrices;
import structures.MarketPricesComparatorBySellerRevenue;
import util.Printer;

public abstract class AbstractLPReservePrices {
	
	protected Market market;
	protected MarketAllocation initialMarketAllocation;
	protected double[] initialPrices;
	
	public AbstractLPReservePrices(Market market){
		this.market = market;
	}
	
	public MarketPrices Solve() throws IloException{
		/* * Compute an allocation  * */
		this.initialMarketAllocation = new Waterfall(this.market).Solve().getMarketAllocation();
		/* * Compute Initial Prices * */
		this.initialPrices = new EnvyFreePricesVectorLP(this.initialMarketAllocation,new IloCplex(),true).Solve().getPriceVector();
		/* * Prints for debugging purposes * */
		Printer.printMatrix(this.initialMarketAllocation.getAllocation());
		Printer.printVector(this.initialPrices);
		System.out.println(new EnvyFreePricesVectorLP(this.initialMarketAllocation,new IloCplex(),true).Solve().sellerRevenuePriceVector());
		ArrayList<MarketPrices> setOfSolutions = new ArrayList<MarketPrices>();
		/* * For each x_{ij}. * */
		for(int i=0;i<this.market.getNumberUsers();i++){
			for(int j=0;j<this.market.getNumberCampaigns();j++){
				if(this.initialMarketAllocation.getAllocation()[i][j]>0){ 	// If x_{ij}>0
					//System.out.println("x["+i+"]["+j+"] = " + this.initialMarketAllocation.getAllocation()[i][j]);
					EnvyFreePricesVectorLP efpLP = new EnvyFreePricesVectorLP(new MarketAllocation(this.market,this.copyInitialAllocationMatrixWihtoutCampaignJ(j)),new IloCplex(),true);
					ArrayList<Integer> users = this.selectUsers(j); 		//Select users to set reserve prices
					for(Integer userIndex: users){
						this.setReservePrices(userIndex, j, efpLP);
					}
					EnvyFreePricesSolutionLP sol = efpLP.Solve();
					if(sol.getStatus().equals("Optimal")){ 					//We obtained an optimal solution from this LP
						//Printer.printVector(sol.getPriceVector());
						this.tryReallocate(j,sol);//Try to reallocate
						//Printer.printMatrix(sol.getMarketAllocation().getAllocation());
						System.out.println("Total revenue = " + sol.sellerRevenuePriceVector());
						setOfSolutions.add(sol);
					}
				}
			}
		}
		Collections.sort(setOfSolutions,new MarketPricesComparatorBySellerRevenue());
		for(MarketPrices sol:setOfSolutions){
			System.out.println(sol.sellerRevenuePriceVector());			
		}
		return null;
	}
	/*
	 * Given a campaign index j, return a list of users to which we want to set reserve prices
	 */
	protected abstract ArrayList<Integer> selectUsers(int j);
	/*
	 * Given a user index i and a campaign index j, and a LP 
	 */
	protected abstract void setReservePrices(int i,int j,EnvyFreePricesVectorLP LP);
	/*
	 * Try to reallocate campaign j after solving the LP.
	 */
	protected EnvyFreePricesSolutionLP tryReallocate(int j, EnvyFreePricesSolutionLP sol){
		//System.out.println("Reallocating campaign " + j);
		double currentcost = 0.0;
		boolean compactcondition = true;
		for(int i=0;i<this.market.getNumberUsers();i++){
			currentcost += sol.getPriceVectorComponent(i) * this.initialMarketAllocation.getAllocation()[i][j];
			if(this.initialMarketAllocation.getAllocation()[i][j] > 0){
				for(int k=0;k<this.market.getNumberUsers();k++){
					if(this.market.isConnected(k, j)){
						if(this.initialMarketAllocation.getAllocation()[k][j] < this.market.getUser(k).getSupply() && sol.getPriceVectorComponent(i) > sol.getPriceVectorComponent(k)){
							compactcondition = false;
						}
					}
				}
			}
		}
		if(currentcost <= this.market.getCampaign(j).getReward() && compactcondition){ // Makes Sense to reallocate
			//System.out.println("MAKES SENSE TO RE-ALLOCATE!!!");
			for(int i=0;i<this.market.getNumberUsers();i++){
				sol.getMarketAllocation().updateAllocationEntry(i, j, this.initialMarketAllocation.getAllocation()[i][j]);
			}
		}else{
			/*
			 * Only for debugging purposes
			 
			if(currentcost > this.market.getCampaign(j).getReward()){
				System.out.println("\t\t --- >I.R. failed");
			}
			if(!compactcondition){
				System.out.println("\t\t --- >C.C. failed");				
			}*/
		}
		return sol;
	}
	/*
	 * This methods makes a copy of the initial allocation
	 */
	protected int[][] copyInitialAllocationMatrixWihtoutCampaignJ(int campaignIndex){
		int[][] newAllocation = new int[this.market.getNumberUsers()][this.market.getNumberCampaigns()];
		for(int i=0;i<this.market.getNumberUsers();i++){
			for(int j=0;j<this.market.getNumberCampaigns();j++){
				if(j != campaignIndex){
					newAllocation[i][j] = this.initialMarketAllocation.getAllocation()[i][j];
				}else{
					newAllocation[i][j] = 0;
				}
			}
		}
		return newAllocation;
	}

}
