package edu.berkeley.icsi.metanet.owl2sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

public class Connector {
	
	/**Gets connection to the given MySQL server with the given user
	 * credentials. 
	 * @param server - Name of a MySQL server (i.e. localhost:3306)
	 * @param username - Username
	 * @param pw - Password
	 * @return
	 * @throws SQLException
	 * @throws PermissionException 
	 */
	protected static Connection getConnection(String server, String username, 
			String pw) throws SQLException {
		Connection con = DriverManager.getConnection("jdbc:mysql://" + server, 
				username, pw);		
		return con;
	}
	
	protected static Connection getConnection(String server, int port, 
			String username, String pw) throws SQLException {
		Connection con = DriverManager.getConnection("jdbc:mysql://" + server +
				":" + port, username, pw);		
		return con;
	}
	
	protected static boolean adequatePermissions(Connection con) 
			throws SQLException {
		boolean contains;
		int privBegin, privEnd, rangeBegin, rangeEnd;
		String line, privArray[], rangeArray[];
		privBegin = "GRANT ".length();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("SHOW GRANTS");
		HashSet<String> privSet = new HashSet<String>();
		
		while (rs.next()) {
			line = rs.getString(1);
			privEnd = line.indexOf(" ON");
			rangeBegin = line.indexOf(" ON ") + 4;
			rangeEnd = line.indexOf(" TO");
			privArray = line.substring(privBegin, privEnd).split(", ");
			rangeArray = line.substring(rangeBegin, rangeEnd).split(", ");
			for (String range : rangeArray) {
				if (range.equals("*.*") || range.equals(Basics.DB_NAME)) {
					for (String priv : privArray) {
						privSet.add(priv);
					}
					break;
				}
			}
		}
		
		if (privSet.contains("ALL PRIVILEGES")) {
			return true;
		} else {
			for (String reqPriv : Basics.REQ_PRIV_SET) {
				contains = false;
				for (String priv : privSet) {
					if (priv.equals(reqPriv)) {
						contains = true;
						break;
					}
				}
				if (!contains) {
					return false;
				}
			}
			return true;
		}
	}
}
