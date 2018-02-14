package structures.rewardfunctions;

import org.apache.commons.math3.distribution.NormalDistribution;

import structures.factory.Parameters;

public class ElitistRewardFunction implements RewardsGeneratorInterface {
  /**
   * Reward function object
   */
  public static ElitistRewardFunction singletonInstance = new ElitistRewardFunction();

  /**
   * Generates an elitist reward function, i.e., with probability p get a reward from a normal distribution with elititst parameters (see class Parameters) and
   * with probability 1-p get a uniform random reward.
   */
  @Override
  public Double getReward() {
    return (RewardsGeneratorInterface.generator.nextDouble() <= Parameters.elitistProbReward) ? new NormalDistribution(Parameters.elitistMeanReward, Parameters.elitistStDevReward).sample() : UniformRewardFunction.singletonInstance.getReward();
  }

}
