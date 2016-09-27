package structures;

import structures.exceptions.BidderCreationException;

/**
 * Represents a single bidder.
 * A bidder here refers to a size-interchangeable bidder
 * (see paper for definition).
 * 
 * @author Enrique Areyan Viqueira
 */
public class Bidder {
  
  /**
   * Demand of this bidder.
   */
  protected int demand;
  
  /**
   * Reward of this bidder.
   */
  protected double reward;
  
  /** 
   * User access level. Indicates what percentage of visibility the campaign has
   * across all users.
   */
  protected double level = 1.0;
  
  /**
   * Reserve Price. Indicates the minimum amount a campaign must pay per
   * impression.
   */
  protected double reserve = 0.0;
  
  /** 
   * Backpointer to another campaign, default value -1 means no backpointer.
   */
  protected int backpointer = -1;
  /**
   * Priority. This is used to have a notion of priority among campaigns.
   */
  protected int priority = -1;
  /**
   * How many users allocated so far to this campaign.
   */
  protected int allocationSoFar = 0;

  /**
   * Constructor.
   * @param demand - the bidder demand.
   * @param reward - the bidder reward.
   * @throws BidderCreationException in case the bidder could not be created.
   */
  public Bidder(int demand, double reward) throws BidderCreationException {
    if (demand <= 0) {
      throw new BidderCreationException(
          "The demand of a bidder must be a positive integer");
    }
    this.demand = demand;
    if (reward <= 0) {
      throw new BidderCreationException(
          "The reward of a bidder must be a positive double");
    }
    this.reward = reward;
  }

  /**
   * Constructor.
   * @param demand - the bidder demand.
   * @param reward - the bidder reward.
   * @param level - the campaign level.
   * @param reserve - the campaign reserve.
   * @throws BidderCreationException in case the campaign could not be created.
   */
  public Bidder(int demand, double reward, double level, double reserve)
      throws BidderCreationException {
    this(demand, reward);
    if (reserve < 0.0) {
      throw new BidderCreationException(
          "The reserve price of a campaign must be a positive double at least zero");
    }
    this.level = level;
    this.reserve = reserve;
  }

  /**
   * Constructor.
   * @param demand - the bidder demand.
   * @param reward - the bidder reward.
   * @param backpointer - the campaign backpointer.
   * @throws BidderCreationException in case the campaign could not be created.
   */
  public Bidder(int demand, double reward, int backpointer)
      throws BidderCreationException {
    this(demand, reward);
    this.backpointer = backpointer;
  }

  /**
   * Constructor.
   * @param demand - the bidder demand.
   * @param reward - the bidder reward.
   * @param backpointer - the campaign backpointer.
   * @param priority - the campaign priority.
   * @throws BidderCreationException in case the campaign could not be created.
   */
  public Bidder(int demand, double reward, int backpointer, int priority)
      throws BidderCreationException {
    this(demand, reward, backpointer);
    this.priority = priority;
  }

  /**
   * Constructor.
   * @param demand - the campaign demand.
   * @param reward - the campaign reward.
   * @param level - the campaign level.
   * @param reserve - the campaign reserve.
   * @param backpointer - the campaign backpointer.
   * @param priority - the campaign priority.
   * @throws BidderCreationException in case the campaign could not be created.
   */
  public Bidder(int demand, double reward, double level, double reserve, int backpointer, int priority) throws BidderCreationException {
    this(demand, reward, backpointer, priority);
    this.level = level;
    this.reserve = reserve;
  }

  /**
   * Constructor.
   * @param demand - the campaign demand.
   * @param reward - the campaign reward.
   * @param level - the campaign level.
   * @param reserve - the campaign reserve.
   * @param backpointer - the campaign backpointer.
   * @param priority - the campaign priority.
   * @param allocationSoFar - the campaign allocation so far.
   * @throws BidderCreationException in case the campaign could not be created.
   */
  public Bidder(int demand, double reward, double level, double reserve, int backpointer, int priority, int allocationSoFar) throws BidderCreationException {
    this(demand, reward, level, reserve, backpointer, priority);
    if (allocationSoFar < 0) {
      throw new BidderCreationException(
          "The allocation so far must be a positive (possibly zero) integer");
    }
    this.allocationSoFar = allocationSoFar;
  }
  
  /**
   * Getter.
   * @return this bidder demand.
   */
  public int getDemand() {
    return this.demand;
  }

  /**
   * Getter.
   * @return this bidder reward.
   */
  public double getReward() {
    return this.reward;
  }

  /**
   * Getter.
   * @return this campaign level.
   */
  public double getLevel() {
    return this.level;
  }

  /**
   * Getter.
   * @return this campaign reserve.
   */
  public double getReserve() {
    return this.reserve;
  }

  /**
   * Getter.
   * @return this campaign backpointer.
   */
  public int getBackpointer() {
    return this.backpointer;
  }

  /**
   * Getter.
   * @return this campaign priority.
   */
  public int getPriority() {
    return this.priority;
  }

  /**
   * Getter.
   * @return the allocation of this campaign so far.
   */
  public int getAllocationSoFar() {
    return this.allocationSoFar;
  }
  
  /**
   * Setter.
   * @param reward - bidder reward.
   */
  public void setReward(double reward) {
    this.reward = reward;
  }

  /**
   * Setter.
   * @param demand - bidder demand.
   */
  public void setDemand(int demand) {
    this.demand = demand;
  }

  /**
   * Setter.
   * @param reserve - campaign reserve.
   */
  public void setReserve(double reserve) {
    this.reserve = reserve;
  }

  @Override
  public String toString() {
    return "(Demand = " + this.getDemand() + ", Reward = " + this.reward
        + ", Backpointer = " + this.getBackpointer() + ", priority = "
        + this.getPriority() + ")";
  }
}
