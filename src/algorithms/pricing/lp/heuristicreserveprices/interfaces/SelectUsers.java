package algorithms.pricing.lp.heuristicreserveprices.interfaces;

import java.util.ArrayList;

import structures.Market;

/**
 * This interface SelectUsers defines one method selectUsers.
 * Implementing classes should define how users are selected when setting reserve prices.
 * 
 * @author Enrique Areyan Viqueira
 */
public interface SelectUsers {
  
  /**
   * Given a campaign index j, return a list of users to which we want to set
   * reserve prices.
   * @param j - a campaign index.
   * @param market - a market object.
   * @return an ArrayList of integers corresponding to selected user indices.
   */
  public ArrayList<Integer> selectUsers(int j, Market market);

}
