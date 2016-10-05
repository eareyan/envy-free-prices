package structures.comparators;

import java.util.Comparator;

import allocations.objectivefunction.interfaces.ObjectiveFunction;
import structures.Bidder;
import structures.Goods;
import structures.MarketOutcome;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketOutcomeException;

/**
 * Comparator to compare MarketPrices objects by sellerrevenue.
 * 
 * @author Enrique Areyan Viqueira
 */
public class MarketPricesComparatorBySellerRevenue<O extends ObjectiveFunction> implements Comparator<MarketOutcome<Goods, Bidder<Goods>, O>> {
  @Override
  public int compare(MarketOutcome<Goods, Bidder<Goods>, O> MP1, MarketOutcome<Goods, Bidder<Goods>, O> MP2) {
    try {
      if (MP1.sellerRevenue() < MP2.sellerRevenue()) {
        return 1;
      } else if (MP1.sellerRevenue() > MP2.sellerRevenue()) {
        return -1;
      } else {
        return 0;
      }
    } catch (MarketOutcomeException e) {
      System.out.println("MarketPricesException = " + e.getMessage());
      e.printStackTrace();
    } catch (MarketAllocationException e) {
      System.out.println("MarketAllocationException = " + e.getMessage());
      e.printStackTrace();
    }
    return 0;
  }
  
}