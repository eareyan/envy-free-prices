package allocations.optimal;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.ArrayList;

import allocations.error.AllocationErrorCodes;
import allocations.error.AllocationException;
import allocations.interfaces.AllocationAlgoInterface;
import allocations.objectivefunction.ObjectiveFunction;
import allocations.objectivefunction.SingleStepFunction;
import structures.Market;
import structures.MarketAllocation;
import util.Printer;

/*
 * This class uses CPLEX to implement and solve a mixed-ILP to find an efficient allocation for an input market. 
 * 
 * @author Enrique Areyan Viqueira
 */
public class SingleStepEfficientAllocationILP implements AllocationAlgoInterface{
	/*
	 * Boolean to control whether or not to output.
	 */
	protected boolean verbose = false;
	/*
	 * How many solutions
	 */
	protected static final int numSolutions = 1;
	/*
	 * Solver method. Returns an ArrayList of int[][] containing all the efficient allocations
	 * found by the ILP.
	 */
	public SingleStepEfficientAllocationILP(){
		
	}
	public SingleStepEfficientAllocationILP(boolean verbose){
		this.verbose = verbose;
	}
	public MarketAllocation Solve(Market market) throws AllocationException{
		try {
			IloCplex cplex = new IloCplex();
			if(!this.verbose) cplex.setOut(null);
			/*
			 * These two next parameters controls how many solutions we want to get.
			 * The first parameter controls how far from the optimal we allow solutions to be,
			 * The second parameter controls how many solutions we will get in total.
			 */
			cplex.setParam(IloCplex.DoubleParam.SolnPoolGap, 0.0);
			cplex.setParam(IloCplex.IntParam.PopulateLim, SingleStepEfficientAllocationILP.numSolutions);
			/*
			 * variables
			 */
			IloNumVar[] indicatorVariable = cplex.boolVarArray(market.getNumberCampaigns());
			IloNumVar[][] allocationMatrixVariable = new IloNumVar[market.getNumberUsers()][];
			for (int i=0; i<market.getNumberUsers(); i++){
				allocationMatrixVariable[i] = cplex.intVarArray(market.getNumberCampaigns(),0,Integer.MAX_VALUE);
			}
			/*
			 * Objective.
			 */
			IloLinearNumExpr obj = cplex.linearNumExpr();
			for (int j=0; j<market.getNumberCampaigns(); j++){
				obj.addTerm(market.getCampaign(j).getReward() - market.getCampaign(j).getReserve()*(market.getCampaign(j).getDemand() - market.getCampaign(j).getAllocationSoFar()), indicatorVariable[j]);
			}
			//System.out.println(obj);
			cplex.addMaximize(obj);
			/*
			 * Constraint (1). Allocation satisfies campaign. 
			 */
			for (int j=0; j<market.getNumberCampaigns(); j++){
				double coeff = 1.0 / ((double)(market.getCampaign(j).getDemand() - market.getCampaign(j).getAllocationSoFar()));
				IloLinearNumExpr expr = cplex.linearNumExpr();
				for (int i=0; i<market.getNumberUsers(); i++){
					if(market.isConnected(i, j)){
						expr.addTerm(coeff,allocationMatrixVariable[i][j]);
					}else{
						cplex.addEq(0,allocationMatrixVariable[i][j]);
					}
				}
				cplex.addGe(expr,indicatorVariable[j]);
				cplex.addLe(expr,indicatorVariable[j]);
			}
			/*
			 * Constrain (2). Allocation from user can not be more than supply.
			 */
			for (int i=0; i<market.getNumberUsers(); i++){
				IloLinearNumExpr expr = cplex.linearNumExpr();
				for (int j=0; j<market.getNumberCampaigns(); j++){
					expr.addTerm(1.0,allocationMatrixVariable[i][j]);
				}
				cplex.addLe(expr,market.getUser(i).getSupply());
			}
			/*
			 * Constrain (3). Campaigns have access to at most as many users as given by their level
			 */
			for(int i=0;i<market.getNumberUsers();i++){
				for(int j=0;j<market.getNumberCampaigns();j++){
					cplex.addLe(allocationMatrixVariable[i][j],Math.floor(market.getCampaign(j).getLevel() * market.getUser(i).getSupply()));
				}
			}
			/*
			 * Solve the problem and get many solutions:
			 */
			cplex.solve();
			if ( cplex.populate() ) {
				int numsol = cplex.getSolnPoolNsolns();
				int numsolreplaced = cplex.getSolnPoolNreplaced();
				/*
				 * Print some information
				 */
				if(verbose){
					System.out.println("***********************************Populate");
					System.out.println("The solution pool contains " + numsol + " solutions.");
					System.out.println(numsolreplaced + " solutions were removed due to the " + "solution pool relative gap parameter.");
					System.out.println("In total, " + (numsol + numsolreplaced) + " solutions were generated.");
					System.out.println("Solution status = " + cplex.getStatus());
			    	System.out.println("Solution value  = " + cplex.getObjValue());
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
	    			int[][] sol = new int[market.getNumberUsers()][market.getNumberCampaigns()];
	    			double[][] solDouble = new double[market.getNumberUsers()][market.getNumberCampaigns()];
	    			for (int i=0; i<market.getNumberUsers(); i++){
	    				solDouble[i] = cplex.getValues(allocationMatrixVariable[i],l);
	    				/*
	    				 * Unfortunately in Java the only way to cast your array is to iterate through each element and cast them one by one
	    				 */
	    				for(int j=0;j<market.getNumberCampaigns(); j++){
	    					sol[i][j] = (int) Math.round(solDouble[i][j]);
	    				}
	    			}
	    			Solutions.add(sol);
	    			if(verbose){
	    				Printer.printMatrix(sol);
	    				System.out.println();
		    			for(int j=0;j<market.getNumberCampaigns();j++){
		    				System.out.println(cplex.getValue(indicatorVariable[j],l));
		    			}
	    			}
	            }
	            cplex.end();
	            return new MarketAllocation(market, Solutions.get(0), this.getObjectiveFunction());
			}
		} catch (IloException e) {
			/* Report that CPLEX failed. */
			e.printStackTrace();
			throw new AllocationException(AllocationErrorCodes.CPLEX_FAILED);
		}
		/* If we ever do reach this point, then we don't really know what happened. */
		throw new AllocationException(AllocationErrorCodes.UNKNOWN_ERROR);
	}
	/*
	 * This algorithm optimizes a single setp function.
	 * @see allocations.interfaces.AllocationAlgoInterface#getObjectiveFunction()
	 */
	@Override
	public ObjectiveFunction getObjectiveFunction() {
		return new SingleStepFunction();
	}
}
