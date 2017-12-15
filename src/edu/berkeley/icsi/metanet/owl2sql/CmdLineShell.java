package edu.berkeley.icsi.metanet.owl2sql;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.cli.CommandLine;

public class CmdLineShell extends Shell {
	String dbName;

	public CmdLineShell(CommandLine cmd) {
		super();
		String portStr;
		
		server = cmd.getOptionValue("server");
		username = cmd.getOptionValue("u");
		pw = cmd.getOptionValue("p");
		dbName = cmd.getOptionValue("db");
		portStr = cmd.getOptionValue("port");
		
		if (server == null) {
			System.out.println("No MySQL server specified. Defaulting to " +
					"localhost.");
			server = "localhost";
		} else if (server.length() == 0 || server.contains("/") || 
				server.contains(":")) {
			System.err.println("Error: Invalid server name. No \":\" " +
					"or \"/\". (i.e. \"localhost\")");
			System.exit(1);
		}
		
		if (portStr == null) {
			System.out.println("No MySQL port number specified. Defaulting" +
					" to 3306.");
			port = 3306;
		} else {
			try {
				port = Integer.parseInt(portStr);
			} catch (NumberFormatException ex) {
				System.err.println("Error: Port option must be an integer");
				System.exit(1);
			}
		}
		
		if (username == null) {
			System.out.println("No MySQL username specified. Defaulting to" +
					" blank username.");
			username = "";
		}
		
		if (pw == null) {
			System.out.println("No MySQL password specified. Defaulting to " +
					"no password.");
			pw = "";
		}
		
		if (dbName == null) {
			System.out.println("No MySQL database name specified. Defaulting " +
					"to Metaphors.");
			dbName = "Metaphors";
		}
		
	}

	@Override
	Connection establishConnection() {
		Connection con = null;
		try {
			con = Connector.getConnection(server, port, username, pw);
			con.setAutoCommit(false);
		} catch (SQLException ex) {
			System.err.println("Error: Unable to establish connection");
			System.exit(1);
		}
		return con;
	}

	@Override
	String getDBName() {
		return dbName;
	}

}
