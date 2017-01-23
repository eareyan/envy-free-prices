package structures.factory;

import java.util.Random;
import java.util.concurrent.Callable;

import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * Implements different ways to generate rewards.
 * 
 * @author Enrique Areyan Viqueira
 */
public class RewardsGenerator {

  /**
   * Random number generator.
   */
  private static Random generator = new Random();

  /**
   * Generates a random reward between the bounds.
   * 
   * @return a random reward between allowable bounds.
   */
  public static Callable<Double> getRandomUniformRewardFunction() {
    return new Callable<Double>() {
      public Double call() {
        return RewardsGenerator.generator.nextDouble() * (Parameters.defaultMaxReward - Parameters.defaultMinReward) + Parameters.defaultMinReward;
      }
    };
  }

  /**
   * Produces an elitist reward defined as: with probability Parameters.elitistProbReward, the reward is Normal(Parameters.elitistMeanReward,
   * Parameters.elitistStDevReward) i.e., a big reward with Parameters.elitistProbReward probability. O/w, with probability (1-Parameters.elitistProbReward) the
   * reward is drawn from uniform.
   * 
   * @return a double.
   * @throws Exception
   */
  public static Callable<Double> getElitistRewardFunction() throws Exception {
    return new Callable<Double>() {
      public Double call() throws Exception {
        return (RewardsGenerator.generator.nextDouble() <= Parameters.elitistProbReward) ? new NormalDistribution(Parameters.elitistMeanReward,
            Parameters.elitistStDevReward).sample() : RewardsGenerator.getRandomUniformRewardFunction().call();
      }
    };
  }
  
}
