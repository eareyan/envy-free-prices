package algorithms;

import java.util.ArrayList;
import java.util.Collections;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketPrices;
import util.Printer;

public class GeneralApproximation1 extends GeneralApproximation{
	public GeneralApproximation1(Market market, boolean efficient){
		super(market,efficient);
	}
	public GeneralApproximation1(MarketAllocation marketAllocation){
		super(marketAllocation);
	}
	public MarketPrices Solve() throws IloException{
		/* Get a list with the users that were allocated */
		ArrayList<Integer> usersAllocated = this.getAllocatedUsers();
		/* For each Allocated User, compute WaterfallMAXWEQ with reserve prices*/
		//Printer.printMatrix(this.allocationMatrix);
		ArrayList<EnvyFreePricesSolutionLP> setOfSolutions = new ArrayList<EnvyFreePricesSolutionLP>();
		EnvyFreePricesVectorLPReservePrices EFPrices;
		for(int i=0;i<usersAllocated.size();i++){		
			for(int j=0;j<this.market.getNumberCampaigns();j++){
				if(this.allocationMatrix[usersAllocated.get(i)][j]>0){
					EFPrices = new EnvyFreePricesVectorLPReservePrices(this.marketAllocation,new IloCplex());
					EFPrices.createLP(usersAllocated.get(i),j);
					setOfSolutions.add(EFPrices.Solve());
				}
			}
		}
		Collections.sort(setOfSolutions,new EnvyFreePricesSolutionLPComparatorBySellerRevenue());
		//System.out.println(setOfSolutions);
		return setOfSolutions.get(0);
	}
}
