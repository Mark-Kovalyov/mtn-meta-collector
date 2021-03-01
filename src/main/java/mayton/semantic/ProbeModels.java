package mayton.semantic;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.*;

public class ProbeModels {

    public static void main(String[] args) {

        Model schema = ModelFactory.createDefaultModel();
        Model model = ModelFactory.createDefaultModel();
        InfModel rdfsModel = ModelFactory.createRDFSModel(schema, model);

        Seq seq;

        Resource resource;

        OntModel ontModel = ModelFactory.createOntologyModel();

    }

}
