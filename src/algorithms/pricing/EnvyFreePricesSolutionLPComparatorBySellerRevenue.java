package algorithms.pricing;

import java.util.Comparator;

/*
 * Comparator class to compare to solutions from LP by seller revenue.
 * 
 * @author Enrique Areyan Viqueira
 */
public class EnvyFreePricesSolutionLPComparatorBySellerRevenue implements Comparator<EnvyFreePricesSolutionLP>{
	@Override
	public int compare(EnvyFreePricesSolutionLP sol1, EnvyFreePricesSolutionLP sol2) {
		if(sol1.sellerRevenuePriceVector() < sol2.sellerRevenuePriceVector()) return 1;
		if(sol1.sellerRevenuePriceVector() > sol2.sellerRevenuePriceVector()) return -1;
		return 0;
	}
}