package test.ontology;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

//import com.hp.hpl.jena.ontology.Individual;
//import com.hp.hpl.jena.ontology.OntModel;
//import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Property;




import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.ResultSet;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;


public class WellSetTableOntologyParser {

	/**
	 * @author iran Mansouri
	 */

	private static final String NSUPPER = "http://purl.org/net/race/upper.owl#";
	private static final String NSRACE = "http://purl.org/net/race/race.owl#";
	private static final String NSTABLESETTING = "http://www.owl-ontologies.com/TableSetting.owl#";
	private static final String inputFileName = "/home/iran/fuerte_workspace/race/race_cbr/constraint_based_reasoning_services/dist/TableSetting.owl";
	private static final String OWL = "http://www.w3.org/2002/07/owl";
	private static final String RDFS = "http://www.w3.org/2000/01/rdf-schema";

	//http://purl.org/net/race/upper.owl#

	public static void main(String[] args) {

		//		Model model = ModelFactory.createDefaultModel();
		//		InputStream in = FileManager.get().open(inputFileName);
		//        if (in == null) {
		//            throw new IllegalArgumentException( "File: " + inputFileName + " not found");
		//        }
		//        // read the RDF/XML file
		//        model.read(in, "");
		//        
		//        Resource res = model.createResource(NSTABLESETTING + "TableSetting");
		//        Property prop = model.getProperty(subClassOf);
		//        //StmtIterator  iter = model.listStatements(new SimpleSelector(res, prop, (RDFNode)null));
		//        StmtIterator  iter = model.listStatements(new SimpleSelector(null, null, (RDFNode)null));
		//        while(iter.hasNext()){
		//        	System.out.println(iter.nextStatement().toString());
		//        }

		String SOURCE = "http://www.owl-ontologies.com/TableSetting.owl";
		String NS = SOURCE + "#";
		//create a model using reasoner
		OntModel model1 = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF);
		//create a model which doesn't use a reasoner
		OntModel model2 = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM);
		// read the RDF/XML file
		InputStream in = FileManager.get().open(inputFileName);
		model1.read( in, "" );
		model2.read( in, "" );	
		//prints out the RDF/XML structure
		System.out.println(" ");


		// Create a new query
		String queryString =        
				"PREFIX NS: <http://www.owl-ontologies.com/TableSetting.owl#> " +
						"PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>" +
						"PREFIX owl:<http://www.w3.org/2002/07/owl#>" +

    		"SELECT ?object" +
    		"WHERE { ?x owl:onProperty  NS:hasPart ." +
    		"NS:LunchConfiguration rdfs:subClassOf ?x ." +
    		"?x owl:someValuesFrom ?object" +
    		"} \n";


		Query query = QueryFactory.create(queryString);

		System.out.println("----------------------");

		System.out.println("Query Result Sheet");

		System.out.println("----------------------");

		System.out.println("Direct&Indirect Descendants (model1)");

		System.out.println("-------------------");


		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, model1);
		com.hp.hpl.jena.query.ResultSet rs =  qe.execSelect();
		
		
		// Output query results    
		//ResultSetFormatter.out(rs, query);
		while (rs.hasNext()) {
			   QuerySolution row = rs.next();
			   Iterator columns = row.varNames();
			   while (columns.hasNext()) {
			      RDFNode cell = row.get((String)columns.next());
			      if (cell.isResource()) {
			         Resource resource =  cell.asResource();
			         //do something maybe with the OntModel??? 
			      }
			      else {
			         //do something else
			      }
			   }
			}
		
		qe.close();

		//    System.out.println("----------------------");
		//    System.out.println("Only Direct Descendants");
		//    System.out.println("----------------------");
		//    
		//    // Execute the query and obtain results
		//    qe = QueryExecutionFactory.create(query, model2);
		//    results =  qe.execSelect();
		//
		//    // Output query results    
		//    ResultSetFormatter.out(System.out, results, query);  
		//    qe.close();



	}





}
