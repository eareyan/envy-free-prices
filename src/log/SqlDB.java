package log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
 
/*
 * Saves results of experiments to a SQL database.
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
    public boolean checkIfFancyDemandRowExists(String tablename,int n, int m, double p, int b) throws SQLException{
        String sql = "SELECT * FROM "+tablename+" WHERE n = ? AND m = ? AND p = ? AND b = ?";
        PreparedStatement preparedStatement = (PreparedStatement) conn.prepareStatement(sql);
        preparedStatement.setInt(1, n);
        preparedStatement.setInt(2, m);
        preparedStatement.setDouble(3, p);
        preparedStatement.setDouble(4, b);
        return ((ResultSet) preparedStatement.executeQuery()).next();
    }
	public void saveAllocationData(int n, int m, double p, double greedy0ToEfficient, double greedy1ToEfficient, double greedy2ToEfficient, double wfToEfficient, double wfToGreedy0, double wfToGreedy1, double wfToGreedy2) throws SQLException {
        String sql = "INSERT INTO allocation(n,m,p,greedy0ToEfficient,greedy1ToEfficient,greedy2ToEfficient,wfToEfficient,wfToGreedy0,wfToGreedy1,wfToGreedy2) VALUES (?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = (PreparedStatement) conn.prepareStatement(sql);
        preparedStatement.setInt(1, n);
        preparedStatement.setInt(2, m);
        preparedStatement.setDouble(3, p);
        preparedStatement.setDouble(4, greedy0ToEfficient);
        preparedStatement.setDouble(5, greedy1ToEfficient);
        preparedStatement.setDouble(6, greedy2ToEfficient);
        preparedStatement.setDouble(7, wfToEfficient);
        preparedStatement.setDouble(8, wfToGreedy0);
        preparedStatement.setDouble(9, wfToGreedy1);
        preparedStatement.setDouble(10, wfToGreedy2);
        preparedStatement.execute();        
	}
	public void save_unit_demand(int n, int m, double p,
			double ckEfficiency, double ckRevenue, double ckTime, double ckWE1, double ckWE2,
			double evpEfficiency, double evpRevenue, double evpTime, double evpWE1, double evpWE2,
			double mweqEfficiency, double mweqRevenue, double mweqTime, double mweqWE1, double mweqWE2) throws SQLException {
        String sql = "INSERT INTO unit_demand(n,m,p,ckEfficiency,ckRevenue,ckTime,ckWE1,ckWE2,evpEfficiency,evpRevenue,evpTime,evpWE1,evpWE2,mweqEfficiency,mweqRevenue,mweqTime,mweqWE1,mweqWE2) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = (PreparedStatement) conn.prepareStatement(sql);
        preparedStatement.setInt(1, n);
        preparedStatement.setInt(2, m);
        preparedStatement.setDouble(3, p);
        preparedStatement.setDouble(4, ckEfficiency);
        preparedStatement.setDouble(5, ckRevenue);
        preparedStatement.setDouble(6, ckTime);
        preparedStatement.setDouble(7, ckWE1);
        preparedStatement.setDouble(8, ckWE2);
        preparedStatement.setDouble(9, evpEfficiency);
        preparedStatement.setDouble(10, evpRevenue);
        preparedStatement.setDouble(11, evpTime);
        preparedStatement.setDouble(12, evpWE1);
        preparedStatement.setDouble(13, evpWE2);
        preparedStatement.setDouble(14, mweqEfficiency);
        preparedStatement.setDouble(15, mweqRevenue);
        preparedStatement.setDouble(16, mweqTime);
        preparedStatement.setDouble(17, mweqWE1);
        preparedStatement.setDouble(18, mweqWE2);
        preparedStatement.execute();        		
	}
	public void save_unit_uniform_demand(int n, int m, double p,
			double ckEfficiency, double ckRevenue, double ckTime, double ckWE1, double ckWE2,
			double evpEfficiency, double evpRevenue, double evpTime, double evpWE1, double evpWE2,
			double mweqEfficiency, double mweqRevenue, double mweqTime, double mweqWE1, double mweqWE2,
			double lpEfficiency, double lpRevenue, double lpTime, double lpWE1, double lpWE2) throws SQLException {
        String sql = "INSERT INTO unit_uniform_demand(n,m,p,ckEfficiency,ckRevenue,ckTime,ckWE1,ckWE2,evpEfficiency,evpRevenue,evpTime,evpWE1,evpWE2,mweqEfficiency,mweqRevenue,mweqTime,mweqWE1,mweqWE2,lpEfficiency,lpRevenue,lpTime,lpWE1,lpWE2) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = (PreparedStatement) conn.prepareStatement(sql);
        preparedStatement.setInt(1, n);
        preparedStatement.setInt(2, m);
        preparedStatement.setDouble(3, p);
        preparedStatement.setDouble(4, ckEfficiency);
        preparedStatement.setDouble(5, ckRevenue);
        preparedStatement.setDouble(6, ckTime);
        preparedStatement.setDouble(7, ckWE1);
        preparedStatement.setDouble(8, ckWE2);
        preparedStatement.setDouble(9, evpEfficiency);
        preparedStatement.setDouble(10, evpRevenue);
        preparedStatement.setDouble(11, evpTime);
        preparedStatement.setDouble(12, evpWE1);
        preparedStatement.setDouble(13, evpWE2);
        preparedStatement.setDouble(14, mweqEfficiency);
        preparedStatement.setDouble(15, mweqRevenue);
        preparedStatement.setDouble(16, mweqTime);
        preparedStatement.setDouble(17, mweqWE1);
        preparedStatement.setDouble(18, mweqWE2);
        preparedStatement.setDouble(19, lpEfficiency);
        preparedStatement.setDouble(20, lpRevenue);
        preparedStatement.setDouble(21, lpTime);
        preparedStatement.setDouble(22, lpWE1);
        preparedStatement.setDouble(23, lpWE2);
        preparedStatement.execute();		
	}
	public void save_fancy_demand(String table_name, int n, int m, double p, int b,
			double ckEfficiency, double ckRevenue, double ckTime, double ckWE1, double ckWE2,
			double lpOptEfficiency, double lpOptRevenue, double lpOptTime, double lpOptWE1, double lpOptWE2,
			double lpWFEfficiency, double lpWFRevenue, double lpWFTime, double lpWFWE1, double lpWFWE2,
			double lpG1Efficiency, double lpG1Revenue, double lpG1Time, double lpG1WE1, double lpG1WE2,
			double lpG2Efficiency, double lpG2Revenue, double lpG2Time, double lpG2WE1, double lpG2WE2
			) throws SQLException {
        String sql = "INSERT INTO "+table_name+" (n,m,p,b,ckEfficiency,ckRevenue,ckTime,ckWE1,ckWE2,lpOptEfficiency,lpOptRevenue,lpOptTime,lpOptWE1,lpOptWE2,lpWFEfficiency,lpWFRevenue,lpWFTime,lpWFWE1,lpWFWE2,lpG1Efficiency,lpG1Revenue,lpG1Time,lpG1WE1,lpG1WE2,lpG2Efficiency,lpG2Revenue,lpG2Time,lpG2WE1,lpG2WE2) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = (PreparedStatement) conn.prepareStatement(sql);
        preparedStatement.setInt(1, n);
        preparedStatement.setInt(2, m);
        preparedStatement.setDouble(3, p);
        preparedStatement.setInt(4, b);
        preparedStatement.setDouble(5, ckEfficiency);
        preparedStatement.setDouble(6, ckRevenue);
        preparedStatement.setDouble(7, ckTime);
        preparedStatement.setDouble(8, ckWE1);
        preparedStatement.setDouble(9, ckWE2);
        preparedStatement.setDouble(10, lpOptEfficiency);
        preparedStatement.setDouble(11, lpOptRevenue);
        preparedStatement.setDouble(12, lpOptTime);
        preparedStatement.setDouble(13, lpOptWE1);
        preparedStatement.setDouble(14, lpOptWE2);
        preparedStatement.setDouble(15, lpWFEfficiency);
        preparedStatement.setDouble(16, lpWFRevenue);
        preparedStatement.setDouble(17, lpWFTime);
        preparedStatement.setDouble(18, lpWFWE1);
        preparedStatement.setDouble(19, lpWFWE2);
        preparedStatement.setDouble(20, lpG1Efficiency);
        preparedStatement.setDouble(21, lpG1Revenue);
        preparedStatement.setDouble(22, lpG1Time);
        preparedStatement.setDouble(23, lpG1WE1);
        preparedStatement.setDouble(24, lpG1WE2);
        preparedStatement.setDouble(25, lpG2Efficiency);
        preparedStatement.setDouble(26, lpG2Revenue);
        preparedStatement.setDouble(27, lpG2Time);
        preparedStatement.setDouble(28, lpG2WE1);
        preparedStatement.setDouble(29, lpG2WE2);
        
        preparedStatement.execute();
		
	}
}