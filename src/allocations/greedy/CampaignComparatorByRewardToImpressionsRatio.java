package allocations.greedy;

import java.util.Comparator;

import structures.Campaign;

public class CampaignComparatorByRewardToImpressionsRatio implements Comparator<Campaign>{

	@Override
	public int compare(Campaign c1, Campaign c2) {
		if(c1.getReward() / (c1.getDemand() - c1.getAllocationSoFar()) < c2.getReward() / (c2.getDemand() - c2.getAllocationSoFar())) return 1;
		if(c1.getReward() / (c1.getDemand() - c1.getAllocationSoFar()) > c2.getReward() / (c2.getDemand() - c2.getAllocationSoFar())) return -1;
		return 0;
	}

}
