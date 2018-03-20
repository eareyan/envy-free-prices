package experiments;

import singleminded.algorithms.complete.CompleteSearchExperiments;
import singleminded.algorithms.complete.WEExistenceExperiments;

import com.google.common.collect.ImmutableList;

/**
 * This class implements operations common to experiments. It contains all the parameters needed for one batch of experiments.
 * 
 * @author Enrique Areyan Viqueira
 */
public class RunParameters {

  /**
   * Global parameters of the experiments.
   */
  public static final int numTrials = 100;
  public static final int numGoods = 100;
  public static final int numBidder = 100;
  public static final ImmutableList<String> distributions = ImmutableList.of("Uniform", "Elitist");
  public static final ImmutableList<Double> probabilities = ImmutableList.of(1.0 / 4.0, 2.0 / 4.0, 3.0 / 4.0, 1.0);

  /**
   * Data to interface the DB.
   */
  public int id;
  public String dbProvider;
  public String dbHost;
  public int dbPort;
  public String dbName;
  public String dbUsername;
  public String dbPassword;
  public Experiments experimentObject;
  public String experimentName;
  public static String distribution; // Need to fix this! shouldn't be static

  /**
   * Constructor.
   * 
   * @param args - command line arguments.
   * @throws Exception
   */
  public RunParameters(String[] args) throws Exception {
    if (args == null) {
      throw new Exception("Received null parameters.");
    } else if (args.length < 7) {
      throw new Exception("Invalid number of parameters.");
    }
    this.experimentName = args[0];
    this.experimentObject = this.getExperimentObject(this.experimentName);
    this.dbProvider = args[1];
    this.dbHost = args[2];
    this.dbPort = Integer.parseInt(args[3]);
    this.dbName = args[4];
    this.dbUsername = args[5];
    this.dbPassword = args[6];
    if(args.length >= 8) {
      RunParameters.distribution = args[7];
    }
  }

  /**
   * Factory method to create the type of experiment object.
   * 
   * @param type
   *          - string with the number of the experiment object.
   * @return an experiment object.
   * @throws Exception
   */
  private Experiments getExperimentObject(String type) throws Exception {
    if (type.equals("SingleMinded")) {
      return new SingleMinded();
    } else if (type.equals("Singleton")) {
      return new Singleton();
    } else if (type.equals("SizeInterchangeable")) {
      return new SizeInterchangeable();
    } else if (type.equals("TAC")) {
      return new TAC();
    } else if (type.equals("CompleteSearch")) {
      return new CompleteSearchExperiments();
    } else if (type.equals("WEExistence")) {
      return new WEExistenceExperiments();
    } else {
      throw new Exception("Unknown demand type");
    }
  }

}