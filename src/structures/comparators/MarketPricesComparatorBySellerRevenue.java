package structures.comparators;

import java.util.Comparator;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketOutcome;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketOutcomeException;

/**
 * Comparator to compare MarketPrices objects by sellerrevenue.
 * 
 * @author Enrique Areyan Viqueira
 */
public class MarketPricesComparatorBySellerRevenue<M extends Market<G, B>, G extends Goods, B extends Bidder<G>> implements Comparator<MarketOutcome<M, G, B>> {
  @Override
  public int compare(MarketOutcome<M, G, B> MP1, MarketOutcome<M, G, B> MP2) {
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