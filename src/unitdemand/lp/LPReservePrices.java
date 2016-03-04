package unitdemand.lp;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import algorithms.EnvyFreePricesSolutionLP;
import algorithms.EnvyFreePricesVectorLP;
import experiments.UnitDemandExperiments;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketPrices;
import structures.MarketPricesComparatorBySellerRevenue;
import unitdemand.Link;
import unitdemand.Matching;
import util.Printer;

public class LPReservePrices {
	protected Market market;
	
	public LPReservePrices(Market market){
		this.market = market;
	}

	public MarketPrices Solve() throws IloException{
		/* Compute MWM */
		double [][] valuationMatrix = UnitDemandExperiments.getValuationMatrixFromMarket(this.market);
		//Printer.printMatrix(valuationMatrix);
		ArrayList<Link> valuations = Link.getEdgeValuations(valuationMatrix);
		valuations.add(new Link(-1,0.0)); // Add valuation of zero
		double[] reservePrices = new double[this.market.getNumberUsers()];
		ArrayList<MarketPrices> setOfSolutions = new ArrayList<MarketPrices>();
		for(Link valueLink: valuations){//For each link
			IloCplex iloObject = new IloCplex();
			//System.out.println("================= reserve price from campaign ("+valueLink.getJ()+") = " + valueLink.getValue() + "++++++++++");
			//Get the new allocation
			//Printer.printMatrix(this.computeNewAllocation(valuationMatrix, valueLink.getValue()));
			MarketAllocation marketAlloc = new MarketAllocation(this.market,this.computeNewAllocation(valuationMatrix, valueLink.getValue()));
			//Feed new allocation with initial market to LP and set reserve prices.			
			EnvyFreePricesVectorLP LP = new EnvyFreePricesVectorLP(marketAlloc,iloObject,true);
			Arrays.fill(reservePrices, valueLink.getValue());
			LP.setReservePrices(reservePrices);
			setOfSolutions.add(LP.Solve());
		}
		Collections.sort(setOfSolutions,new MarketPricesComparatorBySellerRevenue());
		/* For debugging purposes only: */ 
		/*System.out.println(setOfSolutions);
		for(MarketPrices sol:setOfSolutions){
			System.out.println("Solution-->");
			Printer.printMatrix(sol.getMarketAllocation().getAllocation());
			Printer.printVector(sol.getPriceVector());
			System.out.println(sol.sellerRevenuePriceVector());
		}*/
		return setOfSolutions.get(0);
	}
	/*
	 * This method first computes a new valuation matrix by subtracting the reserve values
	 * and setting negative values to negative infinity.
	 * Then, get an allocation for the new valuation matrix by solving for the MWM with new valuation matrix	
	 */
	protected int[][] computeNewAllocation(double[][] valuationMatrix, double reserve){
		double[][] valuationMatrixWithReserve = new double[valuationMatrix.length][valuationMatrix[0].length];
		for(int i=0;i<valuationMatrix.length;i++){
			for(int j=0;j<valuationMatrix[0].length;j++){
				valuationMatrixWithReserve[i][j] = (valuationMatrix[i][j] - reserve < 0) ? Double.NEGATIVE_INFINITY : (valuationMatrix[i][j] - reserve);
			}
		}
		//Printer.printMatrix(valuationMatrixWithReserve);
		return Matching.computeMaximumWeightMatchingValue(valuationMatrixWithReserve).getMatching();
	}
}
