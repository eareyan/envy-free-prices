package unitdemand.structures;

import java.util.Comparator;

/**
 * Comparator to compare two matchings by seller revenue.
 * 
 * @author Enrique Areyan Viqueira
 */
public class UnitDemandMarketOutcomeComparatorBySellerRevenue implements Comparator<UnitDemandMarketOutcome> {
  @Override
  public int compare(UnitDemandMarketOutcome o1, UnitDemandMarketOutcome o2) {
    if (o1.getSellerRevenue() < o2.getSellerRevenue()) {
      return 1;
    } else if (o1.getSellerRevenue() > o2.getSellerRevenue()) {
      return -1;
    } else {
      return 0;
    }
  }
}