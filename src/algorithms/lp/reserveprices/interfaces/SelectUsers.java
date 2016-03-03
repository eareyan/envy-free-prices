package algorithms.lp.reserveprices.interfaces;

import java.util.ArrayList;

import structures.Market;

public interface SelectUsers {
	/*
	 * Given a campaign index j, return a list of users to which we want to set reserve prices
	 */
	public ArrayList<Integer> selectUsers(int j,Market market);

}
