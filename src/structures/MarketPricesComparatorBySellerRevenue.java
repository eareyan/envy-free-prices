package structures;

import java.util.Comparator;

/*
 * Comparator to compare MarketPrices objects by sellerrevenue.
 * 
 * @author Enrique Areyan Viqueira
 */
public class MarketPricesComparatorBySellerRevenue implements Comparator<MarketPrices>{
	@Override
	public int compare(MarketPrices MP1, MarketPrices MP2) {
		if(MP1.sellerRevenuePriceVector() < MP2.sellerRevenuePriceVector()) return 1;
		if(MP1.sellerRevenuePriceVector() > MP2.sellerRevenuePriceVector()) return -1;
		return 0;
	}
}