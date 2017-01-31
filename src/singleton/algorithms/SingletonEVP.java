package singleton.algorithms;

import singleton.structures.SingletonMarket;
import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.factory.UnitDemandMarketAllocationFactory;
import unitdemand.structures.UnitDemandException;
import util.Printer;
import algorithms.pricing.error.PrincingAlgoException;

public class SingletonEVP {

  SingletonMarket<Goods, Bidder<Goods>> market;

  private final double[][] V;

  public SingletonEVP(SingletonMarket<Goods, Bidder<Goods>> market) {
    this.market = market;
    this.V = UnitDemandMarketAllocationFactory.getValuationMatrixFromMarket(market);
    Printer.printMatrix(this.V);
  }

  public PricesStatistics<SingletonMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> Solve() throws PrincingAlgoException, UnitDemandException {
    /*System.out.println(this.market);
    Printer.printMatrix(V);
    EVPApproximation evpApproximation = new EVPApproximation(this.V);
    UnitDemandMarketOutcome x = evpApproximation.Solve();
    x.getMarketAllocation().printAllocation();
    MarketAllocation<SingletonMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> allocation = new MarketAllocation<SingletonMarket<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>();
    x.printPrices();*/
    return null;
  }

}
