package log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
 
/*
 * Saves results of experiments to a sql database.
 * 
 * @author Enrique Areyan Viqueira
 */
public class SqlDB {
	
	Connection conn;
	
    public SqlDB(String provider,String host, int port, String dbName, String user, String pass) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
    	/*
    	 * Connect to the database. First determine the driver.
    	 */
    	if(provider.equals("postgresql")){
    		Class.forName("org.postgresql.Driver");
    	}else if(provider.equals("mysql")){
    		Class.forName("com.mysql.jdbc.Driver");
    	}
		this.conn = DriverManager.getConnection("jdbc:"+provider+"://"+host+":"+port+"/"+dbName+"?user="+user+"&password="+pass);
		if (this.conn == null) {
		    System.out.println("NOT Connected to database...");
		}
    }
    public void closeConnection(Connection conn) throws SQLException{
    	this.conn.close();
    }
    public boolean checkIfUnitDemandRowExists(String tablename,int n, int m, double p) throws SQLException{
        String sql = "SELECT * FROM "+tablename+" WHERE n = ? AND m = ? AND p = ?";
        PreparedStatement preparedStatement = (PreparedStatement) conn.prepareStatement(sql);
        preparedStatement.setInt(1, n);
        preparedStatement.setInt(2, m);
        preparedStatement.setDouble(3, p);
        return ((ResultSet) preparedStatement.executeQuery()).next();
    }
    public void saveUnitDemandData(int n, int m, double p, double maxWEQRevenue, double maxWEQTime, double evpAppRevenue, double evpAppTime, double lpRevenue, double lpTime, double lpWEViolations, double lpWERelativeViolations, double lpEFViolations) throws SQLException{
        String sql = "INSERT INTO unit_demand(n,m,p,maxWEQRevenue,maxWEQTime,evpAppRevenue,evpAppTime,lpRevenue,lpTime,lpWEViolations,lpWERelativeViolations,lpEFViolations) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = (PreparedStatement) conn.prepareStatement(sql);
        preparedStatement.setInt(1, n);
        preparedStatement.setInt(2, m);
        preparedStatement.setDouble(3, p);
        preparedStatement.setDouble(4, maxWEQRevenue);
        preparedStatement.setDouble(5, maxWEQTime);
        preparedStatement.setDouble(6, evpAppRevenue);
        preparedStatement.setDouble(7, evpAppTime);
        preparedStatement.setDouble(8, lpRevenue);
        preparedStatement.setDouble(9, lpTime);
        preparedStatement.setDouble(10, lpWEViolations);
        preparedStatement.setDouble(11, lpWERelativeViolations);
        preparedStatement.setDouble(12, lpEFViolations);
        preparedStatement.execute();
    }
    public void saveUnitComparisonData(int n,int m, double p, double maxWEQRevenue, double maxWEQTime, double evpAppRevenue, double evpAppTime ) throws SQLException{
        String sql = "INSERT INTO unit_comparison(n,m,p,maxWEQRevenue,maxWEQTime,evpAppRevenue,evpAppTime) VALUES (?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = (PreparedStatement) conn.prepareStatement(sql);
        preparedStatement.setInt(1, n);
        preparedStatement.setInt(2, m);
        preparedStatement.setDouble(3, p);
        preparedStatement.setDouble(4, maxWEQRevenue);
        preparedStatement.setDouble(5, maxWEQTime);
        preparedStatement.setDouble(6, evpAppRevenue);
        preparedStatement.setDouble(7, evpAppTime);    
        preparedStatement.execute();        
    }
    public void saveGeneralCaseData(int n, int m, double p, double ratioEfficiency, double effAllocationRevenue, double effAlocationTime, double effAllocWEViolation,double effAllocWERelativeViolations, double effAllocEFViolation, double wfAllocationRevenue, double wfAllocationTime, double wfAllocWEViolation,double wfAllocWERelativeViolations, double wfAllocEFViolation, double wfMaxWEQRevenue, double wfMaxWEQTime) throws SQLException{
        String sql = "INSERT INTO general_demand(n,m,p,ratioEfficiency,effAllocationRevenue,effAllocationTime,effAllocWEViolation,effAllocWERelativeViolations,effAllocEFViolation,wfAllocationRevenue,wfAllocationTime,wfAllocWEViolation,wfAllocWERelativeViolations,wfAllocEFViolation,wfMaxWEQRevenue,wfMaxWEQTime) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = (PreparedStatement) conn.prepareStatement(sql);
        preparedStatement.setInt(1, n);
        preparedStatement.setInt(2, m);
        preparedStatement.setDouble(3, p);
        preparedStatement.setDouble(4, ratioEfficiency);
        preparedStatement.setDouble(5, effAllocationRevenue);
        preparedStatement.setDouble(6, effAlocationTime);
        preparedStatement.setDouble(7, effAllocWEViolation);
        preparedStatement.setDouble(8, effAllocWERelativeViolations);
        preparedStatement.setDouble(9, effAllocEFViolation);
        preparedStatement.setDouble(10, wfAllocationRevenue);
        preparedStatement.setDouble(11, wfAllocationTime);
        preparedStatement.setDouble(12, wfAllocWEViolation);
        preparedStatement.setDouble(13, wfAllocWERelativeViolations);
        preparedStatement.setDouble(14, wfAllocEFViolation);
        preparedStatement.setDouble(15, wfMaxWEQRevenue);
        preparedStatement.setDouble(16, wfMaxWEQTime);
        preparedStatement.execute();
    }
}