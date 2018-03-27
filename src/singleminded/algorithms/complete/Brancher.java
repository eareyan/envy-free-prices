package singleminded.algorithms.complete;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;

public class Brancher extends IloCplex.BranchCallback {
  
  private boolean atRoot;  // are we at the root node?
  
  public Brancher() {
    super();
    this.atRoot = true;
    System.out.println("*************Created brancher***************");
  }

  @Override
  protected void main() throws IloException {
    Object raw;
    raw = this.getNodeData();
    String msg = ">>> At node " + getNodeId() + ", " + this.atRoot + ", " + raw;
    System.out.println(msg);
    if (atRoot) {
      this.atRoot = false;
      //BranchInfo x = new BranchInfo(1);
      return;
    }
    
  }
}
