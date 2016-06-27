package allocations.interfaces;

import ilog.concert.IloException;
import allocations.error.AllocationException;
import structures.Market;
import structures.MarketAllocation;
import structures.exceptions.CampaignCreationException;

public interface AllocationAlgoInterface {
	public MarketAllocation Solve(Market market) throws IloException, AllocationException, CampaignCreationException;
}
