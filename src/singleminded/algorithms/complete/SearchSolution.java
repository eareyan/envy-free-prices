package singleminded.algorithms.complete;

import java.util.HashMap;

import com.google.common.collect.ImmutableMap;

import structures.Bidder;
import structures.Goods;

public class SearchSolution {

  private final double objValue;

  private final HashMap<Bidder<Goods>, Boolean> allocation;
  
  private final ImmutableMap<Goods, Double> prices;

  public SearchSolution(double objValue, HashMap<Bidder<Goods>, Boolean> allocation, ImmutableMap<Goods, Double> prices) {
    this.objValue = objValue;
    this.allocation = new HashMap<Bidder<Goods>, Boolean>(allocation);
    this.prices = prices;
  }

  public double getObjValue() {
    return this.objValue;
  }

  public HashMap<Bidder<Goods>, Boolean> getAllocation() {
    return this.allocation;
  }
  
  public double getGoodPrice(Goods good) {
    return this.prices.get(good);
  }
  
  public String toString() {
    return "Sol = " + this.objValue + ", assignment = " + this.allocation + ", prices = " + this.prices;
  }

}
