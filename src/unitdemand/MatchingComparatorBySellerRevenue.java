package unitdemand;

import java.util.Comparator;

/*
 * Comparator to compare two matchings by seller revenue.
 * 
 * @author Enrique Areyan Viqueira
 */
public class MatchingComparatorBySellerRevenue implements Comparator<Matching>{
	@Override
	public int compare(Matching m1, Matching m2) {
		if(m1.getSellerRevenue() < m2.getSellerRevenue()) return 1;
		if(m1.getSellerRevenue() > m2.getSellerRevenue()) return -1;
		return 0;
	}
}