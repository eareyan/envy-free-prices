package unitdemand;

import java.util.ArrayList;
import java.util.Collections;

/*
 * This class represents a link of the valuation matrix.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Link {
	/* Campaign Index */
	protected int j;
	/* Value of the link */
	protected double value;
	/*
	 * Constructor
	 */
	public Link(int j,double value){
		this.j = j;
		this.value = value;
	}
	/*
	 * Getters
	 */
	public int getJ(){
		return this.j;
	}
	public double getValue(){
		return this.value;
	}
	public String toString(){
		return "(j="+this.j+",value = "+this.value+")";
	}
	
	/*
	 * Edge valuations are the values of the maximum weight matching.
	 * This method receives a valuation matrix as input and outputs
	 * a list of links.
	 */
	public static ArrayList<Link> getEdgeValuations(double[][] valuationMatrix){
		/* First, find a maximum weight matching */
        int[] result  = new MWBMatchingAlgorithm(valuationMatrix).getMatching();
        ArrayList<Link> valuations = new  ArrayList<Link>();
        for(int i=0;i<result.length;i++){
        	if(result[i] > -1){
        		/* If an assignment is possible, then the valuation for item j is the weight of that assignment, otherwise is zero*/
        		valuations.add(new Link(result[i],valuationMatrix[i][result[i]]));
        	}
        }
        Collections.sort(valuations,new LinksComparatorByValue());//Order valuations in descending order
        //System.out.println("valuations = " + valuations);
		return valuations;
	}	
}
