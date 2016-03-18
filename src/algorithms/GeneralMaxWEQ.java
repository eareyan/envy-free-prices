package algorithms;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import structures.Market;
import structures.MarketAllocation;
import structures.factory.MarketAllocationFactory;
import structures.factory.MarketFactory;
import util.Printer;

public class GeneralMaxWEQ {
	
	protected Market market;
	protected MarketAllocation marketAllocation;
	protected double valueEntireMarket;
	
	public GeneralMaxWEQ(Market market){
		this.market = market;
		try {
			int[][] efficientAllocation = new EfficientAllocationLP(market).Solve(new IloCplex()).get(0);
			this.marketAllocation = new MarketAllocation(this.market,efficientAllocation);
			this.valueEntireMarket = this.marketAllocation.value();
			
			Printer.printMatrix(efficientAllocation);
			System.out.println("value = " + this.valueEntireMarket);
		} catch (IloException e) {
			e.printStackTrace();
		}
	}
	
	public void Solve(){
		try {
			double[] newRewards = new double[this.market.getNumberCampaigns()];
			for(int j=0;j<this.market.getNumberCampaigns();j++){
				Market newMarket = MarketFactory.copyMarketWithoutCampaign(this.market, j);
				newRewards[j] = this.valueEntireMarket - new MarketAllocation(newMarket,new EfficientAllocationLP(newMarket).Solve(new IloCplex()).get(0)).value();
				System.out.println(newRewards[j]);
			}
			for(int j=0;j<this.market.getNumberCampaigns();j++){
				this.market.getCampaign(j).setReward(newRewards[j]);
			}
			System.out.println(this.market);
			int[][] finalAlloc = new EfficientAllocationLP(this.market).Solve(new IloCplex()).get(0);
			EnvyFreePricesVectorLP efpvLP = new EnvyFreePricesVectorLP(new MarketAllocation(this.market,finalAlloc));
			//EnvyFreePricesVectorLP efpvLP = new EnvyFreePricesVectorLP(new MarketAllocation(this.market,this.marketAllocation.getAllocation()));
			efpvLP.createLP();
			EnvyFreePricesSolutionLP sol = efpvLP.Solve();
			Printer.printMatrix(this.marketAllocation.getAllocation());
			Printer.printVector(sol.getPriceVector());
		} catch (IloException e) {
			e.printStackTrace();
		}
	}

}
