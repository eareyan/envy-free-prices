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
import structures.factory.UnitMarketFactory;
import unitdemand.Matching;
import unitdemand.MaxWEQ;
import util.Printer;
import algorithms.pricing.EnvyFreePricesSolutionLP;
import algorithms.pricing.EnvyFreePricesVectorLP;
import allocations.optimal.SingleStepEfficientAllocationILP;

public class Grapher {
	
	public static void probabilityItemAllocated(){
		
	}
	
	
	public static void gradientAscent(){
		
		System.out.println("gradientAscent");
		double reserve = 0.0;
		double gamma = 0.01;
		int numberOfSamples = 1000;
		int numberOfGradientIterations = 1000 , currentGradientIteration = 0;
		while(currentGradientIteration < numberOfGradientIterations){
			double sum = 0.0;
			for(int k=0;k<numberOfSamples;k++){
				double u11 = Math.random();
				double u12 = Math.random();
				double u21 = Math.random();
				double u22 = Math.random();
				if(((u11*((u11 - reserve >= 0) ? 1 : 0)) + (u22*((u22 - reserve >= 0) ? 1 : 0))) > ((u12*((u12 - reserve >= 0) ? 1 : 0)) + (u21*((u21 - reserve >= 0) ? 1 : 0)))){
					//System.out.println("\t identity");
					/* Identity permutation wins*/
					sum += (u11*((u11 - reserve >= 0) ? 1 : 0));// + (u22*((u22 - reserve >= 0) ? 1 : 0));
				}else{
					//System.out.println("\t transposition");
					/* Transposition wins*/
					sum += (u12*((u12 - reserve >= 0) ? 1 : 0));// + (u21*((u21 - reserve >= 0) ? 1 : 0)); 
				}
			}
			System.out.println("\t\t\t" + (sum / numberOfSamples));
			//reserve = reserve + gamma * ( (1.0-(sum / numberOfSamples)) );
			 /*
			  * The problem here is that I am treating the delta function as an indicator...
			  * */
			reserve = reserve + gamma * ( 1.0 - (sum / numberOfSamples) );
			currentGradientIteration++;
			System.out.println(currentGradientIteration + " = " + reserve);
		}

		/*int samples = 1000;
		double sum = 0.0;
		reserve = 0.0;
		for(int k=0;k<samples;k++){
			double u11 = Math.random();
			double u12 = Math.random();
			double u21 = Math.random();
			double u22 = Math.random();
			sum +=  Math.max((u11*((u11 - reserve >= 0) ? 1 : 0)) + (u22*((u22 - reserve >= 0) ? 1 : 0)) , (u12*((u12 - reserve >= 0) ? 1 : 0)) + (u21*((u21 - reserve >= 0) ? 1 : 0)));
		}
		System.out.println("Estimate = " + sum /(double) samples);*/
	}
	
	
	static double constant = 1.0;
	static int numberPoints = 10000;
	
	public static void main6(String args[]){
		double acum = 0.0;
		for(int k=0;k<numberPoints;k++){
			double u11 = constant*Math.random();
			double u12 = constant*Math.random();
			double u13 = constant*Math.random();
			double u21 = constant*Math.random();
			double u22 = constant*Math.random();
			double u23 = constant*Math.random();
			double u31 = constant*Math.random();
			double u32 = constant*Math.random();
			double u33 = constant*Math.random();
			double v1 = Math.max(u11+u22+u33, u12+u21+u33);
			double v2 = Math.max(u12+u23+u32, u13+u22+u31);
			double v3 = Math.max(u13+u21+u32, u11+u23+u32);
			acum += Math.max(Math.max(v1,v2),v3);
		}
		System.out.println("Acum = " + acum / numberPoints);
	}

