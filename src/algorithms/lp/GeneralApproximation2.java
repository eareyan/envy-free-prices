package algorithms.lp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import algorithms.EnvyFreePricesSolutionLP;
import algorithms.EnvyFreePricesVectorLP;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import structures.Campaign;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketFactory;
import structures.MarketPrices;
import structures.MarketPricesComparatorBySellerRevenue;
import util.Printer;

public class GeneralApproximation2 extends GeneralApproximation{
	public GeneralApproximation2(Market market, boolean efficient){
		super(market,efficient);
	}
	public GeneralApproximation2(MarketAllocation marketAllocation){
		super(marketAllocation);
	}
	public MarketPrices Solve() throws IloException{
		/* Get a list with the users that were allocated */
		ArrayList<Integer> usersAllocated = this.getAllocatedUsers();
		/* For each Allocated User, compute WaterfallMAXWEQ with reserve prices*/
		//Printer.printMatrix(this.allocationMatrix);
		ArrayList<MarketPrices> setOfSolutions = new ArrayList<MarketPrices>();
		MarketPrices allocWithPrices;
		for(int i=0;i<usersAllocated.size();i++){
			for(int j=0;j<this.market.getNumberCampaigns();j++){
				if(this.allocationMatrix[usersAllocated.get(i)][j]>0){
					//System.out.println("x["+usersAllocated.get(i)+"]["+j+"] = " + this.allocationMatrix[usersAllocated.get(i)][j]);
					//System.out.println("Find prices for this allocation");
					allocWithPrices = this.solveLPWithReservePrices(i, j);
					if(allocWithPrices.getMarketAllocation() != null){
						//Printer.printMatrix(allocWithPrices.getMarketAllocation().getAllocation());
						//Printer.printVector(allocWithPrices.getPriceVector());
						setOfSolutions.add(allocWithPrices);
					}
				}
			}
		}
		Collections.sort(setOfSolutions,new MarketPricesComparatorBySellerRevenue());
		//System.out.println(setOfSolutions);
		if(setOfSolutions.size()>0)
			return setOfSolutions.get(0);
		else
			return null;
	}
	
	
	protected MarketPrices solveLPWithReservePrices(int userIndex,int campaignIndex) throws IloException{
		int[][] newAllocation = this.copyInitialAllocationMatrix();
		//System.out.println("New allocation before");
		//Printer.printMatrix(newAllocation);
		Map<Integer,Double> reserves = new HashMap<Integer,Double>();
		for(int i=0;i<this.market.getNumberUsers();i++){
			//if(this.market.isConnected(i, campaignIndex) &&  this.userAllocatesToOtherCampaigns(i, campaignIndex)){
			if(this.market.isConnected(i, campaignIndex)){
				//System.out.println("Add reserve to user "+ i );
				if(!reserves.containsKey(i)){
					reserves.put(i, this.market.getCampaign(campaignIndex).getReward() / this.market.getCampaign(campaignIndex).getDemand());
				}
				newAllocation[i][campaignIndex] = 0;					
			}
		}
		//System.out.println("New allocation after");
		//Printer.printMatrix(newAllocation);
		//System.out.println("Reserves:");
		//System.out.println(reserves);
		EnvyFreePricesVectorLP EFP = new EnvyFreePricesVectorLP(new MarketAllocation(this.market,newAllocation),new IloCplex());
		EFP.createLP();
		for(Entry<Integer, Double> entry : reserves.entrySet()){
			//System.out.println("set reserve of user "+entry.getKey() + " to " + entry.getValue());
			EFP.setReservePriceForUser(entry.getKey(), entry.getValue());
		}
		EnvyFreePricesSolutionLP sol = EFP.Solve();
		if(sol.getStatus().equals("Optimal")){
			/*Check if we can reallocate campaign j*/
			double currentcost = 0.0;
			for(int i=0;i<this.market.getNumberUsers();i++){
				currentcost += sol.getPriceVectorComponent(i) * this.allocationMatrix[i][campaignIndex]; 
			}	
			if(currentcost <= this.market.getCampaign(campaignIndex).getReward()){
				//System.out.println("MAKES SENSE TO RE-ALLOCATE!!!");
				for(int i=0;i<this.market.getNumberUsers();i++){
					sol.getMarketAllocation().updateAllocationEntry(i, campaignIndex, this.allocationMatrix[i][campaignIndex]);
				}
			}
		}
		return new MarketPrices(sol.getMarketAllocation(),sol.getPriceVector());
	}
	
	protected boolean userAllocatesToOtherCampaigns(int userIndex,int campaignIndex){
		for(int j=0;j<this.market.getNumberCampaigns();j++){
			//if(j!=campaignIndex && this.allocationMatrix[userIndex][j]>0){
			if(this.allocationMatrix[userIndex][j]>0){
				return true;
			}
		}
		return false;
	}
	
	protected int[][] copyInitialAllocationMatrix(){
		int[][] newAllocation = new int[this.market.getNumberUsers()][this.market.getNumberCampaigns()];
		for(int i=0;i<this.market.getNumberUsers();i++){
			for(int j=0;j<this.market.getNumberCampaigns();j++){
				newAllocation[i][j] = this.allocationMatrix[i][j];
			}
		}
		return newAllocation;
	}
}
