package allocations.greedy.multistep;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import structures.Market;
import structures.MarketAllocation;
import allocations.error.AllocationErrorCodes;
import allocations.error.AllocationException;
import allocations.interfaces.AllocationAlgoInterface;
import allocations.objectivefunction.ObjectiveFunction;

/**
 * This class implements greedy allocation with multi-steps.
 * 
 * @author Enrique Areyan Viqueira
 */
public class GreedyMultiStepAllocation implements AllocationAlgoInterface {
  
  /**
   * stepSize. Impressions get allocated in multiples of this step only.
   */
  protected int stepSize;
  
  /**
   * objective function that indicates how good is to allocate one chunk of a campaign.
   */
  protected ObjectiveFunction f;
  
  /**
   * Initial allocation: indicates, for each campaign, how many impressions were already allocated
   */
  protected int[] currentAllocation;
  
  /**
   * Constructor.
   * Does not receive current allocation, so all campaigns start at zero allocation.
   * @param stepSize
   * @param f
   * @throws AllocationException
   */
  public GreedyMultiStepAllocation(int stepSize, ObjectiveFunction f) throws AllocationException {
    if (stepSize <= 0) {
      throw new AllocationException(AllocationErrorCodes.STEP_NEGATIVE);
    }
    this.stepSize = stepSize;
    this.f = f;
  }
  
  /**
   * Solve method. Returns an allocation.
   * 
   * @param market - a Market object.
   * @return an allocation
   */
  public MarketAllocation Solve(Market market) {
    // Initial structures.
    int[][] allocation = new int[market.getNumberUsers()][market
        .getNumberCampaigns()];
    this.currentAllocation = new int[market.getNumberCampaigns()];
    for (int j = 0; j < market.getNumberCampaigns(); j++) {
      this.currentAllocation[j] = market.getCampaign(j).getAllocationSoFar();
    }
    /*
     * First, compute user queue supply. A user is added to the queue if it can
     * supply at least the step size.
     */
    PriorityQueue<userSupply> usersQueue = new PriorityQueue<userSupply>(new UserSupplyComparator());
    for (int i = 0; i < market.getNumberUsers(); i++) {
      if (market.getUser(i).getSupply() >= this.stepSize) {
        usersQueue.add(new userSupply(i, market.getUser(i).getSupply()));
      }
    }
    /*
     * Second, compute campaign queue. A campaign is added to the queue if it
     * has a positive value and has not been fully allocated.
     */
    PriorityQueue<campaignValue> campaignQueue = new PriorityQueue<campaignValue>(new CampaignValueComparator());
    for (int j = 0; j < market.getNumberCampaigns(); j++) {
      double value = this.f.getObjective(market.getCampaign(j).getReward(),
          market.getCampaign(j).getDemand(), this.currentAllocation[j]
              + this.stepSize)
          - this.f.getObjective(market.getCampaign(j).getReward(), market
              .getCampaign(j).getDemand(), this.currentAllocation[j])
          - (market.getCampaign(j).getReserve() * this.stepSize);
      if (this.currentAllocation[j] + this.stepSize <= market.getCampaign(j).getDemand() && value > 0) {
        campaignQueue.add(new campaignValue(j, value));
      }
    }
    boolean userNotFound = true;
    int j;
    int i;
    campaignValue campaign;
    while ((campaign = campaignQueue.poll()) != null) {
      ArrayList<userSupply> auxUserSupplyList = new ArrayList<userSupply>();
      j = campaign.getIndex();
      userSupply user;
      // Looking for a user connected to this campaign.
      while (userNotFound && (user = usersQueue.poll()) != null) { 
        i = user.getIndex();
        if (market.isConnected(i, j) && (Math.floor(market.getCampaign(j).getLevel() * market.getUser(i).getSupply()) - allocation[i][j] >= this.stepSize)) {
          // This user is connected to this campaign, allocate it.
          allocation[i][j] += this.stepSize;
          this.currentAllocation[j] += this.stepSize;
          user.decrementSupply(this.stepSize);
          userNotFound = false;
        }
        if (user.getSupply() >= this.stepSize) {
          //This user still has some to give.
          auxUserSupplyList.add(user);
        }
      }
      // Add users back to the queue.
      for (userSupply u : auxUserSupplyList) {
        usersQueue.add(u);
      }
      /*
       * If we did found a user connected to this campaign and the campaign
       * still requires some, then compute its value.
       */
      if (!userNotFound && this.currentAllocation[j] + this.stepSize <= market.getCampaign(j).getDemand()) {
        double value = this.f.getObjective(market.getCampaign(j).getReward(),market.getCampaign(j).getDemand(), this.currentAllocation[j] + this.stepSize)
            - this.f.getObjective(market.getCampaign(j).getReward(), market.getCampaign(j).getDemand(), this.currentAllocation[j]) - market.getCampaign(j).getReserve() * this.stepSize;
        if (value > 0) { // If value of the campaign is positive, then allocate.
          campaign.updateValue(value);
          campaignQueue.add(campaign);
        }
      }
      userNotFound = true;
    }
    return new MarketAllocation(market, allocation, this.f);
  }

