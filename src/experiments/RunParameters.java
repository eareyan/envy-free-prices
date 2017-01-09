package experiments;

/**
 * This class implements operations common to experiments.
 * It contains all the parameters needed for one batch of experiments.
 * 
 * @author Enrique Areyan Viqueira
 */
public class RunParameters {
  
  public static final int numTrials = 100;
  public static final int numGoods = 40;
  public static final int numBidder = 40;

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
    this.experimentName = args[1];
    this.experimentObject = this.getExperimentObject(this.experimentName);
    this.dbProvider = args[2];
    this.dbHost = args[3];
    this.dbPort = Integer.parseInt(args[4]);
    this.dbName = args[5];
    this.dbUsername = args[6];
    this.dbPassword = args[7];
  }
  
  /**
   * Factory method to create the type of experiment object. 
   * @param type - string with the number of the experiment object.
   * @return an experiment object. 
   * @throws Exception
   */
  private Experiments getExperimentObject(String type) throws Exception {
    if (type.equals("SingleMinded")) {
      return new SingleMinded();
    } else {
      throw new Exception("Unknown demand type");
    }
  }

}