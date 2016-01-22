package com.termOne;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.derby.impl.sql.execute.CreateConstraintConstantAction;

import java.sql.ResultSetMetaData;


public class DatabaseConnector
{
	private String dbURL = "jdbc:derby:myDB;create=true;";
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
		System.out.println(dbc.lookup(tableName, "HAND_ID", new Hand("3c5cJcJd").toDatabaseString()));
	}

	private void createConnection()
	{
		try
		{
			Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
			//Get a connection
			conn = DriverManager.getConnection(dbURL); 
		}
		catch (Exception except)
		{
			except.printStackTrace();
		}
	}


	private void shutdown()
	{
		try
		{
			if (stmt != null)
			{
				stmt.close();
			}
			if (conn != null)
			{
				DriverManager.getConnection(dbURL + ";shutdown=true");
				conn.close();
			}           
		}
		catch (SQLException sqlExcept)
		{

		}

	}

	public void insertRow(String hand, float confidence) {
		try
		{
			Statement statement = conn.createStatement();
			statement.execute("insert into " + tableName + " values ('" +
					hand + "', " + confidence + ")");
			statement.close();
		}
		catch (SQLException sqlExcept)
		{
			sqlExcept.printStackTrace();
		}
	}
	
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
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
    }
	
	public float lookup(String tableName, String keyName, String key) {
		float ans = 0;
		try {
			Statement s = conn.createStatement();
			ResultSet results = s.executeQuery("SELECT * FROM " + tableName + " WHERE " + keyName + " = '" + key + "'");
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
	
	/**
	 * For an inputted hand, returns a new hand String ensuring that 
	 * the order in which suits appear follows the database standard 
	 * of "clubs-->diamonds-->hearts-->spades". May change one or 
	 * more cards' suits in rep to accomplish this, i.e.
	 * 		suitify 2s3s4s5s returns 2c3c4c5c  
	 * @param hand the String representation of the hand to suitify
	 * @return a new String containing the suit-adjusted hand, ready 
	 * to be looked up in the database.
	 */
	public static String suitify(String hand) {
		String ans = new String(hand);
		return ans;
	}
}
