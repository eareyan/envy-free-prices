package structures.comparators;

import java.util.Comparator;

import structures.MarketPrices;
import structures.exceptions.MarketPricesException;

/**
 * Comparator to compare MarketPrices objects by sellerrevenue.
 * 
 * @author Enrique Areyan Viqueira
 */
public class MarketPricesComparatorBySellerRevenue implements
    Comparator<MarketPrices> {
  @Override
  public int compare(MarketPrices MP1, MarketPrices MP2) {
    try {
      if (MP1.sellerRevenuePriceVector() < MP2.sellerRevenuePriceVector()) {
        return 1;
      } else if (MP1.sellerRevenuePriceVector() > MP2.sellerRevenuePriceVector()) {
        return -1;
      } else {
        return 0;
      }
    } catch (MarketPricesException e) {
      System.out.println("MarketPricesException = " + e.getMessage());
      e.printStackTrace();
    }
    return 0;
  }
  
}