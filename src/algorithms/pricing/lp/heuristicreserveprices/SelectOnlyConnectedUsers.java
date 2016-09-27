package algorithms.pricing.lp.heuristicreserveprices;

import java.util.ArrayList;

import algorithms.pricing.lp.heuristicreserveprices.interfaces.SelectUsers;
import structures.Market;

/**
 * This class implements interface SelectUsers and defines a very simple functionality
 * for method selectUsers where we select a user only in case it is connected to the campaign
 * being considered.
 * 
 * @author Enrique Areyan Viqueira
 */
public class SelectOnlyConnectedUsers implements SelectUsers {

  /**
   * Select a user only in case it is connected to the current campaign.
   * 
   * @see algorithms.lp.reserveprices.AbstractLPReservePrices#selectUsers(int)
   */
  @Override
  public ArrayList<Integer> selectUsers(int j, Market market) {
    ArrayList<Integer> users = new ArrayList<Integer>();
    for (int i = 0; i < market.getNumberGoods(); i++) {
      if (market.isConnected(i, j)) {
        users.add(i);
      }
    }
    return users;
  }
  
}
