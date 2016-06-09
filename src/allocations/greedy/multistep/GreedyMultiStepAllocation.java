package allocations.greedy.multistep;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import structures.Market;
import allocations.objectivefunction.ObjectiveFunction;

/*
 * This class implements greedy allocation with multisteps.
 * 
 * @author Enrique Areyan Viqueira
 */
public class GreedyMultiStepAllocation {
	/* Market Object wich we are going to allocate.*/
	protected Market market;
	/* stepSize. Impressions get allocated in multiples of this step only.*/
	protected int stepSize;
	/* objective function that indicates how good is to allocate one chunk of a campaign.*/
	protected ObjectiveFunction f;
	/*Constructor*/
	public GreedyMultiStepAllocation(Market market, int stepSize, ObjectiveFunction f){
		this.market = market;
		this.stepSize = stepSize;
		this.f = f;
	}
	/* Solve method. Returns an allocation.*/
	public int[][] Solve(){
		int[][] allocation = new int[this.market.getNumberUsers()][this.market.getNumberCampaigns()];
		/* First compute user supply.*/
		int[] userSupply = new int[this.market.getNumberUsers()];
		for(int i=0;i<this.market.getNumberUsers();i++){
			userSupply[i] = this.market.getUser(i).getSupply();
		}
		/* Allocation to each campaign begins at zero and increments as we go.*/
		int[] currentAllocation = new int[this.market.getNumberCampaigns()];
		boolean allocatedSome = true;
		/* While we have allocated something, keep going.*/
		while(allocatedSome){
			/* Compute the chunks that we want to allocate.*/
			ArrayList<chunk> currentChunks = new ArrayList<chunk>();
			for(int j=0;j<currentAllocation.length;j++){
				if(currentAllocation[j] < this.market.getCampaign(j).getDemand()){
					currentChunks.add(new chunk(j,this.market.getCampaign(j).getPriority(),this.f.getObjective(this.market.getCampaign(j).getReward(), this.market.getCampaign(j).getDemand(), currentAllocation[j] + this.stepSize)));
				}
			}
			/* Order the chunks, first by priority, second by value.*/
			Comparator<chunk> comparator = Comparator.comparing(chunk -> chunk.priority);
		    comparator = comparator.thenComparing(Comparator.comparing(chunk -> chunk.value));
		    comparator = comparator.reversed(); /* The default sorting is ascending but we want descending. */
		    Stream<chunk> chunckStream = currentChunks.stream().sorted(comparator);
		    List<chunk> sortedChunks= chunckStream.collect(Collectors.toList());
		    allocatedSome = false;
		    /* For each chunk, try to allocate it.*/
		    mainloop: for(chunk c:sortedChunks){
		    	for(int i=0;i<this.market.getNumberUsers();i++){
		    		if(this.market.isConnected(i, c.id) && userSupply[i]>=this.stepSize){
		    			/*
		    			 * In case this user is allocated to this campaign, and this user has enough
		    			 * impressions (at least the step size), then allocate the chunk to this campaign.
		    			 */
		    			allocation[i][c.id] += this.stepSize;
		    			userSupply[i] -= this.stepSize;
		    			currentAllocation[c.id] += this.stepSize;
		    			allocatedSome = true;
		    			continue mainloop;
		    		}
		    	}
		    }
		}
	    return allocation;
	}
	/*
	 * This class represent a single chunk of impressions that we want to allocate.
	 */
	private class chunk{
		protected int id;
		protected int priority;
		protected double value;
		
		public chunk(int id, int priority,double value){
			this.id = id;
			this.priority = priority;
			this.value = value;
		}
		
		public String toString(){
			return "("+this.id+","+this.priority+","+this.value+")";
		}
	}
}
