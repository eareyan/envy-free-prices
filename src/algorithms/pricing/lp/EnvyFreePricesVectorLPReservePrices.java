package algorithms.pricing.lp;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

import java.util.ArrayList;

import algorithms.EnvyFreePricesVectorLP;
import structures.MarketAllocation;
import util.Printer;

/*
 * LP to find a single vector of envy-free prices.
 * Implements Compact Condition and Individual Rationality.
 * 
 * @author Enrique Areyan Viqueira
 */

public class EnvyFreePricesVectorLPReservePrices extends EnvyFreePricesVectorLP{	/*
	 * Constructor receives an allocated market M.
	 */
	public EnvyFreePricesVectorLPReservePrices(MarketAllocation allocatedMarket,IloCplex iloObject){
		super(allocatedMarket,iloObject);
	}
	/*
	 * This method creates the LP
	 */
	public void createLP(int userIndex,int campaignIndex){
		super.createLP();
		try {
			this.createReservePrice(userIndex,campaignIndex);
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * This method creates a reserve price for user i
	 */
	protected void createReservePrice(int userIndex,int campaignIndex) throws IloException{
		//Printer.printMatrix(this.allocatedMarket.getAllocation());
		//System.out.println("==Set reserve using link: ("+userIndex+","+campaignIndex+")");
		int allocationFromOtherUsers = 0;
		IloLinearNumExpr sumPrices = cplex.linearNumExpr();
		for(int i=0;i<this.allocatedMarket.getMarket().getNumberUsers();i++){
			if(this.allocatedMarket.getAllocation()[i][campaignIndex]>0){
				//System.out.println("The edge ("+i+","+campaignIndex+") is bigger than zero");
				if(i!=userIndex){
					allocationFromOtherUsers += this.allocatedMarket.getAllocation()[i][campaignIndex];
					sumPrices.addTerm(1.0, this.prices[i]);
				}
			}
		}
		//System.out.println(sumPrices);
		this.linearConstrains.add(
				(IloRange) this.cplex.addGe(this.prices[userIndex], 
									this.cplex.prod( 1.0/(this.allocatedMarket.getMarket().getCampaign(campaignIndex).getDemand() - allocationFromOtherUsers) ,
												this.cplex.sum(	this.allocatedMarket.getMarket().getCampaign(campaignIndex).getReward(),
																this.cplex.prod(-1.0,sumPrices)))));
		//System.out.println(this.linearConstrains);
		//System.out.println("Allocation from other users = " + allocationFromOtherUsers);
		//System.out.println("denominator = " + (this.allocatedMarket.getMarket().getCampaign(campaignIndex).getDemand() - allocationFromOtherUsers));
	}
}
