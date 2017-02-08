package util;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;

/**
 * A class with a singleton method to get the Cplex object.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Cplex {

  /**
   * Cplex object.
   */
  private static IloCplex cplexUniqueObject;

  /**
   * Returns a unique instance of cplex.
   * 
   * @return
   * @throws IloException
   */
  public static IloCplex getCplex() throws IloException {
    if (Cplex.cplexUniqueObject == null) {
      Cplex.cplexUniqueObject = new IloCplex();
    }
    Cplex.cplexUniqueObject.clearModel();
    return Cplex.cplexUniqueObject;
  }

}
