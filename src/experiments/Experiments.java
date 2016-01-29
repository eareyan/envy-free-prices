package experiments;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import algorithms.EfficientAllocationLP;
//import algorithms.EnvyFreePricesMatrixLP;
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
	
	public static void main(String[] args) throws InterruptedException{
		
		int numUsers = 26 , startUsers = 15;
		int numCampaigns = 26;
		int numTrials = 100;
				
		for(int i=startUsers;i<numUsers;i++){
			for(int j=1;j<numCampaigns;j++){
				for(int p=0;p<4;p++){
					double prob = 0.25 + p*(0.25);
					System.out.println(" n = " + i + ", m = " + j + ", prob = " + prob);
					DescriptiveStatistics statRatioEfficiency = new DescriptiveStatistics();
					DescriptiveStatistics statEffAllocValue = new DescriptiveStatistics();
					DescriptiveStatistics statEfficientAlocationRevenueVector = new DescriptiveStatistics();
					DescriptiveStatistics statWfAllocationValue = new DescriptiveStatistics();
					DescriptiveStatistics statWfAllocationRevenue = new DescriptiveStatistics();
					for(int t=0;t<numTrials;t++){
						/* Create a random Market*/
						Market randomMarket = MarketFactory.randomMarket(i, j, prob);
						//System.out.println(randomMarket);
						/* Find the efficient allocation*/
						int[][] efficientAllocation = new EfficientAllocationLP(randomMarket).Solve().get(0);
						MarketAllocation randomMarketEfficientAllocation = new MarketAllocation(randomMarket, efficientAllocation);
						double effAllocValue = randomMarketEfficientAllocation.value();
						statEffAllocValue.addValue(effAllocValue);
						//System.out.println("Efficient " + randomMarketEfficientAllocation.stringAllocationMatrix());
						/* Run LP Programs*/
						EnvyFreePricesSolutionLP VectorSolEfficientAllocation = new EnvyFreePricesVectorLP(randomMarketEfficientAllocation).Solve();
						statEfficientAlocationRevenueVector.addValue(VectorSolEfficientAllocation.sellerRevenuePriceVector());
						/*System.out.println("Prices from efficient allocation");
						VectorSolEfficientAllocation.printPricesVector();*/
						/* Run Waterfall*/
						WaterfallPrices waterFallAllocationPrices = new Waterfall(randomMarket).Solve();
						//System.out.println("Waterfall " + waterFallAllocationPrices.getMarketAllocation().stringAllocationMatrix());
						EnvyFreePricesSolutionLP VectorSolWaterfallAllocation = new EnvyFreePricesVectorLP(waterFallAllocationPrices.getMarketAllocation()).Solve();
						/*System.out.println("Prices from waterfall");
						VectorSolWaterfallAllocation.printPricesVector();*/
						double wfAllocValue = VectorSolWaterfallAllocation.getMarketAllocation().value();
						statWfAllocationValue.addValue(wfAllocValue);
						statWfAllocationRevenue.addValue(VectorSolWaterfallAllocation.sellerRevenuePriceVector());
						if(effAllocValue == 0.0 && wfAllocValue == 0.0){
							statRatioEfficiency.addValue(1.0);
						}else{
							statRatioEfficiency.addValue(wfAllocValue/effAllocValue);
						}
					}
					try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/gpfs/main/home/eareyanv/workspace/envy-free-prices/results/results.csv", true)))) {
					    out.println(i + "," + j + "," + prob + "," + statRatioEfficiency.getMean() + "," + statRatioEfficiency.getStandardDeviation() + "," + statEfficientAlocationRevenueVector.getMean() + "," + statWfAllocationRevenue.getMean());
					}catch (IOException e) {
					    //exception handling left as an exercise for the reader
					}
					//System.exit(-1); /* stop execution... for debugging purposes */
				}
			}
		}
	}
}
