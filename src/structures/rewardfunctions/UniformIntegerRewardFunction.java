package structures.rewardfunctions;

import structures.factory.Parameters;
import util.MyRandom;

public class UniformIntegerRewardFunction implements RewardsGeneratorInterface {
  /**
   * Reward function object
   */
  public static UniformIntegerRewardFunction singletonInstance = new UniformIntegerRewardFunction();

  /**
   * Generates a random reward between the bounds.
   * 
   * @return a random reward between allowable bounds.
   */
  @Override
  public Double getReward() {
    return (double) (MyRandom.generator.nextInt(Parameters.defaultMaxIntegerReward) + Parameters.defaultMinIntegerReward);
  }

}
