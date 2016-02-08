package experiments;

import java.sql.SQLException;

import log.SqlDB;

public class Experiments {

	public void bulkTest(String[] args) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		
		int numUsers = 21;
		int numCampaigns = 21;
		SqlDB dbLogger = new SqlDB(null,args[0],0,args[1],args[2], null);
		for(int i=2;i<numUsers;i++){
			for(int j=2;j<numCampaigns;j++){
				for(int p=0;p<4;p++){
					double prob = 0.25 + p*(0.25);
					System.out.println(" n = " + i + ", m = " + j + ", prob = " + prob);
					//GeneralDemandExperiments.runOneExperiment(i, j, prob, dbLogger);
				}
			}
		}
	}
	public static void main(String[] args) throws Exception{
		/*
		 * Check if we are running on the grid
		 */
		if(args[0].equals("grid")){
			RunParameters Parameters = new RunParameters(args);
			SqlDB dbLogger = new SqlDB(Parameters.dbProvider,Parameters.dbHost,Parameters.dbPort,Parameters.dbName,Parameters.dbUsername,Parameters.dbPassword);
			Parameters.experimentObject.runOneExperiment(Parameters.numUsers, Parameters.numCampaigns, Parameters.prob, dbLogger);
		}else{
			/*
			 * Running local... no logic implemented yet...
			 * It used to be bulk tests...
			 */
			System.out.println("Running local...");
			//UnitDemandExperiments.bulkTest(args);
		}
	}

	public void runOneExperiment(int numUsers, int numCampaigns, double prob, SqlDB dbLogger)  throws SQLException{
		/* 
		 * This method is implemented by a particular type of experiment class.
		 * This method receives the number of users, number of campaigns and probability
		 * and runs one experiments, saving the result in the database.
		 * This method should also check if we have that result first before running the
		 * experiment.
		 */
	}
}
