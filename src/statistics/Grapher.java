package statistics;

import java.util.Arrays;

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

import org.math.plot.*;

public class Grapher {
	
	public static void main(String args[]) throws IloException{
		Market M = RandomMarketFactory.randomMarket(30, 30, 0.25);
		//Market M = RandomMarketFactory.generateOverSuppliedMarket(30, 30, 0.25, 4);
		//Market M = RandomMarketFactory.generateOverDemandedMarket(30, 30, 0.5, 4);
		System.out.println(M);
		
		
		int numberOfPoints = 400;
		double reserve = 0.0;
		double[] reservePrices = new double[M.getNumberUsers()];
		double sellerRevenue = 0.0;
		
		double[] x = new double[numberOfPoints];
		double[] y = new double[numberOfPoints];
		
		for(int i=0;i<numberOfPoints;i++){
			//System.out.println("Reserve price r = " + reserve);
			/* Compute allocation that respects reserve price */
			MarketAllocation allocRespectReserve = new MarketAllocation(M,new EfficientAllocationILP(M,reserve).Solve(new IloCplex()).get(0));
			//Printer.printMatrix(allocRespectReserve.getAllocation());
		
			/* Compute envy-free prices */
			EnvyFreePricesVectorLP efp = new EnvyFreePricesVectorLP(allocRespectReserve);
			efp.setWalrasianConditions(false);
			efp.createLP();
			Arrays.fill(reservePrices, reserve);
			efp.setReservePrices(reservePrices);
			EnvyFreePricesSolutionLP sol = efp.Solve();
			//System.out.println("Solution status = " + sol.getStatus());
			if(sol.getStatus().equals("Infeasible")){
				sellerRevenue = 0.0;
			}else{
				sellerRevenue = sol.sellerRevenuePriceVector();
			}
			//Printer.printVector(sol.getPriceVector());
			//System.out.println("Seller revenue = " + sol.sellerRevenuePriceVector());
			System.out.println("(r,s) = (" + reserve +"," + sellerRevenue + ")");
			x[i] = reserve;
			y[i] = sellerRevenue;
			reserve += 0.025;
		}
		Plot2DPanel plot = new Plot2DPanel();
		plot.addLinePlot("my plot", x, y);
		 JFrame frame = new JFrame("a plot panel");
		 frame.setSize(1000, 1000);
		  frame.setContentPane(plot);
		  frame.setVisible(true);
	}

}
