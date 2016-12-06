package experiments;

/**
 * This class implements operations common to experiments.
 * It contains all the parameters needed for one batch of experiments.
 * 
 * @author Enrique Areyan Viqueira
 */
public class RunParameters {

  public static final int totalNumGoods = 19;
  public static final int totalNumBidders = 19;
  public static final int totalProbabilities = 4;
  public static final int totalB = 3;
  public static final int numTrials = 100;

  public int numGoods;
  public int numBidders;
  public double prob;
  public int b;
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
    this.id = Integer.parseInt(args[8]);
    this.computeRangeOfWork();
  }
  
  /**
   * Implements logic to divide the work among many processes
   */
  public void computeRangeOfWork() {
    int k = 0;
    for (int i = 0; i < RunParameters.totalNumGoods; i++) {
      for (int j = 0; j < RunParameters.totalNumBidders; j++) {
        for (int p = 0; p < RunParameters.totalProbabilities; p++) {
          for (int b = 0; b < RunParameters.totalB; b++) {
            k++;
            if (this.id == k) {
              switch (p) {
              case 0:
                this.prob = 0.25;
                break;
              case 1:
                this.prob = 0.5;
                break;
              case 2:
                this.prob = 0.75;
                break;
              case 3:
                this.prob = 1.0;
                break;
              }
              switch (b) {
              case 0:
                this.b = 1;
                break;
              case 1:
                this.b = 2;
                break;
              case 2:
                this.b = 3;
                break;
              // case 3: this.b = 4; break;
              }
              this.numGoods = i + 2;
              this.numBidders = j + 2;
            }
          }
        }
      }
    }
  }
  
  /**
   * Factory method to create the type of experiment object. 
   * @param type - string with the number of the experiment object.
   * @return an experiment object. 
   * @throws Exception
   */
  public Experiments getExperimentObject(String type) throws Exception {
    if (type.equals("SingleMinded")) {
      return new SingleMinded();
    } else {
      throw new Exception("Unknown demand type");
    }
  }

  @Override
  public String toString() {
    return "Param: " + "\n" + "numUsers \t= " + this.numGoods + "\n"
        + "numCampaigns \t= " + this.numBidders + "\n" + "prob \t\t= "
        + this.prob + "\n" + "b \t\t= " + this.b + "\n" + "experiment \t= "
        + this.experimentName;
  }
  
}