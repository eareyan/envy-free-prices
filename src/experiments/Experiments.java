package experiments;

import ilog.concert.IloException;

import java.sql.SQLException;

import allocations.error.AllocationException;
import log.SqlDB;

/*
 * Abstract class to test algorithms. 
 * 
 * @author Enrique Areyan Viqueira
 */
public abstract class Experiments {
	
	public void bulkTest(SqlDB dbLogger) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, IloException, AllocationException{
		
		int numUsers = 21;
		int numCampaigns = 21;
		for(int i=2;i<numUsers;i++){
			for(int j=2;j<numCampaigns;j++){
				for(int p=0;p<4;p++){
					double prob = 0.25 + p*(0.25);
					for(int b=0;b<4;b++){
						System.out.print(" n = " + i + ", m = " + j + ", prob = " + prob + ", b = " + (b+1));
						this.runOneExperiment(i, j, prob, b+1 , dbLogger);
					}
				}
			}
		}
	}
	public static void main(String[] args) throws Exception{
		/*
		 * Check if we are running on the grid
		 */
		RunParameters Parameters = new RunParameters(args);
		SqlDB dbLogger = new SqlDB(Parameters.dbProvider,Parameters.dbHost,Parameters.dbPort,Parameters.dbName,Parameters.dbUsername,Parameters.dbPassword);
		if(args[0].equals("grid")){
			Parameters.experimentObject.runOneExperiment(Parameters.numUsers, Parameters.numCampaigns, Parameters.prob,Parameters.b, dbLogger);
		}else{
			/*
			 * Running local bulk tests
			 */
			System.out.println("Running local...");
			Parameters.experimentObject.bulkTest(dbLogger);
		}
	}

	/* 
	 * This method is implemented by a particular type of experiment class.
	 * This method receives the number of users, number of campaigns and probability
	 * and runs one experiments, saving the result in the database.
	 * This method should also check if we have that result first before running the
	 * experiment.
	 */
	abstract public void runOneExperiment(int numUsers, int numCampaigns, double prob, int b, SqlDB dbLogger)  throws SQLException, IloException, AllocationException;
}
