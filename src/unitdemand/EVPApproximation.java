package unitdemand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import util.Printer;

/*
 * Implements Envy-Free Pricing Approximation Algorithm by Guruswami et. al.
 * 
 *  @author Enrique Areyan Viqueira
 */
public class EVPApproximation {
	/*
	 * valuation matrix. Provides a valuation v_ij of user i by campaign j.
	 */	
	double[][] valuationMatrix;
	
	public EVPApproximation(double[][] valuationMatrix){
		this.valuationMatrix = valuationMatrix;
	}

	/*
	 * Edge valuations are the values of the maximum weight matching.
	 * This will be used as the reserve prices.
	 */
	public ArrayList<Link> getEdgeValuations(){
		/* First, find a maximum weight matching */
        int[] result  = new MWBMatchingAlgorithm(this.valuationMatrix).getMatching();
        ArrayList<Link> valuations = new  ArrayList<Link>();
        for(int i=0;i<result.length;i++){
        	if(result[i] > -1){
        		/* If an assignment is possible, then the valuation for item j is the weight of that assignment, otherwise is zero*/
        		valuations.add(new Link(result[i],this.valuationMatrix[i][result[i]]));
        	}else{
        		valuations.add(new Link(-1,0.0));
        	}
        }
        Collections.sort(valuations,new LinksComparatorByValue());//Order valuations in descending order
        System.out.println("valuations = " + valuations);
		return valuations;
	}
	/*
	 * Implements Envy-Free Pricing Approximation Algorithm as stated in Guruswami et al.
	 */
	public Matching Solve(){
		//System.out.println("============ SOLVE ============");
		 ArrayList<Link> valuations = this.getEdgeValuations();
		double[] reservePrices = new double[this.valuationMatrix.length];
		//Printer.printVector(valuations);
		ArrayList<Matching> setOfMatchingsBaseline = new ArrayList<Matching>();
		ArrayList<Matching> setOfMatchingsIdea1 = new ArrayList<Matching>();
		ArrayList<Matching> setOfMatchingsIdea2 = new ArrayList<Matching>();
		ArrayList<Matching> setOfMatchingsIdea3 = new ArrayList<Matching>();
		/* For each item, run MaxWEQ_r with reserve prices given by the valuation*/
		for(int i=0;i<this.valuationMatrix.length;i++){
			System.out.println("================= reserve price("+i+") = " + valuations.get(i).getValue() + "++++++++++");
			Arrays.fill(reservePrices,valuations.get(i).getValue());
			setOfMatchingsBaseline.add(new MaxWEQReservePrices(this.valuationMatrix,reservePrices).Solve());
			if(valuations.get(i).getJ()>-1){
				Idea1 idea1 = new Idea1(this.valuationMatrix,reservePrices);
				idea1.setCurrentCampaignIndex(valuations.get(i).getJ());
				setOfMatchingsIdea1.add(idea1.Solve());

				Idea2 idea2 = new Idea2(this.valuationMatrix,reservePrices);
				idea2.setCurrentCampaignIndex(valuations.get(i).getJ());
				setOfMatchingsIdea2.add(idea2.Solve());

				Idea3 idea3 = new Idea3(this.valuationMatrix,reservePrices);
				idea3.setCurrentCampaignIndex(valuations.get(i).getJ());
				setOfMatchingsIdea3.add(idea3.Solve());
}
		}
		System.out.println("*****EVPApproximation*******");		
		Collections.sort(setOfMatchingsBaseline,new MatchingComparatorBySellerRevenue());
		System.out.println(setOfMatchingsBaseline);
		/*Printer.printMatrix(this.valuationMatrix);
		System.out.println("Prices for max revenue");
		Printer.printVector(setOfMatchingsBaseline.get(0).getPrices());*/
		System.out.println("Matching for max revenue");
		Printer.printMatrix(setOfMatchingsBaseline.get(0).getMatching());
		Printer.printVector(setOfMatchingsBaseline.get(0).getPrices());
		//System.out.println("Max revenue = " + setOfMatchingsBaseline.get(0).getSellerRevenue());
		System.out.println("*****Idea1*******");		
		Collections.sort(setOfMatchingsIdea1,new MatchingComparatorBySellerRevenue());
		System.out.println(setOfMatchingsIdea1);
		System.out.println("Matching for max revenue");
		Printer.printMatrix(setOfMatchingsIdea1.get(0).getMatching());
		Printer.printVector(setOfMatchingsIdea1.get(0).getPrices());
		System.out.println("*****Idea2*******");		
		Collections.sort(setOfMatchingsIdea2,new MatchingComparatorBySellerRevenue());
		System.out.println(setOfMatchingsIdea2);
		System.out.println("Matching for max revenue");
		Printer.printMatrix(setOfMatchingsIdea2.get(0).getMatching());
		Printer.printVector(setOfMatchingsIdea2.get(0).getPrices());
		System.out.println("*****Idea3*******");		
		Collections.sort(setOfMatchingsIdea3,new MatchingComparatorBySellerRevenue());
		System.out.println(setOfMatchingsIdea3);
		System.out.println("Matching for max revenue");
		Printer.printMatrix(setOfMatchingsIdea3.get(0).getMatching());
		Printer.printVector(setOfMatchingsIdea3.get(0).getPrices());
		/*if(setOfMatchingsBaseline.get(0).getSellerRevenue() - setOfMatchingsIdea1.get(0).getSellerRevenue()>0.1){
		System.out.println("They are different!");
		System.exit(-1);
		}*/
		if(Math.abs(setOfMatchingsIdea2.get(0).getSellerRevenue() - setOfMatchingsIdea3.get(0).getSellerRevenue())>0.000001){
			System.out.println("They are different!");
			System.exit(-1);
		}
		return setOfMatchingsBaseline.get(0);
	}
	
	public class Link{
		protected int j;
		protected double value;
		
		public Link(int j,double value){
			this.j = j;
			this.value = value;
		}
		public int getJ(){
			return this.j;
		}
		public double getValue(){
			return this.value;
		}
		public String toString(){
			return "j="+this.j+",value = "+this.value;
		}
	}
	public class LinksComparatorByValue implements Comparator<Link>{
		@Override
		public int compare(Link l1, Link l2) {
			if(l1.getValue() < l2.getValue()) return 1;
			if(l1.getValue() > l2.getValue()) return -1;
			return 0;
		}
	}	
}