  /**
   * This auxiliary class represents a user and its supply.
   * 
   * @author Enrique Areyan Viqueira
   */
  public class userSupply {
    
    /**
     * User index.
     */
    protected int i;
    
    /**
     * User supply.
     */
    protected int supply;

    /**
     * Constructor.
     * @param i - user index.
     * @param supply - user supply.
     */
    public userSupply(int i, int supply) {
      this.i = i;
      this.supply = supply;
    }

    /**
     * Getter.
     * @return user index.
     */
    public int getIndex() {
      return this.i;
    }

    /**
     * Getter.
     * @return user supply.
     */
    public int getSupply() {
      return this.supply;
    }

    /**
     * Decrements this user supply.
     * @param supplyDecrement - by how much to decrement this user supply.
     */
    public void decrementSupply(int supplyDecrement) {
      this.supply -= supplyDecrement;
    }

    @Override
    public String toString() {
      return "(" + this.i + "," + this.supply + ")";
    }
    
  }

  /**
   * Implements comparator to compare user by supply.
   * @author Enrique Areyan Viqueira
   */
  public class UserSupplyComparator implements Comparator<userSupply> {
    @Override
    public int compare(userSupply u1, userSupply u2) {
      if (u1.getSupply() < u2.getSupply()) {
        return 1;
      } else if (u1.getSupply() > u2.getSupply()) {
        return -1;
      } else {
        return 0;
      }
    }
  }

  /**
   * This auxiliary class represents a campaign and its value.
   * @author Enrique Areyan Viqueira
   */
  public class campaignValue {
    
    /**
     * Campaign index.
     */
    protected int j;
    
    /**
     * Campaign value.
     */
    protected double value;

    /**
     * Constructor.
     * @param j - campaign index.
     * @param value - campaign value.
     */
    public campaignValue(int j, double value) {
      this.j = j;
      this.value = value;
    }

    /**
     * Getter.
     * @return campaign index.
     */
    public int getIndex() {
      return this.j;
    }

    /**
     * Getter.
     * @return campaign value.
     */
    public double getValue() {
      return this.value;
    }

    /**
     * Updates the value of a campaign.
     * @param value - the new value of the campaign.
     */
    public void updateValue(double value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return "(" + this.j + "," + this.value + ")";
    }
  }

  /**
   * Comparator to compare campaigns by value.
   * @author Enrique Areyan Viqueira
   */
  public class CampaignValueComparator implements Comparator<campaignValue> {
    @Override
    public int compare(campaignValue c1, campaignValue c2) {
      if (c1.getValue() < c2.getValue()) {
        return 1;
      } else if (c1.getValue() > c2.getValue()) {
        return -1;
      } else {
        return 0;
      }
    }
  }

  /**
   * Implements AllocationAlgoInterface
   */
  @Override
  public ObjectiveFunction getObjectiveFunction() {
    return this.f;
  }
  
}