package structures.factory;

import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.cp.IloCP;
import util.Cplex;
import util.Printer;

public class CPBipartiteGraph {

  public static void main(String[] args) throws IloException {
    System.out.println("Creating bipartite graphs");
    CPBipartiteGraph.createBiPartiteGraph(3, 2);
  }

  public static void createBiPartiteGraph(int n, int m) throws IloException {
    IloCP cp = Cplex.getCP();
    IloNumVar[][] allocationMatrixVariable = new IloNumVar[n][];
    for (int j = 0; j < n; j++) {
      allocationMatrixVariable[j] = cp.intVarArray(m, 0, 1);
    }
    for (int i = 0; i < n; i++) {
      //IloLinearNumExpr expr = cp.linearNumExpr();
      for (int j = 0; j < m; j++) {
        cp.addGe(allocationMatrixVariable[i][j], 0);
        //expr.addTerm(1, allocationMatrixVariable[i][j]);
      }
      //cp.addGe(expr, 1);
    }
    cp.startNewSearch();

    while (cp.next()) {
      System.out.println("CP next");
      int[][] X = new int[n][m];
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
          X[i][j] = (int) cp.getValue(allocationMatrixVariable[i][j]);
        }
      }
      System.out.println("X = ");
      Printer.printMatrix(X);
    }
  }

}
