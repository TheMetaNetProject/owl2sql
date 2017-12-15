package edu.berkeley.icsi.metanet.owl2sql;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author brandon
 *
 */
public class Utilities {
	
	/**
	 * Returns the set of all named subclasses of the given OWL class, including
	 * the given OWL class
	 * @param owlClass - the named OWL class
	 * @param ont - the ontology containing the OWL class
	 * @return the set of all named subclasses, including the given OWL class
	 */
	public static HashSet<OWLClass> getSubClasses(OWLClass owlClass, 
			Set<OWLOntology> ontClosure) {
		OWLClass subClass;
		
		HashSet<OWLClass> subClasses = new HashSet<OWLClass>();
		subClasses.add(owlClass);
		
		for (OWLClassExpression subClassEx : owlClass.getSubClasses(ontClosure)) {
			if (subClassEx.isAnonymous()) {
				continue;
			}
			subClass = subClassEx.asOWLClass();
			subClasses.addAll(getSubClasses(subClass, ontClosure));
		}
		return subClasses;
	}
	
	/**
	 * Returns the set of all named super-classes of the given OWL class, 
	 * including the given OWL class
	 * @param owlClass - the named OWL class
	 * @param ont - the ontology containing the OWL class
	 * @return the set of all named subclasses, including the given OWL class
	 */
	public static HashSet<OWLClass> getSuperClasses(OWLClass owlClass, 
			Set<OWLOntology> ontClosure) {
		OWLClass superClass;
		
		HashSet<OWLClass> superClasses = new HashSet<OWLClass>();
		superClasses.add(owlClass);
		
		for (OWLClassExpression superClassEx : 
				owlClass.getSuperClasses(ontClosure)) {
			if (superClassEx.isAnonymous()) {
				continue;
			}
			superClass = superClassEx.asOWLClass();
			superClasses.addAll(getSuperClasses(superClass, ontClosure));
		}
		return superClasses;
	}
	
	/**
	 * Returns the set of all named super-properties of the given object
	 * property, including the given object property itself
	 * @param objProp - the named OWL object property
	 * @param ont - the OWL ontology containing the object property
	 * @return the set of all named superproperties of the given object
	 * property, including the given object property itself
	 */
	public static HashSet<OWLObjectProperty> getSuperProps(
			OWLObjectProperty objProp, Set<OWLOntology> ontClosure) {
		OWLObjectProperty superproperty;
		
		HashSet<OWLObjectProperty> superproperties = 
				new HashSet<OWLObjectProperty>();
		superproperties.add(objProp);
		
		for (OWLObjectPropertyExpression superPropExp : 
				objProp.getSuperProperties(ontClosure)) {
			if (superPropExp.isAnonymous()) {
				System.out.println(superPropExp);
			} else {
				superproperty = superPropExp.asOWLObjectProperty();
				superproperties.addAll(getSuperProps(superproperty, ontClosure));
			}
		}
		
		return superproperties;
	}
	
	/**
	 * Returns the set of all named super-properties of the given data property 
	 * including the given data property itself
	 * @param dataProp - the named OWL data property
	 * @param ont - the OWL ontology containing the data property
	 * @return the set of all named superproperties of the given data property, 
	 * including the given data property itself
	 */
	public static HashSet<OWLDataProperty> getSuperProps(OWLDataProperty dataProp,
			Set<OWLOntology> ontClosure) {
		HashSet<OWLDataProperty> superproperties = 
				new HashSet<OWLDataProperty>();
		superproperties.add(dataProp);
		
		for (OWLDataPropertyExpression supPropExp : 
				dataProp.getSuperProperties(ontClosure)) {
			if (supPropExp.isAnonymous()) {
				// not currently handled
			} else {
				superproperties.addAll(getSuperProps(
						supPropExp.asOWLDataProperty(), ontClosure));
			}
		}
		
		return superproperties;
	}
	
	/**
	 * Returns a SQL INSERT statement for the given table and with the given
	 * values.
	 * @param tableName - name of the target table
	 * @param fieldValueMap - maps the name of each field to the string 
	 * representation of its value. If the value is itself a string, it should
	 * not be delimitted by apostrophes. For example, "value" rather than
	 * "'value'".
	 * @return a SQL INSERT statement
	 */
	public static String getInsertString(String tableName, 
			Map<String, String> fieldValueMap) {
		String fields = "";
		String values = "";
		String value;
		
		for (String field : fieldValueMap.keySet()) {
			if (!fields.isEmpty()) {
				fields += ", ";
				values += ", ";
			}
			fields += field;
			value = fieldValueMap.get(field).toString();
			if (!value.equals("true") && !value.equals("false")) {
				value = "'" + value + "'";
			}
			values += value;
		}
		return "INSERT INTO " + tableName + " (" + fields + ") VALUES (" + 
				values + ")";
	}

	/**
	 * Returns the of disjunct OWL classes in the given OWLClassExpression. 
	 * Handles named OWL classes and disjunct anonymous classes but ignores
	 * other types of anonymous classes.
	 * @param owlClassExp
	 * @return the set of disjunct OWL classes in the given class expression
	 */
	public static HashSet<OWLClass> extractClasses(
			OWLClassExpression owlClassExp) {
		HashSet<OWLClass> owlClasses = new HashSet<OWLClass>();
		if (!owlClassExp.isAnonymous()) {
			owlClasses.add(owlClassExp.asOWLClass());
		} else if (!owlClassExp.asDisjunctSet().contains(owlClassExp)) {
			for (OWLClassExpression disjunctClassExp : 
					owlClassExp.asDisjunctSet()) {
				owlClasses.addAll(extractClasses(disjunctClassExp));
			}
		}
		return owlClasses;
	}
}
