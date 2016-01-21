package test;

import java.util.ArrayList;

import algorithms.EfficientAllocationLP;
import structures.Market;
import structures.MarketFactory;

/*
 * Main class. Use for testing purposes.
 */
public class Main {
	public static void main(String[] args){
		System.out.println("Envy-free prices testing");
		Market M = MarketFactory.randomMarket(10,10, 0.25);
		System.out.println(M);
		EfficientAllocationLP E = new EfficientAllocationLP(M);
		E.Solve();
		ArrayList<int[][]> efficAllocations =  E.Solve();
		if(efficAllocations.size()>0){
			System.out.println("There are "+ efficAllocations.size() + " efficient allocations");
		}
	}
}
