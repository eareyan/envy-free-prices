package util;

import ilog.concert.IloException;
import ilog.cp.IloCP;
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
   * CP (constraint programming) unique object.
   */
  private static IloCP cpUniqueObject;

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

  /**
   * Returns a unique instance of CP model.
   * 
   * @return
   * @throws IloException
   */
  public static IloCP getCP() throws IloException {
    if (Cplex.cpUniqueObject == null) {
      Cplex.cpUniqueObject = new IloCP();
    } else {
      Cplex.cpUniqueObject.clearModel();
    }
    return Cplex.cpUniqueObject;
  }

}
