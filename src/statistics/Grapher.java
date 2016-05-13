package statistics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import algorithms.EnvyFreePricesSolutionLP;
import algorithms.EnvyFreePricesVectorLP;
import algorithms.allocations.EfficientAllocationILP;
import structures.Market;
import structures.MarketAllocation;
import structures.factory.RandomMarketFactory;
import util.Printer;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.math.plot.*;

public class Grapher {
	
	public static void main(String args[]) throws IloException, IOException{
		//Market M = RandomMarketFactory.randomMarket(30, 30, 0.25);
		//Market M = RandomMarketFactory.generateOverSuppliedMarket(40, 40, 0.25, 4);
		//Market M = RandomMarketFactory.generateOverDemandedMarket(30, 30, 0.5, 4);
		//System.out.println(M);
		
		/*
		 * Parameters received by commandline.
		 */
		int numberOfPoints = Integer.parseInt(args[0]);
		int numberOfSamples = Integer.parseInt(args[1]);
		int numUsers = Integer.parseInt(args[2]);
		int numCampa = Integer.parseInt(args[3]);
		double p = Double.parseDouble(args[4]);
		int b = Integer.parseInt(args[5]);
		
		//Initial variables
		double reserve = 0.0;
		//double[] x = new double[numberOfPoints];
		//double[] y = new double[numberOfPoints];
		FileWriter writer = new FileWriter("/home/eareyanv/workspace/graphs/data-"+numUsers+"-"+numCampa+"-"+p+"-"+b+"-"+numberOfPoints+"-"+numberOfSamples+"-.csv");
		
		for(int i=0;i<numberOfPoints;i++){
			double sellerRevenue = 0.0;
			double[] reservePrices = new double[numUsers];
			DescriptiveStatistics revenue = new DescriptiveStatistics();
			for(int j=0;j<numberOfSamples;j++){
				/* Sample a Market */
				Market M = RandomMarketFactory.generateOverSuppliedMarket(numUsers, numCampa, p, b);
				MarketAllocation efficient = new MarketAllocation(M,new EfficientAllocationILP(M).Solve(new IloCplex()).get(0));
				double valueOptAllocaction = efficient.value();		

				/* Compute allocation that respects reserve price */
				MarketAllocation allocRespectReserve = new MarketAllocation(M,new EfficientAllocationILP(M,reserve).Solve(new IloCplex()).get(0));

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
			//System.out.println("(r,s) = (" + reserve +"," + revenue.getMean() + ")");
			//x[i] = reserve;
			//y[i] = revenue.getMean();
			writer.append(reserve +"," + revenue.getMean() + "\n");
			reserve += 0.025;
		}
		writer.flush();
		writer.close();
		/*Plot2DPanel plot = new Plot2DPanel();
		plot.addLinePlot("my plot", x, y);
		JFrame frame = new JFrame("a plot panel");
		frame.setSize(1000, 1000);
		frame.setContentPane(plot);
		frame.setVisible(true);
		
		final BufferedImage image = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics gr = image.getGraphics();
		frame.printAll(gr);
		gr.dispose();
		ImageIO.write(image, "PNG", new File("/home/eareyanv/workspace/graphs/WindowCapture.png"));*/
	}

}
