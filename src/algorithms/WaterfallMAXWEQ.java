package algorithms;

import structures.Market;
import structures.MarketAllocation;
import structures.MarketFactory;
import structures.MarketPrices;
import util.Printer;

/*
 * Generalization of MaxWEQ to non-unit demand case, using Waterfall 
 * 
 * @author Enrique Areyan Viqueira
 */
public class WaterfallMAXWEQ {

	protected Market market;

	protected MarketAllocation marketAllocation;

	protected WaterfallPrices waterfallPrices;

	protected double waterfallValue;
	
	public WaterfallMAXWEQ(Market market){
		this.market = market;
		this.waterfallPrices = new Waterfall(this.market).Solve();
		this.marketAllocation = new Waterfall(this.market).Solve().getMarketAllocation(); 
		this.waterfallValue = marketAllocation.value();
	}
	/*
	 * Implements our generalization to non-unit demand of MaxWEQ.
	 * Essentially, the price of an item is the difference between the valuation of the 
	 * waterfall allocation with and without the item.
	 */
	public MarketPrices Solve(){
		/* Initialize price vector */
		//System.out.println("Initial wf value = " + this.waterfallValue);
		Printer.printMatrix(this.marketAllocation.getAllocation());
		double[] prices = new double[this.market.getNumberUsers()];
		for(int i=0;i<this.market.getNumberUsers();i++){
			//System.out.println("wfvalue without item "+i+": "+ new Waterfall(MarketFactory.copyMarketWithoutUser(market, i)).Solve().getMarketAllocation().value());
			prices[i] = (this.waterfallValue - new Waterfall(MarketFactory.copyMarketWithoutUser(market, i)).Solve().getMarketAllocation().value()) / this.market.getUser(i).getSupply(); 
		}
		/*Printer.printVector(prices);
		Printer.printMatrix(this.waterfallPrices.getMarketAllocation().getAllocation());*/
		return new MarketPrices(marketAllocation, prices);
	}
}
