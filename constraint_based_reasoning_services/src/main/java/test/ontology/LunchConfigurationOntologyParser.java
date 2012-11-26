package test.ontology;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Property;

public class LunchConfigurationOntologyParser {

	/**
	 * @author iran Mansouri
	 */

	private static final String NSTABLESETTING = "http://www.owl-ontologies.com/TableSetting.owl#";
	private static final String inputFileName = "/home/iran/fuerte_workspace/race/race_cbr/constraint_based_reasoning_services/dist/TableSetting.owl";
	private static final String OWL = "http://www.w3.org/2002/07/owl#";
	private static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private static final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Model model = ModelFactory.createDefaultModel();
		InputStream in = FileManager.get().open(inputFileName);
		if (in == null) {
			throw new IllegalArgumentException( "File: " + inputFileName + " not found");
		}
		// read the RDF/XML file
		model.read(in, "");

		Resource res = model.createResource(NSTABLESETTING + "TableSetting");
		//Property prop = model.getProperty(subClassOf);
		//StmtIterator  iter = model.listStatements(new SimpleSelector(res, prop, (RDFNode)null));
		StmtIterator  iter = model.listStatements(new SimpleSelector(null, null, (RDFNode)null));
		while(iter.hasNext()){
			System.out.println(iter.nextStatement().toString());
		}

	}

}
