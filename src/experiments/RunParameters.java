package experiments;

/*
 * This class implements operations common to experiments.
 * It contains all the parameters needed for one batch of experiments.
 * 
 * @author Enrique Areyan Viqueira
 */
public class RunParameters{
	
	public static final int totalNumUsers = 19;
	public static final int totalNumCamp = 19;
	public static final int totalProbabilities = 4;
	public static final int totalB = 3;
	public static final int numTrials = 50;
	
	public int numUsers;
	public int numCampaigns;
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
					for(int b=0;b<RunParameters.totalB;b++){
						k++;
						if(this.id == k){
							switch(p){
								case 0: this.prob = 0.25; break;
								case 1: this.prob = 0.5; break;
								case 2: this.prob = 0.75; break;
								case 3: this.prob = 1.0; break;
							}
							switch(b){
								case 0: this.b = 1; break; 
								case 1: this.b = 2; break;
								case 2: this.b = 3; break;
								//case 3: this.b = 4; break;
							}
							this.numUsers = i+2;
							this.numCampaigns = j+2;
						}
					}
				}
			}
		}
	}
	/*
	 * Factory method to create the type of experiment object.
	 */
	public Experiments getExperimentObject(String type) throws Exception{
		if(type.equals("unit_demand")){
			return new unit_demand();
		}else if(type.equals("unit_uniform_demand")){
			return new unit_uniform_demand();
		}else if(type.equals("fancy_underdemand")){
			return new fancy_underdemand();
		}else if(type.equals("fancy_overdemand")){
			return new fancy_overdemand();
		}else if(type.equals("allocation")){
			return new allocation();
		}else{
			throw new Exception("Unknown demand type");
		}
	}
	
	public String toString(){
		return  "Param: " + "\n"
				+ "numUsers \t= " + this.numUsers + "\n" 
				+ "numCampaigns \t= " + this.numCampaigns + "\n"
				+ "prob \t\t= " + this.prob + "\n"
				+ "b \t\t= " + this.b + "\n"
				+ "experiment \t= " + this.experimentName;
		
	}
}