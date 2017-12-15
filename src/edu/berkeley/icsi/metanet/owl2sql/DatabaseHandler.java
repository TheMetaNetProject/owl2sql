package edu.berkeley.icsi.metanet.owl2sql;

import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler {
	
	/**Drops the previous database if necessary and creates a new database
	 * 
	 * @throws SQLException Throws a SQL exception if an error occurs in 
	 * creating the new database
	 */
	protected static void prepare(Statement stmt, String dbName) 
			throws SQLException {
		try {
			stmt.execute("DROP DATABASE " + dbName);
			System.out.println("Previous database " + dbName + " dropped");
		} catch (SQLException e) {
			
		}
		stmt.execute("CREATE DATABASE " + dbName);
		System.out.println("Empty database " + dbName + " created");
		stmt.execute("use " + dbName);
	}
}
