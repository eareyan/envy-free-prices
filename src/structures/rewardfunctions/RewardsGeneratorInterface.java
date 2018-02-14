package structures.rewardfunctions;

import java.util.Random;

public interface RewardsGeneratorInterface {
  /**
   * Random number generator.
   */
  public static Random generator = new Random();

  /**
   * A reward function must implement this method.
   * 
   * @return
   */
  public Double getReward();
}
