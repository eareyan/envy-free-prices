package algorithms;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

import java.util.ArrayList;

import structures.MarketAllocation;

/*
 * This class uses CPLEX to implement and solve a LP to find a matrix of restricted envy-free prices for an input market
 * The LP must receive an input allocation. 
 * 
 * @author Enrique Areyan Viqueira
 */
public class EnvyFreePricesMatrixLP {
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
	protected IloNumVar[][] prices;
	protected IloCplex cplex;
	/*
	 * Constructor receives an allocated market M.
	 */
	public EnvyFreePricesMatrixLP(MarketAllocation m){
		this.allocatedMarket = m;
	} 	
	/*
	 * This method generate the compact conditions.
	 */
	protected void generateCompactConditions() throws IloException{
		for(int i=0;i<this.allocatedMarket.getMarket().getNumberUsers();i++){
			for(int j=0;j<this.allocatedMarket.getMarket().getNumberCampaigns();j++){
				if(this.allocatedMarket.getAllocation()[i][j] > 0){
					/*
					 * In this case we have to add a condition for the compact condition
					 */
					for(int k=0;k<this.allocatedMarket.getMarket().getNumberUsers();k++){						
						//If you are connected to this campaign and you don't get all of this campaign
						if(i!=k && this.allocatedMarket.getMarket().isConnected(k, j) && this.allocatedMarket.getAllocation()[k][j] < this.allocatedMarket.getMarket().getUser(k).getSupply()){
							this.linearConstrains.add(this.cplex.addLe(this.cplex.sum(	this.cplex.prod(-1.0, 	this.prices[k][j]), this.cplex.prod( 1.0, this.prices[i][j])), 0.0));
						}
					}
				}
			}
		}
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
						lhs.addTerm(this.allocatedMarket.getAllocation()[i][j], this.prices[i][j]);
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
	 * This method implements conditions so that the matrix of prices is not unbounded.
	 * If x_ij=0, then there is no allocation from i to j, so the price P_ij is unbounded.
	 * Instead, let us bounded by the reward of j. This is arbitrary, it can be any value we like.
	 */
	protected void  generateBoundConditions() throws IloException{
		for(int i=0;i<this.allocatedMarket.getMarket().getNumberUsers();i++){
			for(int j=0;j<this.allocatedMarket.getMarket().getNumberCampaigns();j++){
				if(this.allocatedMarket.getAllocation()[i][j] == 0){
					this.linearConstrains.add(this.cplex.addLe(this.prices[i][j],Math.ceil(this.allocatedMarket.getMarket().getCampaign(j).getReward())));
				}
			}
		}
	}
	/*
	 * This method creates and solves the LP.
	 */	
	public EnvyFreePricesSolutionLP Solve(){
		EnvyFreePricesSolutionLP Solution = new EnvyFreePricesSolutionLP();
		try{
			/*
			 * Create Cplex variables.
			 */
			this.cplex = new IloCplex();			
			if(!this.verbose) this.cplex.setOut(null);
			this.linearConstrains = new ArrayList<IloRange>();
		 	cplex.setParam(IloCplex.BooleanParam.PreInd, false);
		    /*
		     * Create the variables. Matrix of prices.
		     */
		    this.prices = new IloNumVar[this.allocatedMarket.getMarket().getNumberUsers()][];
			for (int i=0; i<this.allocatedMarket.getMarket().getNumberUsers(); i++){
				this.prices[i]  = this.cplex.numVarArray(this.allocatedMarket.getMarket().getNumberCampaigns(), 0.0, Double.MAX_VALUE);
				
			}
		    /*
		     * Create the objective function, i.e., the sum of all the prices
		     */			
			IloLinearNumExpr objective = cplex.linearNumExpr();
			for(int i=0; i < this.allocatedMarket.getMarket().getNumberUsers(); i++){
				for(int j=0; j<this.allocatedMarket.getMarket().getNumberCampaigns(); j++){
					objective.addTerm(this.allocatedMarket.getAllocation()[i][j],this.prices[i][j]);
				}
			}
			//this.cplex.addMinimize(objective);
			this.cplex.addMaximize(objective);
			/*
			 * Generate Conditions.
			 */
		    this.generateCompactConditions();
		    this.generateIndividualRationalityConditions();
		    this.generateBoundConditions();
		    /*
		     * Solve the LP.
		     */
		    if ( this.cplex.solve() ) {
		    	double[][] solDouble = new double[this.allocatedMarket.getMarket().getNumberUsers()][this.allocatedMarket.getMarket().getNumberCampaigns()];
		    	for (int i=0; i<this.allocatedMarket.getMarket().getNumberUsers(); i++){
		    		solDouble[i]     = cplex.getValues(this.prices[i]);
		    	}
		    	Solution = new EnvyFreePricesSolutionLP(this.allocatedMarket, solDouble, cplex.getStatus().toString(),this.cplex.getObjValue());
		    }else{
		    	Solution = new EnvyFreePricesSolutionLP(this.allocatedMarket,cplex.getStatus().toString());
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
