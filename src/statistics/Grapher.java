package statistics;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import structures.Market;
import structures.MarketAllocation;
import structures.factory.RandomMarketFactory;
import unitdemand.Matching;
import unitdemand.MaxWEQ;
import util.Printer;
import algorithms.EnvyFreePricesSolutionLP;
import algorithms.EnvyFreePricesVectorLP;
import algorithms.allocations.EfficientAllocationILP;

public class Grapher {
	
	/*
	 * Some Testing function....
	 */
	public static void main2(String args[]){
		double[][] X = RandomMarketFactory.getValuationMatrix(4, 4, 1.0);
		Printer.printMatrix(X);
		double reserve = 0.5;
		double[][] XReserve = RandomMarketFactory.getValuationReserve(X, reserve);
		System.out.println("---");
		Printer.printMatrix(XReserve);
		MaxWEQ maxWEQAlgo = new MaxWEQ(XReserve);
		Matching matchingWithReserve = maxWEQAlgo.Solve();
		System.out.println("---");
		Printer.printMatrix(matchingWithReserve.getMatching());
		System.out.println("---");
		Printer.printVector(matchingWithReserve.getPrices());
		double[] newPrices = new double[matchingWithReserve.getPrices().length]; 
		for(int k=0;k<matchingWithReserve.getPrices().length;k++){
			newPrices[k] = matchingWithReserve.getPrices()[k] + reserve;
		}
		System.out.println("---");		
		Matching newMatching = new Matching(X,matchingWithReserve.getMatching(),newPrices);
		Printer.printVector(newMatching.getPrices());		
		System.out.println("sellerRevenue w/o reserve= " + matchingWithReserve.getSellerRevenue());
		System.out.println("sellerRevenue w reserve= " + newMatching.getSellerRevenue());
		System.out.println("There are "+newMatching.numberOfEnvyCampaigns()+ " envy campaigns");
		
	}
	/*
	 * Function for plotting unit demand graphs.
	 * A graph here means a plot of the seller revenue as a function of the reserve price.
	 * Reserve prices range from 0 to 10, since we know that at 10 no bidder can afford an item.
	 * The procedure is the following:
	 * 		1) Draw a random valuation matrix
	 * 		2) "Shift" this matrix by a reserve price r (see RandomMarketFactory.getValuationReserve)
	 * 		3) Run MaxWeq on the shifted matrix
	 * 		4) Increment all prices obtained by MaxWEQ by the reserve
	 * 		5) Compute the seller revenue from the matching obtained by MaxWEQ on the reserve matrix and the updated prices
	 * Repeat this process many times to get an estimate for the mean seller revenue.
	 */
	public static void main1(String args[]) throws IOException{
		System.out.println("Unit-demand testing");
		/*
		 * Basic Parameters received by command line
		 */
		int numberOfPoints = 401;		//We know we need exactly 401 points until we are sure to get 0 seller revenue.
		int numberOfSamples = 1000;
		int numUsers = Integer.parseInt(args[0]);
		int numCampa = Integer.parseInt(args[1]);
		double p = Double.parseDouble(args[2]);
		/* Start at reserve 0.0 */
		double reserve = 0.0;
		FileWriter fw = new FileWriter("/home/eareyanv/workspace/graphs/unitdemand-"+numberOfPoints+"-"+numberOfSamples+"-"+numUsers+"-"+numCampa+"-"+p+".csv", true);
		BufferedWriter bw = new BufferedWriter(fw);
	    @SuppressWarnings("resource")
		PrintWriter out = new PrintWriter(bw);
	    /* For each number of points we want */
		for(int i=0;i<numberOfPoints;i++){
			double sellerRevenue = 0.0;
			DescriptiveStatistics revenue = new DescriptiveStatistics();
			/* Obtained as many samples as we want */
			for(int j=0;j<numberOfSamples;j++){
				/* generate a random market */
				double[][] X = RandomMarketFactory.getValuationMatrix(numUsers, numCampa, p);
				/* compute the value of the optimal allocation*/
				double valueOptAllocaction = Matching.computeMaximumWeightMatchingValue(X).getValueOfMatching();
				/* compute the allocation that respects reserve price*/
				double[][] XReserve = RandomMarketFactory.getValuationReserve(X, reserve);
				/* Run MaxWEQ on the allocation that respect reserve*/
				MaxWEQ maxWEQAlgo = new MaxWEQ(XReserve);
				Matching matchingWithReserve = maxWEQAlgo.Solve();
				/* Update the prices to reflect the true prices */
				double[] newPrices = new double[matchingWithReserve.getPrices().length]; 
				for(int k=0;k<matchingWithReserve.getPrices().length;k++){
					newPrices[k] = matchingWithReserve.getPrices()[k] + reserve;
				}
				/* Create a new matching with the original valuation matrix, the matching and new prices*/
				Matching newMatching = new Matching(X,matchingWithReserve.getMatching(),newPrices);
				sellerRevenue = newMatching.getSellerRevenue();
				if(valueOptAllocaction == 0){
					revenue.addValue(0.0);
				}else{
					//revenue.addValue((double) sellerRevenue / valueOptAllocaction);
					revenue.addValue((double) sellerRevenue);
				}
			}
			/* Report average */
			System.out.println("(r,s) = (" + reserve +"," + revenue.getMean() + ")");
			out.println(reserve +"," + revenue.getMean());
			out.flush();
			reserve += 0.025;
		}
	}
	/*
	 * Function for plotting multi-minded demand graphs.
	 * A graph here means a plot of the seller revenue as a function of the reserve price.
	 * Reserve prices range from 0 to 10, since we know that at 10 no bidder can afford an item.
	 * The procedure is the following:
	 * 		1) Draw a random market (either over or underdemanded, with some coefficient b)
	 * 		2) Compute the allocation that respect reserve price r
	 * 		3) Get restricted envy-free prices for that reserve price.
	 * 		4) Compute the seller revenue as usual (allocation times prices)
	 * Repeat this process many times to get an estimate for the mean seller revenue.
	 */	
	public static void main(String args[]) throws Exception{		
		/*
		 * Basic Parameters received by command line
		 */
		int numberOfPoints = Integer.parseInt(args[0]);
		int numberOfSamples = Integer.parseInt(args[1]);
		int numUsers = Integer.parseInt(args[2]);
		int numCampa = Integer.parseInt(args[3]);
		double p = Double.parseDouble(args[4]);
		int overunderdemand = Integer.parseInt(args[5]);
		int b = Integer.parseInt(args[6]);
		/* We start at an arbitrary seller revenue, in case we have to restart the script */
		double reserve = Double.parseDouble(args[7]);
		/* Determine if we want an over or under demanded market */
		if(overunderdemand != 0 && overunderdemand!=1){
			throw new Exception("The sixth parameter must be 0 or 1, indicating either an underdemanded or an overdemanded market.");
		}
		FileWriter fw = new FileWriter("/home/eareyanv/workspace/graphs/multiminded-"+numberOfPoints+"-"+numberOfSamples+"-"+numUsers+"-"+numCampa+"-"+p+"-"+overunderdemand+"-"+b+".csv", true);
	    BufferedWriter bw = new BufferedWriter(fw);
	    @SuppressWarnings("resource")
		PrintWriter out = new PrintWriter(bw);
		for(int i=0;i<numberOfPoints;i++){
			double sellerRevenue = 0.0;
			double[] reservePrices = new double[numUsers];
			DescriptiveStatistics revenue = new DescriptiveStatistics();
			for(int j=0;j<numberOfSamples;j++){
				/* Sample a Market */
				Market M;
				/* Determine if it is an underdemanded or overdemanded market and produce the market accordingly. */
				if(overunderdemand == 0){
					M = RandomMarketFactory.generateOverSuppliedMarket(numUsers, numCampa, p, b);
				}else{
					M = RandomMarketFactory.generateOverDemandedMarket(numUsers, numCampa, p, b);
				}
				/* Compute the efficient allocation */
				MarketAllocation efficient = new MarketAllocation(M,new EfficientAllocationILP(M).Solve().get(0));
				double valueOptAllocaction = efficient.value();		

				/* Compute allocation that respects reserve price */
				MarketAllocation allocRespectReserve = new MarketAllocation(M,new EfficientAllocationILP(M,reserve).Solve().get(0));

				/* Compute envy-free prices */
				EnvyFreePricesVectorLP efp = new EnvyFreePricesVectorLP(allocRespectReserve);
				efp.setWalrasianConditions(false);
				efp.createLP();
				Arrays.fill(reservePrices, reserve);
				efp.setReservePrices(reservePrices);
				EnvyFreePricesSolutionLP sol = efp.Solve();
				if(sol.getStatus().equals("Infeasible")){
					sellerRevenue = 0.0;
				}else{
					sellerRevenue = sol.sellerRevenuePriceVector();
				}
				revenue.addValue((double) sellerRevenue / valueOptAllocaction);
			}
			out.println(reserve +"," + revenue.getMean());
			out.flush();
			//System.out.println("(r,s) = (" + reserve +"," + revenue.getMean() + ")");
			reserve += 0.025;
		}
	}
}
