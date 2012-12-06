package test.ontology;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.*;
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

public class TableAreaOntologyPareser {

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
		

		// Create a new query
		String queryString =        
				"PREFIX NS: <http://purl.org/net/race/race.owl#> " +
						"PREFIX upper: <http://purl.org/net/race/upper.owl#>" +
						"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
						"PREFIX owl: <http://www.w3.org/2002/07/owl#>" +

    		"SELECT   ?y" +
    		"WHERE { NS:HorizontalTable rdfs:subClassOf ?object." +
    		"?object owl:onProperty  NS:hasRectangleAlgebraConstraintA." +
    		"?object owl:someValuesFrom ?x." +
    		"?x owl:intersectionOf ?y"  +
    		"} \n";


		Query query = QueryFactory.create(queryString);

		System.out.println("----------------------");
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		com.hp.hpl.jena.query.ResultSet rs =  qe.execSelect();

		System.out.println(rs.getRowNumber());
		System.out.println(rs.hasNext());
		
		// Output query results    
		//ResultSetFormatter.out(rs, query);
		while (rs.hasNext()) {
			System.out.println("test man injam");
			QuerySolution row = rs.next();
			
			Iterator columns = row.varNames();
			while (columns.hasNext()) {
				System.out.println("helo");
				RDFNode cell = row.get((String)columns.next());
				if (cell.isResource()) {
					Resource resource =  cell.asResource();

					System.out.println(resource.toString());
				}
				else {
					//do something else
				}
			}
		}

		qe.close();


	}

}
