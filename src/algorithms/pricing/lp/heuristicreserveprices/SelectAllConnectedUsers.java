package algorithms.pricing.lp.heuristicreserveprices;

import java.util.ArrayList;

import algorithms.pricing.lp.heuristicreserveprices.interfaces.SelectUsers;
import structures.Market;

/**
 * This class implements interface SelectUsers and defines a very simple functionality
 * for method selectUsers where all users are selected
 * 
 * @author Enrique Areyan Viqueira
 */
public class SelectAllConnectedUsers implements SelectUsers {
  
  /**
   * Selects all users
   * 
   * @see algorithms.lp.reserveprices.AbstractLPReservePrices#selectUsers(int)
   */
  @Override
  public ArrayList<Integer> selectUsers(int j, Market market) {
    ArrayList<Integer> users = new ArrayList<Integer>();
    for (int i = 0; i < market.getNumberGoods(); i++) {
      users.add(i);
    }
    return users;
  }
  
}
