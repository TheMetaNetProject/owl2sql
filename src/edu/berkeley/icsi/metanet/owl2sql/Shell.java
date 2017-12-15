package edu.berkeley.icsi.metanet.owl2sql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public abstract class Shell {
	String server, username, pw;
	int port;
	
	static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("owl2sql [OPTIONS] FILE", options);
		System.exit(0);
	}
	
	abstract Connection establishConnection();
	
	abstract String getDBName();
		
	@SuppressWarnings("unchecked")
	public static void main (String args[]) {
		List<String> argList;
		Shell shell;
		boolean adequatePermissions = false;
		String path, logPath;
		String dbName;
		File owlFile;
		Connection con = null;
		OWLOntology ont = null;
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		TableBuilder tableBuilder;
		Statement stmt = null;
		CommandLineParser parser;
		CommandLine cmd;
		
		System.out.println("owl2sql v" + Basics.VERSION);
		
		/*
		 * Handle command-line arguments
		 */
		
		Options options = new Options();
		options.addOption("C", false, "prompt server and user login info " +
				"from console");
		options.addOption("E", false, "enable error logging");
		options.addOption("server", true, "MySQL server name");
		options.addOption("port", true, "MySQL server port");
		options.addOption("u", true, "MySQL username");
		options.addOption("p", true, "MySQL password");
		options.addOption("db", true, "MySQL database name");
		options.addOption("help", false, "print this message");
		
		parser = new PosixParser();
		cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("Error: Invalid command-line arguments");
			Shell.printHelp(options);
			System.exit(1);
		}
		
		if (cmd.hasOption("help")) {
			Shell.printHelp(options);
		}
		
		argList = cmd.getArgList();
		if (argList.size() == 0) {
			System.err.println("Error: Must provide path of OWL file as " +
					"argument");
			System.exit(1);
		} else if (argList.size() > 1) {
			System.err.println("Error: Only one argument can be provided");
		}
		
		path = argList.get(0);
		owlFile = new File(path);
		if (!owlFile.exists()) {
			System.err.println("Error: Invalid file path");
			System.exit(1);
		}
		
		try {
			ont = manager.loadOntologyFromOntologyDocument(owlFile);
			System.out.println("Ontology file loaded");
		} catch (Exception ex) {
			System.err.println("Error: Invalid file");
			System.exit(1);
		}
		
		if (cmd.hasOption("C")) {
			shell = new ConsoleShell();
		} else {
			shell = new CmdLineShell(cmd);
		}
		
		con = shell.establishConnection();
		System.out.println("Connection established");
		try {
			stmt = con.createStatement();
		} catch (SQLException e1) {
			System.err.println("Error: Could not create SQL statement");
			System.exit(1);
		}
		
		try {
			adequatePermissions = Connector.adequatePermissions(con);
		} catch (SQLException ex) {
			System.err.println("Error: Unable to check user permissions");
			System.exit(1);
		}
		
		if (!adequatePermissions) {
			System.err.println("Error: Inadequate user permissions on this " +
					"MySQL server. Contact the database administrator.");
			System.exit(1);
		} else {
			System.out.println("Permissions checked");
		}
		
		tableBuilder = new TableBuilder(ont, stmt);
		
		dbName = shell.getDBName();
		
		try {
			DatabaseHandler.prepare(stmt, dbName);
		} catch (SQLException ex) {
			System.err.println("Error: Could not prepare database");
			System.exit(1);
		}
		
		if (cmd.hasOption("E")) {
			logPath = System.getProperty("user.dir") + "/error.log";
			try {
				tableBuilder.enableErrorLogging(logPath);
			} catch (IOException e) {
				System.err.println("Error: Could not initialize error " +
						"logging to " + logPath + 
						". Proceeding without error logging.");
			}
		}
		
		try {
			tableBuilder.build();
		} catch (SQLException ex) {
			System.err.println("\nError: " + ex.getMessage());
			System.err.println("Error occurred while creating the " +
					"new database. No changes committed.");
			System.exit(1);
		}
		
		try {
			con.commit();
			System.out.println("Committed changes to jdbc:mysql://" + shell.server + 
					":" + shell.port + "/" + dbName);
		} catch (SQLException e) {
			System.err.println("Error: Unable to commit changes to database");
		}
	}
}
