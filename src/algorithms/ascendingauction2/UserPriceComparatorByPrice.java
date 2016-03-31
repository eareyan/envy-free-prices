package algorithms.ascendingauction2;

import java.util.Comparator;

public class UserPriceComparatorByPrice implements Comparator<UserPrice>{

	@Override
	public int compare(UserPrice o1, UserPrice o2) {
		if(o1.getPrice() > o2.getPrice()) return 1;
		if(o1.getPrice() < o2.getPrice()) return -1;
		return 0;
	}

}
