package singleminded.algorithms.complete;

import structures.Goods;

import com.google.common.collect.ImmutableMap;

public class LPSolution {

  public enum Status {
    Infeasible, Optimal
  };

  private final LPSolution.Status status;
  private final ImmutableMap<Goods, Double> prices;
  private final double objValue;

  public LPSolution(LPSolution.Status status, ImmutableMap<Goods, Double> prices, double objValue) {
    this.status = status;
    this.prices = prices;
    this.objValue = objValue;
  }

  public LPSolution.Status getStatus() {
    return this.status;
  }

  public ImmutableMap<Goods, Double> getPrices() {
    return this.prices;
  }

  public double getObjValue() {
    return this.objValue;
  }

}
