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
	private static final String inputFileName = "/home/iran/fuerte_workspace/race/race_cbr/constraint_based_reasoning_services/dist/race-V14.owl";
	private static final String OWL = "http://www.w3.org/2002/07/owl#";
	private static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private static final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
	private static final String NSRACE = "http://purl.org/net/race/race.owl#";
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Resource tmp = null;
		Model model = ModelFactory.createDefaultModel();
		InputStream in = FileManager.get().open(inputFileName);
		if (in == null) {
			throw new IllegalArgumentException( "File: " + inputFileName + " not found");
		}
		// read the RDF/XML file
		model.read(in, "");

		Resource res = model.createResource(NSRACE + "HorizontalTable");
		Property prop = model.getProperty(RDFS + "subClassOf");
		
		
		StmtIterator  iter = model.listStatements(new SimpleSelector(res, prop, (RDFNode)null));
		//StmtIterator  iter = model.listStatements(new SimpleSelector(null, null, (RDFNode)null));
		while(iter.hasNext()){
			StmtIterator  iter1 = model.listStatements(new SimpleSelector((Resource) iter.nextStatement().getObject(), 
					model.getProperty(OWL + "onProperty"), model.createResource(NSRACE + "hasRectangleAlgebraConstraintA")));
			while(iter1.hasNext()){
				tmp = iter1.nextStatement().getSubject();
				//System.out.println(iter1.nextStatement().asTriple());				
			}
			//?object owl:someValuesFrom ?x.			
		}
		
		//System.out.println(tmp.toString());
		
		StmtIterator  i = model.listStatements(new SimpleSelector(model.createResource("23a42bab:13b671a5a87:-7fa2"), model.getProperty(OWL + "valuesFrom"), (RDFNode)null));
		while(i.hasNext()){
			System.out.println(i.nextStatement().getObject().toString());
		}
		

	}

}
