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
 * Note that this class receives and object AbstractMaxWEQReservePrices that defines
 * how and where dummies are to be included. The original Guruswami et. al. implementation
 * uses class AllConnectedDummies in this same package.
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
		this.MWRP.setValuationMatrix(valuationMatrix);
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
		valuations.add(new Link(-1,0.0)); //We add reserve price of zero always. This makes EVPApp at least as good as MaxWEQ.
		//System.out.println("valuations = " + valuations);
		double[] reservePrices = new double[this.valuationMatrix.length];
		ArrayList<Matching> setOfMatchings = new ArrayList<Matching>();
		/* For each item, run MaxWEQ_r with reserve prices given by the valuation*/
		for(Link valueLink: valuations){
			//System.out.println("================= reserve price from campaign ("+valueLink.getJ()+") = " + valueLink.getValue() + "++++++++++");
			Arrays.fill(reservePrices,valueLink.getValue());
			this.MWRP.setReservePrices(reservePrices);
			Matching x = this.MWRP.Solve(valueLink.getJ());
			setOfMatchings.add(x);
			/*System.out.println("''''''");
			Printer.printMatrix(x.getMatching());
			Printer.printVector(x.getPrices());
			System.out.println(x.getSellerRevenue());
			System.out.println("''''''");	*/		
		}
		Collections.sort(setOfMatchings, new MatchingComparatorBySellerRevenue());
		//System.out.println(setOfMatchings);
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
