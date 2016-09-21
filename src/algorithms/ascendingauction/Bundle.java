package algorithms.ascendingauction;

import java.util.ArrayList;

/**
 * This class represents an entire bundle.
 * A bundle belongs to a campaign j and contains a list of bundle entries.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Bundle {
  
  /**
   * Campaign id
   */
  protected int j;
  
  /**
   * List of bundle entries
   */
  protected ArrayList<BundleEntry> bundle;

  /**
   * Constructor.
   * @param j
   * @param bundle
   */
  public Bundle(int j, ArrayList<BundleEntry> bundle) {
    this.j = j;
    this.bundle = bundle;
  }
  
  /**
   * Getter.
   * @return the campaign index.
   */
  public int getJ() {
    return this.j;
  }
  
  /**
   * Getter.
   * @return the bundle
   */
  public ArrayList<BundleEntry> getBundle() {
    return this.bundle;
  }
  
  @Override
  public String toString() {
    return "(j = " + this.j + "," + this.bundle + ")";
  }
  
}
