package structures;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import structures.exceptions.BidderCreationException;

/**
 * Represents a single bidder.
 * A bidder here refers to a size-interchangeable bidder
 * (see paper for definition).
 * 
 * @author Enrique Areyan Viqueira
 */
public class Bidder<G extends Goods> {
  
  /**
   * Demand of this bidder.
   */
  protected final int demand;
  
  /**
   * Reward of this bidder.
   */
  protected final double reward;
  
  /**
   * Demand set
   */
  protected final ImmutableSet<G> demandSet;

  /**
   * Constructor.
   * 
   * @param demand - the bidder demand.
   * @param reward - the bidder reward.
   * @param demandSet - the bidder demand set.
   * @throws BidderCreationException in case the bidder could not be created.
   */
  public Bidder(int demand, double reward, Set<G> demandSet) throws BidderCreationException {
    if (demand <= 0) {
      throw new BidderCreationException("The demand of a bidder must be a positive integer.");
    }
    this.demand = demand;
    if (reward <= 0) {
      throw new BidderCreationException("The reward of a bidder must be a positive double.");
    }
    this.reward = reward;
    if (demandSet == null) {
      throw new BidderCreationException("The demand set of a bidder cannot be null");
    }
    this.demandSet = ImmutableSet.copyOf(demandSet);
  }
  
  /**
   * Getter.
   * 
   * @return this bidder's demand.
   */
  public int getDemand() {
    return this.demand;
  }

  /**
   * Getter.
   * 
   * @return this bidder's reward.
   */
  public double getReward() {
    return this.reward;
  }
  
  /**
   * Getter.
   * 
   * @return this bidder's demand set.
   */
  public ImmutableSet<G> getDemandSet() {
    return this.demandSet;
  }
  
  /**
   * This method answer the question:
   * Does this bidder wants the good given as parameter?
   * 
   * @param good - the good which we query this bidder demand set.
   * @return true if this bidder wants this good and false otherwise.
   */
  public boolean demandsGood(G good){
    return this.demandSet.contains(good);
  }
  
  @Override
  public String toString() {
    return "(I = " + this.getDemand() + ", R = " + this.reward + ")";
  }
}
