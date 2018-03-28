package util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * A centralized class for all objects related to random number generation.
 * 
 * @author Enrique Areyan Viqueira
 *
 */
public class MyRandom {
  /**
   * Random number generator.
   */
  public static final Random generator = new Random();
  
  /**
   * Computes a set of n distinct, random integers, between 0 and max. If n>=max, returns the set of integers 0...max
   *
   * @param n - the number of integers to produce.
   * @param max - the maximum value of any integer to be produced.
   * @return a list of integers.
   */
  public static Set<Integer> randomNumbers(int n, int max) {
    HashSet<Integer> generated = new HashSet<Integer>();
    if (n >= max) {
      // If we want more numbers than the max, it means we want all numbers from 1...max.
      for (int i = 0; i < max; i++) {
        generated.add(i);
      }
      return generated;
    } else {
      while (generated.size() < n) {
        Integer next = MyRandom.generator.nextInt(max);
        // As we're adding to a set, this will automatically do a containment check
        generated.add(next);
      }
    }
    return generated;
  }

}
