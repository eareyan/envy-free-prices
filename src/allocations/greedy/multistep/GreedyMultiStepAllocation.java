package allocations.greedy.multistep;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import structures.Market;
import structures.MarketAllocation;
import allocations.error.AllocationErrorCodes;
import allocations.error.AllocationException;
import allocations.interfaces.AllocationAlgoInterface;
import allocations.objectivefunction.ObjectiveFunction;

/*
 * This class implements greedy allocation with multi-steps.
 * 
 * @author Enrique Areyan Viqueira
 */
public class GreedyMultiStepAllocation implements AllocationAlgoInterface{
	/* stepSize. Impressions get allocated in multiples of this step only.*/
	protected int stepSize;
	/* objective function that indicates how good is to allocate one chunk of a campaign.*/
	protected ObjectiveFunction f;
	/* Initial allocation: indicates, for each campaign, how many impressions were already allocated*/
	protected int[] currentAllocation;
	/*Constructor. Does not receive current allocation, so all campaigns start at zero allocation. */
	public GreedyMultiStepAllocation(int stepSize, ObjectiveFunction f) throws AllocationException{
		if(stepSize<=0){
			throw new AllocationException(AllocationErrorCodes.STEP_NEGATIVE);
		}
		this.stepSize = stepSize;
		this.f = f;
	}
	/* Solve method. Returns an allocation.*/
	public MarketAllocation Solve(Market market){
		int[][] allocation = new int[market.getNumberUsers()][market.getNumberCampaigns()];
		this.currentAllocation = new int[market.getNumberCampaigns()];
		for(int j=0;j<market.getNumberCampaigns();j++){
			this.currentAllocation[j] = market.getCampaign(j).getAllocationSoFar();
		}
		/* First compute user supply.*/
		int[] userSupply = new int[market.getNumberUsers()];
		for(int i=0;i<market.getNumberUsers();i++){
			userSupply[i] = market.getUser(i).getSupply();
		}
		boolean allocatedSome = true;
		/* While we have allocated something, keep going.*/
		while(allocatedSome){
			/* Compute the chunks that we want to allocate.*/
			ArrayList<chunk> currentChunks = new ArrayList<chunk>();
			for(int j=0;j<market.getNumberCampaigns();j++){
				if(currentAllocation[j] + this.stepSize <= market.getCampaign(j).getDemand()){
					for(int i=0;i<market.getNumberUsers();i++){
						/* The value of the chunk is the difference between the current step and the next step, minus reserve price for getting the extra impressions.*/
						double value = this.f.getObjective(market.getCampaign(j).getReward(), market.getCampaign(j).getDemand(), currentAllocation[j] + this.stepSize) - this.f.getObjective(market.getCampaign(j).getReward(), market.getCampaign(j).getDemand(), currentAllocation[j]) - market.getCampaign(j).getReserve()*this.stepSize;
						if(market.isConnected(i, j) && userSupply[i]>=this.stepSize && (Math.floor(market.getCampaign(j).getLevel()*market.getUser(i).getSupply()) - allocation[i][j] >= this.stepSize) && value >0){
							currentChunks.add(new chunk(
									j,
									market.getCampaign(j).getPriority(),
									value,
									i,
									userSupply[i]));
						}
					}
				}
			}
			if(currentChunks.size()>0){
				/* Order the chunks, first by priority, second by value.*/
				Comparator<chunk> comparator = Comparator.comparing(chunk -> chunk.campaignPriority);
			    comparator = comparator.thenComparing(Comparator.comparing(chunk -> chunk.value));
			    comparator = comparator.thenComparing(Comparator.comparing(chunk -> chunk.remainingUserSupply));
			    comparator = comparator.reversed(); /* The default sorting is ascending but we want descending. */
			    Stream<chunk> chunckStream = currentChunks.stream().sorted(comparator);
			    List<chunk> sortedChunks= chunckStream.collect(Collectors.toList());
			    /* Debug Print: */
			    //System.out.println(sortedChunks);
			    int cIndex = sortedChunks.get(0).campaignId;
			    int uIndex = sortedChunks.get(0).userId;
			    allocation[uIndex][cIndex] += this.stepSize;
    			userSupply[uIndex] -= this.stepSize;
    			currentAllocation[cIndex] += this.stepSize;
			}else{
				/* The first chunk is always allocated. Thus, if we had at least one chunk, we know it was allocated. */
				allocatedSome = false;				
			}
		}
	    return new MarketAllocation(market,allocation);
	}
	/*
	 * This class represent a single chunk of impressions.
	 * Specifically, a chunk is a tuple (campaignId,campaignPriority,value,userId,remainingUserSupply)
	 * that represents a chunk of impressions to allocate from a user to a campaign.
	 * We take these chunks and order them before allocation happens.
	 */
	private class chunk{
		protected int campaignId;
		protected int campaignPriority;
		protected double value;
		protected int userId;
		protected int remainingUserSupply;
		
		public chunk(int id, int priority,double value, int userId, int remainingUserSupply){
			this.campaignId = id;
			this.campaignPriority = priority;
			this.value = value;
			this.userId = userId;
			this.remainingUserSupply = remainingUserSupply;
		}
		
		public String toString(){
			return "(id = "+this.campaignId+",prio = "+this.campaignPriority+", f = "+this.value+", u.id = "+this.userId+", remaining = "+this.remainingUserSupply+")";
		}
	}
}
