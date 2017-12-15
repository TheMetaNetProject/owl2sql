package edu.berkeley.icsi.metanet.owl2sql;

import java.util.regex.Pattern;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

public class Basics {
	public static final String VERSION = "1.1";
	public static final String CLASS_PREFIX = "";
	public static final String JOIN_TABLE_PREFIX = "JoinTable_";
	public static final String OBJ_PROP_PREFIX = "";
	public static final String DATA_PROP_PREFIX = "";
	public static final String DEFAULT_SQL_DATATYPE = "VARCHAR(333) BINARY";
	public static final String DB_NAME = "owl2sql";
	public static final String[] REQ_PRIV_SET = {
		"CREATE", "DROP", "INSERT"
	};
	
	public static String getClassName(OWLClass owlClass) {
		return CLASS_PREFIX + owlClass.getIRI().getFragment().toString();
	}
	
	public static String getIndName(OWLNamedIndividual namedInd) {
		return namedInd.getIRI().getFragment().toString();
	}
	
	public static String getObjPropName(OWLObjectProperty objProp) {
		return OBJ_PROP_PREFIX + objProp.getIRI().getFragment().toString();
	}
	
	public static String getDataPropName(OWLDataProperty dataProp) {
		return DATA_PROP_PREFIX + dataProp.getIRI().getFragment().toString();
	}
	
	/**
	 * Takes the given string and returns it in a SQL-acceptable format (i.e.
	 * with regular expressions)
	 */
	public static String format(String str) {
		String out;
		
		out = str.replace("'", "\\'");
		str = str.replace("\"", "\\\"");
		return out;
	}
	
	/**
	 * Checks if the given string is a valid MySQL database name
	 * @param dbName - the proposed database name
	 * @return true if the name is valid, false if not
	 */
	public static boolean isValidDBName(String dbName) {
		if (!Pattern.matches(".*[^\\w\\$].*", dbName) && dbName.length() > 0) {
			return true;
		} else {
			return false;
		}
	}
}
