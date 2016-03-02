package algorithms;

import java.util.Comparator;

public class EnvyFreePricesSolutionLPComparatorBySellerRevenue implements Comparator<EnvyFreePricesSolutionLP>{
	@Override
	public int compare(EnvyFreePricesSolutionLP sol1, EnvyFreePricesSolutionLP sol2) {
		if(sol1.sellerRevenuePriceVector() < sol2.sellerRevenuePriceVector()) return 1;
		if(sol1.sellerRevenuePriceVector() > sol2.sellerRevenuePriceVector()) return -1;
		return 0;
	}
}