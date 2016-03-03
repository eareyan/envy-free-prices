package algorithms.lp.reserveprices;

import java.util.ArrayList;

import algorithms.lp.reserveprices.interfaces.SelectUsers;
import structures.Market;

/*
 * This class implements interface SelectUsers and defines a very simple functionality
 * for method selectUsers where all users are selected
 * 
 * @author Enrique Areyan Viqueira
 */
public class SelectAllConnectedUsers implements SelectUsers{
	/*
	 * Selects all users
	 * @see algorithms.lp.reserveprices.AbstractLPReservePrices#selectUsers(int)
	 */
	@Override
	public ArrayList<Integer> selectUsers(int j,Market market) {
		ArrayList<Integer> users = new ArrayList<Integer>();
		for(int i=0;i<market.getNumberUsers();i++){
			users.add(i);
		}
		return users;
	}
}
