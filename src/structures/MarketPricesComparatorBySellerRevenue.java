package structures;

import java.util.Comparator;

public class MarketPricesComparatorBySellerRevenue implements Comparator<MarketPrices>{
	@Override
	public int compare(MarketPrices MP1, MarketPrices MP2) {
		if(MP1.sellerRevenuePriceVector() < MP2.sellerRevenuePriceVector()) return 1;
		if(MP1.sellerRevenuePriceVector() > MP2.sellerRevenuePriceVector()) return -1;
		return 0;
	}
}