	public static void main(String args[]){
		//Grapher.gradientAscent();
		//System.exit(0);
		System.out.println("Grapher");
		//DescriptiveStatistics revenue = new DescriptiveStatistics();
		
		double welfare = 0.0, expectedSellerRevenue = 0.0;
		double expectedW = 0.0, expectedW_i = 0.0;
		double reserve = 0.0;
		for(int k=0;k<numberPoints;k++){
			/* Draw uniform i.i.d valuations for a 3x3 market. */
			//double u11 = Math.random();
			//double u12 = Math.random();
			//double u13 = Math.random();
			//double u21 = Math.random();
			//double u22 = Math.random();
			//double u23 = Math.random();
			//double u31 = Math.random();
			//double u32 = Math.random();
			//double u33 = Math.random();
			//double v1 = Math.max(u11+u22+u33, u12+u21+u33);
			//double v2 = Math.max(u12+u23+u31, u13+u22+u31);
			//double v3 = Math.max(u13+u21+u32, u11+u23+u32);
			//expectedW += Math.max(Math.max(v1,v2),v3);
			
			double u11 = (Math.random() <= 0.5) ? 1 : 2;
			double u12 = (Math.random() <= 0.5) ? 1 : 2;
			double u21 = (Math.random() <= 0.5) ? 1 : 2;
			double u22 = (Math.random() <= 0.5) ? 1 : 2;
			
			expectedW += Math.max( u11 * ((u11 - reserve >= 0) ? 1 : 0) + u22 * ((u22 - reserve >= 0) ? 1 : 0) , u12 * ((u12 - reserve >= 0) ? 1 : 0) + u21 * ((u21 - reserve >= 0) ? 1 : 0)); 
			/* Set the valuation matrix. */
			//double[][] X = {{u11,u12,u13},{u21,u22,u23},{u31,u32,u33}};
			double[][] X = {{u11,u12} , {u21,u22}};
			/* Compute using MaxWEQ. */
			MaxWEQ maxWEQAlgo = new MaxWEQ(X);
			Matching matching = maxWEQAlgo.Solve();
			welfare += matching.getValueOfMatching();
			expectedSellerRevenue += matching.getSellerRevenue();
			
			/* Compute which permutation won: */
			if(u11 * ((u11 - reserve >= 0) ? 1 : 0) + u22 * ((u22 - reserve >= 0) ? 1 : 0) >= u12 * ((u12 - reserve >= 0) ? 1 : 0) + u21 * ((u21 - reserve >= 0) ? 1 : 0)){
				/* Identity permutation wins */
				expectedW_i += u11 * ((u11 - reserve >= 0) ? 1 : 0);
			}else{
				/* Transposition wins*/
				expectedW_i += u12 * ((u12 - reserve >= 0) ? 1 : 0) ;
			}
		}
		System.out.println("TOTAL Welfare \t = " + welfare / numberPoints);
		System.out.println("Acum \t = " + expectedW / numberPoints);
		
		System.out.println("expectedW_i \t = " + 2.0 * expectedW_i * 0.0625);
		System.out.println("expectedSellerRevenue \t = " + expectedSellerRevenue / numberPoints);
		
	}
	public static void main2(String args[]){
		double[][] X = UnitMarketFactory.getValuationMatrix(4, 4, 1.0);
		Printer.printMatrix(X);
		double reserve = 0.5;
		double[][] XReserve = UnitMarketFactory.getValuationReserve(X, reserve);
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
	
	public static void main777(String args[]){
		
		double L = 0.0;
		double R = 10.0;
		double M = (R - L) / 10.0;
		
		int k = 0;
		while(k < 100 && R-L > 0){
			System.out.println(L + "," + M + "," + R);
			System.out.println("\t" + (R-L));
			//if(parabola(M)> parabola(L)){
			if(expectedRevenue(M)> expectedRevenue(L)){
				L = M;
				M = M + (R - M) / 10.0;
			}else{
				R = M;
				M = L + (M - L) / 10.0;
			}
			k++;
		}
	}
	
	public static double expectedRevenue(double reserve){
		int numberOfSamples = 1000;
		double sellerRevenue = 0.0;
		DescriptiveStatistics revenue = new DescriptiveStatistics();
		/* Obtained as many samples as we want */
		for(int j=0;j<numberOfSamples;j++){
			/* generate a random market */
			double[][] X = UnitMarketFactory.getValuationMatrix(10, 10, 0.75);
			/* compute the value of the optimal allocation*/
			double valueOptAllocaction = Matching.computeMaximumWeightMatchingValue(X).getValueOfMatching();
			/* compute the allocation that respects reserve price*/
			double[][] XReserve = UnitMarketFactory.getValuationReserve(X, reserve);
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
		return revenue.getMean();
	}
	
	public static double parabola(double x){
		return -1.0*x*(x-20.0);
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
	public static void main55(String args[]) throws IOException{
		System.out.println("Unit-demand testing");
		/*
		 * Basic Parameters received by command line
		 */
		int numberOfPoints = 401;		//We know we need exactly 401 points until we are sure to get 0 seller revenue.
		int numberOfSamples = 500;
		int numUsers = 10;//Integer.parseInt(args[0]);
		int numCampa = 10;//Integer.parseInt(args[1]);
		double p = 0.25;//Double.parseDouble(args[2]);
		/* Start at reserve 0.0 */
		double reserve = 0.0;
		//FileWriter fw = new FileWriter("/home/eareyanv/workspace/graphs/unitdemand-"+numberOfPoints+"-"+numberOfSamples+"-"+numUsers+"-"+numCampa+"-"+p+".csv", true);
		//BufferedWriter bw = new BufferedWriter(fw);
	    //@SuppressWarnings("resource")
		//PrintWriter out = new PrintWriter(bw);
	    /* For each number of points we want */
		for(int i=0;i<numberOfPoints;i++){
			double sellerRevenue = 0.0;
			DescriptiveStatistics revenue = new DescriptiveStatistics();
			/* Obtained as many samples as we want */
			for(int j=0;j<numberOfSamples;j++){
				/* generate a random market */
				double[][] X = UnitMarketFactory.getValuationMatrix(numUsers, numCampa, p);
				/* compute the value of the optimal allocation*/
				double valueOptAllocaction = Matching.computeMaximumWeightMatchingValue(X).getValueOfMatching();
				/* compute the allocation that respects reserve price*/
				double[][] XReserve = UnitMarketFactory.getValuationReserve(X, reserve);
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
			//out.println(reserve +"," + revenue.getMean());
			//out.flush();
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
	public static void main3(String args[]) throws Exception{		
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
				MarketAllocation efficient = new SingleStepEfficientAllocationILP().Solve(M);
				double valueOptAllocaction = efficient.value();		

				/* Compute allocation that respects reserve price */
				M.setReserveAllCampaigns(reserve);
				MarketAllocation allocRespectReserve = new SingleStepEfficientAllocationILP().Solve(M);

				/* Compute envy-free prices */
				EnvyFreePricesVectorLP efp = new EnvyFreePricesVectorLP(allocRespectReserve);
				efp.setMarketClearanceConditions(false);
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
