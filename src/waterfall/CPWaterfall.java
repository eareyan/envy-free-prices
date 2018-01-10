package waterfall;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cp.IloCP;

import java.util.HashMap;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.factory.RandomMarketFactory;
import util.Cplex;
import util.Printer;

/**
 * A class that implements the Waterfall CP program.
 *
 */
public class CPWaterfall<M extends Market<G, B>, G extends Goods, B extends Bidder<G>> {

  /**
   * Market object in which to run the CPWaterfall.
   */
  private final M market;

  /**
   * CP object.
   */
  protected IloCP cp;

  /**
   * Constructor.
   * 
   * @param market
   * @throws IloException
   */
  public CPWaterfall(M market) throws IloException {
    this.market = market;
    this.cp = Cplex.getCP();
    //this.cp.setOut(null);
  }

  public void run() throws IloException {
    System.out.println("Run the CP Waterfall model");

    // These next two maps point from a good (resp. a bidder) to a positive integer.
    // These maps are used to point from a bidder to its CPLEX variable.
    HashMap<G, Integer> goodToCPLEXIndex = new HashMap<G, Integer>();
    HashMap<B, Integer> bidderToCPLEXIndex = new HashMap<B, Integer>();
    for (int i = 0; i < market.getNumberGoods(); i++) {
      goodToCPLEXIndex.put(market.getGoods().get(i), i);
    }
    for (int j = 0; j < market.getNumberBidders(); j++) {
      bidderToCPLEXIndex.put(market.getBidders().get(j), j);
    }

    // -- Variables

    // Allocation matrix variable
    IloNumVar[][] allocationMatrixVariable = new IloNumVar[market.getNumberGoods()][];
    for (G good : market.getGoods()) {
      allocationMatrixVariable[goodToCPLEXIndex.get(good)] = this.cp.intVarArray(market.getNumberBidders(), 0, good.getSupply());
    }

    // Construct the domain of the order allocation matrix
    int[] domain = new int[market.getNumberBidders() + 1];
    for (int j = 0; j < market.getNumberBidders(); j++) {
      domain[j] = j;
    }
    domain[market.getNumberBidders()] = -1;
    // Order allocation matrix
    IloNumVar[][] orderMatrixVariable = new IloNumVar[market.getNumberGoods()][market.getNumberBidders()];
    for (G good : market.getGoods()) {
      for (B bidder : market.getBidders()) {
        orderMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)] = this.cp.intVar(domain);
        // System.out.println(orderMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)]);
      }
    }

    // Prices variables
    IloNumVar[][] pricesMatrixVariable = new IloNumVar[market.getNumberGoods()][market.getNumberBidders()];
    for (G good : market.getGoods()) {
      for (B bidder : market.getBidders()) {
        pricesMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)] = this.cp.numVar(0, market.getHighestReward(), IloNumVarType.Int);
        //pricesMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)] = this.cp.numVar(0, market.getHighestReward(), IloNumVarType.Float);
        //pricesMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)] = this.cp.intVar(0, Integer.MAX_VALUE);
        // Bound the variables, otherwise cplex complains that there are uninitialized variables
        this.cp.addGe(pricesMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)], 0.0);
        this.cp.addLe(pricesMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)], market.getHighestReward());
      }
    }
    // Indicator variable
    IloNumVar[] indicatorVariable = this.cp.intVarArray(market.getNumberBidders(), 0, 1);

    // -- Constraints

    // (1) Capacity constraint
    for (G good : market.getGoods()) {
      IloLinearNumExpr expr = this.cp.linearNumExpr();
      for (B bidder : market.getBidders()) {
        expr.addTerm(1.0, allocationMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)]);
      }
      this.cp.addLe(expr, good.getSupply());
    }

    // (2) All-or-nothing allocation; and connectivity constraint
    for (B bidder : market.getBidders()) {
      IloLinearNumExpr expr = this.cp.linearNumExpr();
      for (G good : market.getGoods()) {
        if (bidder.demandsGood(good)) {
          expr.addTerm(1, allocationMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)]);
        } else {
          this.cp.addEq(0, allocationMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)]);
        }
      }
      this.cp.add(this.cp.equiv(this.cp.eq(1, indicatorVariable[bidderToCPLEXIndex.get(bidder)]), this.cp.eq(bidder.getDemand(), expr)));
      this.cp.add(this.cp.equiv(this.cp.eq(0, indicatorVariable[bidderToCPLEXIndex.get(bidder)]), this.cp.eq(0, expr)));
    }

    // (3) Order Constraints
    for (G good : market.getGoods()) {
      for (B bidder1 : market.getBidders()) {
        this.cp.add(this.cp.equiv(this.cp.eq(-1, orderMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder1)]), this.cp.eq(0, allocationMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder1)])));
        for (B bidder2 : market.getBidders()) {
          if (bidder1 != bidder2) {
            this.cp.add(this.cp.ifThen(this.cp.eq(orderMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder1)],
                orderMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder2)]), this.cp.eq(-1,
                orderMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder1)])));
          }
        }
      }
    }

    // (4) Prices constraints
    for (B bidder : market.getBidders()) {
      IloNumExpr expr = this.cp.linearNumExpr();
      for (G good : market.getGoods()) {
        expr = this.cp.sum(expr, this.cp.prod(pricesMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)], allocationMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)]));
      }
      // Individual rationality
      this.cp.addLe(expr, bidder.getReward());
    }

    // (5) Prices and order constraints. This is what makes waterfall prices.
    for (G good : market.getGoods()) {
      for (B bidder1 : market.getBidders()) {
        this.cp.ifThen(this.cp.eq(orderMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder1)], -1), this.cp.eq(pricesMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder1)], Integer.MAX_VALUE));
        for (B bidder2 : market.getBidders()) {
          if (bidder1 != bidder2) {
            this.cp.add(
                this.cp.ifThen(
                    this.cp.and(
                        this.cp.not(this.cp.eq(-1, orderMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder2)])),
                        this.cp.le(orderMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder1)], orderMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder2)])), 
                    this.cp.ge(pricesMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder1)], pricesMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder2)])));
          }
        }
      }
    }

    // Objective
    IloNumExpr obj = this.cp.linearNumExpr();
    for (B bidder : market.getBidders()) {
      //obj = this.cp.sum(obj, this.cp.prod(bidder.getReward(), indicatorVariable[bidderToCPLEXIndex.get(bidder)]));
      for (G good : market.getGoods()) {
        obj = this.cp.sum(obj, this.cp.prod(pricesMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)],
            allocationMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)]));
      }
    }

    this.cp.addMaximize(obj);
    // this.cp.startNewSearch();

    // while (this.cp.next()) {
    if (this.cp.solve()) {
      // System.out.println("SOLVED CP! with value " + this.cp.getObjValue());
      int[][] X = new int[market.getNumberGoods()][market.getNumberBidders()];
      int[][] T = new int[market.getNumberGoods()][market.getNumberBidders()];
      double[][] P = new double[market.getNumberGoods()][market.getNumberBidders()];
      int[] y = new int[market.getNumberBidders()];
      for (B bidder : market.getBidders()) {
        y[bidderToCPLEXIndex.get(bidder)] = (int) Math.round(this.cp.getValue(indicatorVariable[bidderToCPLEXIndex.get(bidder)]));
        for (G good : market.getGoods()) {
          X[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)] = (int) Math
              .round(this.cp.getValue(allocationMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)]));
          T[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)] = (int) Math
              .round(this.cp.getValue(orderMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)]));
          P[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex.get(bidder)] = this.cp.getValue(pricesMatrixVariable[goodToCPLEXIndex.get(good)][bidderToCPLEXIndex
              .get(bidder)]);
        }
      }
      System.out.println("X = ");
      Printer.printMatrix(X);
      System.out.println("\nT = ");
      Printer.printMatrix(T);
      System.out.println("\nP = ");
      Printer.printMatrix(P);
      System.out.println("\ny = ");
      Printer.printVector(y);

      // } else {
      // System.out.println("No solution!");
      // }
    }
    this.cp.end();
  }

  public static void main(String[] args) throws Exception {
    System.out.println("Testing CP Waterfall algorithm");
    // Market<Goods, Bidder<Goods>> market = RandomMarketFactory.generateUniformRewardOverDemandedMarket(4, 4, 0.75, 2);
    Market<Goods, Bidder<Goods>> market = RandomMarketFactory.generateUniformRewardOverDemandedMarket(2, 3, 1.0, 1);
    // Market<Goods, Bidder<Goods>> market = RandomMarketFactory.generateUniformRewardOverDemandedMarket(4, 2, 1.0, -2);
    System.out.println(market);
    CPWaterfall<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> cpWaterfall = new CPWaterfall<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(market);
    cpWaterfall.run();
    
    Waterfall<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> wf = new Waterfall<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(market);
    WaterfallSolution<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> x = wf.run();
    System.out.println("Waterfall Solution:");
    x.printAllocationTable();
    x.printPricesTable();
  }

}
