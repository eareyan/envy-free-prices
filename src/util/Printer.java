package util;

import java.text.DecimalFormat;

import statistics.PricesStatistics;
import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.MarketOutcome;
import structures.exceptions.MarketAllocationException;
import structures.exceptions.MarketOutcomeException;

/**
 * Library with common methods for printing information to the standard output.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Printer {

  /**
   * Nice printing for matrix of doubles.
   * 
   * @param matrix
   *          - a matrix of doubles.
   */
  public static void printMatrix(double[][] matrix) {
    DecimalFormat df = new DecimalFormat("#.00");
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[0].length; j++) {
        if (matrix[i][j] == Double.MAX_VALUE) {
          System.out.print("Inf \t");
        } else {
          System.out.print(df.format(matrix[i][j]) + "\t");
        }
      }
      System.out.print("\n");
    }
  }

  /**
   * Nice printing for matrix of integers.
   * 
   * @param matrix
   *          - a matrix of integers.
   */
  public static void printMatrix(int[][] matrix) {
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[0].length; j++) {
        if (matrix[i][j] == Integer.MAX_VALUE) {
          System.out.print("max \t ");
        } else {
          System.out.print(matrix[i][j] + "\t");
        }
      }
      System.out.print("\n");
    }
  }

  /**
   * Nice printing for matrix of booleans.
   * 
   * @param matrix
   *          - a matrix of booleans
   */
  public static void printMatrix(boolean[][] matrix) {
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[0].length; j++) {
        if (matrix[i][j]) {
          System.out.print("\t yes");
        } else {
          System.out.print("\t no");
        }
      }
      System.out.print("\n");
    }
  }

  /**
   * Nice printing for vector of booleans.
   * 
   * @param vector
   *          - a vector of booleans.
   */
  public static void printVector(boolean[] vector) {
    for (int i = 0; i < vector.length; i++) {
      if (vector[i]) {
        System.out.println("\t yes");
      } else {
        System.out.println("\t no");
      }
    }
  }

  /**
   * Nice printing for vector of doubles.
   * 
   * @param vector
   *          - a vector of doubles.
   */
  public static void printVector(double[] vector) {
    DecimalFormat df = new DecimalFormat("#.000000");
    for (int i = 0; i < vector.length; i++) {
      if (vector[i] == -1.0 * Double.MAX_VALUE) {
        System.out.print("-Inf \t");
      } else {
        System.out.print(df.format(vector[i]) + "\t");
      }
    }
    System.out.print("\n");
  }

  /**
   * Nice printing for vector of Double objects.
   * 
   * @param vector
   *          - a vector of Double objects.
   */
  public static void printVector(Double[] vector) {
    DecimalFormat df = new DecimalFormat("#.00");
    for (int i = 0; i < vector.length; i++) {
      if (vector[i] == -1.0 * Double.MAX_VALUE) {
        System.out.print("-Inf \t");
      } else {
        System.out.print(df.format(vector[i]) + "\t");
      }
    }
    System.out.print("\n");
  }

  /**
   * Nice printing for vector of integers.
   * 
   * @param vector
   *          - a vector of integers.
   */
  public static void printVector(int[] vector) {
    for (int i = 0; i < vector.length; i++) {
      System.out.print(vector[i] + "\t");
    }
    System.out.print("\n");
  }

  /**
   * Takes a market and prints basic information.
   * 
   * @param M
   */
  public static void PrintMarketInfo(Market<Goods, Bidder<Goods>> M) {
    System.out.println(M);
    System.out.println("Highest Reward = " + M.getHighestReward());
    System.out.println("Total Demand = " + M.getTotalDemand());
    System.out.println("Total Supply = " + M.getTotalSupply());
    System.out.println("S/D = " + M.getSupplyToDemandRatio());
  }

  /**
   * Takes a market outcome and prints basic information.
   * 
   * @param outcome
   * @throws MarketOutcomeException
   * @throws MarketAllocationException
   */
  public static void PrintOutcomeInfo(MarketOutcome<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> outcome) throws MarketOutcomeException,
      MarketAllocationException {
    /* Statistics for the allocation. */
    outcome.getMarketAllocation().printAllocation();
    outcome.printPrices();
    System.out.println("Seller Revenue = " + outcome.sellerRevenue());
    System.out.println("Value = " + outcome.getMarketAllocation().getValue());
    for (Goods good : outcome.getMarketAllocation().getMarket().getGoods()) {
      System.out.println("G = " + outcome.getMarketAllocation().allocationFromGood(good));
    }
    for (Bidder<Goods> bidder : outcome.getMarketAllocation().getMarket().getBidders()) {
      System.out.println("B = " + outcome.getMarketAllocation().allocationToBidder(bidder) + " " + outcome.getMarketAllocation().isBidderBundleZero(bidder));
    }
    /* Statistics for the prices. */
    PricesStatistics<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>> pricesStatistics = new PricesStatistics<Market<Goods, Bidder<Goods>>, Goods, Bidder<Goods>>(
        outcome);
    System.out.println("MC Violations : " + pricesStatistics.getMarketClearanceViolations().getKey() + ","
        + pricesStatistics.getMarketClearanceViolations().getValue());
    System.out.println("EF violations : " + pricesStatistics.numberOfEnvyBidders());
  }

}
