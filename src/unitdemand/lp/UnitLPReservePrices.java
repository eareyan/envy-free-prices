package unitdemand.lp;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import structures.Market;
import structures.MarketAllocation;
import structures.MarketPrices;
import structures.comparators.MarketPricesComparatorBySellerRevenue;
import structures.factory.MarketAllocationFactory;
import unitdemand.Link;
import algorithms.EnvyFreePricesVectorLP;

public class UnitLPReservePrices {
	protected Market market;
	
	public UnitLPReservePrices(Market market){
		this.market = market;
	}

	public MarketPrices Solve() throws IloException{
		/* Compute MWM */
		double [][] valuationMatrix = MarketAllocationFactory.getValuationMatrixFromMarket(this.market);
		//Printer.printMatrix(valuationMatrix);
		ArrayList<Link> valuations = Link.getEdgeValuations(valuationMatrix);
		valuations.add(new Link(-1,0.0)); // Add valuation of zero
		double[] reservePrices = new double[this.market.getNumberUsers()];
		ArrayList<MarketPrices> setOfSolutions = new ArrayList<MarketPrices>();
		for(Link valueLink: valuations){//For each link
			IloCplex iloObject = new IloCplex();
			//System.out.println("================= reserve price from campaign ("+valueLink.getJ()+") = " + valueLink.getValue() + "++++++++++");
			//Printer.printMatrix(MarketAllocationFactory.getAllocationWithReservePrices(valuationMatrix, valueLink.getValue()));
			/* Get the new allocation */
			MarketAllocation marketAlloc = new MarketAllocation(this.market,MarketAllocationFactory.getAllocationWithReservePrices(valuationMatrix, valueLink.getValue()));
			/* Feed new allocation with initial market to LP and set reserve prices. */			
			EnvyFreePricesVectorLP LP = new EnvyFreePricesVectorLP(marketAlloc,iloObject);
			LP.setWalrasianConditions(false);
			LP.createLP();
			Arrays.fill(reservePrices, valueLink.getValue());
			LP.setReservePrices(reservePrices);
			setOfSolutions.add(LP.Solve());
		}
		/* For debugging purposes only: */ 
		//System.out.println(setOfSolutions);
		/*for(MarketPrices sol:setOfSolutions){
			System.out.println("Solution-->");
			Printer.printMatrix(sol.getMarketAllocation().getAllocation());
			Printer.printVector(sol.getPriceVector());
			System.out.println(sol.sellerRevenuePriceVector());
		}*/
		Collections.sort(setOfSolutions,new MarketPricesComparatorBySellerRevenue());
		return setOfSolutions.get(0);
	}
}
