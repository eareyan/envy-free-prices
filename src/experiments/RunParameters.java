package experiments;

import com.google.common.collect.ImmutableList;

/**
 * This class implements operations common to experiments. It contains all the parameters needed for one batch of experiments.
 * 
 * @author Enrique Areyan Viqueira
 */
public class RunParameters {

  public static final int numTrials = 100;
  public static final int numGoods = 21;
  public static final int numBidder = 21;
  public static final ImmutableList<String> distributions = ImmutableList.of("Uniform", "Elitist");

  public int id;
  public String dbProvider;
  public String dbHost;
  public int dbPort;
  public String dbName;
  public String dbUsername;
  public String dbPassword;
  public Experiments experimentObject;
  public String experimentName;

  /**
   * Constructor.
   * 
   * @param args - command line arguments.
   * @throws Exception
   */
  public RunParameters(String[] args) throws Exception {
    this.experimentName = args[0];
    this.experimentObject = this.getExperimentObject(this.experimentName);
    this.dbProvider = args[1];
    this.dbHost = args[2];
    this.dbPort = Integer.parseInt(args[3]);
    this.dbName = args[4];
    this.dbUsername = args[5];
    this.dbPassword = args[6];
  }

  /**
   * Factory method to create the type of experiment object.
   * 
   * @param type - string with the number of the experiment object.
   * @return an experiment object.
   * @throws Exception
   */
  private Experiments getExperimentObject(String type) throws Exception {
    if (type.equals("SingleMinded")) {
      return new SingleMinded();
    } else if (type.equals("SizeInterchangeable")) {
      return new SizeInterchangeable();
    } else if (type.equals("Singleton")) {
      return new Singleton();
    } else {
      throw new Exception("Unknown demand type");
    }
  }

}