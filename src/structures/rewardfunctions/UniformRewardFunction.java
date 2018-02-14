package structures.rewardfunctions;

import structures.factory.Parameters;

public class UniformRewardFunction implements RewardsGeneratorInterface {
  /**
   * Reward function object
   */
  public static UniformRewardFunction singletonInstance = new UniformRewardFunction();

  /**
   * Generates a random reward between the bounds.
   * 
   * @return a random reward between allowable bounds.
   */
  @Override
  public Double getReward() {
    return RewardsGeneratorInterface.generator.nextDouble() * (Parameters.defaultMaxReward - Parameters.defaultMinReward) + Parameters.defaultMinReward;
  }
}
