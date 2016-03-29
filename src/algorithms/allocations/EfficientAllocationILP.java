package algorithms.allocations;

import java.util.ArrayList;

import structures.Market;
import util.Printer;
import ilog.concert.IloException;
import ilog.concert.IloLPMatrix;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

/*
 * This class uses CPLEX to implement and solve a mixed-ILP to find an efficient allocation for an input market. 
 * 
 * @author Enrique Areyan Viqueira
 */
public class EfficientAllocationILP {
	/*
	 * Boolean to control whether or not to output.
	 */
	protected boolean verbose = false;
	/*
	 * How many solutions
	 */
	protected static int numSolutions = 1;
	/*
	 * The market for which we are going to build the linear programming solution
	 */
	protected Market market;
	/*
	 * Objects needed to interface with CPlex Library.
	 */
	protected IloCplex cplex;
	/*
	 * Reserve price. Default value 0.0, in which case the reserve price is ignore.
	 */
	protected double reservePrice = 0.0;
	/*
	 * Constructor receives a market.
	 */
	public EfficientAllocationILP(Market market){
		this.market = market;
	}
	/*
	 * Constructor receives a market and a reserve price
	 */
	public EfficientAllocationILP(Market market, double reservePrice){
		this.market = market;
		this.reservePrice = reservePrice;
		
	}
	/*
	 * Solver method. Returns an ArrayList of int[][] containing all the efficient allocations
	 * found by the ILP.
	 */
	public ArrayList<int[][]> Solve(IloCplex iloObject){
		try {
			this.cplex = iloObject;
			if(!this.verbose) cplex.setOut(null);
			/*
			 * These two next parameters controls how many solutions we want to get.
			 * The first parameter controls how far from the optimal we allow solutions to be,
			 * The second parameter controls how many solutions we will get in total.
			 */
			this.cplex.setParam(IloCplex.DoubleParam.SolnPoolGap, 0.0);
			this.cplex.setParam(IloCplex.IntParam.PopulateLim, EfficientAllocationILP.numSolutions);
			/*
			 * variables
			 */
			IloNumVar[] indicatorVariable = cplex.boolVarArray(this.market.getNumberCampaigns());
			IloNumVar[][] allocationMatrixVariable = new IloNumVar[this.market.getNumberUsers()][];
			for (int i=0; i<this.market.getNumberUsers(); i++){
				allocationMatrixVariable[i] = cplex.intVarArray(this.market.getNumberCampaigns(),0,Integer.MAX_VALUE);
			}
			/*
			 * Objective.
			 */
			IloLinearNumExpr obj = this.cplex.linearNumExpr();
			/*
			 * Check if we need reserve price.
			 */
			if(this.reservePrice <= 0){
				//System.out.println("No reserve price");
				for (int j=0; j<this.market.getNumberCampaigns(); j++){
					obj.addTerm(this.market.getCampaign(j).getReward(), indicatorVariable[j]);
				}
			}else{
				//System.out.println("Accounting for reserve price of " + this.reservePrice);
				for (int j=0; j<this.market.getNumberCampaigns(); j++){
					obj.addTerm(this.market.getCampaign(j).getReward() - this.reservePrice*this.market.getCampaign(j).getDemand(), indicatorVariable[j]);
				}
				//System.out.println(obj);
			}
			this.cplex.addMaximize(obj);
			/*
			 * Constraint (1). Allocation satisfies campaign. 
			 */
			for (int j=0; j<this.market.getNumberCampaigns(); j++){
				double coeff = 1.0/(double)this.market.getCampaign(j).getDemand();
				IloLinearNumExpr expr = cplex.linearNumExpr();
				for (int i=0; i<this.market.getNumberUsers(); i++){
					if(this.market.isConnected(i, j)){
						expr.addTerm(coeff,allocationMatrixVariable[i][j]);
					}else{
						IloLinearNumExpr restrict = cplex.linearNumExpr();
						restrict.addTerm(1,allocationMatrixVariable[i][j]);
						this.cplex.addEq(0,restrict);
					}
				}
				this.cplex.addGe(expr,indicatorVariable[j]);
				this.cplex.addLe(expr,indicatorVariable[j]);
			}
			/*
			 * Constrain (2). Allocation from user can not be more than supply.
			 */
			for (int i=0; i<this.market.getNumberUsers(); i++){
				IloLinearNumExpr expr = cplex.linearNumExpr();
				for (int j=0; j<this.market.getNumberCampaigns(); j++){
					expr.addTerm(1.0,allocationMatrixVariable[i][j]);
				}
				this.cplex.addLe(expr,this.market.getUser(i).getSupply());
			}
			/*
			 * Constrain (3). For all j: If y_j = 0 then x_{ij} = 0 for all i.
			 */
			/*IloNumVar[] indicatorVariableIfThen = cplex.boolVarArray(this.market.getNumberCampaigns());
			for(int j=0;j<this.market.getNumberCampaigns();j++){
				//System.out.println("Add IF-THEN constraint for campaign " + j);
				this.cplex.addGe(cplex.sum(indicatorVariable[j],
									cplex.prod(Integer.MAX_VALUE,
											cplex.sum(1,
													cplex.prod(-1,indicatorVariableIfThen[j]))))
													,1);
				for(int i=0;i<this.market.getNumberUsers();i++){
					this.cplex.addGe(cplex.prod(Integer.MAX_VALUE, indicatorVariableIfThen[j]), allocationMatrixVariable[i][j]);
				}
				
			}*/
			/*
			 * Solve the problem and get many solutions:
			 */
			this.cplex.solve();
			if ( cplex.populate() ) {
				int numsol = cplex.getSolnPoolNsolns();
				int numsolreplaced = cplex.getSolnPoolNreplaced();
				/*
				 * Print some information
				 */
				if(this.verbose){
					System.out.println("***********************************Populate");
					System.out.println("The solution pool contains " + numsol + " solutions.");
					System.out.println(numsolreplaced + " solutions were removed due to the " + "solution pool relative gap parameter.");
					System.out.println("In total, " + (numsol + numsolreplaced) + " solutions were generated.");
					System.out.println("Solution status = " + this.cplex.getStatus());
			    	System.out.println("Solution value  = " + this.cplex.getObjValue());
				}
	            /*
	             * Store all the solutions in an ArrayList.
	             */
	            ArrayList<int[][]> Solutions = new ArrayList<>();
	            for (int l = 0; l < numsol; l++) {
	            	/*
	            	 * The solution should be a matrix of integers. However, CPLEX returns a matrix of doubles.
	            	 * So we are going to have to cast this into integers.
	            	 */
	    			int[][] sol = new int[this.market.getNumberUsers()][this.market.getNumberCampaigns()];
	    			double[][] solDouble = new double[this.market.getNumberUsers()][this.market.getNumberCampaigns()];
	    			for (int i=0; i<this.market.getNumberUsers(); i++){
	    				solDouble[i] = this.cplex.getValues(allocationMatrixVariable[i],l);
	    				/*
	    				 * Unfortunately in Java the only way to cast your array is to iterate through each element and cast them one by one
	    				 */
	    				for(int j=0;j<this.market.getNumberCampaigns(); j++){
	    					sol[i][j] = (int) Math.round(solDouble[i][j]);
	    				}
	    			}
	    			Solutions.add(sol);
	    			if(this.verbose){
	    				Printer.printMatrix(sol);
	    				System.out.println();
	    			}
	    			/*for(int j=0;j<this.market.getNumberCampaigns();j++){
	    				System.out.println(this.cplex.getValue(indicatorVariable[j],l));
	    			}*/
	            }
	            this.cplex.end();
	            return Solutions;
			}
			return null;
		} catch (IloException e) {
			// TODO Auto-generated catch block
			System.out.println("Exception: ==>");
			e.printStackTrace();
		}
		return null;//if we reach this point, then there were no envy-free prices
	}
}
