package experiments;

import ilog.concert.IloException;

import java.sql.SQLException;

import structures.exceptions.AllocationException;
import structures.exceptions.BidderCreationException;
import structures.exceptions.GoodsCreationException;
import structures.exceptions.GoodsException;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketCreationException;
import structures.exceptions.MarketOutcomeException;
import algorithms.pricing.error.PrincingAlgoException;
import allocations.error.AllocationAlgoException;
import log.SqlDB;

/**
 * Abstract class to test algorithms. 
 * 
 * @author Enrique Areyan Viqueira
 */
public abstract class Experiments {

  /**
   * Constructor. 
   * @param dbLogger
   * @throws Exception 
   */
  public void bulkTest(SqlDB dbLogger) throws Exception {

    for (int i = 2; i < RunParameters.numGoods; i++) {
      for (int j = 2; j < RunParameters.numBidder; j++) {
        for (int k = 1; k <= i; k++) {
          for(int b = 0 ; b < 2; b++) {
            System.out.print("(n, m, k, b) = (" + i + ", " + j + ", " + k + ","+ ((b % 2) == 0 )+")");
            this.runOneExperiment(i, j, k, (b % 2) == 0, dbLogger);
          }
        }
      }
    }
  }
  
  /**
   * This method is implemented by a particular type of experiment class. This
   * method receives the number of goods, number of bidders and probability, and
   * runs one experiments, saving the results in the database. This method
   * should also check if we have that result first before running the
   * experiment.
   * 
   * @param numGoods
   * @param numBidders
   * @param prob
   * @param b
   * @param dbLogger
   * @throws SQLException
   * @throws IloException
   * @throws AllocationAlgoException
   * @throws BidderCreationException
   * @throws MarketAllocationException
   * @throws MarketOutcomeException
   * @throws GoodsCreationException
   * @throws AllocationException
   * @throws GoodsException
   * @throws MarketCreationException
   * @throws PrincingAlgoException 
   * @throws Exception 
   */
  abstract public void runOneExperiment(int numGoods, int numBidders, int k, boolean uniform, SqlDB dbLogger) throws SQLException, IloException, AllocationAlgoException, BidderCreationException, MarketAllocationException, MarketOutcomeException, GoodsCreationException, GoodsException, AllocationException, MarketCreationException, PrincingAlgoException, Exception;

  /**
   * Main method to run experiments. 
   * 
   * @param args - command line arguments. 
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    RunParameters Parameters = new RunParameters(args);
    SqlDB dbLogger = new SqlDB(Parameters.dbProvider, Parameters.dbHost, Parameters.dbPort, Parameters.dbName, Parameters.dbUsername, Parameters.dbPassword);
    Parameters.experimentObject.bulkTest(dbLogger);
  }
  
}
