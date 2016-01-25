package com.termOne;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseConnector
{
	private String dbURL = "jdbc:derby:lib/myDB;";// + System.getProperty("user.dir") + "/lib/myDB;create=true;";
	static String tableName = "confidence_lookup9";
	// jdbc Connection
	private Connection conn = null;
	private Statement stmt = null;

	public DatabaseConnector(boolean createTable) {
		createConnection();


		if(createTable)
			createTable(tableName);
	}

	private void createTable(String tableName) {
		try
		{
			stmt = conn.createStatement();
			stmt.execute("CREATE TABLE " + tableName + "(HAND_ID CHAR(8) NOT NULL, CONFIDENCE FLOAT NOT NULL)");
			stmt.close();
		}
		catch (SQLException sqlExcept)
		{
			sqlExcept.printStackTrace();
		}
	}

	public static void main(String[] args) {
		DatabaseConnector dbc = new DatabaseConnector(false);
//		dbc.printTable(tableName);
//		System.out.println(new Hand("2c3h4d5d").toDatabaseString());
		System.out.println(dbc.lookup(tableName, "HAND_ID", new Hand("8c4hAh2s").toDatabaseString()));
		System.out.println(dbc.lookup(tableName, "HAND_ID", new Hand("4cTcKd3s").toDatabaseString()));
		System.out.println(dbc.lookup(tableName, "HAND_ID", new Hand("7d7hAdAh").toDatabaseString()));
	}

	private void createConnection()
	{
		try
		{
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
			//Get a connection
			conn = DriverManager.getConnection(dbURL); 
		}
		catch (Exception except)
		{
			except.printStackTrace();
		}
	}


	public void shutdown() {
		try {
			if (stmt != null)
				stmt.close();
			if (conn != null) {
				DriverManager.getConnection(dbURL + ";shutdown=true");
				conn.close();
			}           
		}
		catch (SQLException sqlExcept){
			System.out.println("Problems shutting down");
			sqlExcept.printStackTrace();
		}

	}

	public void insertRow(String hand, float confidence) {
		try {
			Statement statement = conn.createStatement();
			statement.execute("insert into " + tableName + " values ('" +
					hand + "', " + confidence + ")");
			statement.close();
		}
		catch (SQLException sqlExcept) {
			sqlExcept.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private void printTable(String tableName) {
        try {
            Statement s = conn.createStatement();
            ResultSet results = s.executeQuery("select * from " + tableName);
            ResultSetMetaData rsmd = results.getMetaData();
            int numberCols = rsmd.getColumnCount();
            for (int i=1; i<=numberCols; i++)
                System.out.print(rsmd.getColumnLabel(i)+"\t\t");  

            System.out.println("\n-------------------------------------------------");

            while(results.next()) {
            	String hand = results.getString(1);
            	float conf = results.getFloat(2);
                System.out.println(hand + "\t\t" + conf);
            }
            results.close();
            s.close();
        }
        catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
        }
    }
	
	public float lookup(String tableName, String keyName, String key) {
		float ans = 0;
		try {
			Statement s = conn.createStatement();
			ResultSet results = s.executeQuery("select * from " + tableName + " where " + keyName + " = '" + key + "'");
			while(results.next()) {
				ans = results.getFloat(2);
				break;
			}
			results.close();
			s.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return ans;
	}
}
