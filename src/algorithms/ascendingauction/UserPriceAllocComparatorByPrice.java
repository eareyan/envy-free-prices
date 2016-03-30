package algorithms.ascendingauction;

import java.util.Comparator;

public class UserPriceAllocComparatorByPrice implements Comparator<UserPriceAlloc>{

	@Override
	public int compare(UserPriceAlloc o1, UserPriceAlloc o2) {
		if(o1.getPrice() > o2.getPrice()) return 1;
		if(o1.getPrice() < o2.getPrice()) return -1;
		return 0;
	}

}
