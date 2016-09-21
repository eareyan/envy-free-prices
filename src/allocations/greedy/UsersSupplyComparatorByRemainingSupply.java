package allocations.greedy;

import java.util.Comparator;

/**
 * This class implements a comparator to compare users by their 
 * remaining supply.
 * @author Enrique Areyan Viqueira
 */
public class UsersSupplyComparatorByRemainingSupply implements Comparator<UserSupply> {
  /**
   * order 1 means ASC and -1 means DESC, any other means no order
   */
  protected int Order = 0;
  
  /**
   * Constructor. 
   * @param Order - the order in which to order users.
   */
  public UsersSupplyComparatorByRemainingSupply(int Order) {
    this.Order = Order;
  }

  /**
   * Constructor.
   */
  public UsersSupplyComparatorByRemainingSupply() {

  }

  @Override
  public int compare(UserSupply U1, UserSupply U2) {
    if (this.Order == -1) {
      if (U1.getRemainingSupply() < U2.getRemainingSupply()){
        return 1;
      }else if (U1.getRemainingSupply() > U2.getRemainingSupply()){
        return -1;
      }
    } else if (this.Order == 1) {
      if (U1.getRemainingSupply() < U2.getRemainingSupply()){
        return -1;
      }else if (U1.getRemainingSupply() > U2.getRemainingSupply()){
        return 1;
      }
    }
    return 0;
  }
  
}