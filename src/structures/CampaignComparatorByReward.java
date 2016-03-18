package structures;

import java.util.Comparator;

public class CampaignComparatorByReward  implements Comparator<Campaign>{
	@Override
	public int compare(Campaign c1, Campaign c2) {
		if(c1.getReward() < c2.getReward()) return 1;
		if(c1.getReward() > c2.getReward()) return -1;
		return 0;
	}

}
