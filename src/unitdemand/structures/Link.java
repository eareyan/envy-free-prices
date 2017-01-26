package unitdemand.structures;

import java.util.ArrayList;
import java.util.Collections;

import unitdemand.algorithms.MWBMatchingAlgorithm;

/**
 * This class represents a link of the valuation matrix.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Link {

  /**
   * Bidder Index
   */
  private final int j;

  /**
   * Value of the link
   */
  private final double value;

  /**
   * Constructor.
   * 
   * @param j
   *          - bidder index.
   * @param value
   *          - link value.
   */
  public Link(int j, double value) {
    this.j = j;
    this.value = value;
  }

  /**
   * Getter.
   * 
   * @return bidder index.
   */
  public int getJ() {
    return this.j;
  }

  /**
   * Getter.
   * 
   * @return link value.
   */
  public double getValue() {
    return this.value;
  }

  @Override
  public boolean equals(Object l) {
    return (l.getClass() == this.getClass()) && this.j == ((Link) l).getJ() && this.value == ((Link) l).getValue();
  }

  @Override
  public String toString() {
    return "(j = " + this.j + ", value = " + this.value + ")";
  }

  /**
   * Edge valuations are the values of the maximum weight matching. This method receives a valuation matrix as input and outputs a list of links.
   * 
   * @param valuationMatrix
   * @return an Arraylist of Link objects.
   */
  public static ArrayList<Link> getEdgeValuations(double[][] valuationMatrix) {
    // First, find a maximum weight matching.
    int[] result = new MWBMatchingAlgorithm(valuationMatrix).getMatching();
    ArrayList<Link> valuations = new ArrayList<Link>();
    for (int i = 0; i < result.length; i++) {
      if (result[i] > -1) {
        // If an assignment is possible, then the valuation for item j is the weight of that assignment, otherwise is zero
        valuations.add(new Link(result[i], valuationMatrix[i][result[i]]));
      }
    }
    // Order valuations in descending order of value.
    Collections.sort(valuations, new LinksComparatorByValue());
    // System.out.println("valuations = " + valuations);
    return valuations;
  }

}
