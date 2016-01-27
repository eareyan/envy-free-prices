package experiments;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import algorithms.EfficientAllocationLP;
import algorithms.EnvyFreePricesMatrixLP;
import algorithms.EnvyFreePricesSolutionLP;
import algorithms.EnvyFreePricesVectorLP;
import algorithms.Waterfall;
import algorithms.WaterfallPrices;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketFactory;

/*
 * This class implements experiments between Waterfall and LP.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Experiments {
	
	public static void main(String[] args){
		
		int numUsers = 20;
		int numCampaigns = 20;
		int numTrials = 100;
		
		for(int i=18;i<numUsers;i++){
			for(int j=17;j<numCampaigns;j++){
				for(int p=0;p<4;p++){
					double WFallocationValue = 0.0, EfficientallocationValue = 0.0, WFsellerRevenue = 0.0, LPsellerRevenueMatrix = 0.0, LPsellerRevenueVector = 0.0, WFerror = 0.0, WFMaxError = 0.0;
					double prob = 0.25 + p*(0.25);
					System.out.println(" n = " + i + ", m = " + j + ", prob = " + prob);
					for(int t=0;t<numTrials;t++){
						/* Create a random Market*/
						Market randomMarket = MarketFactory.randomMarket(i, j, prob);
						//System.out.println(randomMarket);
						/* Find the efficient allocation*/
						int[][] efficientAllocation = new EfficientAllocationLP(randomMarket).Solve().get(0);
						MarketAllocation randomMarketEfficientAllocation = new MarketAllocation(randomMarket, efficientAllocation);
						EfficientallocationValue += randomMarketEfficientAllocation.value();
						/* Run LP Programs*/
						EnvyFreePricesSolutionLP MatrixSol = new EnvyFreePricesMatrixLP(randomMarketEfficientAllocation).Solve();
						LPsellerRevenueMatrix += MatrixSol.sellerRevenuePriceMatrix();
						EnvyFreePricesSolutionLP VectorSol = new EnvyFreePricesVectorLP(randomMarketEfficientAllocation).Solve();
						LPsellerRevenueVector += VectorSol.sellerRevenuePriceVector();
						/* Run Waterfall*/
						WaterfallPrices waterFallAllocationPrices = new Waterfall(randomMarket).Solve();
						WFsellerRevenue += waterFallAllocationPrices.sellerRevenuePriceMatrix();
						//System.out.println(+ "\t" + MatrixSol.sellerRevenuePriceMatrix());
						WFallocationValue += waterFallAllocationPrices.getMarketAllocation().value();
						double violations = waterFallAllocationPrices.computeViolations();
						WFerror += violations;
						if(violations > WFMaxError) WFMaxError = violations;
					}
					System.out.println(((WFallocationValue / numTrials) / (EfficientallocationValue / numTrials)) + "\t" + (WFsellerRevenue / numTrials) + "\t" + (LPsellerRevenueMatrix / numTrials) + "\t" + (LPsellerRevenueVector / numTrials) + "\t" + (WFerror/numTrials) + "\t" + WFMaxError);
					try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/gpfs/main/home/eareyanv/workspace/envy-free-prices/results/results.csv", true)))) {
					    out.println(i + "," + j + "," + prob + "," + ((WFallocationValue / numTrials) / (EfficientallocationValue / numTrials)) + "," + (WFsellerRevenue / numTrials) + "," + (LPsellerRevenueMatrix / numTrials) + "," + (LPsellerRevenueVector / numTrials) + "," + (WFerror/numTrials) + "," + WFMaxError);
					}catch (IOException e) {
					    //exception handling left as an exercise for the reader
					}					
				}
			}
		}
	}
}
