package unitdemand.evpapprox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import unitdemand.MWBMatchingAlgorithm;
import unitdemand.Matching;
import unitdemand.MatchingComparatorBySellerRevenue;
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
	protected double[][] valuationMatrix;
	protected AbstractMaxWEQReservePrices MWRP;
	
	public EVPApproximation(double[][] valuationMatrix, AbstractMaxWEQReservePrices MWRP){
		this.valuationMatrix = valuationMatrix;
		this.MWRP = MWRP;
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
        		//valuations.add(new Link(-1,0.0));
        	}
        }
        Collections.sort(valuations,new LinksComparatorByValue());//Order valuations in descending order
        //System.out.println("valuations = " + valuations);
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
		ArrayList<Matching> setOfMatchings = new ArrayList<Matching>();
		/* For each item, run MaxWEQ_r with reserve prices given by the valuation*/
		for(Link valueLink: valuations){
			//System.out.println("================= reserve price from campaign ("+valueLink.getJ()+") = " + valueLink.getValue() + "++++++++++");
			Arrays.fill(reservePrices,valueLink.getValue());
			this.MWRP.setReservePrices(reservePrices);
			setOfMatchings.add(this.MWRP.Solve(valueLink.getJ()));
		}
		Collections.sort(setOfMatchings, new MatchingComparatorBySellerRevenue());
		//System.out.println(setOfMatchings);
		if(setOfMatchings.size()==0){
			setOfMatchings.add(new Matching());
		}
		return setOfMatchings.get(0);
	}
	/*
	 * This class represents a link of the valuation matrix.
	 */
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
			return "(j="+this.j+",value = "+this.value+")";
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
