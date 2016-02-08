package experiments;

/*
 * This class implements operations common to experiments.
 * It contains all the parameters needed for one batch of experiments.
 * 
 * @author Enrique Areyan Viqueira
 */
public class RunParameters{
	
	public static final int totalNumUsers = 26;
	public static final int totalNumCamp = 26;
	public static final int totalProbabilities = 4;
	public static final int numTrials = 500;
	
	public int numUsers;
	public int numCampaigns;
	double prob;
	public int id;
	public String dbProvider;
	public String dbHost;
	public int dbPort;
	public String dbName;
	public String dbUsername;
	public String dbPassword;
	public Experiments experimentObject;
	public String experimentName;
	
	public RunParameters(String[] args) throws Exception{
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
	/*
	 * Implements logic to divide the work among many processes
	 */
	public void computeRangeOfWork(){
		int k=0;
		for(int i=0;i<RunParameters.totalNumUsers;i++){
			for(int j=0;j<RunParameters.totalNumCamp;j++){
				for(int p=0;p<RunParameters.totalProbabilities;p++){
					k++;
					if(this.id == k){
						double prob = 0.0;
						switch(p){
							case 0: prob = 0.25; break;
							case 1: prob = 0.5; break;
							case 2: prob = 0.75; break;
							case 3: prob = 1.0; break;
						}
						this.numUsers = i+2;
						this.numCampaigns = j+2;
						this.prob = prob;
					}
				}
			}
		}
	}
	/*
	 * Factory method to create the type of experiment object.
	 */
	public Experiments getExperimentObject(String type) throws Exception{
		if(type.equals("UnitDemand")){
			return new UnitDemandExperiments();
		}else if(type.equals("GeneralDemand")){
			return new GeneralDemandExperiments();
		}else{
			throw new Exception("Unknown demand type");
		}
	}
	
	public String toString(){
		return  "Param: " + "\n"
				+ "numUsers \t= " + this.numUsers + "\n" 
				+ "numCampaigns \t= " + this.numCampaigns + "\n"
				+ "prob \t\t= " + this.prob + "\n"
				+ "experiment \t= " + this.experimentName;
		
	}
}