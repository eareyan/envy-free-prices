package algorithms;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

import java.util.ArrayList;

import structures.MarketAllocation;

/*
 * LP to find a single vector of envy-free prices.
 * Implements Compact Condition and Individual Rationality.
 * 
 * @author Enrique Areyan Viqueira
 */

public class EnvyFreePricesVectorLP {
	/*
	 * Boolean to control whether or not to output.
	 */
	protected boolean verbose = false;
	/*
	 * Market Allocation Object. Contains the market and the allocation.
	 */
	protected MarketAllocation allocatedMarket;
	/*
	 * Contains all the linear constrains
	 */
	protected ArrayList<IloRange> linearConstrains;
	/*
	 * Objects needed to interface with CPlex Library.
	 */
	protected IloNumVar[] prices;
	protected IloCplex cplex;
	/*
	 * Constructor receives an allocated market M.
	 */
	public EnvyFreePricesVectorLP(MarketAllocation allocatedMarket){
		this.allocatedMarket = allocatedMarket;
	}
	/*
	 * This method generate the compact conditions.
	 */
	protected void generateCompactConditions() throws IloException{
		if(this.verbose) System.out.println("--- Start generate Compact Conditions ---");
		for(int i=0;i<this.allocatedMarket.getMarket().getNumberUsers();i++){
			for(int j=0;j<this.allocatedMarket.getMarket().getNumberCampaigns();j++){
				if(this.allocatedMarket.getAllocation()[i][j] > 0){
					if(this.verbose) System.out.println("Entry: "+i+","+j);
					/*
					 * In this case we have to add a condition for the compact condition
					 */
					for(int k=0;k<this.allocatedMarket.getMarket().getNumberUsers();k++){						
						//If you are connected to this campaign and you don't get all of this campaign
						if(i!=k && this.allocatedMarket.getMarket().isConnected(k, j) && this.allocatedMarket.getAllocation()[k][j] < this.allocatedMarket.getMarket().getUser(k).getSupply()){
							if(this.verbose){
								System.out.println("Add compact condition for user "+k+" on campaign " + j + ", where x_{"+k+j+"} = " + this.allocatedMarket.getAllocation()[k][j]);
								System.out.println("\t Price("+i+") <= Price("+k+")");
							}
							this.linearConstrains.add(this.cplex.addLe(this.cplex.sum(	this.cplex.prod(-1.0, 	this.prices[k]), this.cplex.prod( 1.0, this.prices[i])), 0.0));
						}
					}
				}
			}
		}
		if(this.verbose) System.out.println("--- End generate Compact Conditions ---");		
	}
	/*
	 * This method generate the Individual Rationality condition.
	 */
	protected void generateIndividualRationalityConditions() throws IloException{
		for(int j=0;j<this.allocatedMarket.getMarket().getNumberCampaigns();j++){
			if(!this.allocatedMarket.isCampaignBundleZero(j)){
				IloLinearNumExpr lhs = cplex.linearNumExpr();
				int counter = 0;
				for(int i=0;i<this.allocatedMarket.getMarket().getNumberUsers();i++){
					if(this.allocatedMarket.getAllocation()[i][j]>0){
						lhs.addTerm(this.allocatedMarket.getAllocation()[i][j], this.prices[i]);
						counter += this.allocatedMarket.getAllocation()[i][j];
					}
				}
				if(counter >= this.allocatedMarket.getMarket().getCampaign(j).getDemand()){
					this.linearConstrains.add(cplex.addLe(lhs, this.allocatedMarket.getMarket().getCampaign(j).getReward()));
				}else{
					this.linearConstrains.add(cplex.addLe(lhs, 0));
				}
			}
		}
	}
	/*
	 * This method implements conditions so that the vector of prices is not unbounded.
	 * We will simply constrain the price of a market to be that of the highest reward of the market.
	 */
	protected void  generateBoundConditions() throws IloException{
		double highestReward = this.allocatedMarket.getMarket().getHighestReward();
		for(int i=0;i<this.allocatedMarket.getMarket().getNumberUsers();i++){
			this.linearConstrains.add(this.cplex.addLe(this.prices[i],Math.ceil(highestReward)));
		}
	}	
	/*
	 * This method creates and solves the LP.
	 */
	public EnvyFreePricesSolutionLP Solve(IloCplex iloObject){
		double[] LP_Prices  = {};
		EnvyFreePricesSolutionLP Solution = new EnvyFreePricesSolutionLP();
		try{
			/*
			 *  Create Cplex variables.
			 */
			this.cplex = iloObject;			
			if(!this.verbose) this.cplex.setOut(null);
			this.linearConstrains = new ArrayList<IloRange>();
		 	cplex.setParam(IloCplex.BooleanParam.PreInd, false);
		    /*
		     * Create the variables. Vector of prices.
		     */
		    this.prices  = this.cplex.numVarArray(this.allocatedMarket.getMarket().getNumberUsers(), 0.0, Double.MAX_VALUE);
		    
		    /*
		     * Create the objective function, i.e., the sum of all the prices
		     */			
			IloLinearNumExpr objective = cplex.linearNumExpr();
			for(int i=0; i < this.allocatedMarket.getMarket().getNumberUsers(); i++){
				for(int j=0; j<this.allocatedMarket.getMarket().getNumberCampaigns(); j++){
					objective.addTerm(this.allocatedMarket.getAllocation()[i][j],this.prices[i]);
				}
			}		    
		    this.cplex.addMaximize(objective);
		    this.generateCompactConditions();
		    this.generateIndividualRationalityConditions();
		    this.generateBoundConditions();
		    /*
		     * Solve the LP.
		     */
		    if ( cplex.solve() ) {
		    	LP_Prices = cplex.getValues(this.prices);
		    	Solution  = new EnvyFreePricesSolutionLP(this.allocatedMarket, LP_Prices, cplex.getStatus().toString(),this.cplex.getObjValue());
		    }else{
		    	Solution = new EnvyFreePricesSolutionLP(cplex.getStatus().toString());
		    }
	    	if(this.verbose){
	    		System.out.println("Solution status = " + cplex.getStatus());
	    		System.out.println("Solution value  = " + cplex.getObjValue());
	    	}
		} catch (IloException e) {
			System.out.println("Exception: ==>");
			e.printStackTrace();
		}
		return Solution;
	}
}
