package unitdemand;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import experiments.UnitDemandExperiments;
import algorithms.EnvyFreePricesSolutionLP;
import algorithms.EnvyFreePricesVectorLP;
import structures.Market;
import structures.MarketAllocation;
import util.Printer;

public class LPReservePrices extends EVPApproximation{
	
	protected int[][] maximumMatchingAllocation;
	protected MarketAllocation marketMaxMatchingAllocation;
	
	public LPReservePrices(Market market){
		super(UnitDemandExperiments.getValuationMatrixFromMarket(market));
        this.maximumMatchingAllocation = UnitDemandExperiments.getMaximumMatchingFromValuationMatrix(valuationMatrix);
        this.marketMaxMatchingAllocation = new MarketAllocation(market, maximumMatchingAllocation);
		
	}
	
	public Matching Solve(){
		//System.out.println("============ SOLVE ============");
		Double[] valuations = this.getEdgeValuations();
		double[] reservePrices = new double[this.valuationMatrix.length];
		//Printer.printVector(valuations);
		ArrayList<Matching> setOfMatchings = new ArrayList<Matching>();
		int numberInfeasible = 0;
		/* For each item, run MaxWEQ_r with reserve prices given by the valuation*/
		try {
			for(int i=0;i<this.valuationMatrix.length;i++){
				IloCplex iloObject = new IloCplex();
				Arrays.fill(reservePrices,valuations[i]);
				Printer.printVector(reservePrices);
				EnvyFreePricesSolutionLP VectorSol = new EnvyFreePricesVectorLP(this.marketMaxMatchingAllocation,reservePrices).Solve(iloObject);
				if(VectorSol.getStatus() == "Optimal"){
					setOfMatchings.add(new Matching(VectorSol.getPriceVector(),VectorSol.getMarketAllocation().getAllocation()));
				}else{
					//System.out.println("-----"+VectorSol.getStatus());
					numberInfeasible++;
				}
			}
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("*****LPReservePrices*******");
		Collections.sort(setOfMatchings,new MatchingComparatorBySellerRevenue());
		System.out.println(setOfMatchings);
		Printer.printMatrix(this.valuationMatrix);
		System.out.println("Prices for max revenue");
		Printer.printVector(setOfMatchings.get(0).getPrices());
		System.out.println("Matching for max revenue");
		Printer.printMatrix(setOfMatchings.get(0).getMatching());
		System.out.println("Max revenue = " + setOfMatchings.get(0).getSellerRevenue());
		System.out.println("\t" + numberInfeasible);
		return setOfMatchings.get(0);
	}

}
